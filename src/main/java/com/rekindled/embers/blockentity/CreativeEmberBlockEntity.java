package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.power.DefaultEmberCapability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.LazyOptional;

public class CreativeEmberBlockEntity extends BlockEntity {

	public IEmberCapability capability = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			super.onContentsChanged();
			CreativeEmberBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return true;
		}

		@Override
		public double getEmber() {
			return getEmberCapacity() / 2.0;
		}

		@Override
		public double addAmount(double value, boolean doAdd) {
			return value;
		}

		@Override
		public double removeAmount(double value, boolean doRemove) {
			return value;
		}
	};

	public CreativeEmberBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CREATIVE_EMBER_ENTITY.get(), pPos, pBlockState);
		capability.setEmberCapacity(80000);
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
