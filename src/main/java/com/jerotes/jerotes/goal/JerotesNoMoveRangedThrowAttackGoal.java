package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.UseThrowEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ThrowablePotionItem;

import javax.annotation.Nullable;

public class JerotesNoMoveRangedThrowAttackGoal<T extends Mob & UseThrowEntity> extends Goal {
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

    public JerotesNoMoveRangedThrowAttackGoal(RangedAttackMob rangedAttackMob, int n, float f) {
        this(rangedAttackMob, n, n, f);
    }

    public JerotesNoMoveRangedThrowAttackGoal(RangedAttackMob rangedAttackMob, int n, int n2, float f) {
        if (!(rangedAttackMob instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttackMob = rangedAttackMob;
        this.mob = (Mob)rangedAttackMob;
        this.attackIntervalMin = n;
        this.attackIntervalMax = n2;
        this.attackRadius = f;
        float reach = 8;
        if (this.mob instanceof InventoryEntity inventoryEntity) {
            reach = inventoryEntity.meleeOrRangeDistance();
        }
        this.reach = reach;
    }

    public boolean isThrowImminent() {
        return this.attackTime > 0 && this.attackTime <= 10;
    }

    public static boolean isTargetFar(Mob mob) {
        LivingEntity livingEntity = mob.getTarget();
        if (livingEntity == null) {
            return false;
        }
        float reach = 8;
        if (mob instanceof InventoryEntity inventoryEntity) {
            reach = inventoryEntity.meleeOrRangeDistance();
        }
        return mob.distanceTo(livingEntity) > reach;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        if (!isTargetFar(this.mob)) {
            return false;
        }
        if (!JerotesRangedThrowAttackGoal.isHoldingThrow(this.mob)) {
            return false;
        }
        this.target = livingEntity;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive()
                && this.mob.getTarget() == this.target
                && isTargetFar(this.mob)
                && JerotesRangedThrowAttackGoal.isHoldingThrow(this.mob);
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
                && InventoryEntity.isThrow(this.mob.getOffhandItem())) {
            handItem = this.mob.getOffhandItem();
        }
        double d = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean bl = this.mob.getSensing().hasLineOfSight(this.target);
        this.seeTime = bl ? ++this.seeTime : 0;
        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (--this.attackTime == 0) {
            if (!bl) {
                return;
            }
            float f = (float)Math.sqrt(d) / this.attackRadius;
            float f2 = Mth.clamp(f, 0.1f, 1.0f);
            this.rangedAttackMob.performRangedAttack(this.target, f2);
            this.attackTime = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
        }
        else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d) / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
        }
        float f = (float)Math.sqrt(d) / this.attackRadius;
        int full = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
        if (this.attackTime < full - 10 && !(handItem.getItem() instanceof ThrowablePotionItem)) {
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
