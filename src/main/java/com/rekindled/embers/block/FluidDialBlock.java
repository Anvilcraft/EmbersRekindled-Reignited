package com.rekindled.embers.block;

import java.util.ArrayList;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.FluidDialBlockEntity;
import com.rekindled.embers.datagen.EmbersFluidTags;
import com.rekindled.embers.util.FluidAmounts;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidDialBlock extends DialBaseBlock {

	public static final String DIAL_TYPE = "fluid";

	public FluidDialBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	protected void getBEData(Direction facing, ArrayList<Component> text, BlockEntity blockEntity, int maxLines) {
		if (blockEntity instanceof FluidDialBlockEntity dial && dial.display) {
			int extraLines = 0;
			for (int i = 0; i < dial.fluids.length && (i + extraLines) < maxLines; i++) {
				FluidStack contents = dial.fluids[i];
				text.add(formatFluidStack(contents, dial.capacities[i]));
				if (contents.getFluid().defaultFluidState().is(EmbersFluidTags.INGOT_TOOLTIP) && contents.getAmount() >= FluidAmounts.nuggetValue()) {
					if ((i + extraLines + 1) < maxLines)
						text.add(FluidAmounts.getIngotTooltip(contents.getAmount()));
					extraLines++;
				}
			}
			if ((dial.fluids.length + dial.extraLines + extraLines) > Math.min(maxLines, dial.fluids.length + extraLines)) {
				text.add(Component.translatable(Embers.MODID + ".tooltip.too_many", dial.fluids.length + extraLines - Math.min(maxLines, dial.fluids.length + extraLines) + dial.extraLines));
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static MutableComponent formatFluidStack(FluidStack contents, int capacity) {
		if (!contents.isEmpty())
			return Component.translatable(Embers.MODID + ".tooltip.fluiddial.fluid", contents.getHoverName().getString(), contents.getAmount(), capacity);
		else
			return Component.translatable(Embers.MODID + ".tooltip.fluiddial.nofluid", capacity);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.FLUID_DIAL_ENTITY.get().create(pPos, pState);
	}

	@Override
	public String getDialType() {
		return DIAL_TYPE;
	}
}
