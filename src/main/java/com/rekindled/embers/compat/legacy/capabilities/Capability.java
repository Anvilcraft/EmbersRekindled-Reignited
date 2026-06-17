package com.rekindled.embers.compat.legacy.capabilities;

import com.rekindled.embers.compat.legacy.LazyOptional;

public final class Capability<T> {
	public <R> LazyOptional<R> orEmpty(Capability<R> requested, LazyOptional<T> value) {
		return this == requested ? value.cast() : LazyOptional.empty();
	}
}
