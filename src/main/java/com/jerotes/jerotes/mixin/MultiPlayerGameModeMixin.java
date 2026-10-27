package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.item.Interface.ItemSpecialAttack;
import com.jerotes.jerotes.item.Interface.SpearBaseItem;
import com.jerotes.jerotes.network.JerotesPlayerOtherItemAboutPacket;
import com.jerotes.jerotes.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin implements SpearBaseItem {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void ensureHasSentCarriedItem();

    @Shadow @Final private ClientPacketListener connection;

    @Inject(method = "releaseUsingItem(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private void jerotes$syncPlayerRotationOnRelease(Player player, CallbackInfo ci) {
        if (player instanceof LocalPlayer localPlayer && localPlayer.connection != null) {
            localPlayer.connection.send(new ServerboundMovePlayerPacket.Rot(localPlayer.getYRot(), localPlayer.getXRot(), localPlayer.onGround()));
        }
    }

    public void jerotesSpearPiercingAttack(Player player, ItemSpecialAttack piercingWeapon) {
        this.ensureHasSentCarriedItem();
        if (this.minecraft.player != null) {
            piercingWeapon.jerotesSpecialAttackClient(this.minecraft.player);
            PacketHandler.sendToServer(new JerotesPlayerOtherItemAboutPacket(this.minecraft.player.getId(), 2));
        }
        // this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, BlockPos.ZERO, Direction.DOWN));
    }

}