package com.jerotes.jerotes.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

import javax.annotation.Nullable;
import java.util.List;

public class JerotesAnimalFollowMobOwnerGoal extends Goal {
    private final TamableAnimal tameMob;
    @Nullable
    private Mob owner;
    private final double speedModifier;
    private int timeToRecalcPath;

    public JerotesAnimalFollowMobOwnerGoal(TamableAnimal tameMob, double d) {
        this.tameMob = tameMob;
        this.speedModifier = d;
    }

    @Override
    public boolean canUse() {
        if (this.tameMob.getTarget() != null) {
            return false;
        }
        List<Mob> list = ((Mob)tameMob).level().getEntitiesOfClass(Mob.class,  ((Mob)tameMob).getBoundingBox().inflate(64.0, 64.0, 64.0));
        Mob owners = null;
        double d = Double.MAX_VALUE;
        for (Mob owner : list) {
            double d2;
            if ((d2 = ((Mob)tameMob).distanceToSqr(owner)) > d) continue;
            if (((OwnableEntity)tameMob).getOwnerUUID() != owner.getUUID()) continue;
            d = d2;
            owners = owner;
        }
        if (owners == null) {
            return false;
        }
        if (d < 64.0) {
            return false;
        }
        this.owner = (Mob) owners;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.tameMob.getTarget() != null) {
            return false;
        }
        if (!this.owner.isAlive()) {
            return false;
        }
        if (((TamableAnimal)tameMob).isTame()) {
            return false;
        }
        if (((OwnableEntity)tameMob).getOwnerUUID() != owner.getUUID()) {
            return false;
        }
        if (!(this.owner instanceof Mob)) {
            return false;
        }
        if (((Mob)tameMob).getTarget() != null) {
            return false;
        }
        double d = ((Mob)tameMob).distanceToSqr(this.owner);
        return !(d < 18.0) && !(d > 1024.0);
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.owner = null;
    }

    @Override
    public void tick() {
        if (this.owner != null) {
            if (--this.timeToRecalcPath > 0) {
                return;
            }
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            ((Mob)tameMob).getNavigation().moveTo(this.owner, this.speedModifier);
        }
    }
}

