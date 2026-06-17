package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class EmberActivationRecipe implements IEmberActivationRecipe {

	public static final Serializer SERIALIZER = new Serializer(); 

	public final ResourceLocation id;

	public final Ingredient ingredient;
	public final int ember;

	public EmberActivationRecipe(ResourceLocation id, Ingredient ingredient, int ember) {
		this.id = id;
		this.ingredient = ingredient;
		this.ember = ember;
	}

	@Override
	public boolean matches(RecipeInput context, Level pLevel) {
		for (int i = 0; i < context.size(); i++) {
			if (ingredient.test(context.getItem(i)))
				return true;
		}
		return false;
	}

	@Override
	public int getOutput(RecipeInput context) {
		return ember;
	}

	@Override
	public int process(RecipeInput context) {
		for (int i = 0; i < context.size(); i++) {
			if (ingredient.test(context.getItem(i))) {
				context.getItem(i).shrink(1);
				break;
			}
		}
		return ember;
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
	public int getDisplayOutput() {
		return ember;
	}

	public static class Serializer extends LegacyRecipeSerializer<EmberActivationRecipe> {

		@Override
		public EmberActivationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient ingredient = RecipeSerialization.readIngredient(json.get("input"));
			int ember = GsonHelper.getAsInt(json, "ember");

			return new EmberActivationRecipe(recipeId, ingredient, ember);
		}

		@Override
		public @Nullable EmberActivationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient ingredient = RecipeSerialization.readIngredient(buffer);
			int ember = buffer.readVarInt();

			return new EmberActivationRecipe(recipeId, ingredient, ember);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, EmberActivationRecipe recipe) {
			RecipeSerialization.writeIngredient(buffer, recipe.ingredient);
			buffer.writeVarInt(recipe.ember);
		}
	}
}
