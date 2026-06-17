package com.rekindled.embers.compat.legacy.capabilities;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandler;

public final class ForgeCapabilities {
	public static final Capability<IItemHandler> ITEM_HANDLER = new Capability<>();
	public static final Capability<IItemHandler> ITEM_HANDLER_ITEM = new Capability<>();
	public static final Capability<IFluidHandler> FLUID_HANDLER = new Capability<>();
	public static final Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM = new Capability<>();

	private ForgeCapabilities() {
	}
}
