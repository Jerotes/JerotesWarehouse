
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.entity.ControlVehicleEntity;
import com.jerotes.jerotes.spell.*;
import com.jerotes.jerotes.util.Main;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ControlVehicleMessage {
    int type, pressedms;
    int livingEntity;
    int isAdd;

    public ControlVehicleMessage(int type, int pressedms, int livingEntity, int isAdd) {
        this.type = type;
        this.pressedms = pressedms;
        this.livingEntity = livingEntity;
        this.isAdd = isAdd;
    }

    public ControlVehicleMessage(FriendlyByteBuf buffer) {
        this.type = buffer.readInt();
        this.pressedms = buffer.readInt();
        this.livingEntity = buffer.readInt();
        this.isAdd = buffer.readInt();
    }

    public static void encode(ControlVehicleMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.type);
        buffer.writeInt(message.pressedms);
        buffer.writeInt(message.livingEntity);
        buffer.writeInt(message.isAdd);
    }

    public static ControlVehicleMessage decode(FriendlyByteBuf buffer) {
        return new ControlVehicleMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void consume(ControlVehicleMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer playerEntity = context.get().getSender();
            if (playerEntity != null) {
                Entity entity = playerEntity.level().getEntity(message.livingEntity);
                pressAction(context.get().getSender(), message.type, message.pressedms, message.livingEntity, message.isAdd);
            }
        });
        context.get().setPacketHandled(true);
    }

    public static void pressAction(Player player, int type, int pressedms, int entity, int isAdd) {
        Level level = player.level();
        if (!level.hasChunkAt(player.blockPosition()))
            return;
        Entity entitys = player.level().getEntity(entity);
        if (!(entitys instanceof ControlVehicleEntity controlVehicleEntity))
            return;
        if (type == 0) {
            if (!controlVehicleEntity.canBeControl(player))
                controlVehicleEntity.setManuallyControlCombat(false);
            //主
            else if (isAdd == 0) {
                controlVehicleEntity.pressMain(player);
            }
            //次
            else if (isAdd == 1) {
                controlVehicleEntity.pressAdd(player);
            }
            //切换
            else {
                controlVehicleEntity.setManuallyControlCombat(!controlVehicleEntity.isManuallyControlCombat());
            }
        }
    }
}
