package com.shinypings.ping;

import com.shinypings.config.PingConfig;
import com.shinypings.hud.FakeAdvancement;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class PingManager {

	private PingManager() {
	}

	public static void onPing(Minecraft mc) {
		if (mc.player == null || mc.level == null) {
			return;
		}

		Entity camera = (mc.getCameraEntity() != null) ? mc.getCameraEntity() : mc.player;
		double range = PingConfig.get().pingRange;

		Vec3 start = camera.getEyePosition(1.0F);
		Vec3 look = camera.getViewVector(1.0F);
		Vec3 end = start.add(look.x * range, look.y * range, look.z * range);

		// Stop the ray at the first solid block so we don't ping entities behind walls.
		BlockHitResult block = mc.level.clip(new ClipContext(
				start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, camera));
		double maxReachSq = (block.getType() == HitResult.Type.BLOCK)
				? block.getLocation().distanceToSqr(start)
				: range * range;

		AABB searchBox = camera.getBoundingBox()
				.expandTowards(look.x * range, look.y * range, look.z * range)
				.inflate(1.0D);

		// Dropped items aren't "pickable" (Entity.isPickable() is false and ItemEntity doesn't
		// override it), so accept them explicitly — otherwise item pings never hit.
		EntityHitResult hit = ProjectileUtil.getEntityHitResult(
				camera, start, end, searchBox,
				e -> e != camera && !e.isSpectator() && (e.isPickable() || e instanceof ItemEntity),
				maxReachSq);

		if (hit == null) {
			return;
		}

		Entity target = hit.getEntity();
		if (target instanceof ItemEntity itemEntity) {
			pingItem(mc, itemEntity);
		} else {
			pingEntity(mc, target);
		}
	}

	private static void pingEntity(Minecraft mc, Entity target) {
		PingConfig cfg = PingConfig.get();

		PingTracker.addGlow(target, cfg.entityGlowDurationTicks);
		FakeAdvancement.show(mc, target);
		PingExpansion.start(target);
	}

	private static void pingItem(Minecraft mc, ItemEntity itemEntity) {
		PingConfig cfg = PingConfig.get();
		ItemStack stack = itemEntity.getItem();

		int distance = (int) Math.round(mc.player.position().distanceTo(itemEntity.position()));

		PingTracker.addItemSpot(stack.copy(), itemEntity, cfg.itemSpotDurationTicks, distance);
		PingTracker.addGlow(itemEntity, cfg.itemSpotDurationTicks);
	}
}
