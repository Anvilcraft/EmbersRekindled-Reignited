package com.rekindled.embers.util;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class CompatRegistryObject<T> implements Supplier<T> {
	private final DeferredHolder<?, ?> holder;

	CompatRegistryObject(DeferredHolder<?, ?> holder) {
		this.holder = holder;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get() {
		return (T) holder.get();
	}

	public ResourceLocation getId() {
		return holder.getId();
	}

	@SuppressWarnings("unchecked")
	public ResourceKey<T> getKey() {
		return (ResourceKey<T>) holder.getKey();
	}
}
