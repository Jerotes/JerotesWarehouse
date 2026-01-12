package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.ChangePoseAbout;
import com.jerotes.jerotes.entity.JerotesEntity;
import com.jerotes.jerotes.entity.TameMobEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class JerotesTameMobOwnerHurtTargetGoal extends TargetGoal {
    private final TameMobEntity tameMob;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public JerotesTameMobOwnerHurtTargetGoal(TameMobEntity tameMob) {
        super((Mob)tameMob, false);
        this.tameMob = tameMob;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!this.tameMob.isTame() || this.tameMob.isOrderedToSit() && !(this.tameMob instanceof ChangePoseAbout changePoseAbout && changePoseAbout.isWander())) {
            return false;
        }
        if (this.tameMob instanceof JerotesEntity jerotes && !jerotes.OwnerCanOrderAttack()) {
            return false;
        }
        LivingEntity livingEntity = ((OwnableEntity)this.tameMob).getOwner();
        if (livingEntity == null) {
            return false;
        }
        this.ownerLastHurt = livingEntity.getLastHurtMob();
        int n = livingEntity.getLastHurtMobTimestamp();
        return n != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tameMob.wantsToAttack(this.ownerLastHurt, livingEntity);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurt);
        LivingEntity livingEntity = ((OwnableEntity)this.tameMob).getOwner();
        if (livingEntity != null) {
            this.timestamp = livingEntity.getLastHurtMobTimestamp();
        }
        super.start();
    }
}

