package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.misc.AlchemyResult;
import com.rekindled.embers.item.DynamicCrystalSeedBlockItem;
import com.rekindled.embers.util.DynamicMetalSeeds;
import com.rekindled.embers.util.ItemData;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class DynamicMetalSeedAlchemyRecipe extends AlchemyRecipeBase implements IVisuallySplitRecipe<IAlchemyRecipe> {

	public static final Serializer SERIALIZER = new Serializer();

	public DynamicMetalSeedAlchemyRecipe(ResourceLocation id, Ingredient tablet, ArrayList<Ingredient> aspects, ItemStack failure) {
		super(id, tablet, aspects, repeatedIngredient(Ingredient.of(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots")))), DynamicCrystalSeedBlockItem.withMetal(DynamicMetalSeeds.DEFAULT_METAL, DynamicMetalSeeds.DEFAULT_COLOR), failure);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean matches(AlchemyContext context, Level level) {
		return tablet.test(context.tablet) && findVariant(context).isPresent();
	}

	@Override
	public boolean matchesCorrect(AlchemyContext context, Level level) {
		return matches(context, level) && getCorrectAspectCount(context) == context.contents.size();
	}

	@Override
	public ItemStack assemble(AlchemyContext context, HolderLookup.Provider registry) {
		Optional<DynamicMetalSeeds.Variant> variant = findVariant(context);
		if (variant.isEmpty()) {
			return failure.copy();
		}

		getCode(context.seed);
		int blackPins = getCorrectAspectCount(context);
		int whitePins = getPartialAspectCount(context) - blackPins;

		if (blackPins < code.size()) {
			ItemStack waste = failure.copy();
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("blackPins", blackPins);
			nbt.putInt("whitePins", whitePins);

			ListTag aspectNBT = new ListTag();
			ListTag inputNBT = new ListTag();
			for (PedestalContents contents : context.contents) {
				aspectNBT.add(ItemData.save(contents.aspect));
				inputNBT.add(ItemData.save(contents.input));
			}
			nbt.put("aspects", aspectNBT);
			nbt.put("inputs", inputNBT);

			ItemData.setTag(waste, nbt);
			return waste;
		}

		return DynamicCrystalSeedBlockItem.withMetal(variant.get().metal(), variant.get().color());
	}

	@Override
	public AlchemyResult getResult(AlchemyContext context) {
		Optional<DynamicMetalSeeds.Variant> variant = findVariant(context);
		ItemStack result = variant.map(value -> DynamicCrystalSeedBlockItem.withMetal(value.metal(), value.color())).orElseGet(() -> output);
		getCode(context.seed);
		int blackPins = getCorrectAspectCount(context);
		int whitePins = getPartialAspectCount(context) - blackPins;
		return new AlchemyResult(new ArrayList<PedestalContents>(context.contents), result, blackPins, whitePins);
	}

	@Override
	public List<IAlchemyRecipe> getVisualRecipes() {
		List<IAlchemyRecipe> visualRecipes = new ArrayList<>();
		for (DynamicMetalSeeds.Variant variant : DynamicMetalSeeds.getVariants()) {
			visualRecipes.add(new AlchemyRecipe(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "/" + variant.metal()), tablet, new ArrayList<>(aspects), repeatedIngredient(Ingredient.of(ingotTag(variant.metal()))), DynamicCrystalSeedBlockItem.withMetal(variant.metal(), variant.color()), failure));
		}
		return visualRecipes;
	}

	private Optional<DynamicMetalSeeds.Variant> findVariant(AlchemyContext context) {
		if (context.contents.size() != 3) {
			return Optional.empty();
		}
		for (DynamicMetalSeeds.Variant variant : DynamicMetalSeeds.getVariants()) {
			TagKey<Item> tag = ingotTag(variant.metal());
			if (context.contents.stream().allMatch(contents -> contents.input.is(tag))) {
				return Optional.of(variant);
			}
		}
		return Optional.empty();
	}

	private int getPartialAspectCount(AlchemyContext context) {
		getCode(context.seed);
		ArrayList<Ingredient> remainingCode = new ArrayList<Ingredient>(code);
		int whitePins = 0;
		for (PedestalContents contents : context.contents) {
			for (int i = 0; i < remainingCode.size(); i++) {
				if (remainingCode.get(i).test(contents.aspect)) {
					whitePins++;
					remainingCode.remove(i);
					break;
				}
			}
		}
		return whitePins;
	}

	private int getCorrectAspectCount(AlchemyContext context) {
		getCode(context.seed);
		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		int blackPins = 0;
		for (Ingredient aspect : code) {
			for (int i = 0; i < remaining.size(); i++) {
				if (aspect.test(remaining.get(i).aspect)) {
					blackPins++;
					remaining.remove(i);
					break;
				}
			}
		}
		return blackPins;
	}

	private static TagKey<Item> ingotTag(String metal) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/" + DynamicMetalSeeds.normalizeMetal(metal)));
	}

	private static ArrayList<Ingredient> repeatedIngredient(Ingredient ingredient) {
		ArrayList<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(ingredient);
		ingredients.add(ingredient);
		ingredients.add(ingredient);
		return ingredients;
	}

	public static class Serializer extends LegacyRecipeSerializer<DynamicMetalSeedAlchemyRecipe> {

		@Override
		public DynamicMetalSeedAlchemyRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient tablet = RecipeSerialization.readIngredient(json.get("tablet"));

			ArrayList<Ingredient> aspects = new ArrayList<>();
			JsonArray aspectJson = GsonHelper.getAsJsonArray(json, "aspects", null);
			if (aspectJson != null) {
				for (JsonElement element : aspectJson) {
					aspects.add(RecipeSerialization.readIngredient(element));
				}
			}

			ItemStack failure = json.has("failure") ? RecipeSerialization.readItem(GsonHelper.getAsJsonObject(json, "failure")) : new ItemStack(RegistryManager.ALCHEMICAL_WASTE.get());
			return new DynamicMetalSeedAlchemyRecipe(recipeId, tablet, aspects, failure);
		}

		@Override
		public @Nullable DynamicMetalSeedAlchemyRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient tablet = RecipeSerialization.readIngredient(buffer);
			ArrayList<Ingredient> aspects = buffer.readCollection((i) -> new ArrayList<>(), (buf) -> RecipeSerialization.readIngredient(buf));
			ItemStack failure = RecipeSerialization.readItem(buffer);
			return new DynamicMetalSeedAlchemyRecipe(recipeId, tablet, aspects, failure);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, DynamicMetalSeedAlchemyRecipe recipe) {
			RecipeSerialization.writeIngredient(buffer, recipe.tablet);
			buffer.writeCollection(recipe.aspects, (buf, input) -> RecipeSerialization.writeIngredient(buf, input));
			RecipeSerialization.writeItem(buffer, recipe.failure);
		}
	}
}
