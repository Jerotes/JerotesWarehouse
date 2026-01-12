package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;

public class JerotesAnimalChangeOwnerHurtTargetGoal extends JerotesAnimalOwnerHurtTargetGoal {
    private final ChangePoseAbout tameMob;

    public JerotesAnimalChangeOwnerHurtTargetGoal(ChangePoseAbout tameMob) {
        super((TamableAnimal) tameMob);
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
