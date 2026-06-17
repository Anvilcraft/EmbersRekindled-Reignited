package com.rekindled.embers.recipe;

import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class LegacyRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {

	private static final ResourceLocation DECODED_ID = ResourceLocation.fromNamespaceAndPath("embers", "decoded");

	public abstract T fromJson(ResourceLocation recipeId, JsonObject json);

	@Nullable
	public abstract T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer);

	public abstract void toNetwork(FriendlyByteBuf buffer, T recipe);

	@Override
	public MapCodec<T> codec() {
		Codec<T> codec = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
			JsonElement json = dynamic.convert(JsonOps.INSTANCE).getValue();
			return json.isJsonObject()
					? DataResult.success(fromJson(DECODED_ID, json.getAsJsonObject()))
					: DataResult.error(() -> "Expected a recipe object");
		}, recipe -> new Dynamic<>(JsonOps.INSTANCE, JsonNull.INSTANCE));
		return MapCodec.assumeMapUnsafe(codec);
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
		return StreamCodec.of(this::toNetwork, buffer -> fromNetwork(DECODED_ID, buffer));
	}
}
