/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.init.JerotesEnchantments;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class JerotesPikeUseGoal extends Goal {
    public final PathfinderMob mob;
    public final double speedModifier;
    public final boolean followingTargetEvenIfNotSeen;
    public Path path;
    public double pathedTargetX;
    public double pathedTargetY;
    public double pathedTargetZ;
    public int ticksUntilNextPathRecalculation;
    public int ticksUntilNextAttack;
    public final int attackInterval = 20;
    public long lastCanUseCheck;
    public static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
    public int ticksUntilNextUse;

    public JerotesPikeUseGoal(PathfinderMob pathfinderMob, double d, boolean bl) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.followingTargetEvenIfNotSeen = bl;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean ableToAttack() {
        return InventoryEntity.isPike(mob.getMainHandItem());
    }

    @Override
    public boolean canUse() {
        if (!ableToAttack()) {
            return false;
        }
        long l = this.mob.level().getGameTime();
        if (l - this.lastCanUseCheck < 20L) {
            return false;
        }
        this.lastCanUseCheck = l;
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        }
        if (!livingEntity.isAlive()) {
            return false;
        }
        this.path = this.mob.getNavigation().createPath(livingEntity, 0);
        if (this.path != null) {
            return true;
        }
        return this.mob.isWithinMeleeAttackRange(livingEntity);
    }

    @Override
    public boolean canContinueToUse() {
        if (!ableToAttack()) {
            return false;
        }
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        }
        if (!livingEntity.isAlive()) {
            return false;
        }
        if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        }
        if (!this.mob.isWithinRestriction(livingEntity.blockPosition())) {
            return false;
        }
        return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player)livingEntity).isCreative();
    }
    @Override
    public void start() {
        super.start();
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        ((Mob)this.mob).setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
        this.ticksUntilNextUse = 0;
    }

    @Override
    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!(livingEntity != null && this.mob instanceof InventoryEntity inventoryEntity && inventoryEntity.isCanChangeMeleeOrRange())) {
            if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
                this.mob.setTarget(null);
            }
            this.mob.setAggressive(false);
        }
        this.mob.getNavigation().stop();
        ((LivingEntity)this.mob).stopUsingItem();
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return;
        }
        this.ticksUntilNextUse = Math.max(this.ticksUntilNextUse - 1, 0);
        this.mob.getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);
        this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
        if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingEntity)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || livingEntity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.mob.getRandom().nextFloat() < 0.05f)) {
            this.pathedTargetX = livingEntity.getX();
            this.pathedTargetY = livingEntity.getY();
            this.pathedTargetZ = livingEntity.getZ();
            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
            double d = this.mob.distanceToSqr(livingEntity);
            if (d > 1024.0) {
                this.ticksUntilNextPathRecalculation += 10;
            } else if (d > 256.0) {
                this.ticksUntilNextPathRecalculation += 5;
            }
            if (!this.mob.getNavigation().moveTo(livingEntity, this.speedModifier)) {
                this.ticksUntilNextPathRecalculation += 15;
            }
            this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
        }
        this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        this.checkAndPerformAttack(livingEntity, this.mob.getMainHandItem());
        Item item = this.mob.getMainHandItem().getItem();
        if (item instanceof ItemToolBasePike itemToolBasePike) {
            //进入对方攻击范围内或超过最小距离时后退
            if ((livingEntity instanceof Mob mobs && isWithinMeleeAttackRange(mobs, this.mob) ||
                    this.mob.distanceTo(livingEntity) < itemToolBasePike.effectiveMinRange(this.mob) * (this.ticksUntilNextAttack > 0 ? 1.65f : 1.2)) && this.mob.onGround()) {
                float f1 = this.mob.getYRot();
                float f2 = this.mob.getXRot();
                float f3 = -Mth.sin(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
                float f4 = -Mth.sin(f2 * 0.017453292f);
                float f5 = Mth.cos(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
                float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
                float f7 = -0.0235f;
                if (this.mob instanceof JerotesEntity jerotes) {
                    jerotes.setSprintingCooldown(10);
                }
                if (this.mob.getDeltaMovement().x <= 0.15 && this.mob.getDeltaMovement().z <= 0.15)
                  this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(f3 *= f7 / f6 * 2, 0, f5 *= f7 / f6 * 2));
            }
            //敌人过多、被靠近、间隔中 且可以攻击到
            if (this.mob.distanceTo(livingEntity) <= itemToolBasePike.effectiveMaxRange(this.mob) * 0.7f) {
                if (this.ticksUntilNextUse <= 0) {
                    boolean bl1 = ItemToolBasePike.getHitEntitiesAlong(this.mob, itemToolBasePike, itemToolBasePike.hitboxMargin,
                            entity -> ItemToolBaseSpearBase.canHitEntity(this.mob, entity)).size() > 3;
                    boolean bl2 = this.mob.distanceTo(livingEntity) < itemToolBasePike.effectiveMinRange(this.mob) * 1.5;
                    boolean bl3 = this.mob.getRandom().nextInt(20) == 1;
                    if (bl1 || bl2 || bl3) {
                        this.mob.startUsingItem(InteractionHand.MAIN_HAND);
                        this.ticksUntilNextUse = this.adjustedTickDelay(80);
                    }
                }
            }
            //突刺赶路
            if (itemToolBasePike.getEnchantmentLevel(this.mob.getMainHandItem(), JerotesEnchantments.LUNGE.get()) > 0 && this.mob.distanceTo(livingEntity) > itemToolBasePike.effectiveMaxRange(this.mob) * 1.5 && this.mob.getRandom().nextInt(30) == 1) {
                this.checkAndPerformAttackRush(livingEntity, this.mob.getMainHandItem());
            }
        }
    }

    public double getMeleeAttackRangeSqr(LivingEntity livingEntity, LivingEntity self) {
        return (double)(self.getBbWidth() * 2.0F * self.getBbWidth() * 2.0F + livingEntity.getBbWidth());
    }

    public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity livingEntity, LivingEntity self) {
        return Math.max(self.distanceToSqr(getMeleeAttackReferencePosition(livingEntity)), self.distanceToSqr(livingEntity.position()));
    }
    protected Vec3 getMeleeAttackReferencePosition(LivingEntity livingEntity) {
        Entity entity = livingEntity.getVehicle();
        if (entity instanceof RiderShieldingMount ridershieldingmount) {
            return livingEntity.position().add(0.0, ridershieldingmount.getRiderShieldingHeight(), 0.0);
        } else {
            return livingEntity.position();
        }
    }

    public boolean isWithinMeleeAttackRange(LivingEntity livingEntity, LivingEntity self) {
        double d0 = this.getPerceivedTargetDistanceSquareForMeleeAttack(livingEntity, self);
        return d0 <= this.getMeleeAttackRangeSqr(livingEntity, self) * 1.3f;
    }


    protected void checkAndPerformAttack(LivingEntity livingEntity, ItemStack itemStack) {
        if (this.canPerformAttack(livingEntity, itemStack)) {
            this.resetAttackCooldown();
            this.mob.stopUsingItem();
            if (this.mob.getMainHandItem().getItem() instanceof ItemToolBasePike itemToolBasePike) {
                this.mob.swing(InteractionHand.MAIN_HAND);
                itemToolBasePike.attack(this.mob, EquipmentSlot.MAINHAND);
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
            this.mob.swing(InteractionHand.MAIN_HAND);
        }
    }

    protected void resetAttackCooldown() {
        Item item = this.mob.getMainHandItem().getItem();
        if (item instanceof ItemToolBasePike itemToolBasePike) {
            this.ticksUntilNextAttack = this.adjustedTickDelay((int) (itemToolBasePike.swingTimes() * 2) * (this.mob.getOffhandItem().isEmpty() ? 1 : 2));
        } else {
            this.ticksUntilNextAttack = this.adjustedTickDelay(20);
        }
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity livingEntity, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemToolBasePike itemToolBasePike)) {
            return false;
        }
        if (ItemToolBasePike.getHitEntitiesAlong(this.mob, itemToolBasePike, itemToolBasePike.hitboxMargin,
                entity -> ItemToolBaseSpearBase.canHitEntity(this.mob, entity) && (entity == livingEntity || entity instanceof Mob mobs && mobs.getTarget() == this.mob)).isEmpty())
            return false;
        return this.isTimeToAttack() && !this.mob.isUsingItem();
    }

    protected boolean canPerformAttackRush(LivingEntity livingEntity, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemToolBasePike itemToolBasePike)) {
            return false;
        }
        return this.isTimeToAttack() && !this.mob.isUsingItem();
    }
}
