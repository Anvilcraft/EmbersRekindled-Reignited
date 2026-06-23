package com.rekindled.embers.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rekindled.embers.blockentity.DawnstoneAnvilBlockEntity;
import com.rekindled.embers.compat.create.CreateDawnstoneAnvilPressBridge;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.kinetics.press.PressingBehaviour.PressingBehaviourSpecifics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

@Mixin(PressingBehaviour.class)
public abstract class CreatePressDawnstoneAnvilMixin {
	@Shadow
	public boolean running;
	@Shadow
	public PressingBehaviourSpecifics specifics;

	@Shadow
	public abstract Level getWorld();

	@Shadow
	public abstract BlockPos getPos();

	@Inject(method = "tick", at = @At("HEAD"))
	private void embers$pressDawnstoneAnvil(CallbackInfo ci) {
		Level level = getWorld();
		if (level == null || level.isClientSide()) {
			return;
		}
		DawnstoneAnvilBlockEntity anvil = getAnvil(level);
		if (!running) {
			if (anvil != null) {
				moveLooseItemsIntoAnvil(level, anvil);
			}
			if (anvil != null && specifics instanceof BlockEntity press) {
				CreateDawnstoneAnvilPressBridge.tryStartPress(anvil, press, (PressingBehaviour) (Object) this);
			}
			return;
		}
		if (anvil != null && specifics instanceof BlockEntity press) {
			CreateDawnstoneAnvilPressBridge.strikeIfReady(anvil, press, (PressingBehaviour) (Object) this);
		}
	}

	private DawnstoneAnvilBlockEntity getAnvil(Level level) {
		BlockPos[] candidates = { getPos().below(2), getPos().below() };
		for (BlockPos pos : candidates) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof DawnstoneAnvilBlockEntity anvil) {
				return anvil;
			}
		}
		return null;
	}

	private void moveLooseItemsIntoAnvil(Level level, DawnstoneAnvilBlockEntity anvil) {
		AABB inputArea = new AABB(anvil.getBlockPos().above()).inflate(0.25D, 0.25D, 0.25D);
		for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, inputArea, ItemEntity::isAlive)) {
			ItemStack original = itemEntity.getItem();
			if (original.isEmpty()) {
				continue;
			}
			ItemStack remainder = original.copy();
			int startingCount = remainder.getCount();
			for (int slot = 0; slot < anvil.inventory.getSlots() && !remainder.isEmpty(); slot++) {
				remainder = anvil.inventory.insertItem(slot, remainder, false);
			}
			if (remainder.getCount() != startingCount) {
				if (remainder.isEmpty()) {
					itemEntity.discard();
				} else {
					itemEntity.setItem(remainder);
				}
				anvil.setChanged();
			}
		}
	}
}
