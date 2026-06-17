package com.rekindled.embers.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;

public interface LegacyParticleOptions extends ParticleOptions {

	void writeToNetwork(FriendlyByteBuf buffer);
}
