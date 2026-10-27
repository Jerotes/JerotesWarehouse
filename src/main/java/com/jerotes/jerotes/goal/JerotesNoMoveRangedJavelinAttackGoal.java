package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.UseThrownJavelinEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class JerotesNoMoveRangedJavelinAttackGoal<T extends Mob & UseThrownJavelinEntity> extends Goal {
    private final Mob mob;
    public final RangedAttackMob rangedAttackMob;
    @Nullable
    public LivingEntity target;
    public int attackTime = -1;
    public int seeTime;
    public final int attackIntervalMin;
    public final int attackIntervalMax;
    public final float attackRadius;
    public final float reach;

    public JerotesNoMoveRangedJavelinAttackGoal(RangedAttackMob rangedAttackMob, int n, float f, float reach) {
        this(rangedAttackMob, n, n, f, reach);
    }

    public JerotesNoMoveRangedJavelinAttackGoal(RangedAttackMob rangedAttackMob, int n, int n2, float f, float reach) {
        if (!(rangedAttackMob instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttackMob = rangedAttackMob;
        this.mob = (Mob)rangedAttackMob;
        this.attackIntervalMin = n;
        this.attackIntervalMax = n2;
        this.attackRadius = f;
        this.reach = reach;
    }

    public boolean isThrowImminent() {
        return this.attackTime > 0 && this.attackTime <= 10;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        if (!JerotesRangedJavelinAttackGoal.isHoldingRangedJavelin(this.mob, this.reach)) {
            return false;
        }
        this.target = livingEntity;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive()
                && this.mob.getTarget() == this.target
                && JerotesRangedJavelinAttackGoal.isHoldingRangedJavelin(this.mob, this.reach);
    }

    @Override
    public void start() {
        this.seeTime = 0;
        this.attackTime = -1;
    }

    @Override
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.stopUsingItem();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }
        ItemStack handItem = this.mob.getMainHandItem();
        if ((this.mob.getMainHandItem().isEmpty()
                || InventoryEntity.isMeleeWeapon(this.mob.getMainHandItem()) && !InventoryEntity.isRangeJavelin(this.mob.getMainHandItem()))
                && !this.mob.getOffhandItem().isEmpty()
                && InventoryEntity.isRangeJavelin(this.mob.getOffhandItem())) {
            handItem = this.mob.getOffhandItem();
        }
        double d = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean bl = this.mob.getSensing().hasLineOfSight(this.target);
        this.seeTime = bl ? ++this.seeTime : 0;
        this.mob.getLookControl().setLookAt(this.target, 360.0f, 360.0f);
        if (--this.attackTime == 0) {
            if (!bl) {
                return;
            }
            float f = (float)Math.sqrt(d) / this.attackRadius;
            float f2 = Mth.clamp(f, 0.1f, 1.0f);
            this.rangedAttackMob.performRangedAttack(this.target, f2);
            if (!(this.mob instanceof UseThrownJavelinEntity useThrownJavelinEntity && !useThrownJavelinEntity.UseThrownJavelinEntityStopUse())) {
                this.mob.stopUsingItem();
            }
            this.attackTime = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
        }
        else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d) / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
        }
        float f = (float)Math.sqrt(d) / this.attackRadius;
        int full = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
        if (this.attackTime < full - 10
                && !(this.mob instanceof UseThrownJavelinEntity useThrownJavelinEntity && !useThrownJavelinEntity.UseThrownJavelinEntityStopUse())) {
            if (!this.mob.isUsingItem()) {
                if (handItem == this.mob.getMainHandItem()) {
                    this.mob.startUsingItem(InteractionHand.MAIN_HAND);
                }
                else {
                    this.mob.startUsingItem(InteractionHand.OFF_HAND);
                }
            }
            if (this.mob.getUseItem() != handItem) {
                this.mob.stopUsingItem();
                if (handItem == this.mob.getMainHandItem()) {
                    this.mob.startUsingItem(InteractionHand.MAIN_HAND);
                }
                else {
                    this.mob.startUsingItem(InteractionHand.OFF_HAND);
                }
            }
        }
    }
}
