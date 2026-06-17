package com.rekindled.embers.util;

import java.util.function.Supplier;

public class LegacyDeferredRegister<T> {

	CompatDeferredRegister<T> current;
	CompatDeferredRegister<T> old;

	public LegacyDeferredRegister(CompatDeferredRegister<T> current, CompatDeferredRegister<T> old) {
		this.current = current;
		this.old = old;
	}

	public <I extends T> CompatRegistryObject<I> register(final String name, final Supplier<? extends I> sup) {
		CompatRegistryObject<I> reg = this.current.register(name, sup);

		this.old.register(name, sup);
		return reg;
	}
}
