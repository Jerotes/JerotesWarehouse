package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeLivingEntity;
import com.jerotes.jerotes.entity.Interface.SpecialProneEntity;
import com.jerotes.jerotes.util.Main;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements SpecialProneEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
    @Override
    public void specialProneTick() {
    }
    @Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    private void updatePlayerPose(CallbackInfo ci) {
        if (this instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity && jerotesChangeLivingEntity.getProneTickJerotes() > 0) {
            if (this.canEnterPose(Pose.SWIMMING)) {
                this.setPose(Pose.SWIMMING);
                ci.cancel();
            }
        }
    }
    @Inject(method = "isScoping", at = @At("HEAD"), cancellable = true)
    private void isScoping(CallbackInfoReturnable<Boolean> cir) {
        if (Main.isOtherScoping(this)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "stopSleepInBed", at = @At("HEAD"))
    private void stopSleepInBed(boolean bl, boolean bl2, CallbackInfo ci) {
        if (this instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity) {
            jerotesChangeLivingEntity.setMaxHealthReduceCountJerotes(0);
        }
    }
}