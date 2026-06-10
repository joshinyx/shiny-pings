package com.shinypings.hud;

import com.shinypings.config.PingConfig;
import com.shinypings.ping.PingTracker;
import com.shinypings.ping.PingTracker.ItemEntry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.List;

// Item card at the top of the screen: the item inside a dark slot, with the live distance below.
public final class ItemSpotOverlay {

	// The dark rounded slot used by the F4 gamemode switcher; native size 26x26 (don't scale it).
	private static final Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("gamemode_switcher/slot");
	private static final int SLOT = 26;
	private static final int ITEM = 16;
	private static final int ITEM_INSET = (SLOT - ITEM) / 2;
	private static final int TOP_MARGIN = 6;

	private ItemSpotOverlay() {
	}

	public static void render(GuiGraphicsExtractor graphics, DeltaTracker delta) {
		List<ItemEntry> items = PingTracker.items();
		if (items.isEmpty()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		PingConfig cfg = PingConfig.get();

		ItemEntry item = items.get(items.size() - 1);

		int centerX = graphics.guiWidth() / 2;
		int slotX = centerX - SLOT / 2;
		int slotY = TOP_MARGIN;

		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, slotX, slotY, SLOT, SLOT);
		graphics.item(item.stack, slotX + ITEM_INSET, slotY + ITEM_INSET);

		int dist = currentDistance(mc, item);
		String text = cfg.itemSpotText.replace("{dist}", Integer.toString(dist));
		graphics.text(font, text, centerX - font.width(text) / 2, slotY + SLOT + 3, 0xFFFFFFFF, true);
	}

	// Live distance to the item, frozen at the last value once the entity despawns.
	private static int currentDistance(Minecraft mc, ItemEntry item) {
		if (mc.player != null && item.entity != null && item.entity.isAlive()) {
			item.lastDistance = (int) Math.round(
					mc.player.position().distanceTo(item.entity.position()));
		}
		return item.lastDistance;
	}
}
