package com.rekindled.embers.compat.legacy.capabilities;

public final class CapabilityManager {
	private CapabilityManager() {
	}

	public static <T> Capability<T> get(CapabilityToken<T> token) {
		return new Capability<>();
	}
}
