package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import com.jerotes.jerotes.entity.TameMobEntity;

public class JerotesChangeFollowMobOwnerGoal extends JerotesTameMobFollowMobOwnerGoal {
    private final ChangePoseAbout tameMob;

    public JerotesChangeFollowMobOwnerGoal(ChangePoseAbout tameMob,  double d) {
        super((TameMobEntity) tameMob, d);
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
