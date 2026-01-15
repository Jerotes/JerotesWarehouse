package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.ChangePoseAbout;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;

public class JerotesChangeHelpMobOwnerGoal extends JerotesHelpMobOwnerGoal {
    private final ChangePoseAbout tameMob;

    public JerotesChangeHelpMobOwnerGoal(ChangePoseAbout tameMob) {
        super((Mob) tameMob, LivingEntity.class, false, false, livingEntity -> livingEntity instanceof LivingEntity);
        this.tameMob = tameMob;
    }

    public boolean canUse() {
        if (!((tameMob instanceof OwnableEntity ownable && ownable.getOwner() != null) && tameMob.getChangeType() != 1)) {
            return false;
        }
        else {
            return super.canUse();
        }
    }

    public boolean canContinueToUse() {
        if (!((tameMob instanceof OwnableEntity ownable && ownable.getOwner() != null) && tameMob.getChangeType() != 1)) {
            return false;
        }
        else {
            return super.canContinueToUse();
        }
    }
}
