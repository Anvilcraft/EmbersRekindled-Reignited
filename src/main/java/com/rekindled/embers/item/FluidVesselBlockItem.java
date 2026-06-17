package com.rekindled.embers.item;

import com.rekindled.embers.util.ItemData;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.block.FluidDialBlock;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.rekindled.embers.compat.legacy.capabilities.ForgeCapabilities;
import com.rekindled.embers.compat.legacy.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

public class FluidVesselBlockItem extends BlockItem {

	public FluidVesselBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return super.getMaxStackSize(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		IFluidHandler cap = com.rekindled.embers.util.CapabilityCompat.getCapability(stack, ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
		if (cap != null) {
			tooltip.add(FluidDialBlock.formatFluidStack(cap.getFluidInTank(0), cap.getTankCapacity(0)).withStyle(ChatFormatting.GRAY));
		}
	}
}
