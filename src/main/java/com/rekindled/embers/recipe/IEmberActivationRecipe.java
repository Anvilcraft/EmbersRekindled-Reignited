package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

public interface IEmberActivationRecipe extends Recipe<RecipeInput> {

	public int getOutput(RecipeInput context);

	public int process(RecipeInput context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.EMBER_ACTIVATOR_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.EMBER_ACTIVATION.get();
	}

	public Ingredient getDisplayInput();

	public int getDisplayOutput();

	@Override
	@Deprecated
	public default ItemStack getResultItem(HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default ItemStack assemble(RecipeInput context, HolderLookup.Provider registry) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
