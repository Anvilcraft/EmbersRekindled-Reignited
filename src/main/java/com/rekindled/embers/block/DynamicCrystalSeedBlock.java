package com.rekindled.embers.block;

import java.util.List;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.CrystalSeedBlockEntity;
import com.rekindled.embers.blockentity.DynamicCrystalSeedBlockEntity;
import com.rekindled.embers.item.DynamicCrystalSeedBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class DynamicCrystalSeedBlock extends CrystalSeedBlock {

	public DynamicCrystalSeedBlock(Properties properties) {
		super(properties, "tin");
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DynamicCrystalSeedBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? createTickerHelper(type, RegistryManager.DYNAMIC_CRYSTAL_SEED_ENTITY.get(), CrystalSeedBlockEntity::clientTick) : createTickerHelper(type, RegistryManager.DYNAMIC_CRYSTAL_SEED_ENTITY.get(), CrystalSeedBlockEntity::serverTick);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof DynamicCrystalSeedBlockEntity seed) {
			seed.setDynamicMetal(DynamicCrystalSeedBlockItem.getMetal(stack), DynamicCrystalSeedBlockItem.getColor(stack));
			seed.setChanged();
			if (!level.isClientSide) {
				level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
			}
		}
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof DynamicCrystalSeedBlockEntity seed) {
			return List.of(DynamicCrystalSeedBlockItem.withMetal(seed.type, seed.tintColor));
		}
		return List.of(DynamicCrystalSeedBlockItem.withMetal(DynamicCrystalSeedBlockItem.getMetal(ItemStack.EMPTY), DynamicCrystalSeedBlockItem.getColor(ItemStack.EMPTY)));
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity instanceof DynamicCrystalSeedBlockEntity seed) {
			return DynamicCrystalSeedBlockItem.withMetal(seed.type, seed.tintColor);
		}
		return super.getCloneItemStack(level, pos, state);
	}
}
