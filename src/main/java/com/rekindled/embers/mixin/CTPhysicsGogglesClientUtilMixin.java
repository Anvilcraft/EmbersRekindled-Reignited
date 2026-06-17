package com.rekindled.embers.mixin;

import com.rekindled.embers.compat.createthrusters.ThrustersCompat;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.example.createthrusters.neoforge.client.CTPhysicsGogglesClientUtil", remap = false)
public abstract class CTPhysicsGogglesClientUtilMixin {

	@Inject(method = "isWearingPhysicsGoggles", at = @At("RETURN"), cancellable = true)
	private static void embers$wearingPhysicsAshenGoggles(LocalPlayer player, CallbackInfoReturnable<Boolean> cir) {
		if (Boolean.TRUE.equals(cir.getReturnValue()) || player == null) {
			return;
		}
		if (ThrustersCompat.isWearingPhysicsAshenGoggles(player)) {
			cir.setReturnValue(true);
		}
	}
}
