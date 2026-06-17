package com.rekindled.embers.blockentity;

import java.util.HashSet;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.compat.sublevel.SubLevelCompat;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FieldChartBlockEntity extends BlockEntity implements ISoundController {

	public static final int SOUND_LOOP = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_LOOP};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public FieldChartBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.FIELD_CHART_ENTITY.get(), pPos, pBlockState);
	}

	public AABB getRenderBoundingBox() {
		Vec3 origin = Vec3.atLowerCornerOf(worldPosition);
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double minZ = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;
		for (double x : new double[]{-1.0D, 3.0D}) {
			for (double y : new double[]{0.0D, 2.0D}) {
				for (double z : new double[]{-1.0D, 3.0D}) {
					Vec3 corner = SubLevelCompat.toPhysicalPosition(this, origin.add(x, y, z));
					minX = Math.min(minX, corner.x);
					minY = Math.min(minY, corner.y);
					minZ = Math.min(minZ, corner.z);
					maxX = Math.max(maxX, corner.x);
					maxY = Math.max(maxY, corner.y);
					maxZ = Math.max(maxZ, corner.z);
				}
			}
		}
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(0.125D);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, FieldChartBlockEntity blockEntity) {
		blockEntity.handleSound();
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_LOOP:
			Vec3 soundPos = SubLevelCompat.toPhysicalPosition(this, Vec3.atCenterOf(worldPosition));
			EmbersSounds.playMachineSound(this, SOUND_LOOP, EmbersSounds.FIELD_CHART_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, (float) soundPos.x, (float) soundPos.y, (float) soundPos.z);
			break;
		}
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
		soundsPlaying.remove(id);
	}

	@Override
	public boolean isSoundPlaying(int id) {
		return soundsPlaying.contains(id);
	}

	@Override
	public int[] getSoundIDs() {
		return SOUND_IDS;
	}

	@Override
	public boolean shouldPlaySound(int id) {
		return id == SOUND_LOOP;
	}
}
