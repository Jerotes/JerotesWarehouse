package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.ControlVehicleEntity;
import com.jerotes.jerotes.init.JerotesKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow
    private ClientLevel level;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "handleSetEntityPassengersPacket", at = @At("TAIL"))
    protected void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket clientboundSetPassengersPacket, CallbackInfo ci) {
        Entity entity = this.level.getEntity(clientboundSetPassengersPacket.getVehicle());
        if (entity instanceof ControlVehicleEntity controlVehicleEntity) {
            for (int i : clientboundSetPassengersPacket.getPassengers()) {
                if (this.level.getEntity(i) == this.minecraft.player) {
                    Component component = Component.translatable("message.jerotes.change_control_combat_type", JerotesKeyMappings.CHANGE_CONTROL_COMBAT_TYPE.getTranslatedKeyMessage());
                    this.minecraft.gui.setOverlayMessage(component, false);
                    this.minecraft.getNarrator().sayNow(component);
                }
            }
        }
    }
}