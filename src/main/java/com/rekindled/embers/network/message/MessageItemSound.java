package com.rekindled.embers.network.message;
import org.apache.commons.lang3.Validate;

import com.rekindled.embers.datagen.EmbersSounds;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageItemSound {
	private final int id;
	private final Item item;
	private final SoundEvent sound;
	private final SoundSource source;
	private final boolean repeat;
	private final float volume;
	private final float pitch;

	public MessageItemSound(Entity entity, Item item, SoundEvent sound, SoundSource source, boolean repeat, float volume, float pitch) {
		this(entity.getId(), item, sound, source, repeat, volume, pitch);
	}

	public MessageItemSound(int id, Item item, SoundEvent sound, SoundSource source, boolean repeat, float volume, float pitch) {
		Validate.notNull(sound, "sound");
		this.item = item;
		this.sound = sound;
		this.source = source;
		this.id = id;
		this.repeat = repeat;
		this.volume = volume;
		this.pitch = pitch;
	}

	public static void encode(MessageItemSound msg, FriendlyByteBuf buf) {
		buf.writeVarInt(msg.id);
		buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(msg.item));
		buf.writeResourceLocation(BuiltInRegistries.SOUND_EVENT.getKey(msg.sound));
		buf.writeEnum(msg.source);
		buf.writeBoolean(msg.repeat);
		buf.writeFloat(msg.volume);
		buf.writeFloat(msg.pitch);
	}

	public static MessageItemSound decode(FriendlyByteBuf buf) {
		return new MessageItemSound(buf.readVarInt(), BuiltInRegistries.ITEM.get(buf.readResourceLocation()), BuiltInRegistries.SOUND_EVENT.get(buf.readResourceLocation()), buf.readEnum(SoundSource.class), buf.readBoolean(), buf.readFloat(), buf.readFloat());
	}

	@SuppressWarnings("resource")
	public static void handle(MessageItemSound msg, IPayloadContext ctx) {
		if (ctx.flow() == PacketFlow.CLIENTBOUND) {
			ctx.enqueueWork(() -> ClientHandlers.handle(msg));
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static final class ClientHandlers {
		private ClientHandlers() {
		}

		private static void handle(MessageItemSound msg) {
			net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
			if (minecraft.level != null && minecraft.level.getEntity(msg.id) instanceof LivingEntity entity) {
				EmbersSounds.playItemSoundClient(entity, msg.item, msg.sound, msg.source, msg.repeat, msg.volume, msg.pitch);
			}
		}
	}
}
