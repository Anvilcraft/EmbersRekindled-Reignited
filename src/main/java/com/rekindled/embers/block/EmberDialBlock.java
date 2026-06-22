package com.rekindled.embers.block;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.EmberDialBlockEntity;
import com.rekindled.embers.util.DecimalFormats;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class EmberDialBlock extends DialBaseBlock {

	public static final String DIAL_TYPE = "ember";

	public EmberDialBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	protected void getBEData(Direction facing, ArrayList<Component> text, BlockEntity blockEntity, int maxLines) {
		if (blockEntity instanceof EmberDialBlockEntity dial && dial.display) {
			text.add(formatEmber(dial.ember, dial.capacity));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static MutableComponent formatEmber(double ember, double emberCapacity) {
		DecimalFormat emberFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember");
		return Component.translatable(Embers.MODID + ".tooltip.emberdial.ember", emberFormat.format(ember), emberFormat.format(emberCapacity));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.EMBER_DIAL_ENTITY.get().create(pPos, pState);
	}

	@Override
	public String getDialType() {
		return DIAL_TYPE;
	}
}
