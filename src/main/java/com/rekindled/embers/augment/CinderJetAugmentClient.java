package com.rekindled.embers.augment;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public final class CinderJetAugmentClient {

	private CinderJetAugmentClient() {
	}

	public static boolean isLocalPlayer(LivingEntity entity) {
		return entity == Minecraft.getInstance().player;
	}
}
