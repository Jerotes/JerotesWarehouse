/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.BaseEntityAbout;
import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.init.JerotesEnchantments;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class JerotesSpearUseGoal<T extends PathfinderMob> extends Goal {
    private final T mob;
    private final double speedModifierWhenCharging;
    private final double speedModifierWhenRepositioning;
    private final double attackRadiusSqr;
    private final double targetInRangeSqr;
    private static final double MAX_FLEEING_TIME = JerotesSpearUseGoal.reducedTickDelay(100);
    private int engageTime = -1;
    private int fleeingTime = -1;
    @Nullable
    private Vec3 awayPos;
    private boolean done = false;
    private boolean canNormalAttack = true;
    public int ticksUntilNextAttack;

    public JerotesSpearUseGoal(T t, double d, double d2, float f, float f2) {
        this.mob = t;
        this.speedModifierWhenCharging = d;
        this.speedModifierWhenRepositioning = d2;
        this.attackRadiusSqr = f * f;
        this.targetInRangeSqr = f2 * f2;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    public JerotesSpearUseGoal(T t, double d, double d2, float f, float f2, boolean bl) {
        this.mob = t;
        this.speedModifierWhenCharging = d;
        this.speedModifierWhenRepositioning = d2;
        this.attackRadiusSqr = f * f;
        this.targetInRangeSqr = f2 * f2;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.canNormalAttack = bl;
    }

    @Override
    public boolean canUse() {
        return this.ableToAttack() && (!this.mob.isUsingItem() || this.mob.getUseItem().getItem() instanceof ShieldItem);
    }

    private boolean ableToAttack() {
        return this.mob.getTarget() != null && InventoryEntity.isSpear(mob.getMainHandItem());
    }

    private int getKineticWeaponUseDuration() {
        int n = 200;
        if (this.mob.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase) {
            n = itemToolBaseSpearBase.computeDamageUseDuration();
        }
        return JerotesSpearUseGoal.reducedTickDelay(n);
    }

    @Override
    public boolean canContinueToUse() {
        return !this.done && this.ableToAttack();
    }

    @Override
    public void start() {
        super.start();
        ((Mob)this.mob).setAggressive(true);
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        super.stop();
        ((Mob)this.mob).getNavigation().stop();
        ((Mob)this.mob).setAggressive(false);
        this.engageTime = -1;
        this.fleeingTime = -1;
        this.awayPos = null;
        this.done = false;
        ((LivingEntity)this.mob).stopUsingItem();
    }

    @Override
    public void tick() {
        double d;
        LivingEntity livingEntity = ((Mob)this.mob).getTarget();
        double d2 = ((Entity)this.mob).distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        boolean bl = ((Entity)this.mob).isPassenger();
        //float f = bl ? 1.4f : 1.0f;
        Entity entity = ((Entity)this.mob).getRootVehicle();
        float f = 1.0f;
        if (entity instanceof BaseEntityAbout baseEntityAbout) {
            f = baseEntityAbout.chargeSpeedModifierJerotes();
        }
        int n = ((Entity)this.mob).isPassenger() ? 2 : 0;
        ((Mob)this.mob).lookAt(livingEntity, 30.0f, 30.0f);
        ((Mob)this.mob).getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);
        if (this.engageTime < 0) {
            if (d2 > this.attackRadiusSqr) {
                ((Mob)this.mob).getNavigation().moveTo(livingEntity, (double)f * this.speedModifierWhenRepositioning);
                return;
            }
            this.engageTime = this.getKineticWeaponUseDuration();
            if (this.mob.getUseItem().getItem() instanceof ShieldItem) {
                this.mob.stopUsingItem();
            }
            (this.mob).startUsingItem(InteractionHand.MAIN_HAND);
            if (this.mob.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase) {
                itemToolBaseSpearBase.makeSound(this.mob);
            }
        }
        if (this.engageTime > 0) {
            --this.engageTime;
            if (this.engageTime == 0) {
                (this.mob).stopUsingItem();
                d = Math.sqrt(d2);
                this.awayPos = getPosAway(this.mob, Math.max(0.0, (double)(9 + n) - d), Math.max(1.0, (double)(11 + n) - d), 7, livingEntity.position());
                this.fleeingTime = 1;
            }
        }
        if (this.fleeingTime > 0) {
            ++this.fleeingTime;
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            if (canNormalAttack) {
                if (this.mob.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase) {
                    this.checkAndPerformAttack(livingEntity, this.mob.getMainHandItem());
                }
            }
            if ((double)this.fleeingTime > MAX_FLEEING_TIME) {
                this.done = true;
                return;
            }
        }
        if (this.awayPos != null) {
            ((Mob)this.mob).getNavigation().moveTo(this.awayPos.x, this.awayPos.y, this.awayPos.z, (double)f * this.speedModifierWhenRepositioning);
            if (((Mob)this.mob).getNavigation().isDone()) {
                if (this.fleeingTime > 0) {
                    this.done = true;
                    return;
                }
                this.awayPos = null;
            }
        } else {
            ((Mob)this.mob).getNavigation().moveTo(livingEntity, (double)f * this.speedModifierWhenCharging);
            if (d2 < this.targetInRangeSqr || ((Mob)this.mob).getNavigation().isDone()) {
                d = Math.sqrt(d2);
                this.awayPos = getPosAway(this.mob, (double)(6 + n) - d, (double)(7 + n) - d, 7, livingEntity.position());
            }
        }
        Item item = this.mob.getMainHandItem().getItem();
        if (item instanceof ItemToolBasePike itemToolBasePike && canNormalAttack) {
            //突刺赶路
            if (itemToolBasePike.getEnchantmentLevel(this.mob.getMainHandItem(), JerotesEnchantments.LUNGE.get()) > 0 && this.mob.distanceTo(livingEntity) > itemToolBasePike.effectiveMaxRange(this.mob) * 1.5 && this.mob.getRandom().nextInt(30) == 1) {
                this.checkAndPerformAttackRush(livingEntity, this.mob.getMainHandItem());
            }
        }
    }


    protected void checkAndPerformAttack(LivingEntity livingEntity, ItemStack itemStack) {
        if (this.canPerformAttack(livingEntity, itemStack)) {
            this.resetAttackCooldown();
            this.mob.stopUsingItem();
            if (this.mob.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase) {
                itemToolBaseSpearBase.attack(this.mob, EquipmentSlot.MAINHAND);
            }
            else {
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(livingEntity);
            }
            if (this.mob instanceof JerotesEntity jerotes) {
                jerotes.setCanNotAttackTargetTick(0);
            }
        }
    }

    protected void checkAndPerformAttackRush(LivingEntity livingEntity, ItemStack itemStack) {
        if (this.canPerformAttackRush(livingEntity, itemStack)) {
            this.resetAttackCooldown();
            this.mob.stopUsingItem();
            if (this.mob.getMainHandItem().getItem() instanceof ItemToolBasePike itemToolBasePike) {
                itemToolBasePike.attack(this.mob, EquipmentSlot.MAINHAND);
            }
        }
    }

    protected void resetAttackCooldown() {
        Item item = this.mob.getMainHandItem().getItem();
        if (item instanceof ItemToolBaseSpearBase itemToolBaseSpearBase) {
            this.ticksUntilNextAttack = this.adjustedTickDelay((int)(itemToolBaseSpearBase.swingTimes * 20f));
        } else {
            this.ticksUntilNextAttack = this.adjustedTickDelay(20);
        }
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity livingEntity, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase)) {
            return false;
        }
        if (ItemToolBaseSpearBase.getHitEntitiesAlong(this.mob, itemToolBaseSpearBase, itemToolBaseSpearBase.hitboxMargin, entity -> ItemToolBaseSpearBase.canHitEntity(this.mob, entity) && entity == livingEntity).isEmpty())
            return false;
        return this.isTimeToAttack() && this.mob.getSensing().hasLineOfSight(livingEntity) && !this.mob.isUsingItem();
    }
    protected boolean canPerformAttackRush(LivingEntity livingEntity, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemToolBasePike itemToolBasePike)) {
            return false;
        }
        return this.isTimeToAttack() && !this.mob.isUsingItem();
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(20);
    }


    @Nullable
    public static Vec3 getPosAway(PathfinderMob pathfinderMob, int n, int n2, Vec3 vec3) {
        return getPosAway(pathfinderMob, 0.0, n, n2, vec3);
    }

    @Nullable
    public static Vec3 getPosAway(PathfinderMob pathfinderMob, double d, double d2, int n, Vec3 vec3) {
        Vec3 vec32 = pathfinderMob.position().subtract(vec3);
        boolean bl = GoalUtils.mobRestricted(pathfinderMob, (int)d2);
        return getPosInDirection(pathfinderMob, d, d2, n, vec32, bl);
    }

    @Nullable
    private static Vec3 getPosInDirection(PathfinderMob pathfinderMob, double d, double d2, int n, Vec3 vec3, boolean bl) {
        return RandomPos.generateRandomPos(pathfinderMob, () -> {
            BlockPos blockPos = generateRandomDirectionWithinRadians(pathfinderMob.getRandom(), d, d2, n, 0, vec3.x, vec3.z, 1.5707963705062866);
            if (blockPos == null) {
                return null;
            }
            BlockPos blockPos2 = generateRandomPosTowardDirection(pathfinderMob, d2, bl, blockPos);
            if (blockPos2 == null) {
                return null;
            }
            return LandRandomPos.movePosUpOutOfSolid(pathfinderMob, blockPos2);
        });
    }

    @Nullable
    public static BlockPos generateRandomDirectionWithinRadians(RandomSource randomSource, double d, double d2, int n, int n2, double d3, double d4, double d5) {
        double d6 = Mth.atan2(d4, d3) - 1.5707963705062866;
        double d7 = d6 + (double)(2.0f * randomSource.nextFloat() - 1.0f) * d5;
        double d8 = Mth.lerp(Math.sqrt(randomSource.nextDouble()), d, d2) * (double)Mth.SQRT_OF_TWO;
        double d9 = -d8 * Math.sin(d7);
        double d10 = d8 * Math.cos(d7);
        if (Math.abs(d9) > d2 || Math.abs(d10) > d2) {
            return null;
        }
        int n3 = randomSource.nextInt(2 * n + 1) - n + n2;
        return BlockPos.containing(d9, n3, d10);
    }
    @Nullable
    public static BlockPos generateRandomPosTowardDirection(PathfinderMob pathfinderMob, double d, boolean bl, BlockPos blockPos) {
        BlockPos blockPos2 = generateRandomPosTowardDirection(pathfinderMob, d, pathfinderMob.getRandom(), blockPos);
        if (GoalUtils.isOutsideLimits(blockPos2, pathfinderMob) || GoalUtils.isRestricted(bl, pathfinderMob, blockPos2) || GoalUtils.isNotStable(pathfinderMob.getNavigation(), blockPos2)) {
            return null;
        }
        return blockPos2;
    }

    public static BlockPos generateRandomPosTowardDirection(PathfinderMob pathfinderMob, double d, RandomSource randomSource, BlockPos blockPos) {
        double d2 = blockPos.getX();
        double d3 = blockPos.getZ();
        if (pathfinderMob.hasRestriction() && d > 1.0) {
            BlockPos blockPos2 = pathfinderMob.getRestrictCenter();
            d2 = pathfinderMob.getX() > (double)blockPos2.getX() ? (d2 -= randomSource.nextDouble() * d / 2.0) : (d2 += randomSource.nextDouble() * d / 2.0);
            d3 = pathfinderMob.getZ() > (double)blockPos2.getZ() ? (d3 -= randomSource.nextDouble() * d / 2.0) : (d3 += randomSource.nextDouble() * d / 2.0);
        }
        return BlockPos.containing(d2 + pathfinderMob.getX(), (double)blockPos.getY() + pathfinderMob.getY(), d3 + pathfinderMob.getZ());
    }
}

