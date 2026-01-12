package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.InventoryEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;

import java.util.EnumSet;

public class JerotesRangedBowAttackGoal<T extends Mob & RangedAttackMob> extends Goal {
    private final T mob;
    private final double speedModifier;
    private int attackIntervalMin;
    private final float attackRadiusSqr;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public JerotesRangedBowAttackGoal(T t, double d, int n, float f) {
        this.mob = t;
        this.speedModifier = d;
        this.attackIntervalMin = n;
        this.attackRadiusSqr = f * f;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public void setMinAttackInterval(int p_25798_) {
        this.attackIntervalMin = p_25798_;
    }

    public boolean canUse() {
        return this.mob.getTarget() != null && isHoldingBow(mob);
    }

    public static boolean isHoldingBow(Mob mob) {
        Item main = mob.getMainHandItem().getItem();
        Item off = mob.getOffhandItem().getItem();
        float reach = 8;
        if (mob instanceof InventoryEntity inventoryEntity) {
            reach = inventoryEntity.meleeOrRangeDistance();
        }
        if (InventoryEntity.isMeleeWeapon(mob.getMainHandItem()) && mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= reach) {
            return false;
        }
        return main instanceof BowItem || off instanceof BowItem && (mob.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(mob.getMainHandItem()));
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.mob.getNavigation().isDone()) && isHoldingBow(mob);
    }

    @Override
    public void start() {
        super.start();
        this.mob.setAggressive(true);
    }


    @Override
    public void stop() {
        super.stop();
        LivingEntity livingEntity = this.mob.getTarget();
        if (!(livingEntity != null && this.mob instanceof InventoryEntity inventoryEntity && inventoryEntity.isCanChangeMeleeOrRange())) {
            this.mob.setTarget(null);
            this.mob.setAggressive(false);
        }
        else {
            this.mob.getNavigation().stop();
        }
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
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            if (!(this.mob.getUseItem().getItem() instanceof BowItem)) {
                this.mob.stopUsingItem();
            }
            double d0 = this.mob.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.mob.getSensing().hasLineOfSight(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(d0 > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
                this.mob.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.mob.getNavigation().moveTo(livingentity, this.speedModifier);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double)(this.attackRadiusSqr * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double)(this.attackRadiusSqr * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                Entity entity = this.mob.getControlledVehicle();
                if (entity instanceof Mob mob) {
                    mob.lookAt(livingentity, 30.0F, 30.0F);
                }

                this.mob.lookAt(livingentity, 30.0F, 30.0F);
            } else {
                this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            }

            if (this.mob.isUsingItem()) {
                if (!flag && this.seeTime < -60) {
                    this.mob.stopUsingItem();
                } else if (flag) {
                    int i = this.mob.getTicksUsingItem();
                    if (i >= 20) {
                        this.mob.stopUsingItem();
                        this.mob.performRangedAttack(livingentity, BowItem.getPowerForTime(i));
                        this.attackTime = this.attackIntervalMin;
                    }
                }
            }
            if ((!this.mob.isUsingItem() || !(this.mob.getUseItem().getItem() instanceof BowItem))  && --this.attackTime <= 0 && this.seeTime >= -60) {
                if (!(this.mob.getUseItem().getItem() instanceof BowItem)) {
                    this.mob.stopUsingItem();
                }
                this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, item -> item instanceof BowItem));
            }

        }
    }
}

