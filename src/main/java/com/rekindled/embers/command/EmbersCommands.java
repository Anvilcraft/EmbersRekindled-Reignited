package com.rekindled.embers.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.rekindled.embers.worldgen.EmbersLateWorldgen;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class EmbersCommands {
	private static final int DEFAULT_REGEN_RADIUS = 8;
	private static final int MAX_REGEN_RADIUS = 32;

	private EmbersCommands() {
	}

	public static void register(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("embers")
				.requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(Commands.literal("regen")
						.executes(context -> regen(context, DEFAULT_REGEN_RADIUS))
						.then(Commands.argument("radius", IntegerArgumentType.integer(0, MAX_REGEN_RADIUS))
								.executes(context -> regen(context, IntegerArgumentType.getInteger(context, "radius"))))));
	}

	private static int regen(CommandContext<CommandSourceStack> context, int radius) {
		CommandSourceStack source = context.getSource();
		ServerLevel level = source.getLevel();
		ChunkPos center = new ChunkPos(BlockPos.containing(source.getPosition()));
		int queued = EmbersLateWorldgen.queueRegeneration(level, center, radius);

		if (queued == 0) {
			source.sendFailure(Component.literal("No loaded Overworld chunks were queued for Embers regeneration."));
			return 0;
		}

		source.sendSuccess(() -> Component.literal("Queued Embers regeneration for " + queued + " loaded chunk(s)."), true);
		return queued;
	}
}
