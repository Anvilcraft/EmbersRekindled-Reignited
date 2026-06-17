package com.rekindled.embers.compat.legacy.capabilities;

import net.neoforged.neoforge.common.util.INBTSerializable;
import net.minecraft.core.HolderLookup;

public interface ICapabilitySerializable<T extends net.minecraft.nbt.Tag> extends ICapabilityProvider, INBTSerializable<T> {
	T serializeNBT();

	void deserializeNBT(T tag);

	@Override
	default T serializeNBT(HolderLookup.Provider registries) {
		return serializeNBT();
	}

	@Override
	default void deserializeNBT(HolderLookup.Provider registries, T tag) {
		deserializeNBT(tag);
	}
}
