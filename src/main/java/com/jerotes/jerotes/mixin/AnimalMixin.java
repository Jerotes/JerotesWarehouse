package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.CamelAbout;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalMixin extends AgeableMob {
    protected AnimalMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "canFallInLove", at = @At("HEAD"), cancellable = true)
    protected void canFallInLove(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof CamelAbout camelAbout && camelAbout.isJerotesCamelHusk())
            cir.setReturnValue(false);
    }
}