package com.rekindled.embers.util;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.compat.curios.CuriosCompat;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.fml.ModList;

/**
 * A LootItemCondition that checks worn curios against an {@link ItemPredicate}.
 */
public class MatchCurioLootCondition implements LootItemCondition {

	public static final MapCodec<MatchCurioLootCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(condition -> condition.predicate))
			.apply(instance, MatchCurioLootCondition::new));

	final Optional<ItemPredicate> predicate;

	public MatchCurioLootCondition(ItemPredicate pToolPredicate) {
		this(Optional.of(pToolPredicate));
	}

	public MatchCurioLootCondition(Optional<ItemPredicate> predicate) {
		this.predicate = predicate;
	}

	public LootItemConditionType getType() {
		return RegistryManager.MATCH_CURIO_CONDITION.get();
	}

	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.ATTACKING_ENTITY, LootContextParams.THIS_ENTITY);
	}

	public boolean test(LootContext context) {
		if (!ModList.get().isLoaded("curios"))
			return false;

		Entity user = null;
		if (context.hasParam(LootContextParams.ATTACKING_ENTITY))
			user = context.getParam(LootContextParams.ATTACKING_ENTITY);
		else if (context.hasParam(LootContextParams.THIS_ENTITY))
			user = context.getParam(LootContextParams.THIS_ENTITY);

		if (user instanceof LivingEntity)
			return CuriosCompat.checkForCurios((LivingEntity) user, stack -> this.predicate.isEmpty() || this.predicate.get().test(stack));
		return false;
	}

	public static LootItemCondition.Builder curioMatches(ItemPredicate.Builder pToolPredicateBuilder) {
		return () -> new MatchCurioLootCondition(pToolPredicateBuilder.build());
	}
}
