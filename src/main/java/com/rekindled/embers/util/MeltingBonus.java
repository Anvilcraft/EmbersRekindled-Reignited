package com.rekindled.embers.util;

import com.rekindled.embers.RegistryManager;

import net.minecraft.world.level.material.Fluid;

public class MeltingBonus {

	public static final int NUGGET_AMOUNT = 10;
	public static final MeltingBonus IRON = new MeltingBonus("iron", RegistryManager.MOLTEN_IRON.FLUID.get(), NUGGET_AMOUNT, false);
	public static final MeltingBonus GOLD = new MeltingBonus("gold", RegistryManager.MOLTEN_GOLD.FLUID.get(), NUGGET_AMOUNT, false);
	public static final MeltingBonus COPPER = new MeltingBonus("copper", RegistryManager.MOLTEN_COPPER.FLUID.get(), NUGGET_AMOUNT, false);
	public static final MeltingBonus LEAD = new MeltingBonus("lead", RegistryManager.MOLTEN_LEAD.FLUID.get(), NUGGET_AMOUNT, false);
	public static final MeltingBonus SILVER = new MeltingBonus("silver", RegistryManager.MOLTEN_SILVER.FLUID.get(), NUGGET_AMOUNT, false);
	public static final MeltingBonus NICKEL = new MeltingBonus("nickel", RegistryManager.MOLTEN_NICKEL.FLUID.get(), NUGGET_AMOUNT, true);
	public static final MeltingBonus TIN = new MeltingBonus("tin", RegistryManager.MOLTEN_TIN.FLUID.get(), NUGGET_AMOUNT, true);
	public static final MeltingBonus ALUMINUM = new MeltingBonus("aluminum", RegistryManager.MOLTEN_ALUMINUM.FLUID.get(), NUGGET_AMOUNT, true);
	public static final MeltingBonus ZINC = new MeltingBonus("zinc", RegistryManager.MOLTEN_ZINC.FLUID.get(), NUGGET_AMOUNT, true);
	public static final MeltingBonus PLATINUM = new MeltingBonus("platinum", RegistryManager.MOLTEN_PLATINUM.FLUID.get(), NUGGET_AMOUNT, true);
	public static final MeltingBonus URANIUM = new MeltingBonus("uranium", RegistryManager.MOLTEN_URANIUM.FLUID.get(), NUGGET_AMOUNT, true);

	public final String name;
	public final Fluid fluid;
	public final int amount;
	public final boolean optional;

	public MeltingBonus(String name, Fluid fluid, int amount, boolean optional) {
		this.name = name;
		this.fluid = fluid;
		this.amount = amount;
		this.optional = optional;
	}
}
