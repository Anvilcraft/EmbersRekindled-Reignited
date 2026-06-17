package com.rekindled.embers.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public final class RecipeSerialization {

	private RecipeSerialization() {
	}

	public static Ingredient readIngredient(JsonElement json) {
		return Ingredient.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
	}

	public static JsonElement saveIngredient(Ingredient ingredient) {
		return Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow();
	}

	public static Ingredient readIngredient(FriendlyByteBuf buffer) {
		return Ingredient.CONTENTS_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buffer);
	}

	public static void writeIngredient(FriendlyByteBuf buffer, Ingredient ingredient) {
		Ingredient.CONTENTS_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buffer, ingredient);
	}

	public static JsonElement saveItem(ItemStack stack) {
		JsonElement json = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, stack).getOrThrow();
		if (!json.isJsonObject()) {
			return json;
		}

		JsonObject object = json.getAsJsonObject().deepCopy();
		if (object.has("id") && !object.has("item")) {
			object.add("item", object.remove("id"));
		}
		return object;
	}

	public static ItemStack readItem(JsonElement json) {
		return ItemStack.CODEC.parse(JsonOps.INSTANCE, normalizeRecipeItem(json)).getOrThrow();
	}

	public static ItemStack readItem(FriendlyByteBuf buffer) {
		return ItemStack.STREAM_CODEC.decode((RegistryFriendlyByteBuf) buffer);
	}

	public static void writeItem(FriendlyByteBuf buffer, ItemStack stack) {
		ItemStack.STREAM_CODEC.encode((RegistryFriendlyByteBuf) buffer, stack);
	}

	private static JsonElement normalizeRecipeItem(JsonElement json) {
		if (!json.isJsonObject()) {
			return json;
		}

		JsonObject object = json.getAsJsonObject();
		if (!object.has("item") || object.has("id")) {
			return json;
		}

		JsonObject normalized = object.deepCopy();
		normalized.add("id", normalized.remove("item"));
		return normalized;
	}
}
