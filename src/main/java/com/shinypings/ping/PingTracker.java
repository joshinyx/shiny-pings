package com.shinypings.ping;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the active pings and counts them down each client tick: glowing entities and the item card.
 * (The spotted toast uses the vanilla toast system, which manages its own lifetime.)
 */
public final class PingTracker {

	private PingTracker() {
	}

	public static final class GlowEntry {
		public final Entity entity;
		public int ticks;

		GlowEntry(Entity entity, int ticks) {
			this.entity = entity;
			this.ticks = ticks;
		}
	}

	public static final class ItemEntry {
		public final ItemStack stack;
		// Kept so the card can show a live distance; may despawn.
		public final ItemEntity entity;
		public final int total;
		public int ticks;
		// Last known distance (blocks); frozen if the entity despawns.
		public int lastDistance;

		ItemEntry(ItemStack stack, ItemEntity entity, int total, int distance) {
			this.stack = stack;
			this.entity = entity;
			this.total = total;
			this.ticks = total;
			this.lastDistance = distance;
		}
	}

	private static final List<GlowEntry> GLOWS = new ArrayList<>();
	private static final List<ItemEntry> ITEMS = new ArrayList<>();

	public static void addGlow(Entity entity, int ticks) {
		for (GlowEntry g : GLOWS) {
			if (g.entity == entity) {
				g.ticks = Math.max(g.ticks, ticks);
				return;
			}
		}
		GLOWS.add(new GlowEntry(entity, ticks));
	}

	public static void addItemSpot(ItemStack stack, ItemEntity entity, int ticks, int distance) {
		// One item card at a time: a new ping replaces whatever was showing.
		ITEMS.clear();
		ITEMS.add(new ItemEntry(stack, entity, ticks, distance));
	}

	public static List<ItemEntry> items() {
		return ITEMS;
	}

	// Read by EntityGlowMixin.
	public static boolean isGlowing(Entity entity) {
		for (GlowEntry g : GLOWS) {
			if (g.entity == entity) {
				return true;
			}
		}
		return false;
	}

	public static void tick() {
		GLOWS.removeIf(g -> --g.ticks <= 0 || g.entity.isRemoved());
		ITEMS.removeIf(i -> --i.ticks <= 0);
	}
}
