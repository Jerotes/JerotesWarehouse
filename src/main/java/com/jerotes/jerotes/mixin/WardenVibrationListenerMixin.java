package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VibrationSystem.Listener.class)
public abstract class WardenVibrationListenerMixin {
    @Shadow
    @Final
    private VibrationSystem system;

    @Inject(method = "handleGameEvent", at = @At("HEAD"), cancellable = true)
    private void jerotes$deafenedWardenIgnoresVibration(ServerLevel level, GameEvent gameEvent, GameEvent.Context context, Vec3 pos, CallbackInfoReturnable<Boolean> cir) {
        if (this.system instanceof Warden warden && warden.hasEffect(JerotesMobEffects.DEAFENED.get())) {
            cir.setReturnValue(false);
        }
    }
}
