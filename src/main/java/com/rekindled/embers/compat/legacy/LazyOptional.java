package com.rekindled.embers.compat.legacy;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LazyOptional<T> {
	private Supplier<? extends T> supplier;
	private boolean valid = true;

	private LazyOptional(Supplier<? extends T> supplier) {
		this.supplier = supplier;
	}

	public static <T> LazyOptional<T> of(Supplier<? extends T> supplier) {
		return new LazyOptional<>(supplier);
	}

	public static <T> LazyOptional<T> empty() {
		return new LazyOptional<>(() -> null);
	}

	public boolean isPresent() {
		return resolve() != null;
	}

	public T orElse(T fallback) {
		T value = resolve();
		return value == null ? fallback : value;
	}

	public T orElseThrow() {
		T value = resolve();
		if (value == null) {
			throw new NoSuchElementException();
		}
		return value;
	}

	public void ifPresent(Consumer<? super T> consumer) {
		T value = resolve();
		if (value != null) {
			consumer.accept(value);
		}
	}

	public <R> LazyOptional<R> map(Function<? super T, ? extends R> mapper) {
		return of(() -> {
			T value = resolve();
			return value == null ? null : mapper.apply(value);
		});
	}

	@SuppressWarnings("unchecked")
	public <R> LazyOptional<R> cast() {
		return (LazyOptional<R>) this;
	}

	public Optional<T> resolveOptional() {
		return Optional.ofNullable(resolve());
	}

	public void invalidate() {
		valid = false;
		supplier = () -> null;
	}

	private T resolve() {
		return valid ? supplier.get() : null;
	}
}
