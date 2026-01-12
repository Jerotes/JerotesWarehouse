package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import net.minecraft.world.entity.TamableAnimal;

public class JerotesAnimalChangeFollowOwnerGoal extends JerotesFollowOwnerGoal {
    private final ChangePoseAbout tameMob;

    public JerotesAnimalChangeFollowOwnerGoal(ChangePoseAbout tameMob, double d, float f, float f2, boolean bl) {
        super((TamableAnimal) tameMob, d, f, f2, bl);
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

