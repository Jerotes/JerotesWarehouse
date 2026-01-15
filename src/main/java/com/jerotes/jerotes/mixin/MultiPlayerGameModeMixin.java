package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.item.Interface.ItemSpecialAttack;
import com.jerotes.jerotes.item.Interface.SpearBaseItem;
import com.jerotes.jerotes.network.JerotesSpearAttackPacket;
import com.jerotes.jerotes.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin implements SpearBaseItem {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void ensureHasSentCarriedItem();

    @Shadow @Final private ClientPacketListener connection;

    public void jerotesSpearPiercingAttack(Player player, ItemSpecialAttack piercingWeapon) {
        this.ensureHasSentCarriedItem();
        if (this.minecraft.player != null) {
            piercingWeapon.jerotesSpecialAttackClient(this.minecraft.player);
            PacketHandler.sendToServer(new JerotesSpearAttackPacket(this.minecraft.player.getId()));
        }
        // this.connection.send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, BlockPos.ZERO, Direction.DOWN));
    }

}