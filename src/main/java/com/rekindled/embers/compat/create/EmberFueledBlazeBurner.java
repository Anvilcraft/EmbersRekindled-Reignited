package com.rekindled.embers.compat.create;

import com.rekindled.embers.api.power.IEmberCapability;

public interface EmberFueledBlazeBurner {
	int MAX_STORED_EMBER = 10000;

	IEmberCapability embers$getEmberFuel();

	double embers$addEmber(double amount, boolean simulate);

	void embers$refreshEmberFuelState(boolean playSound);
}
