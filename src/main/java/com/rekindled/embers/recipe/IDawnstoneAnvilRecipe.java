package com.rekindled.embers.recipe;

import java.util.List;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface IDawnstoneAnvilRecipe extends Recipe<RecipeInput> {

	public List<ItemStack> getOutput(RecipeInput context);

	@Override
	public default ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.DAWNSTONE_ANVIL_ITEM.get());
	}

	@Override
	public default RecipeType<?> getType() {
		return RegistryManager.DAWNSTONE_ANVIL_RECIPE.get();
	}

	public List<ItemStack> getDisplayInputBottom();

	public List<ItemStack> getDisplayInputTop();

	public List<ItemStack> getDisplayOutput();


	@Override
	@Deprecated
	public default ItemStack assemble(RecipeInput context, HolderLookup.Provider pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default ItemStack getResultItem(HolderLookup.Provider pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	@Deprecated
	public default boolean canCraftInDimensions(int width, int height) {
		return true;
	}
}
