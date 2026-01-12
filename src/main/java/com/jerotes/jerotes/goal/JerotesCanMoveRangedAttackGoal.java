package com.jerotes.jerotes.goal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.pathfinder.Path;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class JerotesCanMoveRangedAttackGoal extends Goal {
    private final Mob mob;
    public final RangedAttackMob rangedAttackMob;
    private Path path;
    @Nullable
    public LivingEntity target;
    public int attackTime = -1;
    public final double speedModifier;
    public int seeTime;
    public final int attackIntervalMin;
    public final int attackIntervalMax;
    public final float attackRadius;
    private final float attackRadiusSqr;

    public JerotesCanMoveRangedAttackGoal(RangedAttackMob rangedAttackMob, double d, int n, float f) {
        this(rangedAttackMob, d, n, n, f);
    }

    public JerotesCanMoveRangedAttackGoal(RangedAttackMob rangedAttackMob, double d, int n, int n2, float f) {
        if (!(rangedAttackMob instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedAttackMob = rangedAttackMob;
        this.mob = (Mob)((Object)rangedAttackMob);
        this.speedModifier = d;
        this.attackIntervalMin = n;
        this.attackIntervalMax = n2;
        this.attackRadius = f;
        this.attackRadiusSqr = f * f;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        this.target = livingEntity;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
    }

    @Override
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.setAggressive(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        double d = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean bl = this.mob.getSensing().hasLineOfSight(this.target);
        this.seeTime = bl ? ++this.seeTime : 0;
        this.mob.getNavigation().moveTo(this.target, this.speedModifier);
        this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        if (--this.attackTime == 0) {
            if (!bl) {
                return;
            }
            float f = (float)Math.sqrt(d) / this.attackRadius;
            float f2 = Mth.clamp(f, 0.1f, 1.0f);
            this.rangedAttackMob.performRangedAttack(this.target, f2);
            this.attackTime = Mth.floor(f * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d) / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
        }
    }
}

