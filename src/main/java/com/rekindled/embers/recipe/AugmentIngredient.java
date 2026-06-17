package com.rekindled.embers.recipe;

import java.util.Arrays;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record AugmentIngredient(Ingredient base, IAugment augment, int level, boolean inverted) implements ICustomIngredient {

	private static final Codec<IAugment> AUGMENT_CODEC = ResourceLocation.CODEC.xmap(AugmentUtil::getAugment, IAugment::getName);
	public static final MapCodec<AugmentIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC.fieldOf("base").forGetter(AugmentIngredient::base),
			AUGMENT_CODEC.fieldOf("augment").forGetter(AugmentIngredient::augment),
			Codec.INT.optionalFieldOf("level", 1).forGetter(AugmentIngredient::level),
			Codec.BOOL.optionalFieldOf("inverted", false).forGetter(AugmentIngredient::inverted)
	).apply(instance, AugmentIngredient::new));
	public static final IngredientType<AugmentIngredient> TYPE = new IngredientType<>(CODEC);

	public static Ingredient of(Ingredient base, IAugment augment, int level, boolean inverted) {
		return new AugmentIngredient(base, augment, level, inverted).toVanilla();
	}

	public static Ingredient of(Ingredient base, IAugment augment, boolean inverted) {
		return of(base, augment, 1, inverted);
	}

	public static Ingredient of(Ingredient base, IAugment augment, int level) {
		return of(base, augment, level, false);
	}

	public static Ingredient of(Ingredient base, IAugment augment) {
		return of(base, augment, 1, false);
	}

	@Override
	public boolean test(ItemStack stack) {
		return base.test(stack) && AugmentUtil.hasHeat(stack) && (inverted ^ AugmentUtil.getAugmentLevel(stack, augment) >= level);
	}

	@Override
	public Stream<ItemStack> getItems() {
		return Arrays.stream(base.getItems()).map(stack -> {
			ItemStack display = stack.copy();
			if (inverted) {
				AugmentUtil.setHeat(display, 0);
			} else {
				AugmentUtil.setLevel(display, AugmentUtil.getLevel(display) + level);
				AugmentUtil.addAugment(display, ItemStack.EMPTY, augment);
				AugmentUtil.setAugmentLevel(display, augment, level);
			}
			return display;
		});
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public IngredientType<?> getType() {
		return TYPE;
	}
}
