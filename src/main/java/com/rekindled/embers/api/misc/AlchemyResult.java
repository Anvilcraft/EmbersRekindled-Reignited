package com.rekindled.embers.api.misc;

import com.rekindled.embers.util.ItemData;

import java.util.List;

import com.rekindled.embers.recipe.IAlchemyRecipe.PedestalContents;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class AlchemyResult {

	public List<PedestalContents> contents;
	public ItemStack result;
	public int blackPins;
	public int whitePins;

	public AlchemyResult(List<PedestalContents> contents, ItemStack result, int blackPins, int whitePins) {
		this.contents = contents;
		this.result = result;
		this.blackPins = blackPins;
		this.whitePins = whitePins;
	}

	public ItemStack createResultStack(ItemStack stack) {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt("blackPins", blackPins);
		nbt.putInt("whitePins", whitePins);

		nbt.put("result", ItemData.save(result));

		ListTag aspectNBT = new ListTag();
		ListTag inputNBT = new ListTag();
		for (PedestalContents contents : contents) {
			aspectNBT.add(ItemData.save(contents.aspect));
			inputNBT.add(ItemData.save(contents.input));
		}
		nbt.put("aspects", aspectNBT);
		nbt.put("inputs", inputNBT);

		ItemData.setTag(stack, nbt);
		return stack;
	}
}
