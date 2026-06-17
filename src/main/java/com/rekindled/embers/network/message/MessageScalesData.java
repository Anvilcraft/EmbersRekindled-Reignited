package com.rekindled.embers.network.message;


import com.rekindled.embers.augment.ShiftingScalesAugment;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.PacketFlow;

public class MessageScalesData {

	public int scales;

	public MessageScalesData() {
		this.scales = 0;
	}

	public MessageScalesData(int scales) {
		this.scales = scales;
	}

	public MessageScalesData(double scales) {
		this.scales = (int) Math.ceil(scales);
	}

	public static void encode(MessageScalesData msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.scales);
	}

	public static MessageScalesData decode(FriendlyByteBuf buf) {
		return new MessageScalesData(buf.readInt());
	}

	public static void handle(MessageScalesData msg, IPayloadContext ctx) {
		if (ctx.flow() == PacketFlow.CLIENTBOUND) {
			ctx.enqueueWork(() -> {
				ShiftingScalesAugment.scales = msg.scales;
			});
		}
	}
}
