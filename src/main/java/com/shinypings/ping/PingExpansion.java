package com.shinypings.ping;

import com.shinypings.config.PingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Spreading ping: a spotted mob radiates the spot to nearby mobs (within {@code expandRadius}),
 * and from each of those to their neighbours, wave by wave. A mob is only ever spotted once per
 * cascade ({@link #VISITED}), so the spread can't loop back; when no new mobs are reachable the
 * frontier empties and the visited set resets for the next ping.
 */
public final class PingExpansion {

	private PingExpansion() {
	}

	private static final class Wave {
		final Entity source;
		int delay;

		Wave(Entity source, int delay) {
			this.source = source;
			this.delay = delay;
		}
	}

	private static final Deque<Wave> FRONTIER = new ArrayDeque<>();
	// Identity set: we want per-object dedup, not equals().
	private static final Set<Entity> VISITED =
			Collections.newSetFromMap(new IdentityHashMap<>());

	public static void start(Entity seed) {
		if (!PingConfig.get().expandEnabled) {
			return;
		}
		if (VISITED.add(seed)) {
			FRONTIER.add(new Wave(seed, PingConfig.get().expandDelayTicks));
		}
	}

	public static void tick(Minecraft mc) {
		if (FRONTIER.isEmpty()) {
			VISITED.clear();
			return;
		}
		if (mc.level == null) {
			FRONTIER.clear();
			VISITED.clear();
			return;
		}

		List<Entity> ready = new ArrayList<>();
		for (Iterator<Wave> it = FRONTIER.iterator(); it.hasNext(); ) {
			Wave w = it.next();
			if (--w.delay <= 0) {
				ready.add(w.source);
				it.remove();
			}
		}
		for (Entity source : ready) {
			radiate(mc, source);
		}
	}

	private static void radiate(Minecraft mc, Entity source) {
		if (!source.isAlive()) {
			return;
		}
		PingConfig cfg = PingConfig.get();
		Level level = source.level();
		double r = cfg.expandRadius;
		double rSq = r * r;

		AABB box = new AABB(
				source.getX() - r, source.getY() - r, source.getZ() - r,
				source.getX() + r, source.getY() + r, source.getZ() + r);

		List<Mob> nearby = level.getEntitiesOfClass(Mob.class, box,
				m -> m != source && m.isAlive() && !VISITED.contains(m)
						&& source.distanceToSqr(m) <= rSq);

		SoundEvent sound = resolveSound(cfg.expandSound);

		for (Mob m : nearby) {
			// add() == false means another source already took this neighbour this tick.
			if (!VISITED.add(m)) {
				continue;
			}
			PingTracker.addGlow(m, cfg.entityGlowDurationTicks);
			level.playLocalSound(m.getX(), m.getY(), m.getZ(),
					sound, SoundSource.PLAYERS, 1.0F, 1.0F, false);
			FRONTIER.add(new Wave(m, cfg.expandDelayTicks));
		}
	}

	private static SoundEvent resolveSound(String id) {
		Identifier soundId = Identifier.tryParse(id);
		if (soundId == null) {
			return SoundEvents.AMETHYST_BLOCK_RESONATE;
		}
		return SoundEvent.createVariableRangeEvent(soundId);
	}
}
