package com.rekindled.embers.util;

import java.util.List;
import java.util.Map;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public final class AshenArmorMaterial {

	public static final Holder<ArmorMaterial> INSTANCE = Holder.direct(new ArmorMaterial(
			Map.of(
					ArmorItem.Type.HELMET, 3,
					ArmorItem.Type.CHESTPLATE, 7,
					ArmorItem.Type.LEGGINGS, 5,
					ArmorItem.Type.BOOTS, 3,
					ArmorItem.Type.BODY, 0),
			18,
			SoundEvents.ARMOR_EQUIP_GENERIC,
			() -> Ingredient.of(RegistryManager.ASHEN_FABRIC.get()),
			List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Embers.MODID, "ashen_cloak"))),
			1.0F,
			0.0F));

	private AshenArmorMaterial() {
	}
}
