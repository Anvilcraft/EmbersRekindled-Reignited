package com.rekindled.embers.render;

import java.util.function.Function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

public class PipeModel implements IUnbakedGeometry<PipeModel> {

	BlockModel centerModel;
	BlockModel connectionModel;
	BlockModel connectionModel2;
	BlockModel endModel;
	BlockModel endModel2;

	public PipeModel(BlockModel centerModel, BlockModel connectionModel, BlockModel connectionModel2, BlockModel endModel, BlockModel endModel2) {
		this.centerModel = centerModel;
		this.connectionModel = connectionModel;
		this.connectionModel2 = connectionModel2;
		this.endModel = endModel;
		this.endModel2 = endModel2;
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		BakedModel[] connectionModels = getRotatedModels(context, baker, spriteGetter, connectionModel, connectionModel2);
		BakedModel[] endModels = getRotatedModels(context, baker, spriteGetter, endModel, endModel2);

		return new BakedPipeModel(centerModel.bake(baker, spriteGetter, modelState), connectionModels, endModels);
	}

	public static BakedModel[] getRotatedModels(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, BlockModel model, BlockModel model2) {
		BakedModel[] models = {
				model.bake(baker, spriteGetter, BlockModelRotation.X0_Y0),
				model2.bake(baker, spriteGetter, BlockModelRotation.X180_Y0),
				model.bake(baker, spriteGetter, BlockModelRotation.X90_Y180),
				model2.bake(baker, spriteGetter, BlockModelRotation.X90_Y0),
				model.bake(baker, spriteGetter, BlockModelRotation.X90_Y90),
				model2.bake(baker, spriteGetter, BlockModelRotation.X90_Y270)
		};
		return models;
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
		centerModel.resolveParents(modelGetter);
		connectionModel.resolveParents(modelGetter);
		connectionModel2.resolveParents(modelGetter);
		endModel.resolveParents(modelGetter);
		endModel2.resolveParents(modelGetter);
	}

	public static final class Loader implements IGeometryLoader<PipeModel> {

		public static final Loader INSTANCE = new Loader();

		@Override
		public PipeModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
			BlockModel centerModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "center"), BlockModel.class);
			BlockModel connectionModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "connection"), BlockModel.class);
			BlockModel connectionModel2 = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "connection2"), BlockModel.class);
			BlockModel endModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "end"), BlockModel.class);
			BlockModel endModel2 = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "end2"), BlockModel.class);

			return new PipeModel(centerModel, connectionModel, connectionModel2, endModel, endModel2);
		}
	}
}
