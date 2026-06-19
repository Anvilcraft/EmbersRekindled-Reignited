package com.rekindled.embers.item;

import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class DawnstoneShovelItem extends DawnstoneDiggerItem {

	public DawnstoneShovelItem(Properties properties) {
		super(EmbersTiers.DAWNSTONE, BlockTags.MINEABLE_WITH_SHOVEL, 1.5F, -3.0F, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		if (!hasEmber(stack)) {
			return InteractionResult.PASS;
		}
		InteractionResult result = super.useOn(context);
		if (result.consumesAction()) {
			DawnstoneToolUtil.markUsed(stack);
		}
		return result;
	}
}
