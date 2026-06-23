package com.rekindled.embers.compat.create;

import javax.annotation.Nullable;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CreatePoweredEmberUpgradeBlock extends DirectionalKineticBlock implements IBE<CreatePoweredEmberUpgradeBlockEntity>, IWrenchable {
	private final CreatePoweredUpgradeType upgradeType;

	public CreatePoweredEmberUpgradeBlock(CreatePoweredUpgradeType upgradeType, Properties properties) {
		super(properties);
		this.upgradeType = upgradeType;
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
	}

	public CreatePoweredUpgradeType getUpgradeType() {
		return upgradeType;
	}

	public Direction getUpgradeSide(BlockState state) {
		return state.getValue(FACING).getOpposite();
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

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (!CreateCompat.experimentalMechanicsEnabled()) {
			return null;
		}
		return super.getStateForPlacement(context);
	}

	@Override
	public Class<CreatePoweredEmberUpgradeBlockEntity> getBlockEntityClass() {
		return CreatePoweredEmberUpgradeBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends CreatePoweredEmberUpgradeBlockEntity> getBlockEntityType() {
		return CreateCompat.CREATE_POWERED_UPGRADE_ENTITY.get();
	}
}
