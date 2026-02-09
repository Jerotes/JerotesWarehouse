package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherBoss.class)
public abstract class WitherBossMixin extends Monster {
    @Shadow public abstract int getInvulnerableTicks();

    @Shadow public abstract boolean isPowered();

    @Shadow private int destroyBlocksTick;

    @Shadow @Final private int[] idleHeadUpdates;

    protected WitherBossMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "hurt", at = @At(value = "HEAD"), cancellable = true)
    public void hurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (damageSource.getEntity() != null &&
                (EntityFactionFind.isThisEntity(damageSource.getEntity().getType(), "jerotes:can_hurt_wither") || EntityAndItemFind.isLegendary(damageSource.getEntity()))) {
            if (this.isInvulnerableTo(damageSource)) {
                cir.setReturnValue(false);
                cir.cancel();
            } else {
                if (this.getInvulnerableTicks() > 0 && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
                else {
                    Entity entity1;
                    if (this.isPowered()) {
                        entity1 = damageSource.getDirectEntity();
                        if (entity1 instanceof AbstractArrow) {
                            cir.setReturnValue(false);
                            cir.cancel();
                        }
                    }
                    if (this.destroyBlocksTick <= 0) {
                        this.destroyBlocksTick = 20;
                    }

                    for(int i = 0; i < this.idleHeadUpdates.length; ++i) {
                        int[] var10000 = this.idleHeadUpdates;
                        var10000[i] += 3;
                    }
                    cir.setReturnValue(super.hurt(damageSource, amount));
                    cir.cancel();
                }
            }
        }
    }
}