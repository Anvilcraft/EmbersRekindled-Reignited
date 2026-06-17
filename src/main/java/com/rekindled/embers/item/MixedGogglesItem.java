package com.rekindled.embers.item;

import java.util.function.Supplier;

import com.rekindled.embers.util.AshenArmorMaterial;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

public class MixedGogglesItem extends AshenArmorGemItem {

	public MixedGogglesItem(Item.Properties properties, Supplier<Integer> gemSlots) {
		super(AshenArmorMaterial.INSTANCE, ArmorItem.Type.HELMET, properties, gemSlots);
	}
}
