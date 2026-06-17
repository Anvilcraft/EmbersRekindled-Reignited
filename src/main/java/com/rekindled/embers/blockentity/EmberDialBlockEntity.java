package com.rekindled.embers.blockentity;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.IDialEntity;
import com.rekindled.embers.compat.sublevel.SubLevelCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class EmberDialBlockEntity extends BlockEntity implements IDialEntity {

	public double ember = 0;
	public double capacity = 0;
	public boolean display = false;

	public EmberDialBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_DIAL_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		if (nbt.contains(IEmberCapability.EMBER))
			ember = nbt.getDouble(IEmberCapability.EMBER);
		if (nbt.contains(IEmberCapability.EMBER_CAPACITY))
			capacity = nbt.getDouble(IEmberCapability.EMBER_CAPACITY);
		if (nbt.contains("display"))
			display = nbt.getBoolean("display");
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbt = super.getUpdateTag(registries);
		BlockState state = level.getBlockState(worldPosition);
		boolean display = false;
		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			BlockEntity blockEntity = SubLevelCompat.findAdjacent(this, facing.getOpposite());
			if (blockEntity != null) {
				IEmberCapability cap = com.rekindled.embers.util.CapabilityCompat.getCapability(blockEntity, EmbersCapabilities.EMBER_CAPABILITY, facing.getOpposite()).orElse(com.rekindled.embers.util.CapabilityCompat.getCapability(blockEntity, EmbersCapabilities.EMBER_CAPABILITY, null).orElse(null));
				if (cap != null) {
					nbt.putDouble(IEmberCapability.EMBER, cap.getEmber());
					nbt.putDouble(IEmberCapability.EMBER_CAPACITY, cap.getEmberCapacity());
					display = true;
				}
			}
		}
		nbt.putBoolean("display", display);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket(int maxLines) {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
