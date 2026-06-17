package com.rekindled.embers.augment;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ShiftingScalesAugmentClient {

	private static boolean initialized;

	private ShiftingScalesAugmentClient() {
	}

	public static void init() {
		if (!initialized) {
			initialized = true;
			NeoForge.EVENT_BUS.addListener(ShiftingScalesAugmentClient::onClientTick);
		}
	}

	public static void renderHeartsOverlay(Minecraft mc, GuiGraphics graphics, float partialTicks, int width, int height) {
		if (mc.player.isCreative() || mc.player.isSpectator()) {
			return;
		}
		int x = getBarX(width);
		int y = getBarY(height);

		int segs = ShiftingScalesAugment.scales / 3;
		int last = ShiftingScalesAugment.scales % 3;
		if (last > 0) {
			segs++;
		}
		int u = 18;
		int v = 0;

		for (int i = 0; i < segs; i++) {
			if (i == segs - 1) {
				u = ((last + 2) % 3) * 9;
			}
			graphics.blit(ShiftingScalesAugment.TEXTURE_HUD, x + 8 * (i % 10), y - 10 * (i / 10), u, v, 9, 9);
		}
	}

	public static void renderIngameOverlay(Minecraft mc, GuiGraphics graphics, float partialTicks, int width, int height) {
		if (mc.player.isCreative() || mc.player.isSpectator()) {
			return;
		}
		Iterator<ShiftingScalesAugment.ShardParticle> iterator = ShiftingScalesAugment.shards.iterator();
		while (iterator.hasNext()) {
			ShiftingScalesAugment.ShardParticle particle = iterator.next();
			if (particle.getY() > height) {
				iterator.remove();
			}
			int u = particle.getFrame() % 8 > 4 ? 5 : 0;
			int v = 9;
			graphics.blit(ShiftingScalesAugment.TEXTURE_HUD, (int) particle.getX() - 2, (int) particle.getY() - 2, u, v, 5, 5);
		}
		Random random = new Random();
		if (ShiftingScalesAugment.scales < ShiftingScalesAugment.scalesLast) {
			int x = getBarX(width);
			int y = getBarY(height);

			int segsLast = ShiftingScalesAugment.scalesLast / 3;
			int lastLast = ShiftingScalesAugment.scalesLast % 3;
			if (lastLast > 0) {
				segsLast++;
			}
			int segs = ShiftingScalesAugment.scales / 3;
			int last = ShiftingScalesAugment.scales % 3;
			if (last > 0) {
				segs++;
			}

			for (int i = 0; i < Math.max(segs, segsLast); i++) {
				int currentScale = i * 3 + last;
				if (currentScale < ShiftingScalesAugment.scales) {
					continue;
				}
				int xHeart = x + 8 * (i % 10) + 4;
				int yHeart = y - 10 * (i / 10) + 4;
				int pieces = 2;
				if (lastLast == 1 && i == Math.max(segs, segsLast) - 1) {
					pieces = 1;
				}
				for (int e = 0; e < pieces; e++) {
					ShiftingScalesAugment.shards.add(new ShiftingScalesAugment.ShardParticle(xHeart, yHeart, random.nextInt(8), (random.nextDouble() - 0.5) * 10, (random.nextDouble() - 0.5) * 10, 0.5));
				}
			}
		}
		ShiftingScalesAugment.scalesLast = ShiftingScalesAugment.scales;
	}

	private static void onClientTick(ClientTickEvent.Pre event) {
		Iterator<ShiftingScalesAugment.ShardParticle> iterator = ShiftingScalesAugment.shards.iterator();
		while (iterator.hasNext()) {
			iterator.next().update();
		}
	}

	private static int getBarY(int height) {
		return height - 39;
	}

	private static int getBarX(int width) {
		return width / 2 - 11 - 8 * 10;
	}
}
