package com.rekindled.embers.mixin;

import com.rekindled.embers.Embers;
import com.rekindled.embers.util.ComparatorSignalUtil;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityComparatorMixin {
	@Inject(method = "setChanged()V", at = @At("TAIL"))
	private void embers$notifyComparatorOutputs(CallbackInfo callback) {
		BlockEntity blockEntity = (BlockEntity) (Object) this;
		if (Embers.MODID.equals(BuiltInRegistries.BLOCK.getKey(blockEntity.getBlockState().getBlock()).getNamespace())) {
			ComparatorSignalUtil.notifyOutputChanged(blockEntity);
		}
	}
}
