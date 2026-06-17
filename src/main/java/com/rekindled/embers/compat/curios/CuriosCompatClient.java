package com.rekindled.embers.compat.curios;

import net.minecraft.client.color.item.ItemColor;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public final class CuriosCompatClient {

	private CuriosCompatClient() {
	}

	public static void registerColorHandler(RegisterColorHandlersEvent.Item event, ItemColor itemColor) {
		event.register(itemColor, CuriosCompat.EMBER_BULB.get());
	}
}
