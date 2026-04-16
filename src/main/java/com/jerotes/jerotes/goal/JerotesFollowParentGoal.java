/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.TameMobEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;

import javax.annotation.Nullable;
import java.util.List;

public class JerotesFollowParentGoal extends Goal {
    private final Animal animal;
    @Nullable
    private Animal parent;
    private final double speedModifier;
    private int timeToRecalcPath;

    public JerotesFollowParentGoal(Animal animal, double d) {
        this.animal = animal;
        this.speedModifier = d;
    }

    @Override
    public boolean canUse() {
        if (this.animal.getAge() >= 0) {
            return false;
        }
        if (this.animal instanceof TamableAnimal tamableAnimal && tamableAnimal.isInSittingPose()) {
            return false;
        }
        if (this.animal instanceof TamableAnimal tamableAnimal && tamableAnimal.isOrderedToSit()) {
            return false;
        }
        if (this.animal instanceof TameMobEntity tamableAnimal && tamableAnimal.isInSittingPose()) {
            return false;
        }
        if (this.animal instanceof TameMobEntity tamableAnimal && tamableAnimal.isOrderedToSit()) {
            return false;
        }
        if (this.animal instanceof OwnableEntity ownable && ownable.getOwner() != null) {
            return false;
        }
        else {
            List<? extends Animal> list = this.animal.level().getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate(8.0D, 4.0D, 8.0D));
            Animal animal = null;
            double d0 = Double.MAX_VALUE;

            for(Animal animal1 : list) {
                if (animal1.getAge() >= 0 && !(animal1 instanceof TamableAnimal tamableAnimal && this.animal instanceof TamableAnimal tamableAnimalBaby && ((tamableAnimal.isTame() && !tamableAnimalBaby.isTame()) || (!tamableAnimal.isTame() && tamableAnimalBaby.isTame())))) {
                    double d1 = this.animal.distanceToSqr(animal1);
                    if (!(d1 > d0)) {
                        d0 = d1;
                        animal = animal1;
                    }
                }
            }

            if (animal == null) {
                return false;
            } else if (d0 < 9.0D) {
                return false;
            } else {
                this.parent = animal;
                return true;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.animal instanceof TamableAnimal tamableAnimal && tamableAnimal.isInSittingPose()) {
            return false;
        }
        if (this.animal.getAge() >= 0) {
            return false;
        }
        if (this.animal instanceof TamableAnimal tamableAnimal && tamableAnimal.isInSittingPose()) {
            return false;
        }
        if (this.animal instanceof TamableAnimal tamableAnimal && tamableAnimal.isOrderedToSit()) {
            return false;
        }
        if (this.animal instanceof TameMobEntity tamableAnimal && tamableAnimal.isInSittingPose()) {
            return false;
        }
        if (this.animal instanceof TameMobEntity tamableAnimal && tamableAnimal.isOrderedToSit()) {
            return false;
        }
        else if (!this.parent.isAlive()) {
            return false;
        }
        else {
            double d0 = this.animal.distanceToSqr(this.parent);
            return !(d0 < 9.0D) && !(d0 > 256.0D);
        }
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.parent = null;
    }

    @Override
    public void tick() {
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            this.animal.getNavigation().moveTo(this.parent, this.speedModifier);
        }
    }
}

