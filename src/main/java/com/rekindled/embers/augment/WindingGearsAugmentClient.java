package com.rekindled.embers.augment;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class WindingGearsAugmentClient {

	private static boolean initialized;

	private WindingGearsAugmentClient() {
	}

	public static void init() {
		if (!initialized) {
			initialized = true;
			NeoForge.EVENT_BUS.addListener(WindingGearsAugmentClient::onClientUpdate);
		}
	}

	private static int getBarY(int height) {
		return height - 31;
	}

	private static int getBarX(int width) {
		return width / 2 - 11 - 81;
	}

	private static void onClientUpdate(ClientTickEvent.Pre event) {
		WindingGearsAugment.ticks++;
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;

		if (player != null) {
			ItemStack stack = WindingGearsAugment.getHeldClockworkTool(player);
			if (!stack.isEmpty()) {
				WindingGearsAugment.spoolLast = WindingGearsAugment.spool;
				WindingGearsAugment.spool = (int) (WindingGearsAugment.BAR_WIDTH * 4 * WindingGearsAugment.getCharge(player.level(), stack) / WindingGearsAugment.MAX_CHARGE);
				WindingGearsAugment.angleLast = WindingGearsAugment.angle;
				WindingGearsAugment.angle += WindingGearsAugment.getRotationSpeed(player.level(), stack);
				if (mc.options.keyAttack.isDown() && mc.hitResult instanceof EntityHitResult entityHit && canAutoAttack(player, stack, entityHit)) {
					mc.gameMode.attack(player, entityHit.getEntity());
				}
			}
		}
	}

	private static boolean canAutoAttack(LocalPlayer player, ItemStack stack, EntityHitResult objectMouseOver) {
		return player.getAttackStrengthScale(0) >= 1.0f && WindingGearsAugment.getCharge(player.level(), stack) > 0;
	}

	private static boolean isInvulnerable(Entity entity) {
		return entity.isInvulnerable() || (entity instanceof LivingEntity living && living.invulnerableTime > 0);
	}

	public static void renderSpringUnderlay(Minecraft mc, GuiGraphics graphics, float partialTicks, int width, int height) {
		int fill = (int) (WindingGearsAugment.spoolLast * (1 - partialTicks) + WindingGearsAugment.spool * partialTicks);
		fill += 16;
		Player player = mc.player;
		if (player == null) {
			return;
		}
		ItemStack stack = WindingGearsAugment.getHeldClockworkTool(player);
		if (!stack.isEmpty()) {
			int x = getBarX(width);
			int y = getBarY(height);

			int segs = fill / 32;
			int last = fill % 32;
			int u = WindingGearsAugment.BAR_U;
			int v = WindingGearsAugment.BAR_V + 8;

			int evenWidth = segs * 8;
			int oddWidth = segs * 8 - 4;
			int evenFillBack = Mth.clamp(last - 16, 0, 8);
			int oddFillBack = Mth.clamp(last, 0, 8);

			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x, y, u, v, evenWidth, WindingGearsAugment.BAR_HEIGHT);
			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x + evenWidth, y + 8 - evenFillBack, u + evenWidth, v + 8 - evenFillBack, 8, evenFillBack);
			v += 16;
			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x, y, u, v, oddWidth, WindingGearsAugment.BAR_HEIGHT);
			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x + oddWidth, y + 8 - oddFillBack, u + oddWidth, v + 8 - oddFillBack, 8, oddFillBack);
		}
	}

	public static void renderSpringOverlay(Minecraft mc, GuiGraphics graphics, float partialTicks, int width, int height) {
		int fill = (int) (WindingGearsAugment.spoolLast * (1 - partialTicks) + WindingGearsAugment.spool * partialTicks);
		double currentAngle = WindingGearsAugment.angleLast * (1 - partialTicks) + WindingGearsAugment.angle * partialTicks;
		int gearFrame = (int) (currentAngle * 4f / 360f);
		int uGear = (gearFrame % 4) * 10;
		int vGear = 16;
		fill += 16;
		Player player = mc.player;
		if (player == null) {
			return;
		}
		ItemStack stack = WindingGearsAugment.getHeldClockworkTool(player);
		if (!stack.isEmpty()) {
			int x = getBarX(width);
			int y = getBarY(height);

			int segs = fill / 32;
			int last = fill % 32;
			int u = WindingGearsAugment.BAR_U;
			int v = WindingGearsAugment.BAR_V;

			int evenWidth = segs * 8;
			int oddWidth = segs * 8 - 4;
			int evenFillFront = Mth.clamp(last - 24, 0, 8);
			int oddFillFront = Mth.clamp(last - 8, 0, 8);

			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x - 9, y - 1, uGear, vGear, 10, 10);

			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x, y, u, v, evenWidth, WindingGearsAugment.BAR_HEIGHT);
			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x + evenWidth, y, u + evenWidth, v, 8, evenFillFront);
			v += 16;
			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x, y, u, v, oddWidth, WindingGearsAugment.BAR_HEIGHT);
			graphics.blit(WindingGearsAugment.TEXTURE_HUD, x + oddWidth, y, u + oddWidth, v, 8, oddFillFront);
		}
	}
}
