package com.jerotes.jerotes.network;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.item.Interface.ItemSpecialAttack;
import com.jerotes.jerotes.item.Tool.ItemToolBaseParryShield;
import com.jerotes.jerotes.util.Main;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JerotesPlayerOtherItemAboutPacket {
    public int livingEntity;
    public int type;

    public JerotesPlayerOtherItemAboutPacket(int livingEntity, int type) {
        this.livingEntity = livingEntity;
        this.type = type;
    }

    public static void encode(JerotesPlayerOtherItemAboutPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.livingEntity);
        buffer.writeInt(packet.type);
    }

    public static JerotesPlayerOtherItemAboutPacket decode(FriendlyByteBuf buffer) {
        return new JerotesPlayerOtherItemAboutPacket(buffer.readInt(), buffer.readInt());
    }


    public static void consume(JerotesPlayerOtherItemAboutPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(message.livingEntity);
                if (entity instanceof Player player) {
                    about(player, message.type);
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void about(Player player, int type) {
        //弹反
        if (type == 1) {
            if (!player.isSpectator()) {
                Item item = player.getUseItem().getItem();
                if (player.isUsingItem() && item instanceof ItemToolBaseParryShield itemToolBaseParryShield && !player.getCooldowns().isOnCooldown(itemToolBaseParryShield) && (Main.getJerotesPersistentData(player).getDouble("jerotes_shield_parry_cooldown") <= 0 || Main.getJerotesPersistentData(player).get("jerotes_shield_parry_cooldown") == null)) {
                    Main.getJerotesPersistentData(player).putDouble("jerotes_shield_parry_cooldown", itemToolBaseParryShield.parryCooldownTicks);
                    Main.getJerotesPersistentData(player).putDouble("jerotes_shield_parry_tick", itemToolBaseParryShield.parryDurationTicks);
                    if (!player.level().isClientSide()) {
                        itemToolBaseParryShield.makeParrySound(player);
                    }
                    JerotesWarehouse.LOGGER.error("Parry!!!");
                 }
            }
        }
        //矛刺击
        if (type == 2) {
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
}