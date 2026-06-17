package com.rekindled.embers.network.message;


import com.rekindled.embers.util.EmberGenUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.PacketFlow;

public class MessageEmberGenOffset {

	public int offX = 0;
	public int offZ = 0;

	public MessageEmberGenOffset() {

	}

	public MessageEmberGenOffset(int x, int z) {
		this.offX = x;
		this.offZ = z;
	}

	public static void encode(MessageEmberGenOffset msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.offX);
		buf.writeInt(msg.offZ);
	}

	public static MessageEmberGenOffset decode(FriendlyByteBuf buf) {
		return new MessageEmberGenOffset(buf.readInt(), buf.readInt());
	}

	public static void handle(MessageEmberGenOffset msg, IPayloadContext ctx) {
		if (ctx.flow() == PacketFlow.CLIENTBOUND) {
			ctx.enqueueWork(() -> {
				EmberGenUtil.offX = msg.offX;
				EmberGenUtil.offZ = msg.offZ;
			});
		}
	}
}
