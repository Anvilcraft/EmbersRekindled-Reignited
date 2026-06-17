package com.rekindled.embers.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class AlchemicalNoteItem extends Item {

	public AlchemicalNoteItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		ItemStack result = AlchemyHintItem.getResult(stack);
		if (!result.isEmpty())
			tooltip.add(Component.translatable(result.getDescriptionId()).withStyle(ChatFormatting.GRAY));
	}
}
