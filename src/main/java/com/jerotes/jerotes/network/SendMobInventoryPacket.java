

package com.jerotes.jerotes.network;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class SendMobInventoryPacket {
    public int mob;

    public SendMobInventoryPacket(int id) {
        this.mob = id;
    }

    public static void encode(SendMobInventoryPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.mob);
    }

    public static SendMobInventoryPacket decode(FriendlyByteBuf buffer) {
        return new SendMobInventoryPacket(buffer.readInt());
    }


    public static void consume(SendMobInventoryPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(message.mob);
                if (entity != null && entity instanceof LivingEntity livingEntity) {
                    UUID uuid = livingEntity.getUUID();
                    List<? extends Player> list = playerEntity.level().players();
                    list.removeIf(player -> player.level() != playerEntity.level());
                    list.removeIf(player -> (player.getTeam() != null && player.getTeam() != playerEntity.getTeam()));
                    String command =
                            "tellraw @p [{\"text\":\"new Attributes Share\",\"color\":\"yellow\"," +
                                    "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jerotes other inventory_only_see " + uuid + " @p\"}}]";
                    for (Player player : list) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            if (player.level() instanceof ServerLevel serverLevel) {
                                playerEntity.sendSystemMessage(Component.translatable("message.jerotes.mob_inventory_send", playerEntity.getName(), livingEntity.getName()).withStyle(ChatFormatting.YELLOW));
                                serverLevel.getServer().getCommands().performPrefixedCommand(
                                        new CommandSourceStack(CommandSource.NULL, new Vec3(serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ()), Vec2.ZERO, serverLevel, 4, "", Component.literal(""), serverLevel.getServer(), null).withSuppressedOutput(),
                                        command );
                            }
                        }
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}