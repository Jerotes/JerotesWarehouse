
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK_WRAPPER = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(JerotesWarehouse.MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	public static void register() {
		int packetsRegistered = 0;
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, MobInventoryInventoryPacket.class,
				MobInventoryInventoryPacket::encode,
				MobInventoryInventoryPacket::decode,
				MobInventoryInventoryPacket::consume);
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, MobInventoryMeleeOrRangePacket.class,
				MobInventoryMeleeOrRangePacket::encode,
				MobInventoryMeleeOrRangePacket::decode,
				MobInventoryMeleeOrRangePacket::consume);
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, MobSendEffectsPacket.class,
				MobSendEffectsPacket::encode,
				MobSendEffectsPacket::decode,
				MobSendEffectsPacket::consume);
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, SendMobInventoryPacket.class,
				SendMobInventoryPacket::encode,
				SendMobInventoryPacket::decode,
				SendMobInventoryPacket::consume);
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, JerotesPlayerData.PlayerVariablesSyncMessage.class,
				JerotesPlayerData.PlayerVariablesSyncMessage::encode,
				JerotesPlayerData.PlayerVariablesSyncMessage::decode,
				JerotesPlayerData.PlayerVariablesSyncMessage::consume,
				Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, SpellUseMessage.class,
				SpellUseMessage::encode,
				SpellUseMessage::decode,
				SpellUseMessage::consume);
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, ControlVehicleMessage.class,
				ControlVehicleMessage::encode,
				ControlVehicleMessage::decode,
				ControlVehicleMessage::consume);
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, JerotesSpearAttackPacket.class,
				JerotesSpearAttackPacket::encode,
				JerotesSpearAttackPacket::decode,
				JerotesSpearAttackPacket::consume);
		NETWORK_WRAPPER.registerMessage(packetsRegistered++, JerotesSpearRushAttackPacket.class,
				JerotesSpearRushAttackPacket::encode,
				JerotesSpearRushAttackPacket::decode,
				JerotesSpearRushAttackPacket::consume);

	}

	public static void sendToServer(Object msg) {
		NETWORK_WRAPPER.sendToServer(msg);
	}
}