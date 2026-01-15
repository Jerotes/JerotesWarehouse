package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.ChangePoseAbout;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.Animal;

public class JerotesAnimalChangeFollowParentGoal extends JerotesFollowParentGoal {
    private final ChangePoseAbout tameMob;

    public JerotesAnimalChangeFollowParentGoal(ChangePoseAbout tameMob, double d) {
        super((Animal) tameMob, d);
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
