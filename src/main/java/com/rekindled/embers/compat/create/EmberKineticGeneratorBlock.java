package com.rekindled.embers.compat.create;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EmberKineticGeneratorBlock extends DirectionalKineticBlock implements IBE<EmberKineticGeneratorBlockEntity>, IWrenchable {
	public EmberKineticGeneratorBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	@Override
	public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(FACING);
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getAxis();
	}

	@Override
	public IRotate.SpeedLevel getMinimumRequiredSpeedLevel() {
		return IRotate.SpeedLevel.NONE;
	}

	@Override
	public Class<EmberKineticGeneratorBlockEntity> getBlockEntityClass() {
		return EmberKineticGeneratorBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends EmberKineticGeneratorBlockEntity> getBlockEntityType() {
		return CreateCompat.EMBER_KINETIC_GENERATOR_ENTITY.get();
	}
}
