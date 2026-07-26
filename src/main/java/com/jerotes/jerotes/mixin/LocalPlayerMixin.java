package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow public Input input;
    @Shadow protected int sprintTriggerTime;

    @Shadow
    protected abstract boolean hasEnoughImpulseToStartSprinting();

    @Shadow
    protected abstract boolean vehicleCanSprint(Entity p_265184_);

    @Shadow
    protected abstract boolean hasEnoughFoodToStartSprinting();

    @Shadow
    @Final
    protected Minecraft minecraft;

    @Unique
    protected boolean canStartSprintingBaseJerotes() {
        return !this.isSprinting() && this.hasEnoughImpulseToStartSprinting() && this.hasEnoughFoodToStartSprinting() && !this.hasEffect(MobEffects.BLINDNESS) && (!this.isPassenger() || this.vehicleCanSprint(this.getVehicle())) && !this.isFallFlying();
    }

    @Unique
    public float leftImpulseBaseJerotes;
    @Unique
    public float forwardImpulseBaseJerotes;
    @Unique
    protected int sprintTriggerTimeBaseJerotes;

    public LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
        super(p_250460_, p_249912_);
    }

    @Inject(method = "canStartSprinting", at = @At(value = "HEAD"), cancellable = true)
    public void canStartSprinting(CallbackInfoReturnable<Boolean> cir) {
        if (this.getUseItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear()) {
            cir.setReturnValue(canStartSprintingBaseJerotes());
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/Input;)V",
            shift = At.Shift.BEFORE))
    public void aiStepI(CallbackInfo ci) {
        this.sprintTriggerTimeBaseJerotes = this.sprintTriggerTime;
        this.leftImpulseBaseJerotes = this.input.leftImpulse;
        this.forwardImpulseBaseJerotes = this.input.forwardImpulse;
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;canStartSprinting()Z",
            shift = At.Shift.BEFORE))
    public void aiStepII(CallbackInfo ci) {
        if (this.getUseItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear()) {
            this.input.leftImpulse = leftImpulseBaseJerotes;
            this.input.forwardImpulse = forwardImpulseBaseJerotes;
            this.sprintTriggerTime = sprintTriggerTimeBaseJerotes;
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z",
            ordinal = 1,
            shift = At.Shift.AFTER))
    public void aiStepIII(CallbackInfo ci) {
        if (this.getUseItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear()) {
            if (!this.isSprinting() && (!(this.isInWater() || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType))) || (this.isUnderWater() || this.canStartSwimming())) && this.hasEnoughImpulseToStartSprinting() && this.canStartSprintingBaseJerotes() && !this.hasEffect(MobEffects.BLINDNESS) && this.minecraft.options.keySprint.isDown())
                this.setSprinting(true);
        }
    }
}