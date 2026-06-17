package com.rekindled.embers.item;

import com.rekindled.embers.util.ItemData;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.Embers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class AlchemyHintItem extends Item {

	public AlchemyHintItem(Properties pProperties) {
		super(pProperties);
	}

	public static int getBlackPins(ItemStack stack) {
		if (ItemData.hasTag(stack)) {
			return ItemData.getTag(stack).getInt("blackPins");
		}
		return 0;
	}

	public static int getWhitePins(ItemStack stack) {
		if (ItemData.hasTag(stack)) {
			return ItemData.getTag(stack).getInt("whitePins");
		}
		return 0;
	}

	public static ArrayList<ItemStack> getAspects(ItemStack stack) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if (ItemData.hasTag(stack) && ItemData.getTag(stack).contains("aspects")) {
			ListTag list = ItemData.getTag(stack).getList("aspects", Tag.TAG_COMPOUND);
			for (Tag nbt : list) {
				items.add(ItemData.parse((CompoundTag) nbt));
			}
		}
		return items;
	}

	public static ArrayList<ItemStack> getInputs(ItemStack stack) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		if (ItemData.hasTag(stack) && ItemData.getTag(stack).contains("inputs")) {
			ListTag list = ItemData.getTag(stack).getList("inputs", Tag.TAG_COMPOUND);
			for (Tag nbt : list) {
				items.add(ItemData.parse((CompoundTag) nbt));
			}
		}
		return items;
	}

	public static ItemStack getResult(ItemStack stack) {
		if (ItemData.hasTag(stack) && ItemData.getTag(stack).contains("result")) {
			return ItemData.parse(ItemData.getTag(stack).getCompound("result"));
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag isAdvanced) {
		int blackPins = getBlackPins(stack);
		int whitePins = getWhitePins(stack);
		if (blackPins != 0)
			tooltip.add(Component.translatable(blackPins == 1 ? Embers.MODID + ".alchemy_hint.black.one" : Embers.MODID + ".alchemy_hint.black", blackPins).withStyle(ChatFormatting.GRAY));
		if (whitePins != 0)
			tooltip.add(Component.translatable(whitePins == 1 ? Embers.MODID + ".alchemy_hint.white.one" : Embers.MODID + ".alchemy_hint.white", whitePins).withStyle(ChatFormatting.GRAY));
		if (blackPins == 0 && whitePins == 0)
			tooltip.add(Component.translatable(Embers.MODID + ".alchemy_hint.none").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide)
			return InteractionResultHolder.consume(stack);
		int blackPins = getBlackPins(stack);
		int whitePins = getWhitePins(stack);
		String black = blackPins == 1 ? I18n.get(Embers.MODID + ".alchemy_hint.black.one") : I18n.get(Embers.MODID + ".alchemy_hint.black", blackPins);
		String white = whitePins == 1 ? I18n.get(Embers.MODID + ".alchemy_hint.white.one") : I18n.get(Embers.MODID + ".alchemy_hint.white", whitePins);
		if (blackPins != 0) {
			if (whitePins != 0) {
				player.displayClientMessage(Component.translatable(Embers.MODID + ".alchemy_hint", I18n.get(Embers.MODID + ".alchemy_hint.and", black, white)), false);
				return InteractionResultHolder.consume(stack);
			}
			player.displayClientMessage(Component.translatable(Embers.MODID + ".alchemy_hint", black), false);
			return InteractionResultHolder.consume(stack);
		}
		if (whitePins != 0) {
			player.displayClientMessage(Component.translatable(Embers.MODID + ".alchemy_hint", white), false);
			return InteractionResultHolder.consume(stack);
		}
		player.displayClientMessage(Component.translatable(Embers.MODID + ".alchemy_hint", I18n.get(Embers.MODID + ".alchemy_hint.none")), false);
		return InteractionResultHolder.consume(stack);
	}
}
