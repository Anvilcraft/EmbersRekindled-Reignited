package com.rekindled.embers.item;

import com.rekindled.embers.compat.curios.CuriosCompat;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class NonbeleiverAmuletItem extends Item implements IEmbersCurioItem {

	public NonbeleiverAmuletItem(Properties properties) {
		super(properties);
		NeoForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onDamage(LivingIncomingDamageEvent event) {
		DamageSource source = event.getSource();

		if (!source.is(DamageTypeTags.WITCH_RESISTANT_TO) || event.getAmount() < 0.5f)
			return;

		CuriosCompat.checkForCurios(event.getEntity(), stack -> {
			if (stack.getItem() == this) {
				event.setAmount(Math.max(event.getAmount() * 0.1f, 0.5f));
				return true;
			}
			return false;
		});
	}
}
