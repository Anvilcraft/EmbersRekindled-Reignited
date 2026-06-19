package com.rekindled.embers.item;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.rekindled.embers.api.item.IEmberChargedTool;
import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DawnstoneHoeItem extends HoeItem implements IEmberChargedTool {

	public DawnstoneHoeItem(Properties properties) {
		super(EmbersTiers.DAWNSTONE, properties.attributes(HoeItem.createAttributes(EmbersTiers.DAWNSTONE, (int) -EmbersTiers.DAWNSTONE.getAttackDamageBonus(),
				Math.min(0.0F, EmbersTiers.DAWNSTONE.getAttackDamageBonus() - 3.0F))));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		if (!hasEmber(stack)) {
			return InteractionResult.PASS;
		}
		InteractionResult result = super.useOn(context);
		if (result.consumesAction()) {
			DawnstoneToolUtil.markUsed(stack);
		}
		return result;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		return !hasEmber(stack);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		DawnstoneToolUtil.markUsed(stack);
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
		return DawnstoneToolUtil.damageItem(stack, amount, entity, onBroken);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return DawnstoneToolUtil.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return DawnstoneToolUtil.shouldCauseBlockBreakReset(oldStack, newStack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		DawnstoneToolUtil.inventoryTick(stack, world, entity, selected);
	}

	@Override
	public boolean hasEmber(ItemStack stack) {
		return DawnstoneToolUtil.hasEmber(stack);
	}
}
