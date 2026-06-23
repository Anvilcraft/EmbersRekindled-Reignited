package com.rekindled.embers.compat.create;

import com.rekindled.embers.blockentity.DawnstoneAnvilBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.kinetics.press.PressingBehaviour.Mode;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class CreateDawnstoneAnvilPressBridge {
	private CreateDawnstoneAnvilPressBridge() {
	}

	public static void tickAnvil(DawnstoneAnvilBlockEntity anvil) {
		Level level = anvil.getLevel();
		if (level == null || level.isClientSide) {
			return;
		}
		MechanicalPressBlockEntity press = findPress(level, anvil.getBlockPos());
		if (press == null) {
			anvil.resetCreatePressCycle();
			return;
		}
		PressingBehaviour behaviour = press.getPressingBehaviour();
		if (behaviour.running) {
			strikeIfReady(anvil, press, behaviour);
			return;
		}
		tryStartPress(anvil, press, behaviour);
	}

	public static boolean tryStartPress(DawnstoneAnvilBlockEntity anvil, BlockEntity pressEntity, PressingBehaviour behaviour) {
		if (!(pressEntity instanceof MechanicalPressBlockEntity press) || behaviour.running || press.getSpeed() == 0.0F || !anvil.hasProcessableRecipe()) {
			return false;
		}
		fillParticlesFromAnvil(anvil, behaviour);
		behaviour.start(Mode.BASIN);
		anvil.createPressCycleStarted(press.getBlockPos());
		return true;
	}

	public static boolean strikeIfReady(DawnstoneAnvilBlockEntity anvil, BlockEntity pressEntity, PressingBehaviour behaviour) {
		if (!(pressEntity instanceof MechanicalPressBlockEntity press) || !behaviour.running || behaviour.mode != Mode.BASIN || press.getSpeed() == 0.0F) {
			return false;
		}
		if (!anvil.shouldCreatePressStrike(press.getBlockPos(), behaviour.runningTicks)) {
			return false;
		}
		fillParticlesFromAnvil(anvil, behaviour);
		anvil.onHit(press);
		return true;
	}

	private static MechanicalPressBlockEntity findPress(Level level, BlockPos anvilPos) {
		BlockEntity twoBlocksUp = level.getBlockEntity(anvilPos.above(2));
		if (twoBlocksUp instanceof MechanicalPressBlockEntity press) {
			return press;
		}
		BlockEntity oneBlockUp = level.getBlockEntity(anvilPos.above());
		return oneBlockUp instanceof MechanicalPressBlockEntity press ? press : null;
	}

	private static void fillParticlesFromAnvil(DawnstoneAnvilBlockEntity anvil, PressingBehaviour behaviour) {
		behaviour.particleItems.clear();
		for (int slot = 0; slot < anvil.inventory.getSlots(); slot++) {
			ItemStack stack = anvil.inventory.getStackInSlot(slot);
			if (!stack.isEmpty()) {
				behaviour.particleItems.add(stack.copy());
			}
		}
	}
}
