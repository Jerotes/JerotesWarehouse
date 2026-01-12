package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Mob.JerotesPlayerEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraftforge.common.ForgeMod;

import java.util.EnumSet;

public class JerotesPlayerFloatGoal extends Goal {
    private final JerotesPlayerEntity mob;

    public JerotesPlayerFloatGoal(JerotesPlayerEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.JUMP));
        mob.getNavigation().setCanFloat(true);
    }

    public void stop() {
        this.mob.setJumping(false);
    }
    @Override
    public boolean canUse() {
        return this.mob.isInFluidType();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.mob.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
            if (this.mob.getAirSupply() <= 100) {
                this.mob.setJumping(true);
                this.mob.setSprinting(true);
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.02, 0.0));
            }
        }
    }
}

