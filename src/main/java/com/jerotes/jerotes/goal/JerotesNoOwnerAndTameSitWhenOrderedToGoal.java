package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.JerotesEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class JerotesNoOwnerAndTameSitWhenOrderedToGoal extends Goal {
    private final TamableAnimal mob;

    public JerotesNoOwnerAndTameSitWhenOrderedToGoal(TamableAnimal tamableAnimal) {
        this.mob = tamableAnimal;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isOrderedToSit();
    }

    @Override
    public boolean canUse() {
        if (this.mob.getOwner() == null) {
            return false;
        }
        if (!this.mob.isTame()) {
            return false;
        }
        if (this.mob.isInWaterOrBubble()) {
            return false;
        }
        if (!this.mob.onGround() && !(this.mob instanceof JerotesEntity jerotes && jerotes.isJerotesFlyingMob())) {
            return false;
        }
        LivingEntity livingEntity = this.mob.getOwner();
        if (livingEntity == null) {
            return true;
        }
        if (this.mob.distanceToSqr(livingEntity) < 144.0 && livingEntity.getLastHurtByMob() != null) {
            return false;
        }
        return this.mob.isOrderedToSit();
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setInSittingPose(true);
    }

    @Override
    public void stop() {
        this.mob.setInSittingPose(false);
    }
}

