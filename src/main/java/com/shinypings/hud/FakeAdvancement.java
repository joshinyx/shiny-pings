package com.shinypings.hud;

import com.shinypings.config.PingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;

import java.util.Optional;

public final class FakeAdvancement {

	private FakeAdvancement() {
	}

	public static void show(Minecraft mc, Entity entity) {
		PingConfig cfg = PingConfig.get();
		ItemStack icon = new ItemStack(iconItemFor(entity));

		String name = entity.getName().getString();
		Component header = Component.literal(cfg.toastHeader);
		Component title = Component.literal(cfg.toastTitle.replace("{mob}", name));
		SoundEvent sound = resolveSound(cfg.toastSound);

		// One spotted toast at a time: reuse the existing one (cutting its animation) instead of stacking.
		ToastManager toasts = mc.getToastManager();
		SpottedToast existing = toasts.getToast(SpottedToast.class, SpottedToast.TOKEN);
		if (existing != null) {
			existing.refresh(icon, header, title, sound);
			mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
		} else {
			toasts.addToast(new SpottedToast(icon, header, title, sound));
		}
	}

	private static SoundEvent resolveSound(String id) {
		Identifier soundId = Identifier.tryParse(id);
		if (soundId == null) {
			return SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
		}
		return SoundEvent.createVariableRangeEvent(soundId);
	}

	// Curated representative drop, else the mob's spawn egg, else a name tag.
	private static Item iconItemFor(Entity entity) {
		try {
			EntityType<?> type = entity.getType();

			Item loot = MobLoot.iconFor(type);
			if (loot != null) {
				return loot;
			}

			Optional<Holder<Item>> egg = SpawnEggItem.byId(type);
			if (egg.isPresent()) {
				return egg.get().value();
			}
		} catch (Throwable ignored) {
		}
		return Items.NAME_TAG;
	}
}
