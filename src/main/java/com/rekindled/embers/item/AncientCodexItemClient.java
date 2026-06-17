package com.rekindled.embers.item;

import com.rekindled.embers.gui.GuiCodex;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.research.ResearchManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.context.UseOnContext;

final class AncientCodexItemClient {

	private AncientCodexItemClient() {
	}

	static boolean openResearchPage(UseOnContext context) {
		if (!Screen.hasControlDown() || context.getPlayer() == null) {
			return false;
		}
		ResearchBase research = ResearchManager.researchByItem.get(context.getLevel().getBlockState(context.getClickedPos()).getBlock().asItem());
		if (research == null) {
			return false;
		}
		GuiCodex.instance.researchPage = research;
		ResearchManager.sendCheckmark(research, true);
		Minecraft.getInstance().setScreen(GuiCodex.instance);
		return true;
	}

	static void openCodex() {
		Minecraft.getInstance().setScreen(GuiCodex.instance);
	}
}
