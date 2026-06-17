package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class AlchemyRecipeBuilder {

	public ResourceLocation id;
	public ItemStack output;
	public ItemStack failure = ItemStack.EMPTY;
	public Ingredient tablet;
	public ArrayList<Ingredient> aspects = new ArrayList<Ingredient>();
	public ArrayList<Ingredient> inputs = new ArrayList<Ingredient>();
	public boolean babbyGames = false;

	public static AlchemyRecipeBuilder create(ItemStack itemStack) {
		AlchemyRecipeBuilder builder = new AlchemyRecipeBuilder();
		builder.output = itemStack;
		builder.id = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
		return builder;
	}

	public static AlchemyRecipeBuilder create(ItemLike item) {
		return create(new ItemStack(item));
	}

	public AlchemyRecipeBuilder id(ResourceLocation id) {
		this.id = id;
		return this;
	}

	public AlchemyRecipeBuilder domain(String domain) {
		this.id = ResourceLocation.fromNamespaceAndPath(domain, this.id.getPath());
		return this;
	}

	public AlchemyRecipeBuilder folder(String folder) {
		this.id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), folder + "/" + id.getPath());
		return this;
	}

	public AlchemyRecipeBuilder tablet(Ingredient tablet) {
		this.tablet = tablet;
		return this;
	}

	public AlchemyRecipeBuilder tablet(ItemLike... tablet) {
		tablet(Ingredient.of(tablet));
		return this;
	}

	public AlchemyRecipeBuilder tablet(TagKey<Item> tag) {
		tablet(Ingredient.of(tag));
		return this;
	}

	public AlchemyRecipeBuilder output(ItemStack output) {
		this.output = output;
		return this;
	}

	public AlchemyRecipeBuilder output(Item item) {
		output(new ItemStack(item));
		return this;
	}

	public AlchemyRecipeBuilder failure(ItemStack failure) {
		this.failure = failure;
		return this;
	}

	public AlchemyRecipeBuilder failure(Item item) {
		failure(new ItemStack(item));
		return this;
	}

	public AlchemyRecipeBuilder aspects(ArrayList<Ingredient> aspects) {
		this.aspects = aspects;
		return this;
	}

	public AlchemyRecipeBuilder inputs(ArrayList<Ingredient> inputs) {
		this.inputs = inputs;
		return this;
	}

	public AlchemyRecipeBuilder aspects(Ingredient... aspects) {
		for (Ingredient aspect : aspects)
			this.aspects.add(aspect);
		return this;
	}

	public AlchemyRecipeBuilder inputs(Ingredient... inputs) {
		for (Ingredient input : inputs)
			this.inputs.add(input);
		return this;
	}

	public AlchemyRecipeBuilder aspects(ItemLike... aspects) {
		for (ItemLike aspect : aspects)
			this.aspects.add(Ingredient.of(aspect));
		return this;
	}

	public AlchemyRecipeBuilder inputs(ItemLike... inputs) {
		for (ItemLike input : inputs)
			this.inputs.add(Ingredient.of(input));
		return this;
	}

	@SafeVarargs
	public final AlchemyRecipeBuilder aspects(TagKey<Item>... aspects) {
		for (TagKey<Item> aspect : aspects)
			this.aspects.add(Ingredient.of(aspect));
		return this;
	}

	@SafeVarargs
	public final AlchemyRecipeBuilder inputs(TagKey<Item>... inputs) {
		for (TagKey<Item> input : inputs)
			this.inputs.add(Ingredient.of(input));
		return this;
	}

	public AlchemyRecipeBuilder setBabbyGames(boolean babbyGames) {
		this.babbyGames = babbyGames;
		return this;
	}

	public AlchemyRecipeBase build() {
		if (babbyGames)
			return new AlchemyRecipeForBabies(id, tablet, aspects, inputs, output, failure);
		return new AlchemyRecipe(id, tablet, aspects, inputs, output, failure);
	}

	public void save(Consumer<FinishedRecipe> consumer) {
		consumer.accept(new Finished(build()));
	}

	public static class Finished implements FinishedRecipe {

		public final AlchemyRecipeBase recipe;

		public Finished(AlchemyRecipeBase recipe) {
			this.recipe = recipe;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("output", RecipeSerialization.saveItem(recipe.output));

			if (!recipe.failure.isEmpty()) {
				json.add("failure", RecipeSerialization.saveItem(recipe.failure));
			}
			json.add("tablet", RecipeSerialization.saveIngredient(recipe.tablet));

			JsonArray aspectJson = new JsonArray();
			for (Ingredient aspect : recipe.aspects) {
				aspectJson.add(RecipeSerialization.saveIngredient(aspect));
			}
			json.add("aspects", aspectJson);

			JsonArray inputJson = new JsonArray();
			for (Ingredient input : recipe.inputs) {
				inputJson.add(RecipeSerialization.saveIngredient(input));
			}
			json.add("inputs", inputJson);
		}

		public ResourceLocation getId() {
			return recipe.getId();
		}

		@Override
		public RecipeSerializer<?> getType() {
			if (recipe instanceof AlchemyRecipeForBabies)
				return RegistryManager.ALCHEMY_FOR_BABIES_SERIALIZER.get();
			return RegistryManager.ALCHEMY_SERIALIZER.get();
		}

		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
