package com.shinypings;

import com.mojang.blaze3d.platform.InputConstants;
import com.shinypings.config.PingConfig;
import com.shinypings.hud.ItemSpotOverlay;
import com.shinypings.ping.PingExpansion;
import com.shinypings.ping.PingManager;
import com.shinypings.ping.PingTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class ShinyPingsClient implements ClientModInitializer {

	public static final String MOD_ID = "shinypings";
	public static final Logger LOGGER = LoggerFactory.getLogger("Shiny Pings");

	private static KeyMapping pingKey;

	public static KeyMapping pingKey() {
		return pingKey;
	}

	@Override
	public void onInitializeClient() {
		PingConfig.load();

		// 26.1+ key categories are registered objects, not plain strings (changed in 1.21.9).
		KeyMapping.Category category = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(MOD_ID, "main"));

		pingKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.shinypings.ping",
				InputConstants.Type.MOUSE,
				GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
				category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (pingKey.consumeClick()) {
				PingManager.onPing(client);
			}
			PingTracker.tick();
			PingExpansion.tick(client);
		});

		HudElementRegistry.attachElementBefore(
				VanillaHudElements.CHAT,
				Identifier.fromNamespaceAndPath(MOD_ID, "item_spot"),
				ItemSpotOverlay::render);

		LOGGER.info("Shiny Pings initialised — ping key bound to the middle mouse button.");
	}
}
