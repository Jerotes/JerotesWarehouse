package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.CamelAbout;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AnimalPanic.class)
public abstract class AnimalPanicMixin extends Behavior<PathfinderMob> {

    public AnimalPanicMixin(Map<MemoryModuleType<?>, MemoryStatus> p_22528_) {
        super(p_22528_);
    }

    @Inject(method = "checkExtraStartConditions*", at = @At("HEAD"), cancellable = true)
    private void checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob, CallbackInfoReturnable<Boolean> cir) {
        if (pathfinderMob instanceof CamelAbout camelAbout && camelAbout.isJerotesCamelHusk()) {
            cir.setReturnValue(false);
        }
    }

}