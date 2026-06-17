package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

public class ClockworkHammerItem extends ClockworkToolItem {

	public ClockworkHammerItem(Properties properties) {
		super(3, -3.2f, EmbersTiers.CLOCKWORK_HAMMER, EmbersBlockTags.MINABLE_WITH_HAMMER, properties);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
		return hasEmber(stack) && (toolAction == ItemAbilities.PICKAXE_DIG || toolAction == ItemAbilities.SHOVEL_DIG || toolAction == ItemAbilities.AXE_DIG);
	}

}
