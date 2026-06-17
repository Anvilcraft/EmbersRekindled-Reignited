package com.rekindled.embers.compat.create;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class EmberKineticGeneratorRenderer extends KineticBlockEntityRenderer<EmberKineticGeneratorBlockEntity> {
	public EmberKineticGeneratorRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected BlockState getRenderedBlockState(EmberKineticGeneratorBlockEntity blockEntity) {
		return shaft(getRotationAxisOf(blockEntity));
	}
}
