package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Warden.class)
public abstract class WardenMixin {

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void jerotes$deafenedLoseTarget(CallbackInfo ci) {
        Warden self = (Warden) (Object) this;
        if (self.hasEffect(JerotesMobEffects.DEAFENED.get()) && self.getTarget() != null) {
            self.setTarget(null);
        }
    }
}
