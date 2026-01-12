package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import com.jerotes.jerotes.entity.TameMobEntity;
import net.minecraft.world.entity.OwnableEntity;

public class JerotesChangeOwnerHurtByTargetGoal extends JerotesTameMobOwnerHurtByTargetGoal {
    private final ChangePoseAbout tameMob;

    public JerotesChangeOwnerHurtByTargetGoal(ChangePoseAbout tameMob) {
        super((TameMobEntity) tameMob);
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
