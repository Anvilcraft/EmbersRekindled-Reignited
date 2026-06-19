package com.rekindled.embers.item;

import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.tags.BlockTags;

public class DawnstonePickaxeItem extends DawnstoneDiggerItem {

	public DawnstonePickaxeItem(Properties properties) {
		super(EmbersTiers.DAWNSTONE, BlockTags.MINEABLE_WITH_PICKAXE, 1.0F, -2.8F, properties);
	}
}
