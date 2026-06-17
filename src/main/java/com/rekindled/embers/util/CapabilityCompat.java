package com.rekindled.embers.util;

import java.lang.reflect.Method;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.compat.legacy.capabilities.Capability;
import com.rekindled.embers.compat.legacy.capabilities.ForgeCapabilities;
import com.rekindled.embers.compat.legacy.capabilities.ICapabilityProvider;
import com.rekindled.embers.compat.legacy.LazyOptional;
import net.neoforged.neoforge.capabilities.Capabilities;

/**
 * Bridges the original internal capability providers while the public API uses NeoForge capabilities.
 */
public final class CapabilityCompat {
	private static final ThreadLocal<Boolean> QUERYING_NEOFORGE = ThreadLocal.withInitial(() -> false);

	private CapabilityCompat() {
	}

	public static <T> LazyOptional<T> getCapability(@Nullable Object provider, Capability<T> capability) {
		return getCapability(provider, capability, null);
	}

	public static <T> LazyOptional<T> getCapability(@Nullable Object provider, Capability<T> capability, @Nullable Direction side) {
		if (provider == null) {
			return LazyOptional.empty();
		}
		if (provider instanceof ICapabilityProvider capabilityProvider) {
			LazyOptional<T> optional = capabilityProvider.getCapability(capability, side);
			if (optional.isPresent()) {
				return optional;
			}
		}
		if (provider instanceof ItemStack stack) {
			try {
				Method method = stack.getItem().getClass().getMethod("initCapabilities", ItemStack.class, net.minecraft.nbt.CompoundTag.class);
				Object itemProvider = method.invoke(stack.getItem(), stack, null);
				if (itemProvider instanceof ICapabilityProvider capabilityProvider) {
					LazyOptional<T> optional = capabilityProvider.getCapability(capability, side);
					if (optional.isPresent()) {
						return optional;
					}
				}
			} catch (ReflectiveOperationException ignored) {
			}
		}
		try {
			Method method = provider.getClass().getMethod("getCapability", Capability.class, Direction.class);
			Object result = method.invoke(provider, capability, side);
			if (result instanceof LazyOptional<?> optional) {
				LazyOptional<T> cast = optional.cast();
				if (cast.isPresent()) {
					return cast;
				}
			}
		} catch (ReflectiveOperationException ignored) {
		}
		if (provider instanceof BlockEntity blockEntity
				&& capability == EmbersCapabilities.EMBER_CAPABILITY
				&& blockEntity.getLevel() != null
				&& !QUERYING_NEOFORGE.get()) {
			Object value;
			try {
				QUERYING_NEOFORGE.set(true);
				value = blockEntity.getLevel().getCapability(EmbersCapabilities.EMBER_BLOCK_CAPABILITY,
						blockEntity.getBlockPos(), side);
			} finally {
				QUERYING_NEOFORGE.set(false);
			}
			if (value != null) {
				Object captured = value;
				return LazyOptional.of(() -> (T) captured);
			}
		}
		if (provider instanceof BlockEntity blockEntity && blockEntity.getLevel() != null && !QUERYING_NEOFORGE.get()) {
			Object value = null;
			try {
				QUERYING_NEOFORGE.set(true);
				if (capability == ForgeCapabilities.FLUID_HANDLER) {
					value = blockEntity.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, blockEntity.getBlockPos(), side);
				} else if (capability == ForgeCapabilities.ITEM_HANDLER) {
					value = blockEntity.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, blockEntity.getBlockPos(), side);
				}
			} finally {
				QUERYING_NEOFORGE.set(false);
			}
			if (value != null) {
				Object captured = value;
				return LazyOptional.of(() -> (T) captured);
			}
		}
		return LazyOptional.empty();
	}
}
