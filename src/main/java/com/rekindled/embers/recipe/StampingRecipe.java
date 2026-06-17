package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public class StampingRecipe implements IStampingRecipe {

	public static final Serializer SERIALIZER = new Serializer();

	public final ResourceLocation id;

	public final Ingredient stamp;
	public final Ingredient input;
	public final FluidIngredient fluid;

	public final Either<ItemStack, TagAmount> output;

	public StampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, TagAmount output) {
		this(id, stamp, input, fluid, Either.right(output));
	}

	public StampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, ItemStack output) {
		this(id, stamp, input, fluid, Either.left(output));
	}

	public StampingRecipe(ResourceLocation id, Ingredient stamp, Ingredient input, FluidIngredient fluid, Either<ItemStack, TagAmount> output) {
		this.id = id;
		this.stamp = stamp;
		this.input = input;
		this.fluid = fluid;
		this.output = output;
	}

	@Override
	public boolean matches(StampingContext context, Level pLevel) {
		for (int i = 0; i < context.size(); i++) {
			if (input.test(context.getItem(i))) {
				if (stamp.test(context.stamp)) {
					if (fluid.test(context.fluids.getFluidInTank(0)))
						return true;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public ItemStack getOutput(RecipeWrapper context) {
		return getResultItem();
	}

	@Override
	public ItemStack assemble(StampingContext context, HolderLookup.Provider registry) {
		for (int i = 0; i < context.size(); i++) {
			if (input.test(context.getItem(i))) {
				context.getItem(i).shrink(1);
				break;
			}
		}
		for (FluidStack stack : fluid.getAllFluids()) {
			if (fluid.test(context.fluids.drain(stack, FluidAction.SIMULATE))) {
				context.fluids.drain(stack, FluidAction.EXECUTE);
				break;
			}
		}
		return this.getOutput(context);
	}

	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public ItemStack getResultItem() {
		if (output.left().isPresent())
			return output.left().get();
		return new ItemStack(Misc.getTaggedItem(output.right().get().tag), output.right().get().amount);
	}

	@Override
	public FluidIngredient getDisplayInputFluid() {
		return fluid;
	}

	@Override
	public Ingredient getDisplayInput() {
		return input;
	}

	@Override
	public Ingredient getDisplayStamp() {
		return stamp;
	}

	public static class TagAmount {
		public TagKey<Item> tag;
		public int amount;

		public TagAmount(TagKey<Item> tag, int amount) {
			this.tag = tag;
			this.amount = amount;
		}
	}

	public static class Serializer extends LegacyRecipeSerializer<StampingRecipe> {

		@Override
		public StampingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient stamp = RecipeSerialization.readIngredient(json.get("stamp"));
			Ingredient input = Ingredient.EMPTY;
			FluidIngredient fluid = FluidIngredient.EMPTY;
			if (json.has("input"))
				input = RecipeSerialization.readIngredient(json.get("input"));
			if (json.has("fluid"))
				fluid = FluidIngredient.deserialize(json, "fluid");
			JsonObject outputJson = GsonHelper.getAsJsonObject(json, "output");
			if (outputJson.has("tag")) {
				TagAmount output = new TagAmount(ItemTags.create(ResourceLocation.parse(GsonHelper.getAsString(outputJson, "tag"))), GsonHelper.getAsInt(outputJson, "count", 1));
				return new StampingRecipe(recipeId, stamp, input, fluid, output);
			} else {
				ItemStack output = RecipeSerialization.readItem(outputJson);
				return new StampingRecipe(recipeId, stamp, input, fluid, output);
			}
		}

		@Override
		public @Nullable StampingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient stamp = RecipeSerialization.readIngredient(buffer);
			Ingredient input = RecipeSerialization.readIngredient(buffer);
			FluidIngredient fluid = FluidIngredient.read(buffer);
			if (buffer.readBoolean()) {
				TagAmount output = new TagAmount(ItemTags.create(buffer.readResourceLocation()), buffer.readInt());
				return new StampingRecipe(recipeId, stamp, input, fluid, output);
			}
			ItemStack output = RecipeSerialization.readItem(buffer);

			return new StampingRecipe(recipeId, stamp, input, fluid, output);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, StampingRecipe recipe) {
			RecipeSerialization.writeIngredient(buffer, recipe.stamp);
			RecipeSerialization.writeIngredient(buffer, recipe.input);
			recipe.fluid.write(buffer);
			if (recipe.output.right().isPresent()) {
				buffer.writeBoolean(true);
				buffer.writeResourceLocation(recipe.output.right().get().tag.location());
				buffer.writeInt(recipe.output.right().get().amount);
			} else {
				buffer.writeBoolean(false);
				RecipeSerialization.writeItem(buffer, recipe.output.left().get());
			}
		}
	}
}
