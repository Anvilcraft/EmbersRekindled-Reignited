package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CatalysisCombustionRecipe implements ICatalysisCombustionRecipe {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final Ingredient ingredient;
	public final Ingredient machine;
	public final int burnTime;
	public final double multiplier;

	public CatalysisCombustionRecipe(ResourceLocation id, Ingredient ingredient, Ingredient machine, int burnTime, double multiplier) {
		this.id = id;
		this.ingredient = ingredient;
		this.machine = machine;
		this.burnTime = burnTime;
		this.multiplier = multiplier;
	}

	@Override
	public boolean matches(CatalysisCombustionContext context, Level pLevel) {
		if (machine.test(context.machine)) {
			for (int i = 0; i < context.size(); i++) {
				if (ingredient.test(context.getItem(i)))
					return true;
			}
		}
		return false;
	}

	@Override
	public int getBurnTIme(CatalysisCombustionContext context) {
		return burnTime;
	}

	@Override
	public double getmultiplier(CatalysisCombustionContext context) {
		return multiplier;
	}

	@Override
	public int process(CatalysisCombustionContext context) {
		for (int i = 0; i < context.size(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.getItem(i).shrink(1);
				break;
			}
		}
		return burnTime;
	}

	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public Ingredient getDisplayInput() {
		return ingredient;
	}

	@Override
	public Ingredient getDisplayMachine() {
		return machine;
	}

	@Override
	public int getDisplayTime() {
		return burnTime;
	}

	@Override
	public double getDisplayMultiplier() {
		return multiplier;
	}

	public static class Serializer extends LegacyRecipeSerializer<CatalysisCombustionRecipe> {

		@Override
		public CatalysisCombustionRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = RecipeSerialization.readIngredient(json.get("input"));
			Ingredient machine = RecipeSerialization.readIngredient(json.get("machine"));
			int burnTime = GsonHelper.getAsInt(json, "burn_time");
			double multiplier = GsonHelper.getAsDouble(json, "multiplier");

			return new CatalysisCombustionRecipe(recipeId, ingredient, machine, burnTime, multiplier);
		}

		@Override
		public @Nullable CatalysisCombustionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient ingredient = RecipeSerialization.readIngredient(buffer);
			Ingredient machine = RecipeSerialization.readIngredient(buffer);
			int burnTime = buffer.readVarInt();
			double multiplier = buffer.readDouble();

			return new CatalysisCombustionRecipe(recipeId, ingredient, machine, burnTime, multiplier);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, CatalysisCombustionRecipe recipe) {
			RecipeSerialization.writeIngredient(buffer, recipe.ingredient);
			RecipeSerialization.writeIngredient(buffer, recipe.machine);
			buffer.writeVarInt(recipe.burnTime);
			buffer.writeDouble(recipe.multiplier);
		}
	}
}
