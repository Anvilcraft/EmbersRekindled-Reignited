package com.rekindled.embers.item;

import java.util.Optional;

import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;

public class DawnstoneAxeItem extends DawnstoneDiggerItem {

	public DawnstoneAxeItem(Properties properties) {
		super(EmbersTiers.DAWNSTONE, BlockTags.MINEABLE_WITH_AXE, 6.0F, -3.0F, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		if (!hasEmber(stack)) {
			return InteractionResult.PASS;
		}
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		BlockState state = level.getBlockState(pos);
		Optional<BlockState> stripped = Optional.ofNullable(state.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false));
		Optional<BlockState> scraped = stripped.isPresent() ? Optional.empty() : Optional.ofNullable(state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false));
		Optional<BlockState> waxOff = stripped.isPresent() || scraped.isPresent() ? Optional.empty() : Optional.ofNullable(state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false));
		Optional<BlockState> result = Optional.empty();
		if (stripped.isPresent()) {
			level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			result = stripped;
		} else if (scraped.isPresent()) {
			level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.levelEvent(player, 3005, pos, 0);
			result = scraped;
		} else if (waxOff.isPresent()) {
			level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.levelEvent(player, 3004, pos, 0);
			result = waxOff;
		}
		if (result.isEmpty()) {
			return InteractionResult.PASS;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
		}
		level.setBlock(pos, result.get(), 11);
		level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, result.get()));
		DawnstoneToolUtil.markUsed(stack);
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
