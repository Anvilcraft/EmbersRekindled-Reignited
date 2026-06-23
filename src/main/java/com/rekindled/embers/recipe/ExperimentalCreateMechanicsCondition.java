package com.rekindled.embers.recipe;

import com.mojang.serialization.MapCodec;
import com.rekindled.embers.ConfigManager;

import net.neoforged.neoforge.common.conditions.ICondition;

public final class ExperimentalCreateMechanicsCondition implements ICondition {
	public static final ExperimentalCreateMechanicsCondition INSTANCE = new ExperimentalCreateMechanicsCondition();
	public static final MapCodec<ExperimentalCreateMechanicsCondition> CODEC = MapCodec.unit(INSTANCE).stable();

	private ExperimentalCreateMechanicsCondition() {
	}

	@Override
	public boolean test(IContext context) {
		return ConfigManager.ENABLE_EXPERIMENTAL_CREATE_MECHANICS != null && ConfigManager.ENABLE_EXPERIMENTAL_CREATE_MECHANICS.get();
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	@Override
	public String toString() {
		return "experimental_create_mechanics";
	}
}
