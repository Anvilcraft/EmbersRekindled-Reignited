package com.rekindled.embers.compat.create;

import java.util.List;

import com.rekindled.embers.Embers;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class CreatePoweredUpgradeItem extends BlockItem {
	private final CreatePoweredUpgradeType upgradeType;

	public CreatePoweredUpgradeItem(CreatePoweredEmberUpgradeBlock block, Properties properties) {
		super(block, properties);
		this.upgradeType = block.getUpgradeType();
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		ResourceLocation upgradeLoc = upgradeType.baseUpgradeId();
		tooltip.add(Component.translatable(Embers.MODID + ".tooltip.upgrade").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable("tooltip.embers.create_powered_upgrade.desc." + upgradeLoc.toLanguageKey()).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(Embers.MODID + ".tooltip.upgrade.compatible").withStyle(ChatFormatting.GRAY));
		for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.create(ResourceLocation.fromNamespaceAndPath(Embers.MODID, "upgradeable_with/" + upgradeLoc.getNamespace() + "/" + upgradeLoc.getPath())))) {
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.upgrade.compatible.list", Component.translatable(holder.value().getDescriptionId())).withStyle(ChatFormatting.GRAY));
		}
	}
}
