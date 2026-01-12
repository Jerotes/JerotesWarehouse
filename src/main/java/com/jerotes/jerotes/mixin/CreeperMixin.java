package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.SuperDeathCreeperEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster {
    protected CreeperMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "dropCustomDeathLoot", at = @At(value = "HEAD"), cancellable = true)
    public void dropCustomDeathLoot(DamageSource damageSource, int n, boolean bl, CallbackInfo ci) {
        if (this instanceof SuperDeathCreeperEntity) {
            super.dropCustomDeathLoot(damageSource, n, bl);
            ci.cancel();
        }
    }
}