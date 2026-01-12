package com.jerotes.jerotes.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class JerotesMoveToGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private long lastCanUseCheck;

    public JerotesMoveToGoal(PathfinderMob pathfinderMob, double d, boolean bl) {
        this.mob = pathfinderMob;
        this.speedModifier = d;
        this.followingTargetEvenIfNotSeen = bl;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
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

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
    }

    @Override
    public void stop() {
        this.mob.setAggressive(false);
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
    }
}

