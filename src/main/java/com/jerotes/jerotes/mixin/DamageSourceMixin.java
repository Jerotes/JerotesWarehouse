package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Mob.AddHandEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {
    @Mutable
    @Shadow
    @Final
    @Nullable
    private Entity causingEntity;

    @Inject(method = "<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
    protected void inject(Holder<DamageType> p_270906_, @Nullable Entity p_270796_, @Nullable Entity p_270459_, @Nullable Vec3 p_270623_, CallbackInfo ci) {
        if (p_270459_ instanceof AddHandEntity addHandEntity && addHandEntity.getOwner() != null) {
            this.causingEntity = addHandEntity.getOwner();
        }
    }
}