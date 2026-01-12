package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import com.jerotes.jerotes.entity.TameMobEntity;

public class JerotesChangeFollowOwnerGoal extends JerotesTameMobFollowOwnerGoal {
    private final ChangePoseAbout tameMob;

    public JerotesChangeFollowOwnerGoal(ChangePoseAbout tameMob,  double d, float f, float f2, boolean bl) {
        super((TameMobEntity) tameMob, d, f, f2, bl);
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

