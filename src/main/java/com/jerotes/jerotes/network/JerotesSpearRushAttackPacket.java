
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JerotesSpearRushAttackPacket {
public ItemStack itemStack;
public int n;
public int livingEntity;

public JerotesSpearRushAttackPacket(ItemStack itemStack, int n, int livingEntity) {
    this.itemStack = itemStack;
    this.n = n;
    this.livingEntity = livingEntity;
}

    public static void encode(JerotesSpearRushAttackPacket packet, FriendlyByteBuf buffer) {
        buffer.writeItem(packet.itemStack);
        buffer.writeInt(packet.n);
        buffer.writeInt(packet.livingEntity);
    }

    public static JerotesSpearRushAttackPacket decode(FriendlyByteBuf buffer) {
        return new JerotesSpearRushAttackPacket(buffer.readItem(), buffer.readInt(), buffer.readInt());
    }


    public static void consume(JerotesSpearRushAttackPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                if (!playerEntity.isSpectator()) {
                    Entity entity = playerEntity.level().getEntity(message.livingEntity);
                    if (message.itemStack.getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && entity instanceof LivingEntity livingEntity) {
                        itemToolBaseSpearBase.damageEntities(message.itemStack, message.n,
                                livingEntity,
                                livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND ?
                                        EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}