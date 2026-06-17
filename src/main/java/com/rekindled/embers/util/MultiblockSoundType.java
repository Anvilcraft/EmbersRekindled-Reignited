package com.rekindled.embers.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class MultiblockSoundType extends DeferredSoundType {

	public final SoundType type;

	public MultiblockSoundType(SoundType type) {
		super(type.getVolume(), type.getPitch(), () -> SoundEvents.EMPTY, type::getStepSound, () -> SoundEvents.EMPTY, type::getHitSound, type::getFallSound);
		this.type = type;
	}

	@Override
	public SoundEvent getBreakSound() {
		return SoundEvents.EMPTY;
	}

	@Override
	public SoundEvent getStepSound() {
		return type.getStepSound();
	}

	@Override
	public SoundEvent getPlaceSound() {
		return SoundEvents.EMPTY;
	}

	@Override
	public SoundEvent getHitSound() {
		return type.getHitSound();
	}

	@Override
	public SoundEvent getFallSound() {
		return type.getFallSound();
	}
}
