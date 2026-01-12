package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;

public class JerotesAnimalChangeHelpMobOwnerGoal extends JerotesHelpMobOwnerGoal {
    private final ChangePoseAbout tameMob;

    public JerotesAnimalChangeHelpMobOwnerGoal(ChangePoseAbout tameMob) {
        super((TamableAnimal) tameMob, LivingEntity.class, false, false, livingEntity -> livingEntity instanceof LivingEntity);
        this.tameMob = tameMob;
    }

    public boolean canUse() {
        if (tameMob instanceof OwnableEntity ownable && ownable.getOwner() != null && tameMob.getChangeType() == 1) {
            return false;
        }
        else {
            return super.canUse();
        }
    }

    public boolean canContinueToUse() {
        if (tameMob instanceof OwnableEntity ownable && ownable.getOwner() != null && tameMob.getChangeType() == 1) {
            return false;
        }
        else {
            return super.canContinueToUse();
        }
    }
}
