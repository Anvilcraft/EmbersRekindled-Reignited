package com.rekindled.embers.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public interface LegacyParticleDeserializer<T extends ParticleOptions> {

	T fromCommand(ParticleType<T> type, StringReader reader) throws CommandSyntaxException;

	T fromNetwork(ParticleType<T> type, FriendlyByteBuf buffer);
}
