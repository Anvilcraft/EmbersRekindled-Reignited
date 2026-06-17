package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.power.DefaultEmberCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.LazyOptional;

public class CopperCellBlockEntity extends BlockEntity {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			CopperCellBlockEntity.this.setChanged();
		}
	};

	public CopperCellBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.COPPER_CELL_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(24000);
		capability.setEmber(0);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		capability.deserializeNBT(nbt);
		if (capability.getEmberCapacity() == 0)
			capability.setEmberCapacity(24000);
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		capability.writeToNBT(nbt);
	}

	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.isRemoved() && cap == EmbersCapabilities.EMBER_CAPABILITY) {
			return capability.getCapability(cap, side);
		}
		return LazyOptional.empty();
	}

	public void invalidateCaps() {
		
		capability.invalidate();
	}
}
