package com.jerotes.jerotes.network;

import com.jerotes.jerotes.item.ItemSpecialAttack;
import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JerotesSpearAttackPacket {
    public int livingEntity;

    public JerotesSpearAttackPacket(int livingEntity) {
        this.livingEntity = livingEntity;
    }

    public static void encode(JerotesSpearAttackPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.livingEntity);
    }

    public static JerotesSpearAttackPacket decode(FriendlyByteBuf buffer) {
        return new JerotesSpearAttackPacket(buffer.readInt());
    }


    public static void consume(JerotesSpearAttackPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(message.livingEntity);
                if (entity instanceof Player player) {
                    if (!player.isSpectator()) {
                        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                        if (itemStack.getItem() instanceof ItemSpecialAttack jerotesSpecialAttackNeed) {
                            if (!jerotesSpecialAttackNeed.jerotesSpecialAttackNeed(player)) {
                                return;
                            }
                            jerotesSpecialAttackNeed.jerotesSpecialAttack(player, EquipmentSlot.MAINHAND);
                        }
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}