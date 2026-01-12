package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.JerotesEntity;
import com.jerotes.jerotes.entity.TameMobEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class JerotesTameMobAnimalOwnerHurtByTargetGoal extends TargetGoal {
    private final TameMobEntity tameMob;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;
    public JerotesTameMobAnimalOwnerHurtByTargetGoal(TameMobEntity tameMob) {
        super((Mob)tameMob, false);
        this.tameMob = tameMob;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!this.tameMob.isTame() || this.tameMob.isOrderedToSit()) {
            return false;
        }
        if (this.tameMob instanceof JerotesEntity jerotes && !jerotes.OwnerCanOrderAttack()) {
            return false;
        }
        LivingEntity livingEntity = ((OwnableEntity)this.tameMob).getOwner();
        if (livingEntity == null) {
            return false;
        }
        this.ownerLastHurtBy = livingEntity.getLastHurtByMob();
        int n = livingEntity.getLastHurtByMobTimestamp();
        return n != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameMob.wantsToAttack(this.ownerLastHurtBy, livingEntity);
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.tameMob.isTame() || this.tameMob.isOrderedToSit()) {
            return false;
        }
        if (this.tameMob instanceof JerotesEntity jerotes && !jerotes.OwnerCanOrderAttack()) {
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingEntity = ((OwnableEntity)this.tameMob).getOwner();
        if (livingEntity != null) {
            this.timestamp = livingEntity.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}

