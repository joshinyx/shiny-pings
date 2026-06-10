package com.shinypings.mixin;

import com.shinypings.ping.PingTracker;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces a client-side glow on pinged entities. Entity.setGlowingTag(true) doesn't work on the
 * client (isCurrentlyGlowing() there reads the server-synced shared flag, which the server resets),
 * so we make isCurrentlyGlowing() return true for entities tracked by PingTracker; the vanilla
 * outline pass (via Minecraft.shouldEntityAppearGlowing) then renders the glow.
 */
@Mixin(Entity.class)
public abstract class EntityGlowMixin {

	@Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
	private void shinypings$forceGlowForPings(CallbackInfoReturnable<Boolean> cir) {
		if (PingTracker.isGlowing((Entity) (Object) this)) {
			cir.setReturnValue(true);
		}
	}
}
