package com.rekindled.embers.compat.create;

import com.rekindled.embers.item.TinkerHammerItem;
import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

public class TinkerWrenchItem extends TinkerHammerItem {
	public TinkerWrenchItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null || !player.mayBuild()) {
			return super.useOn(context);
		}
		Level level = context.getLevel();
		BlockState state = level.getBlockState(context.getClickedPos());
		Block block = state.getBlock();
		if (block instanceof IWrenchable wrenchable) {
			return player.isShiftKeyDown()
					? wrenchable.onSneakWrenched(state, context)
					: wrenchable.onWrenched(state, context);
		}
		return super.useOn(context);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, context, tooltip, isAdvanced);
		tooltip.add(Component.translatable("tooltip.embers.tinker_wrench").withStyle(ChatFormatting.GRAY));
	}
}
