package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.compat.sublevel.SubLevelCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class EmberEnergyConverterBlockEntity extends EmberReceiverBlockEntity {
	public static final int FE_PER_EMBER = 100;
	public static final int MAX_FE_OUTPUT = 1000;
	public static final int FE_CAPACITY = 20000;

	private final OutputEnergyStorage energy = new OutputEnergyStorage(FE_CAPACITY, MAX_FE_OUTPUT);

	public EmberEnergyConverterBlockEntity(BlockPos pos, BlockState state) {
		super(RegistryManager.EMBER_ENERGY_CONVERTER_ENTITY.get(), pos, state);
		capability.setEmberCapacity(2000);
	}

	public IEnergyStorage getEnergyStorage(Direction side) {
		Direction output = getBlockState().getValue(BlockStateProperties.FACING).getOpposite();
		return side == null || side == output ? energy : null;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, EmberEnergyConverterBlockEntity blockEntity) {
		blockEntity.ticksExisted++;
		Direction output = state.getValue(BlockStateProperties.FACING).getOpposite();

		int room = blockEntity.energy.getMaxEnergyStored() - blockEntity.energy.getEnergyStored();
		int produced = Math.min(MAX_FE_OUTPUT, room);
		double emberCost = produced / (double) FE_PER_EMBER;
		double available = blockEntity.capability.removeAmount(emberCost, false);
		produced = Math.min(produced, (int) Math.floor(available * FE_PER_EMBER));
		if (produced > 0) {
			blockEntity.capability.removeAmount(produced / (double) FE_PER_EMBER, true);
			blockEntity.energy.internalReceive(produced);
		}

		BlockEntity targetTile = SubLevelCompat.findAdjacent(blockEntity, output);
		IEnergyStorage target = getTargetEnergyStorage(targetTile, output.getOpposite());
		if (target != null && target.canReceive()) {
			int accepted = target.receiveEnergy(Math.min(MAX_FE_OUTPUT, blockEntity.energy.getEnergyStored()), true);
			if (accepted > 0) {
				target.receiveEnergy(blockEntity.energy.internalExtract(accepted), false);
			}
		}
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		energy.setStored(tag.getInt("Energy"));
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("Energy", energy.getEnergyStored());
	}

	private static IEnergyStorage getTargetEnergyStorage(BlockEntity targetTile, Direction side) {
		if (targetTile == null || targetTile.getLevel() == null) {
			return null;
		}
		return targetTile.getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, targetTile.getBlockPos(),
				targetTile.getBlockState(), targetTile, side);
	}

	private static final class OutputEnergyStorage extends EnergyStorage {
		private OutputEnergyStorage(int capacity, int maxExtract) {
			super(capacity, 0, maxExtract);
		}

		private void internalReceive(int amount) {
			energy = Math.min(capacity, energy + amount);
		}

		private int internalExtract(int amount) {
			int extracted = Math.min(energy, amount);
			energy -= extracted;
			return extracted;
		}

		private void setStored(int amount) {
			energy = Math.max(0, Math.min(capacity, amount));
		}
	}
}
