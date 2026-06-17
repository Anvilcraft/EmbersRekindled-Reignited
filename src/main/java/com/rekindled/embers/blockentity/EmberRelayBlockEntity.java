package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.power.IEmberPacketProducer;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.api.power.ITargetable;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.entity.EmberPacketEntity;
import com.rekindled.embers.compat.sublevel.SubLevelCompat;
import com.rekindled.embers.particle.StarParticleOptions;
import com.rekindled.embers.util.EmbersColors;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class EmberRelayBlockEntity extends BlockEntity implements IEmberPacketProducer, ITargetable, IEmberPacketReceiver {

	public BlockPos target = null;
	public UUID targetSubLevelId = null;
	public Random random = new Random();
	public boolean polled = false;
	public Vec3 incomingDirection = Vec3.ZERO;
	public HashSet<ChunkPos> trajectoryChunks = null;

	public EmberRelayBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.EMBER_RELAY_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.loadAdditional(nbt, registries);
		if (nbt.contains("targetX")) {
			target = new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ"));
		}
		targetSubLevelId = readTargetSubLevelId(nbt);
		incomingDirection = new Vec3(nbt.getDouble("incomingX"), nbt.getDouble("incomingY"), nbt.getDouble("incomingZ"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
		super.saveAdditional(nbt, registries);
		if (target != null) {
			nbt.putInt("targetX", target.getX());
			nbt.putInt("targetY", target.getY());
			nbt.putInt("targetZ", target.getZ());
		}
		if (targetSubLevelId != null) {
			nbt.putString("targetSubLevel", targetSubLevelId.toString());
		}
		nbt.putDouble("incomingX", incomingDirection.x);
		nbt.putDouble("incomingY", incomingDirection.y);
		nbt.putDouble("incomingZ", incomingDirection.z);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbt = super.getUpdateTag(registries);
		if (target != null) {
			nbt.putInt("targetX", target.getX());
			nbt.putInt("targetY", target.getY());
			nbt.putInt("targetZ", target.getZ());
		}
		if (targetSubLevelId != null) {
			nbt.putString("targetSubLevel", targetSubLevelId.toString());
		}
		nbt.putDouble("incomingX", incomingDirection.x);
		nbt.putDouble("incomingY", incomingDirection.y);
		nbt.putDouble("incomingZ", incomingDirection.z);
		return nbt;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel)
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
		if (trajectoryChunks == null)
			trajectoryChunks = new HashSet<ChunkPos>();
		Misc.calculateTrajectoryChunks(trajectoryChunks, worldPosition, target, getEmittingDirection(level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING).getOpposite()));
	}

	@Override
	public boolean hasRoomFor(double ember) {
		if (trajectoryChunks == null) {
			trajectoryChunks = new HashSet<ChunkPos>();
			Misc.calculateTrajectoryChunks(trajectoryChunks, worldPosition, target, getEmittingDirection(level.getBlockState(worldPosition).getValue(BlockStateProperties.FACING).getOpposite()));
		}
		if (polled)
			return target != null;
		polled = true;
		BlockEntity targetTile = target == null ? null : SubLevelCompat.findReachableLinkedTarget(this, target, targetSubLevelId);
		if (targetTile instanceof IEmberPacketReceiver targetBE) {
			if (!SubLevelCompat.isInSubLevel(this) && !SubLevelCompat.isInSubLevel(targetTile) && level instanceof ServerLevel serverLevel) {
				for (ChunkPos chunk : trajectoryChunks) {
					if (!serverLevel.isNaturalSpawningAllowed(chunk)) {
						polled = false;
						return false;
					}
				}
			}
			boolean hasRoom = targetBE.hasRoomFor(ember);
			polled = false;
			return hasRoom;
		}
		polled = false;
		return false;
	}

	@Override
	public boolean onReceive(EmberPacketEntity packet) {
		BlockEntity targetTile = target == null ? null : SubLevelCompat.findReachableLinkedTarget(this, target, targetSubLevelId);
		if (targetTile instanceof IEmberPacketReceiver targetBE && targetBE.hasRoomFor(packet.value) && !getBlockPos().equals(packet.pos)) {
			if (level instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(new StarParticleOptions(EmbersColors.EMBER_ID, 3.5f + 0.5f * random.nextFloat()), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 12, 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0125f * (random.nextFloat() - 0.5f), 0.0);
			}
			packet.setLifetime(78);
			packet.dest = BlockPos.containing(SubLevelCompat.linkedTargetPhysicalPosition(this, target, targetSubLevelId));
			packet.pos = getBlockPos().immutable();
			packet.setTrackedTarget(target, targetSubLevelId);
			setIncomingDirection(packet.getDeltaMovement());
			packet.setDeltaMovement(SubLevelCompat.toPhysicalDirection(this, SubLevelCompat.toLocalDirection(this, packet.getDeltaMovement()).scale(1.7)));
			level.playLocalSound(packet.getX(), packet.getY(), packet.getZ(), EmbersSounds.EMBER_RELAY.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
			return false;
		}
		return true;
	}

	@Override
	public void setIncomingDirection(Vec3 direction) {
		incomingDirection = direction.scale(1.7);
		this.setChanged();
	}

	@Override
	public void setTargetPosition(BlockPos pos, Direction side) {
		if (!pos.equals(worldPosition)) {
			target = pos;
			targetSubLevelId = null;
			this.setChanged();
		}
	}

	@Override
	public void setTargetPosition(BlockPos pos, Direction side, BlockEntity targetEntity) {
		if (!pos.equals(worldPosition)) {
			target = pos;
			targetSubLevelId = SubLevelCompat.getContainingSubLevelId(targetEntity);
			this.setChanged();
		}
	}

	@Override
	public Vec3 getEmittingDirection(Direction side) {
		if (incomingDirection.equals(Vec3.ZERO))
			return EmberEmitterBlockEntity.getBurstVelocity(side);
		return incomingDirection;
	}

	@Override
	public BlockPos getTarget(Direction side) {
		BlockState state = level.getBlockState(worldPosition);
		if (state.hasProperty(BlockStateProperties.FACING)) {
			Direction facing = state.getValue(BlockStateProperties.FACING);
			if (side != facing)
				return null;
		}
		return target;
	}

	@Override
	public UUID getTargetSubLevelId(Direction side) {
		return getTarget(side) == null ? null : targetSubLevelId;
	}

	private static UUID readTargetSubLevelId(CompoundTag nbt) {
		if (!nbt.contains("targetSubLevel")) {
			return null;
		}
		try {
			return UUID.fromString(nbt.getString("targetSubLevel"));
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}
}
