package com.rekindled.embers.compat.legacy.fluids;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * Transitional class name for NeoForge's renamed flowing-fluid base.
 */
public abstract class ForgeFlowingFluid extends BaseFlowingFluid {
	protected ForgeFlowingFluid(Properties properties) {
		super(properties);
	}

	public static class Flowing extends BaseFlowingFluid.Flowing {
		public Flowing(Properties properties) {
			super(properties);
		}
	}

	public static class Source extends BaseFlowingFluid.Source {
		public Source(Properties properties) {
			super(properties);
		}
	}

	public static class Properties extends BaseFlowingFluid.Properties {
		public Properties(java.util.function.Supplier<? extends net.neoforged.neoforge.fluids.FluidType> fluidType,
				java.util.function.Supplier<? extends net.minecraft.world.level.material.Fluid> still,
				java.util.function.Supplier<? extends net.minecraft.world.level.material.Fluid> flowing) {
			super(fluidType, still, flowing);
		}
	}
}
