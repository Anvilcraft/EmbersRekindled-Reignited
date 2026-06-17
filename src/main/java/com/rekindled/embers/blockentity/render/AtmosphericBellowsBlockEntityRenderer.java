package com.rekindled.embers.blockentity.render;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rekindled.embers.Embers;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.blockentity.AtmosphericBellowsBlockEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;

public class AtmosphericBellowsBlockEntityRenderer implements BlockEntityRenderer<AtmosphericBellowsBlockEntity> {

	public static float length = 120;
	public static float blowLength = length / 3;
	public static float suckLength = length - blowLength;

	private static final ModelResourceLocation TOP_MODEL = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(Embers.MODID, "block/atmospheric_bellows_top"));
	private static final ModelResourceLocation LEATHER_MODEL = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(Embers.MODID, "block/atmospheric_bellows_leather"));

	public static Random random = new Random();
	private final BlockRenderDispatcher blockRenderer;

	public AtmosphericBellowsBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
		this.blockRenderer = pContext.getBlockRenderDispatcher();
	}

	@Override
	public void render(AtmosphericBellowsBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		BlockState blockState = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
		BakedModel leatherModel = Minecraft.getInstance().getModelManager().getModel(LEATHER_MODEL);
		BakedModel topModel = Minecraft.getInstance().getModelManager().getModel(TOP_MODEL);
		random.setSeed(blockEntity.getBlockPos().asLong());
		float ticks = (EmbersClientEvents.ticks + partialTick + random.nextFloat(length)) % length;
		double magnitude = 1.0D;

		if (ticks < blowLength) {
			magnitude = ticks / blowLength;
		} else {
			magnitude = 1.0D - (ticks - blowLength) / suckLength;
		}

		poseStack.pushPose();
		rotateToFacing(blockState, poseStack);
		poseStack.translate(0, magnitude * -0.1875D, 0);
		if (leatherModel != null)
			blockRenderer.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, leatherModel, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

		poseStack.translate(0, magnitude * -0.1875D, 0);
		if (topModel != null)
			blockRenderer.getModelRenderer().renderModel(poseStack.last(), bufferSource.getBuffer(Sheets.solidBlockSheet()), blockState, topModel, 0.0f, 0.0f, 0.0f, packedLight, packedOverlay, ModelData.EMPTY, Sheets.solidBlockSheet());

		poseStack.popPose();
	}

	private static void rotateToFacing(BlockState blockState, PoseStack poseStack) {
		if (!blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			return;
		}

		Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		float angle = switch (facing) {
			case EAST -> 90.0F;
			case SOUTH -> 180.0F;
			case WEST -> 270.0F;
			default -> 0.0F;
		};

		poseStack.translate(0.5D, 0.5D, 0.5D);
		poseStack.mulPose(Axis.YP.rotationDegrees(angle));
		poseStack.translate(-0.5D, -0.5D, -0.5D);
	}
}
