package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.DynamicMetalSeeds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class DynamicCrystalSeedBlockEntity extends CrystalSeedBlockEntity {

	public DynamicCrystalSeedBlockEntity(BlockPos pos, BlockState state) {
		super(RegistryManager.DYNAMIC_CRYSTAL_SEED_ENTITY.get(), pos, state, DynamicMetalSeeds.DEFAULT_METAL);
		setDynamicMetal(DynamicMetalSeeds.DEFAULT_METAL, DynamicMetalSeeds.DEFAULT_COLOR);
	}

	public void setDynamicMetal(String metal, int color) {
		String normalized = DynamicMetalSeeds.normalizeMetal(metal);
		if (normalized.isBlank()) {
			normalized = DynamicMetalSeeds.DEFAULT_METAL;
		}
		setSeedType(normalized);
		this.texture = DYNAMIC_TEXTURE;
		this.tintColor = color & 0xFFFFFF;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		setDynamicMetal(nbt.getString("metal"), nbt.contains("color") ? nbt.getInt("color") : DynamicMetalSeeds.colorFor(nbt.getString("metal")));
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		nbt.putString("metal", type);
		nbt.putInt("color", tintColor);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbt = super.getUpdateTag(registries);
		nbt.putString("metal", type);
		nbt.putInt("color", tintColor);
		return nbt;
	}
}
