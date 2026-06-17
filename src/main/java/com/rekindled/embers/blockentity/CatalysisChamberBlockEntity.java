package com.rekindled.embers.blockentity;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.recipe.CatalysisCombustionContext;
import com.rekindled.embers.recipe.ICatalysisCombustionRecipe;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.capabilities.ForgeCapabilities;
import com.rekindled.embers.compat.legacy.LazyOptional;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CatalysisChamberBlockEntity extends BlockEntity implements IExtraCapabilityInformation {

	public static ItemStack machine = new ItemStack(RegistryManager.CATALYSIS_CHAMBER_ITEM.get());
	int progress = 0;
	double multiplier = 0;
	public ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			CatalysisChamberBlockEntity.this.setChanged();
		}
	};
	public LazyOptional<IItemHandler> holder = LazyOptional.of(() -> inventory);
	public ICatalysisCombustionRecipe cachedRecipe = null;

	public CatalysisChamberBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.CATALYSIS_CHAMBER_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		if (nbt.contains("inventory"))
			inventory.deserializeNBT(registries, nbt.getCompound("inventory"));
		if (nbt.contains("progress"))
			progress = nbt.getInt("progress");
		multiplier = nbt.getDouble("multiplier");
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		nbt.put("inventory", inventory.serializeNBT(registries));
		nbt.putInt("progress", progress);
		nbt.putDouble("multiplier", multiplier);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbt = super.getUpdateTag(registries);
		nbt.putDouble("multiplier", multiplier);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CatalysisChamberBlockEntity blockEntity) {
		if (blockEntity.progress > 0) {
			blockEntity.progress --;
			if (blockEntity.progress == 0) {
				blockEntity.multiplier = 0;
				((ServerLevel) level).getChunkSource().blockChanged(pos);
			}
			blockEntity.setChanged();
		}
		if (blockEntity.progress == 0 && !blockEntity.inventory.getStackInSlot(0).isEmpty()) {
			CatalysisCombustionContext wrapper = new CatalysisCombustionContext(blockEntity.inventory, machine);
			blockEntity.cachedRecipe = Misc.getRecipe(blockEntity.cachedRecipe, RegistryManager.CATALYSIS_COMBUSTION.get(), wrapper, level);
			if (blockEntity.cachedRecipe != null) {
				blockEntity.multiplier = blockEntity.cachedRecipe.getmultiplier(wrapper);
				blockEntity.progress = blockEntity.cachedRecipe.getBurnTIme(wrapper);

				//the recipe is responsible for taking items from the inventory
				blockEntity.cachedRecipe.process(wrapper);
				((ServerLevel) level).getChunkSource().blockChanged(pos);
			}
		}
	}

	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (!this.isRemoved() && cap == ForgeCapabilities.ITEM_HANDLER) {
			return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, holder);
		}
		return LazyOptional.empty();
	}

	public void invalidateCaps() {
		
		holder.invalidate();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
	}

	@Override
	public boolean hasCapabilityDescription(Capability<?> capability) {
		return capability == ForgeCapabilities.ITEM_HANDLER;
	}

	@Override
	public void addCapabilityDescription(List<Component> strings, Capability<?> capability, Direction facing) {
		if (capability == ForgeCapabilities.ITEM_HANDLER)
			strings.add(IExtraCapabilityInformation.formatCapability(EnumIOType.INPUT, Embers.MODID + ".tooltip.goggles.item", Component.translatable(Embers.MODID + ".tooltip.goggles.item.catalysis")));
	}
}
