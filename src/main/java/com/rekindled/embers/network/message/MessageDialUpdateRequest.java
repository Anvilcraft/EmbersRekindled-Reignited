package com.rekindled.embers.network.message;


import com.rekindled.embers.api.tile.IDialEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.PacketFlow;

public class MessageDialUpdateRequest {
	public long pos = 0;
	public int maxLines;

	public MessageDialUpdateRequest(long pos, int maxLines) {
		this.pos = pos;
		this.maxLines = maxLines;
	}

	public MessageDialUpdateRequest(BlockPos pos, int maxLines) {
		this.pos = pos.asLong();
		this.maxLines = maxLines;
	}

	public static void encode(MessageDialUpdateRequest msg, FriendlyByteBuf buf) {
		buf.writeLong(msg.pos);
		buf.writeInt(msg.maxLines);
	}

	public static MessageDialUpdateRequest decode(FriendlyByteBuf buf) {
		return new MessageDialUpdateRequest(buf.readLong(), buf.readInt());
	}

	public static void handle(MessageDialUpdateRequest msg, IPayloadContext ctx) {
		if (ctx.flow() == PacketFlow.SERVERBOUND) {
			ctx.enqueueWork(() -> {
				ServerPlayer player = (ServerPlayer) ctx.player();
				if (player != null && player.level() != null) {
					BlockEntity blockEntity = player.level().getBlockEntity(BlockPos.of(msg.pos));
					if (blockEntity instanceof IDialEntity dial) {
						Packet<ClientGamePacketListener> packet = dial.getUpdatePacket(msg.maxLines);
						if (packet != null) {
							player.connection.send(packet);
						}
					}
				}
			});
		}
	}
}
