package com.rekindled.embers.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import com.rekindled.embers.compat.legacy.capabilities.ICapabilityProvider;

public class EmberBulbItem extends EmberStorageItem implements IEmbersCurioItem {

	public static final double CAPACITY = 1000.0;

	public EmberBulbItem(Properties properties) {
		super(properties);
	}

	@Override
	public double getCapacity() {
		return CAPACITY;
	}

	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new EmberJarItem.EmberJarCapability(stack, getCapacity());
	}
}
