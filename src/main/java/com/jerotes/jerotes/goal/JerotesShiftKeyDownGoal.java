package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.ShiftKeyDownEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class JerotesShiftKeyDownGoal extends Goal {
    private final Mob mob;
    private float speed;

    public JerotesShiftKeyDownGoal(Mob mob, float f) {
        this.mob = mob;
        this.speed = f;
    }
    public JerotesShiftKeyDownGoal(Mob mob) {
        this(mob, 0.5f);
    }

    @Override
    public boolean canUse() {
        if (!(this.mob instanceof ShiftKeyDownEntity shouldShiftKeyDown)) {
            return false;
        }
        return shouldShiftKeyDown.shouldShiftKeyDown();
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        if (!this.mob.level().isClientSide()) {
            this.mob.setShiftKeyDown(true);
        }
    }

    @Override
    public void stop() {
        if (!this.mob.level().isClientSide()) {
            this.mob.setShiftKeyDown(false);
        }
    }

    @Override
    public void tick() {
        this.mob.getNavigation().setSpeedModifier(speed);
    }
}

