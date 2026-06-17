package com.rekindled.embers.item;

import com.rekindled.embers.render.AlchemicalNoteItemRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class AlchemicalNoteItemClientExtensions implements IClientItemExtensions {

	@Override
	public BlockEntityWithoutLevelRenderer getCustomRenderer() {
		Minecraft minecraft = Minecraft.getInstance();
		return new AlchemicalNoteItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
	}
}
