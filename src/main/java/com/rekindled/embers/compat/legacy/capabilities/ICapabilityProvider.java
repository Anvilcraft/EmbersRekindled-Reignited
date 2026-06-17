package com.rekindled.embers.compat.legacy.capabilities;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import com.rekindled.embers.compat.legacy.LazyOptional;

public interface ICapabilityProvider {
	<T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing);
}
