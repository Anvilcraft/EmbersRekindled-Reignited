package com.rekindled.embers.recipe;

import java.util.Arrays;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.api.augment.AugmentUtil;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record HeatIngredient(Ingredient base, boolean inverted) implements ICustomIngredient {

	public static final MapCodec<HeatIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Ingredient.CODEC.fieldOf("base").forGetter(HeatIngredient::base),
			Codec.BOOL.optionalFieldOf("inverted", false).forGetter(HeatIngredient::inverted)
	).apply(instance, HeatIngredient::new));
	public static final IngredientType<HeatIngredient> TYPE = new IngredientType<>(CODEC);

	public static Ingredient of(Ingredient base, boolean inverted) {
		return new HeatIngredient(base, inverted).toVanilla();
	}

	public static Ingredient of(Ingredient base) {
		return of(base, false);
	}

	@Override
	public boolean test(ItemStack stack) {
		return base.test(stack) && (inverted ^ AugmentUtil.hasHeat(stack));
	}

	@Override
	public Stream<ItemStack> getItems() {
		return Arrays.stream(base.getItems()).map(stack -> {
			ItemStack display = stack.copy();
			if (!inverted)
				AugmentUtil.setHeat(display, 0);
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
