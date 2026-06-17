package com.rekindled.embers.api.misc;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class HammerTarget {
	public BlockPos pos;
	public Direction face;
	public UUID subLevelId;

	public HammerTarget(BlockPos pos, Direction face) {
		this(pos, face, null);
	}

	public HammerTarget(BlockPos pos, Direction face, UUID subLevelId) {
		this.pos = pos;
		this.face = face;
		this.subLevelId = subLevelId;
	}
}
