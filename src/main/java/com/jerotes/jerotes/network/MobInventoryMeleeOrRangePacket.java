
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MobInventoryMeleeOrRangePacket {
    public int mob;

    public MobInventoryMeleeOrRangePacket(int id) {
        this.mob = id;
    }

    public static void encode(MobInventoryMeleeOrRangePacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
    }

    public static MobInventoryMeleeOrRangePacket decode(FriendlyByteBuf buffer) {
        return new MobInventoryMeleeOrRangePacket(buffer.readInt());
    }

    public static void consume(MobInventoryMeleeOrRangePacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(message.mob);
                if (entity != null && entity instanceof InventoryEntity inventory) {
                    if (!playerEntity.level().isClientSide()) {
                        inventory.setCanChangeMeleeOrRange(!inventory.isCanChangeMeleeOrRange());
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
