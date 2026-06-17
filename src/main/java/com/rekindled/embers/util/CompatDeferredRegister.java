package com.rekindled.embers.util;

import java.util.function.Supplier;
import java.util.Collection;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CompatDeferredRegister<T> {
	private final DeferredRegister<T> delegate;

	private CompatDeferredRegister(DeferredRegister<T> delegate) {
		this.delegate = delegate;
	}

	public static <T> CompatDeferredRegister<T> create(Registry<T> registry, String namespace) {
		return new CompatDeferredRegister<>(DeferredRegister.create(registry, namespace));
	}

	public static <T> CompatDeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
		return new CompatDeferredRegister<>(DeferredRegister.create(registryKey, namespace));
	}

	public <I extends T> CompatRegistryObject<I> register(String name, Supplier<? extends I> supplier) {
		DeferredHolder<T, I> holder = delegate.register(name, supplier);
		return new CompatRegistryObject<>(holder);
	}

	public void register(IEventBus eventBus) {
		delegate.register(eventBus);
	}

	public Collection<CompatRegistryObject<T>> getEntries() {
		return delegate.getEntries().stream()
				.map(holder -> new CompatRegistryObject<T>((DeferredHolder<T, T>) holder))
				.toList();
	}
}
