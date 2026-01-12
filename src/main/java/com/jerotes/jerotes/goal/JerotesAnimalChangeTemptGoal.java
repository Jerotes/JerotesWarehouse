package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.crafting.Ingredient;

public class JerotesAnimalChangeTemptGoal extends TemptGoal {
    private final ChangePoseAbout tameMob;

    public JerotesAnimalChangeTemptGoal(ChangePoseAbout tameMob, double d, Ingredient ingredient, boolean bl) {
        super((Animal) tameMob, d, ingredient, bl);
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
