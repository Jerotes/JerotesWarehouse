
package com.jerotes.jerotes.network;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.function.Supplier;

public class MobSendEffectsPacket {
    public int mob;

    public MobSendEffectsPacket(int id) {
        this.mob = id;
    }

    public static void encode(MobSendEffectsPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
    }

    public static MobSendEffectsPacket decode(FriendlyByteBuf buffer) {
        return new MobSendEffectsPacket(buffer.readInt());
    }


    public static void consume(MobSendEffectsPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(message.mob);
                if (entity != null && entity instanceof LivingEntity livingEntity) {
                    if (!playerEntity.level().isClientSide()) {
                        playerEntity.sendSystemMessage(Component.translatable("message.jerotes.effect").withStyle(ChatFormatting.DARK_GREEN));
                        Collection<MobEffectInstance> effects = livingEntity.getActiveEffects();
                        if (!effects.isEmpty()) {
                            for (MobEffectInstance effect : effects) {
                                ChatFormatting color = effect.getEffect().isBeneficial() ? ChatFormatting.GREEN : ChatFormatting.YELLOW;
                                playerEntity.sendSystemMessage(
                                        Component.translatable(effect.getDescriptionId())
                                                .append(" ")
                                                .append(Component.translatable("message.jerotes.effect_level", effect.getAmplifier() + 1))
                                                .append(" ")
                                                .append(Component.translatable("message.jerotes.effect_second", MobEffectUtil.formatDuration(effect, 1.0f)))
                                                .withStyle(color)
                                );
                            }
                        }

                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}