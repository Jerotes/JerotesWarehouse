package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.ChangePoseAbout;
import net.minecraft.world.entity.TamableAnimal;

public class JerotesAnimalChangeFollowMobOwnerGoal extends JerotesAnimalFollowMobOwnerGoal {
    private final ChangePoseAbout tameMob;

    public JerotesAnimalChangeFollowMobOwnerGoal(ChangePoseAbout tameMob, double d) {
        super((TamableAnimal) tameMob, d);
        this.tameMob = tameMob;
    }

    public boolean canUse() {
        if (tameMob.isWander()) {
            return false;
        }
        else {
            return super.canUse();
        }
    }

    public boolean canContinueToUse() {
        if (tameMob.isWander()) {
            return false;
        }
        else {
            return super.canContinueToUse();
        }
    }
}
