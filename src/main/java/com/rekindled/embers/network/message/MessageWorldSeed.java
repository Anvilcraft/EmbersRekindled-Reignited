package com.rekindled.embers.network.message;


import com.rekindled.embers.EmbersClientEvents;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.PacketFlow;

public class MessageWorldSeed {

	long seed;

	public MessageWorldSeed(long seed) {
		this.seed = seed;
	}

	public static void encode(MessageWorldSeed msg, FriendlyByteBuf buf) {
		buf.writeLong(msg.seed);
	}

	public static MessageWorldSeed decode(FriendlyByteBuf buf) {
		return new MessageWorldSeed(buf.readLong());
	}

	public static void handle(MessageWorldSeed msg, IPayloadContext ctx) {
		if (ctx.flow() == PacketFlow.CLIENTBOUND) {
			ctx.enqueueWork(() -> {
				EmbersClientEvents.seed = msg.seed;
			});
		}
	}
}
