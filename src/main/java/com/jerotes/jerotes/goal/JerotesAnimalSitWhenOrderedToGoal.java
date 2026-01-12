package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.JerotesEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class JerotesAnimalSitWhenOrderedToGoal extends Goal {
    private final TamableAnimal mob;

    public JerotesAnimalSitWhenOrderedToGoal(TamableAnimal tameMob) {
        this.mob = tameMob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isOrderedToSit();
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isTame()) {
            return false;
        }
        if (!(((OwnableEntity)this.mob).getOwner() instanceof Player)) {
            return false;
        }
        if (((Mob)this.mob).isInWaterOrBubble()) {
            return false;
        }
        if (!((Mob)this.mob).onGround() && !(this.mob instanceof JerotesEntity jerotes && jerotes.isJerotesFlyingMob())) {
            return false;
        }
        LivingEntity livingEntity = ((OwnableEntity)this.mob).getOwner();
        if (livingEntity == null) {
            return true;
        }
        if (((Mob)this.mob).distanceToSqr(livingEntity) < 144.0 && livingEntity.getLastHurtByMob() != null) {
            return false;
        }
        return this.mob.isOrderedToSit();
    }

    @Override
    public void start() {
        ((Mob)this.mob).getNavigation().stop();
        this.mob.setInSittingPose(true);
    }

    @Override
    public void stop() {
        this.mob.setInSittingPose(false);
    }
}

