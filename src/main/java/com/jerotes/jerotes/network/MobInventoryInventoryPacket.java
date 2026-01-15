
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MobInventoryInventoryPacket {
    public int mob;

    public MobInventoryInventoryPacket(int id) {
        this.mob = id;
    }

    public static void encode(MobInventoryInventoryPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
    }

    public static MobInventoryInventoryPacket decode(FriendlyByteBuf buffer) {
        return new MobInventoryInventoryPacket(buffer.readInt());
    }


    public static void consume(MobInventoryInventoryPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(message.mob);
                if (entity != null && entity instanceof InventoryEntity inventory) {
                    if (!playerEntity.level().isClientSide()) {
                        inventory.setCanChangeInventory(!inventory.isCanChangeInventory());
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}