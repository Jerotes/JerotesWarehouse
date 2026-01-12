package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import net.minecraft.world.entity.TamableAnimal;

public class JerotesAnimalChangeSitWhenOrderedToGoal extends JerotesAnimalSitWhenOrderedToGoal {
    private final ChangePoseAbout tameMob;

    public JerotesAnimalChangeSitWhenOrderedToGoal(ChangePoseAbout tameMob) {
        super((TamableAnimal) tameMob);
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