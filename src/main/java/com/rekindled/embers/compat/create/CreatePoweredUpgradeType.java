package com.rekindled.embers.compat.create;

import com.rekindled.embers.Embers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public enum CreatePoweredUpgradeType {
	MINI_BOILER("mini_boiler", "Mini Boiler", 16.0F, 96.0F, 192.0F, 16.0D),
	CATALYTIC_PLUG("catalytic_plug", "Catalytic Plug", 32.0F, 128.0F, 256.0F, 32.0D),
	WILDFIRE_STIRLING("wildfire_stirling", "Wildfire Stirling", 32.0F, 128.0F, 256.0F, 32.0D),
	MNEMONIC_INSCRIBER("mnemonic_inscriber", "Mnemonic Inscriber", 8.0F, 64.0F, 128.0F, 8.0D);

	private final String basePath;
	private final String englishName;
	private final float minRpm;
	private final float optimalRpm;
	private final float maxRpm;
	private final double stressAtOptimal;

	CreatePoweredUpgradeType(String basePath, String englishName, float minRpm, float optimalRpm, float maxRpm, double stressAtOptimal) {
		this.basePath = basePath;
		this.englishName = englishName;
		this.minRpm = minRpm;
		this.optimalRpm = optimalRpm;
		this.maxRpm = maxRpm;
		this.stressAtOptimal = stressAtOptimal;
	}

	public String basePath() {
		return basePath;
	}

	public String poweredPath() {
		return "create_powered_" + basePath;
	}

	public String englishName() {
		return "Create-Powered " + englishName;
	}

	public ResourceLocation baseUpgradeId() {
		return ResourceLocation.fromNamespaceAndPath(Embers.MODID, basePath);
	}

	public float minRpm() {
		return minRpm;
	}

	public float optimalRpm() {
		return optimalRpm;
	}

	public float maxRpm() {
		return maxRpm;
	}

	public double stressAtOptimal() {
		return stressAtOptimal;
	}

	public double stressImpactPerRpm() {
		return stressAtOptimal / optimalRpm;
	}

	public double efficiency(float speed) {
		float rpm = Math.abs(speed);
		if (rpm <= minRpm || rpm >= maxRpm) {
			return 0.0D;
		}
		if (rpm == optimalRpm) {
			return 1.0D;
		}
		if (rpm < optimalRpm) {
			return Mth.clamp((rpm - minRpm) / (optimalRpm - minRpm), 0.0F, 1.0F);
		}
		return Mth.clamp((maxRpm - rpm) / (maxRpm - optimalRpm), 0.0F, 1.0F);
	}

	public double scale(float speed, double baseValue, double optimalValue) {
		return Mth.lerp(efficiency(speed), baseValue, optimalValue);
	}

	public boolean isStateful() {
		return this == MINI_BOILER || this == MNEMONIC_INSCRIBER;
	}
}
