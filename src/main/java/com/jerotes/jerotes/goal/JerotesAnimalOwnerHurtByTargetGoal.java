/*
 * Decompiled with CFR 0.146.
 */
package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.ChangePoseAbout;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class JerotesAnimalOwnerHurtByTargetGoal
extends TargetGoal {
    private final TamableAnimal tameAnimal;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public JerotesAnimalOwnerHurtByTargetGoal(TamableAnimal tamableAnimal) {
        super(tamableAnimal, false);
        this.tameAnimal = tamableAnimal;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!this.tameAnimal.isTame() || this.tameAnimal.isOrderedToSit() && !(this.tameAnimal instanceof ChangePoseAbout changePoseAbout && changePoseAbout.isWander())) {
            return false;
        }
        if (this.tameAnimal instanceof JerotesEntity jerotes && !jerotes.OwnerCanOrderAttack()) {
            return false;
        }
        LivingEntity livingEntity = ((OwnableEntity)this.tameAnimal).getOwner();
        if (livingEntity == null) {
            return false;
        }
        this.ownerLastHurtBy = livingEntity.getLastHurtByMob();
        int n = livingEntity.getLastHurtByMobTimestamp();
        return n != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tameAnimal.wantsToAttack(this.ownerLastHurtBy, livingEntity);
    }

    @Override
    public boolean canContinueToUse() {
        if (!this.tameAnimal.isTame() || this.tameAnimal.isOrderedToSit() && !(this.tameAnimal instanceof ChangePoseAbout changePoseAbout && changePoseAbout.isWander())) {
            return false;
        }
        if (this.tameAnimal instanceof JerotesEntity jerotes && !jerotes.OwnerCanOrderAttack()) {
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        LivingEntity livingEntity = this.tameAnimal.getOwner();
        if (livingEntity != null) {
            this.timestamp = livingEntity.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}

