package com.rekindled.embers.power;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.LazyOptional;

public class DefaultEmberCapability implements IEmberCapability {
	public static boolean allAcceptVolatile = false;
	private double ember = 0;
	private double capacity = 0;

	private final LazyOptional<IEmberCapability> holder;

	public DefaultEmberCapability() {
		holder = LazyOptional.of(() -> this);
	}

	public DefaultEmberCapability(IEmberCapability capability) {
		holder = LazyOptional.of(() -> capability);
	}

	@Override
	public double getEmber() {
		return ember;
	}

	@Override
	public double getEmberCapacity() {
		return capacity;
	}

	@Override
	public void setEmber(double value) {
		ember = sanitizeEmber(value, capacity);
	}

	@Override
	public void setEmberCapacity(double value) {
		capacity = sanitizeCapacity(value);
		ember = sanitizeEmber(ember, capacity);
	}

	@Override
	public double addAmount(double value, boolean doAdd) {
		double added = Math.min(capacity - ember, sanitizeAmount(value));
		double newEmber = ember + added;
		if (doAdd){
			if(newEmber != ember)
				onContentsChanged();
			ember = sanitizeEmber(newEmber, capacity);
		}
		return added;
	}

	@Override
	public double removeAmount(double value, boolean doRemove) {
		double removed = Math.min(ember, sanitizeAmount(value));
		double newEmber = ember - removed;
		if (doRemove){
			if(newEmber != ember)
				onContentsChanged();
			ember = sanitizeEmber(newEmber, capacity);
		}
		return removed;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		nbt.putDouble(EMBER, sanitizeEmber(ember, capacity));
		nbt.putDouble(EMBER_CAPACITY, sanitizeCapacity(capacity));
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		if (nbt.contains(EMBER_CAPACITY)){
			setEmberCapacity(nbt.getDouble(EMBER_CAPACITY));
		}
		if (nbt.contains(EMBER)){
			setEmber(nbt.getDouble(EMBER));
		}
	}

	@Override
	public void onContentsChanged() {

	}

	@Override
	public boolean acceptsVolatile() {
		return allAcceptVolatile;
	}

	@Override
	public void invalidate() {
		holder.invalidate();
	}

	public <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap, final @Nullable Direction side) {
		if (EmbersCapabilities.EMBER_CAPABILITY != null && cap == EmbersCapabilities.EMBER_CAPABILITY)
			return EmbersCapabilities.EMBER_CAPABILITY.orEmpty(cap, holder);
		return LazyOptional.empty();
	}

	private static double sanitizeCapacity(double value) {
		if (!Double.isFinite(value)) {
			return 0;
		}
		return Math.max(0, value);
	}

	private static double sanitizeAmount(double value) {
		if (!Double.isFinite(value)) {
			return 0;
		}
		return Math.max(0, value);
	}

	private static double sanitizeEmber(double value, double capacity) {
		if (!Double.isFinite(value)) {
			return 0;
		}
		return Math.max(0, Math.min(value, capacity));
	}
}
