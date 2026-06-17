package com.rekindled.embers.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;

public class SneakyBufferSourceWrapper implements MultiBufferSource {

	public final MultiBufferSource buffer;

	public SneakyBufferSourceWrapper(MultiBufferSource buffer) {
		this.buffer = buffer;
	}

	@Override
	public VertexConsumer getBuffer(RenderType renderType) {
		return buffer.getBuffer(renderType);
	}
}
