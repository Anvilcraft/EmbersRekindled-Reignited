package com.rekindled.embers.compat.jei;

import java.util.List;

import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.util.FluidAmounts;

import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.neoforge.fluids.FluidStack;

public class IngotTooltipCallback implements IRecipeSlotTooltipCallback {

	public static IngotTooltipCallback INSTANCE = new IngotTooltipCallback();

	@Override
	public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
		//first find the index of the line we want to insert after
		int index = -1;
		for (Component line : tooltip) {
			if (line.getContents() instanceof TranslatableContents translatable && translatable.getKey().equals("jei.tooltip.liquid.amount")) {
				index = tooltip.indexOf(line);
				break;
			}
		}

		FluidStack fluid = recipeSlotView.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
		if (index != -1 && fluid.getFluid().defaultFluidState().is(EmbersFluidTags.INGOT_TOOLTIP) && fluid.getAmount() >= FluidAmounts.nuggetValue())
			tooltip.add(index + 1, FluidAmounts.getIngotTooltip(fluid.getAmount()).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
	}
}
