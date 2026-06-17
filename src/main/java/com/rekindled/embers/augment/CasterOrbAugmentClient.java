package com.rekindled.embers.augment;

import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.network.message.MessageCasterOrb;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class CasterOrbAugmentClient {

	private static boolean initialized;

	private CasterOrbAugmentClient() {
	}

	public static void init() {
		if (!initialized) {
			initialized = true;
			NeoForge.EVENT_BUS.addListener(CasterOrbAugmentClient::onClientTick);
		}
	}

	public static void tryShoot(Player player) {
		if (CasterOrbAugment.prevCooledStrength == 1.0f && CasterOrbAugment.cooldownTicks == 0) {
			PacketHandler.sendToServer(new MessageCasterOrb(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z));
			CasterOrbAugment.cooldownTicks = 20;
		}
	}

	private static void onClientTick(ClientTickEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			CasterOrbAugment.prevCooledStrength = mc.player.getAttackStrengthScale(0);
		}
		if (CasterOrbAugment.cooldownTicks > 0) {
			CasterOrbAugment.cooldownTicks--;
		}
	}
}
