package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Other.SpellCloud.SpellCloudEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudMixin extends Entity implements TraceableEntity {
    public AreaEffectCloudMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    protected void tick(CallbackInfo ci) {
        if (((AreaEffectCloud)(Object)this) instanceof SpellCloudEntity) {
            super.tick();
            ci.cancel();
        }
    }

    @Inject(method = "onSyncedDataUpdated", at = @At("HEAD"), cancellable = true)
    protected void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor, CallbackInfo ci) {
        if (((AreaEffectCloud)(Object)this) instanceof SpellCloudEntity) {
            super.onSyncedDataUpdated(entityDataAccessor);
            ci.cancel();
        }
    }
}