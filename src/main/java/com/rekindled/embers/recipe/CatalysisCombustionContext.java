package com.rekindled.embers.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class CatalysisCombustionContext extends RecipeWrapper {

	public ItemStack machine;

	public CatalysisCombustionContext(IItemHandlerModifiable inv, ItemStack machine) {
		super(inv);
		this.machine = machine;
	}
}
