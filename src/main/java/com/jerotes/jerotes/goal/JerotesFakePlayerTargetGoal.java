package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class JerotesFakePlayerTargetGoal extends TargetGoal {

    private final TargetingConditions targetConditions;
    @Nullable
    private LivingEntity candidate;

    public JerotesFakePlayerTargetGoal(Mob mob) {
        super(mob, false, false);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance());
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(20) != 0) {
            return false;
        }
        LivingEntity livingEntity = this.findFakePlayer();
        if (livingEntity == null) {
            return false;
        }
        this.candidate = livingEntity;
        return true;
    }

    @Nullable
    private LivingEntity findFakePlayer() {
        if (!(this.mob.level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        List<LivingEntity> list = serverLevel.getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(this.getFollowDistance()));
        LivingEntity best = null;
        for (LivingEntity livingEntity : list) {
            if (!(livingEntity instanceof JerotesPlayerBaseEntity playerBaseEntity) || !playerBaseEntity.beTargetAsPlayer()) {
                continue;
            }
            if (!livingEntity.canBeSeenAsEnemy() || !this.mob.canAttack(livingEntity)) {
                continue;
            }
            if (AttackFind.FindCanNotAttack(this.mob, livingEntity) || AttackFind.SameFactionAvoidDamage(this.mob, livingEntity, false)) {
                continue;
            }
            if (!this.targetConditions.test(this.mob, livingEntity)) {
                continue;
            }
            if (best == null || this.mob.distanceToSqr(livingEntity) < this.mob.distanceToSqr(best)) {
                best = livingEntity;
            }
        }
        return best;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.candidate);
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        return livingEntity != null
                && livingEntity instanceof JerotesPlayerBaseEntity playerBaseEntity
                && playerBaseEntity.beTargetAsPlayer()
                && livingEntity.isAlive()
                && this.mob.canAttack(livingEntity);
    }

    private AABB getTargetSearchArea(double distance) {
        return this.mob.getBoundingBox().inflate(distance, distance, distance);
    }
}
