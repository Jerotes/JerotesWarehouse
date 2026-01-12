package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhaseManager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderDragon.class)
public abstract class EnderDragonMixin extends Mob {
    @Shadow @Final private EnderDragonPhaseManager phaseManager;

    @Shadow @Final public EnderDragonPart head;

    @Shadow protected abstract boolean reallyHurt(DamageSource p_31162_, float p_31163_);

    @Shadow private float sittingDamageReceived;

    protected EnderDragonMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt(Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), cancellable = true)
    public void hurt(EnderDragonPart enderDragonPart, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (damageSource.getEntity() != null && EntityFactionFind.isThisEntity(damageSource.getEntity().getType(), "jerotes:can_hurt_ender_dragon")) {
            if (this.phaseManager.getCurrentPhase().getPhase() == EnderDragonPhase.DYING) {
                cir.setReturnValue(false);
            } else {
                amount = this.phaseManager.getCurrentPhase().onHurt(damageSource, amount);
                if (enderDragonPart != this.head) {
                    amount = amount / 4.0F + Math.min(amount, 1.0F);
                }
                if (amount < 0.01F) {
                    cir.setReturnValue(false);
                } else {
                    float f = this.getHealth();
                    this.reallyHurt(damageSource, amount);
                    if (this.isDeadOrDying() && !this.phaseManager.getCurrentPhase().isSitting()) {
                        this.setHealth(1.0F);
                        this.phaseManager.setPhase(EnderDragonPhase.DYING);
                    }

                    if (this.phaseManager.getCurrentPhase().isSitting()) {
                        this.sittingDamageReceived = this.sittingDamageReceived + f - this.getHealth();
                        if (this.sittingDamageReceived > 0.25F * this.getMaxHealth()) {
                            this.sittingDamageReceived = 0.0F;
                            this.phaseManager.setPhase(EnderDragonPhase.TAKEOFF);
                        }
                    }
                    cir.setReturnValue(true);
                }
            }
        }
    }
}