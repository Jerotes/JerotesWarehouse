
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public class MobSendEffectsPacket {
    public int mob;
    public int type = 1;

    public MobSendEffectsPacket(int id) {
        this.mob = id;
    }
    public MobSendEffectsPacket(int id, int type) {
        this.mob = id;
        this.type = type;
    }

    public static void encode(MobSendEffectsPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
        buffer.writeInt(packet.type);
    }

    public static MobSendEffectsPacket decode(FriendlyByteBuf buffer) {
        return new MobSendEffectsPacket(buffer.readInt(), buffer.readInt());
    }


    public static void consume(MobSendEffectsPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(message.mob);
                if (entity instanceof LivingEntity livingEntity) {
                    if (!playerEntity.level().isClientSide()) {
                        if (message.type == 1) {
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
                        else if (message.type == 2) {
                            if (!playerEntity.level().isClientSide()) {
                                entity = playerEntity.level().getEntity(message.mob);
                                if (entity instanceof ServerPlayer serverPlayer) {
                                    ResourceKey<Level> respawnDimension = serverPlayer.getRespawnDimension();
                                    if (serverPlayer.getRespawnPosition() != null && playerEntity.level() instanceof ServerLevel serverLevel && Player.findRespawnPositionAndUseSpawnBlock(serverLevel, serverPlayer.getRespawnPosition(), serverPlayer.getRespawnAngle(), serverPlayer.isRespawnForced(), false).isPresent()) {
                                        //出生点
                                        playerEntity.sendSystemMessage(Component.translatable("message.jerotes.player_spawn_point", serverPlayer.getRespawnPosition().getX(), serverPlayer.getRespawnPosition().getY(), serverPlayer.getRespawnPosition().getZ()).withStyle(ChatFormatting.BLUE));
                                    }
                                    else {
                                        if (playerEntity.level() instanceof ServerLevelAccessor serverLevelAcc) {
                                            Level level = Objects.requireNonNull(serverLevelAcc.getLevel().getServer().getLevel(respawnDimension));
                                            playerEntity.sendSystemMessage(Component.translatable("message.jerotes.world_spawn_point", level.getLevelData().getXSpawn(), level.getLevelData().getYSpawn(), level.getLevelData().getZSpawn()).withStyle(ChatFormatting.BLUE));
                                        }
                                    }
                                    playerEntity.sendSystemMessage(Component.translatable("message.jerotes.player_spawn_point_dimension", respawnDimension.location()).withStyle(ChatFormatting.BLUE));
                                }
                            }
                        }
                    }

                }
            }
        });
        context.get().setPacketHandled(true);
    }
}