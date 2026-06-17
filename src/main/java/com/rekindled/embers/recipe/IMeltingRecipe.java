package com.rekindled.embers.recipe;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public interface IMeltingRecipe extends Recipe<RecipeInput> {

	public FluidStack getOutput(RecipeInput context);

	public FluidStack getBonus();

	public FluidStack process(RecipeInput context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.MELTER_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.MELTING.get();
	}

	public FluidStack getDisplayOutput();

	public Ingredient getDisplayInput();

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
