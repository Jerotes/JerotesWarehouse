package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesChangeStray;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedBowAttackGoal.class)
public abstract class RangedBowAttackGoalMixin<T extends net.minecraft.world.entity.Mob & RangedAttackMob> extends Goal {

    @Shadow
    @Final
    private T mob;

    @Shadow
    private int attackIntervalMin;

    @Inject(method = "tick", at = @At("HEAD"))
    protected void tick(CallbackInfo ci) {
        if (this.mob instanceof JerotesChangeStray jerotesChangeStray && jerotesChangeStray.isJerotesParched()) {
            this.attackIntervalMin = 50;
        }
    }
}