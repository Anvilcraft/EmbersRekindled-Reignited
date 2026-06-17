package com.rekindled.embers.augment;

import java.util.HashMap;
import java.util.UUID;

import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class CasterOrbAugment extends AugmentBase {

	public static float prevCooledStrength = 0;
	public static float cooldownTicks = 0;
	public static HashMap<UUID,Float> cooldownTicksServer = new HashMap<>();

	public CasterOrbAugment(ResourceLocation id) {
		super(id, 2.0);
		NeoForge.EVENT_BUS.register(this);
	}

	public static void setCooldown(UUID uuid, float ticks) {
		cooldownTicksServer.put(uuid,ticks);
	}

	public static boolean hasCooldown(UUID uuid) {
		return cooldownTicksServer.getOrDefault(uuid,0.0f) > 0;
	}

	@SubscribeEvent
	public void onServerTick(ServerTickEvent.Pre event) { {
			for (UUID uuid : cooldownTicksServer.keySet()) {
				Float ticks = cooldownTicksServer.get(uuid) - 1;
				cooldownTicksServer.put(uuid, ticks);
			}
		}
	}

	@SubscribeEvent
	public void onSwing(PlayerInteractEvent.LeftClickBlock event) {
		Player player = event.getEntity();
		Level world = event.getLevel();
		ItemStack heldStack = event.getItemStack();
		tryShoot(player, world, heldStack);
	}

	@SubscribeEvent
	public void onSwing(PlayerInteractEvent.LeftClickEmpty event) {
		Player player = event.getEntity();
		Level world = event.getLevel();
		ItemStack heldStack = event.getItemStack();
		tryShoot(player, world, heldStack);
	}

	private void tryShoot(Player player, Level world, ItemStack heldStack) {
		if (!world.isClientSide() || !AugmentUtil.hasHeat(heldStack)) {
			return;
		}
		int level = AugmentUtil.getAugmentLevel(heldStack, this);
		if (level > 0 && EmberInventoryUtil.getEmberTotal(player) > cost) {
			CasterOrbAugmentClient.tryShoot(player);
		}
	}
}
