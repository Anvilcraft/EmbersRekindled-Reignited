package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.upgrade.AtmosphericBellowsUpgrade;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.LazyOptional;

public class AtmosphericBellowsBlockEntity extends BlockEntity {

	public AtmosphericBellowsUpgrade upgrade;

	public AtmosphericBellowsBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ATMOSPHERIC_BELLOWS_ENTITY.get(), pPos, pBlockState);
		upgrade = new AtmosphericBellowsUpgrade(this);
	}

	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.isRemoved() && level.getBlockState(worldPosition).hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			if (cap == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY && side == level.getBlockState(worldPosition).getValue(BlockStateProperties.HORIZONTAL_FACING)) {
				return upgrade.getCapability(cap, side);
			}
		}
		return LazyOptional.empty();
	}
}
