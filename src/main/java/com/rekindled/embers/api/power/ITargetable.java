package com.rekindled.embers.api.power;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ITargetable {
	void setTargetPosition(BlockPos pos, Direction side);

	default void setTargetPosition(BlockPos pos, Direction side, BlockEntity targetEntity) {
		setTargetPosition(pos, side);
	}
}
