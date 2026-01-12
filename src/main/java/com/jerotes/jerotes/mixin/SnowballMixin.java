package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public abstract class SnowballMixin extends ThrowableItemProjectile {
    public SnowballMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void onHitEntity(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (EntityAndItemFind.isSnowballCanAttack(entityHitResult.getEntity().getType())) {
            entityHitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), (float)3);
        }
    }
}