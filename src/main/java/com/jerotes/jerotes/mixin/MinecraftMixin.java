package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.ControlVehicleEntity;
import com.jerotes.jerotes.item.ItemSpecialAttack;
import com.jerotes.jerotes.item.SpearBaseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> {

    @Shadow @Nullable public HitResult hitResult;

    @Shadow protected int missTime;

    @Shadow @Nullable public MultiPlayerGameMode gameMode;

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Nullable public ClientLevel level;

    @Shadow private static Minecraft instance;

    public MinecraftMixin(String p_18765_) {
        super(p_18765_);
    }

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void startAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player != null && player.getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity && controlVehicleEntity.isManuallyControlCombat()) {
            cir.setReturnValue(false);
            return;
        }
        if (this.missTime > 0) {
            return;
        }
        if (this.hitResult == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.gameMode.hasMissTime()) {
                this.missTime = 10;
            }
            return;
        }
        if (this.player.isHandsBusy()) {
            return;
        }
        ItemStack itemStack = this.player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemStack.isItemEnabled(this.level.enabledFeatures())) {
            return;
        }
        if (itemStack.getItem() instanceof ItemSpecialAttack itemToolBaseSpearBase) {
            if (player.getAttackStrengthScale(0) < 1) {
                cir.setReturnValue(false);
                return;
            }
            if (this.gameMode instanceof SpearBaseItem spearBaseItem) {
                spearBaseItem.jerotesSpearPiercingAttack(player, itemToolBaseSpearBase);
            }
            this.player.swing(InteractionHand.MAIN_HAND);
            cir.setReturnValue(true);
        }
    }

}