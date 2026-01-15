/*
 * Decompiled with CFR 0.146.
 */
package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import java.util.EnumSet;

public class JerotesMeleeAttackGoal extends Goal {
    public final PathfinderMob mob;
    public final double speedModifier;
    public final boolean followingTargetEvenIfNotSeen;
    public final boolean canJump;
    public final boolean canFlee;
    public Path path;
    public double pathedTargetX;
    public double pathedTargetY;
    public double pathedTargetZ;
    public int ticksUntilNextPathRecalculation;
    public int ticksUntilNextAttack;
    public int ticksUntilNextJump;
    public int ticksJumpUse;
    public final int attackInterval = 20;
    public long lastCanUseCheck;
    public static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
    public int jumpTick = 15;
    public int jumpCooldownTick = 15;
    public int jumpCooldownRandomTick = 20;

    public JerotesMeleeAttackGoal(PathfinderMob pathfinderMob, double d, boolean bl) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.followingTargetEvenIfNotSeen = bl;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.canJump = false;
        this.canFlee = false;
    }

    public JerotesMeleeAttackGoal(PathfinderMob pathfinderMob, double d, boolean bl, boolean canJump,
                                  int jumpTick, int jumpCooldownTick, int jumpCooldownRandomTick, boolean canFlee) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.followingTargetEvenIfNotSeen = bl;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.canJump = canJump;
        this.jumpTick = jumpTick;
        this.jumpCooldownTick = jumpCooldownTick;
        this.jumpCooldownRandomTick = jumpCooldownRandomTick;
        this.canFlee = canFlee;
    }

    public JerotesMeleeAttackGoal(PathfinderMob pathfinderMob, double d, boolean bl, boolean canJump, boolean canFlee) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.followingTargetEvenIfNotSeen = bl;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.canJump = canJump;
        this.canFlee = canFlee;
    }

    @Override
    public boolean canUse() {
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

    public boolean canJump(Mob livingEntity) {
        return canJump(livingEntity, false);
    }
    public boolean canJump(Mob livingEntity, boolean bl) {
        if (livingEntity.hasControllingPassenger()) {
            return false;
        }
        else if (livingEntity instanceof JerotesEntity jerotes && !jerotes.canAttackJump()) {
            return false;
        }
        else if (livingEntity.onClimbable() ||
                livingEntity.isInFluidType() ||
                livingEntity.hasEffect(MobEffects.BLINDNESS) ||
                livingEntity.isPassenger()) {
            return false;
        }
        else if (!bl) {
            if (livingEntity.getTarget() == null) {
                return false;
            }
            if (livingEntity instanceof JerotesEntity jerotes && jerotes.getAttackBoundingBox() != null) {
                if (jerotes.getAttackBoundingBox().inflate(0.5f).intersects(livingEntity.getTarget().getBoundingBox())) {
                    if (!livingEntity.onGround()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
            if (livingEntity.getBoundingBox().inflate(0.5f).intersects(livingEntity.getTarget().getBoundingBox())) {
                if (!livingEntity.onGround()) {
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
        this.ticksUntilNextJump = 0;
        this.ticksJumpUse = 0;
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
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);
        if (this.mob instanceof JerotesPlayerBaseEntity jerotesPlayerEntity) {
            this.mob.lookAt(livingEntity, 360.0f, 360.0f);
            this.mob.getLookControl().setLookAt(livingEntity, 360.0f, 360.0f);
        }
        else {
            this.mob.getLookControl().setLookAt(livingEntity, 30.0f, 30.0f);
        }
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
        this.ticksUntilNextJump = Math.max(this.ticksUntilNextJump - 1, 0);
        this.ticksJumpUse = Math.max(this.ticksJumpUse - 1, 0);
        this.checkAndPerformAttack(livingEntity);

        if (this.canFlee) {
            boolean flee = this.mob.isWithinMeleeAttackRange(livingEntity);
            if (livingEntity instanceof JerotesEntity jerotes && jerotes.getAttackBoundingBox() != null) {
                flee = jerotes.getAttackBoundingBox().deflate(0.5f).intersects(livingEntity.getBoundingBox());
            }
            if (!this.mob.isInFluidType() && !this.mob.onGround() || this.ticksJumpUse > 0 || this.mob.fallDistance <= 0) {
                flee = false;
            }
            if (flee) {
                float f1 = this.mob.getYRot();
                float f2 = this.mob.getXRot();
                float f3 = -Mth.sin(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
                float f4 = -Mth.sin(f2 * 0.017453292f);
                float f5 = Mth.cos(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
                float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
                float f7 = -0.08f;
                if (this.mob instanceof JerotesEntity jerotes) {
                    jerotes.setSprintingCooldown(10);
                }
                if (this.mob.getDeltaMovement().x <= 0.15 && this.mob.getDeltaMovement().z <= 0.15)
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(f3 *= f7 / f6, 0, f5 *= f7 / f6));
            }
        }
        if (this.canJump && canJump(this.mob, true) && this.mob.onGround() && this.mob.isSprinting()) {
            boolean jump = !this.mob.getBoundingBox().inflate(6f).intersects(livingEntity.getBoundingBox());
            if (livingEntity instanceof JerotesEntity jerotes && jerotes.getAttackBoundingBox() != null) {
                jump = !jerotes.getAttackBoundingBox().inflate(3f).intersects(livingEntity.getBoundingBox());
            }
            if (this.ticksUntilNextJump <= 0 && jump) {
                this.mob.lookAt(livingEntity, 360.0f, 360.0f);
                this.mob.getLookControl().setLookAt(livingEntity, 360.0f, 360.0f);
                if (this.mob instanceof JerotesPlayerBaseEntity jerotesPlayerEntity) {
                    jerotesPlayerEntity.playerJump();
                }
                else {
                    Vec3 vec3 = this.mob.getDeltaMovement();
                    this.mob.setDeltaMovement(vec3.x, 0.42F * this.getBlockJumpFactor(this.mob) + this.getJumpBoostPower(this.mob), vec3.z);
                    float f = this.mob.getYRot() * 0.017453292F;
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0, (double)(Mth.cos(f) * 0.2F)));

                    this.mob.hasImpulse = true;
                    ForgeHooks.onLivingJump(this.mob);
                }
                this.ticksUntilNextJump = this.adjustedTickDelay((int) (this.jumpCooldownTick * 1.25));
                this.ticksJumpUse = this.adjustedTickDelay(this.jumpTick);
            }
        }
    }

    public float getBlockJumpFactor(LivingEntity livingEntity) {
        float f = livingEntity.level().getBlockState(livingEntity.blockPosition()).getBlock().getJumpFactor();
        float f1 = livingEntity.level().getBlockState(getOnPos(livingEntity, 0.500001F)).getBlock().getJumpFactor();
        return (double)f == 1.0 ? f1 : f;
    }
    protected BlockPos getOnPos(LivingEntity livingEntity, float p_216987_) {
        if (livingEntity.mainSupportingBlockPos.isPresent()) {
            BlockPos blockpos = (BlockPos)livingEntity.mainSupportingBlockPos.get();
            if (!(p_216987_ > 1.0E-5F)) {
                return blockpos;
            } else {
                BlockState blockstate = livingEntity.level().getBlockState(blockpos);
                return (double)p_216987_ <= 0.5 && blockstate.collisionExtendsVertically(livingEntity.level(), blockpos, livingEntity) ? blockpos : blockpos.atY(Mth.floor(livingEntity.position().y - (double)p_216987_));
            }
        } else {
            int i = Mth.floor(livingEntity.position().x);
            int j = Mth.floor(livingEntity.position().y - (double)p_216987_);
            int k = Mth.floor(livingEntity.position().z);
            return new BlockPos(i, j, k);
        }
    }
    public float getJumpBoostPower(LivingEntity livingEntity) {
        return livingEntity.hasEffect(MobEffects.JUMP) ? 0.1F * ((float)livingEntity.getEffect(MobEffects.JUMP).getAmplifier() + 1.0F) : 0.0F;
    }
    protected void checkAndPerformAttack(LivingEntity livingEntity) {
        if (this.canJump && canJump(this.mob)) {
            if (this.ticksUntilNextAttack <= 5 && this.ticksUntilNextJump <= 0 && this.mob.isWithinMeleeAttackRange(livingEntity) && this.mob.getSensing().hasLineOfSight(livingEntity)) {
               if (this.mob instanceof JerotesPlayerBaseEntity jerotesPlayerEntity) {
                   jerotesPlayerEntity.playerJump();
               }
               else {
                   Vec3 vec3 = this.mob.getDeltaMovement();
                   this.mob.setDeltaMovement(vec3.x, 0.42F * this.getBlockJumpFactor(this.mob) + this.getJumpBoostPower(this.mob), vec3.z);
                   if (this.mob.isSprinting()) {
                       float f = this.mob.getYRot() * 0.017453292F;
                       this.mob.setDeltaMovement(this.mob.getDeltaMovement().add((double)(-Mth.sin(f) * 0.2F), 0.0, (double)(Mth.cos(f) * 0.2F)));
                   }

                   this.mob.hasImpulse = true;
                   ForgeHooks.onLivingJump(this.mob);
               }
                this.mob.lookAt(livingEntity, 360.0f, 360.0f);
                this.ticksUntilNextJump = this.adjustedTickDelay(this.jumpCooldownTick * this.mob.getRandom().nextInt(this.jumpCooldownRandomTick));
                this.ticksJumpUse = this.adjustedTickDelay(this.jumpTick);
                return;
            }
        }
        if (this.canPerformAttack(livingEntity)) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(livingEntity);
            if (this.ticksJumpUse > 0) {
                this.ticksJumpUse = 0;
            }
            if (this.mob instanceof JerotesEntity jerotes) {
                jerotes.setCanNotAttackTargetTick(0);
            }
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(this.mob instanceof JerotesPlayerBaseEntity jerotesPlayerEntity ? jerotesPlayerEntity.getAttackSpeed() : 20);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity livingEntity) {
        if (!this.mob.isInFluidType() && !this.mob.onGround() && this.ticksJumpUse > 0 && this.mob.fallDistance <= 0) {
            return false;
        }
        return this.isTimeToAttack() && this.mob.isWithinMeleeAttackRange(livingEntity) && this.mob.getSensing().hasLineOfSight(livingEntity);
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(20);
    }
}

