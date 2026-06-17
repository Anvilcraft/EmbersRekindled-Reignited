package com.rekindled.embers.compat.create;

import java.util.LinkedList;
import java.util.List;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.tile.ICatalyticPlugLimitProvider;
import com.rekindled.embers.api.tile.IUpgradeable;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.power.DefaultEmberCapability;
import com.rekindled.embers.upgrade.CatalyticPlugUpgrade;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.LazyOptional;

public class EmberKineticGeneratorBlockEntity extends GeneratingKineticBlockEntity implements IUpgradeable, ICatalyticPlugLimitProvider {
	public static final float BASE_GENERATED_RPM = 64.0f;
	public static final float MAX_GENERATED_RPM = 256.0f;
	public static final float BASE_STRESS_CAPACITY = 16.0f;
	public static final float MID_STRESS_CAPACITY = 32.0f;
	public static final float MAX_STRESS_CAPACITY = 384.0f;
	public static final double BASE_EMBER_PER_TICK = 1.0;
	public static final double MAX_EMBER_PER_TICK = 8.0;
	public static final int MAX_CATALYTIC_PLUGS = 4;

	private final IEmberCapability ember = new DefaultEmberCapability() {
		@Override
		public void onContentsChanged() {
			EmberKineticGeneratorBlockEntity.this.setChanged();
		}

		@Override
		public boolean acceptsVolatile() {
			return false;
		}
	};
	private boolean active;
	private int activeCatalyticPlugs;
	private final List<UpgradeContext> upgrades = new LinkedList<>();

	public EmberKineticGeneratorBlockEntity(BlockPos pos, BlockState state) {
		super(CreateCompat.EMBER_KINETIC_GENERATOR_ENTITY.get(), pos, state);
		ember.setEmberCapacity(4000);
	}

	@Override
	public void initialize() {
		super.initialize();
		updateGeneratedRotation();
	}

	@Override
	public void tick() {
		super.tick();
		if (level == null || level.isClientSide) {
			return;
		}
		refreshUpgrades();
		int nextActiveCatalyticPlugs = getActiveCatalyticPlugCount();
		double emberPerTick = getEmberBurnRate(nextActiveCatalyticPlugs);
		boolean nextActive = ember.removeAmount(emberPerTick, false) >= emberPerTick;
		if (nextActive) {
			UpgradeUtil.doWork(this, upgrades);
			ember.removeAmount(emberPerTick, true);
		}
		if (active != nextActive || activeCatalyticPlugs != nextActiveCatalyticPlugs) {
			active = nextActive;
			activeCatalyticPlugs = nextActiveCatalyticPlugs;
			updateGeneratedRotation();
			sendData();
			setChanged();
		}
	}

	@Override
	public float getGeneratedSpeed() {
		if (!active) {
			return 0;
		}
		return convertToDirection(getGeneratedRpm(activeCatalyticPlugs), getBlockState().getValue(EmberKineticGeneratorBlock.FACING));
	}

	@Override
	public float calculateAddedStressCapacity() {
		lastCapacityProvided = active ? getStressCapacity(activeCatalyticPlugs) : 0;
		return lastCapacityProvided;
	}

	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
		if (!isRemoved() && capability == EmbersCapabilities.EMBER_CAPABILITY && ember instanceof DefaultEmberCapability defaultEmber) {
			return defaultEmber.getCapability(capability, side);
		}
		return LazyOptional.empty();
	}

	public IEmberCapability getEmberCapability() {
		return ember;
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		ember.writeToNBT(tag);
		tag.putBoolean("Active", active);
		tag.putInt("ActiveCatalyticPlugs", activeCatalyticPlugs);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		ember.deserializeNBT(tag);
		active = tag.getBoolean("Active");
		activeCatalyticPlugs = tag.getInt("ActiveCatalyticPlugs");
	}

	@Override
	public boolean isSideUpgradeSlot(Direction face) {
		return face.getAxis() != getBlockState().getValue(EmberKineticGeneratorBlock.FACING).getAxis();
	}

	@Override
	public int getCatalyticPlugLimit() {
		return MAX_CATALYTIC_PLUGS;
	}

	private void refreshUpgrades() {
		upgrades.clear();
		upgrades.addAll(UpgradeUtil.getUpgrades(level, worldPosition, Direction.values()));
		upgrades.removeIf(upgrade -> !(upgrade.upgrade() instanceof CatalyticPlugUpgrade));
		UpgradeUtil.verifyUpgrades(this, upgrades);
	}

	private int getActiveCatalyticPlugCount() {
		int activePlugCount = 0;
		for (UpgradeContext upgrade : upgrades) {
			if (upgrade.upgrade() instanceof CatalyticPlugUpgrade plug
					&& plug.getSpeed(this, 1.0, upgrade.distance(), upgrade.count()) > 1.0) {
				activePlugCount++;
			}
		}
		return Math.min(MAX_CATALYTIC_PLUGS, activePlugCount);
	}

	private static float getGeneratedRpm(int activeCatalyticPlugs) {
		return switch (clampCatalyticPlugs(activeCatalyticPlugs)) {
		case 0 -> BASE_GENERATED_RPM;
		case 1, 2 -> 128.0f;
		default -> MAX_GENERATED_RPM;
		};
	}

	private static float getStressCapacity(int activeCatalyticPlugs) {
		return switch (clampCatalyticPlugs(activeCatalyticPlugs)) {
		case 0, 1 -> BASE_STRESS_CAPACITY;
		case 2, 3 -> MID_STRESS_CAPACITY;
		default -> MAX_STRESS_CAPACITY;
		};
	}

	private static double getEmberBurnRate(int activeCatalyticPlugs) {
		int plugs = clampCatalyticPlugs(activeCatalyticPlugs);
		if (plugs <= 0) {
			return BASE_EMBER_PER_TICK;
		}
		double burnRatio = MAX_EMBER_PER_TICK / BASE_EMBER_PER_TICK;
		return BASE_EMBER_PER_TICK * Math.pow(burnRatio, (double) plugs / MAX_CATALYTIC_PLUGS);
	}

	private static int clampCatalyticPlugs(int activeCatalyticPlugs) {
		return Math.max(0, Math.min(MAX_CATALYTIC_PLUGS, activeCatalyticPlugs));
	}
}
