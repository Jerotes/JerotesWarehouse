package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import com.jerotes.jerotes.entity.TameMobEntity;

public class JerotesChangeSitWhenOrderedToGoal extends JerotesTameMobSitWhenOrderedToGoal {
    private final ChangePoseAbout tameMob;

    public JerotesChangeSitWhenOrderedToGoal(ChangePoseAbout tameMob) {
        super((TameMobEntity) tameMob);
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