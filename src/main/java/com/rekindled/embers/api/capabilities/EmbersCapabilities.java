package com.rekindled.embers.api.capabilities;

import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.augment.ShiftingScalesAugment.IScalesCapability;
import com.rekindled.embers.research.capability.IResearchCapability;

import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.capabilities.CapabilityManager;
import com.rekindled.embers.compat.legacy.capabilities.CapabilityToken;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.rekindled.embers.Embers;

public class EmbersCapabilities {
	public static final Capability<IUpgradeProvider> UPGRADE_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<IEmberCapability> EMBER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<IResearchCapability> RESEARCH_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<IScalesCapability> SCALES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final BlockCapability<IEmberCapability, Direction> EMBER_BLOCK_CAPABILITY =
			BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(Embers.MODID, "ember"), IEmberCapability.class);
}
