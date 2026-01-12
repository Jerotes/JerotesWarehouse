package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Mob.JerotesPlayerEntity;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class JerotesPlayerTargetGoal<T extends LivingEntity> extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 10;
    protected final Class<T> targetType;
    protected final int randomInterval;
    protected @Nullable LivingEntity target;
    protected TargetingConditions targetConditions;

    public JerotesPlayerTargetGoal(Mob mob, Class<T> clazz, boolean bl) {
        this(mob, clazz, 10, bl, false, null);
    }

    public JerotesPlayerTargetGoal(Mob mob, Class<T> clazz, boolean bl, Predicate<LivingEntity> p_199894_) {
        this(mob, clazz, 10, bl, false, p_199894_);
    }

    public JerotesPlayerTargetGoal(Mob mob, Class<T> clazz, boolean bl, boolean bl2) {
        this(mob, clazz, 10, bl, bl2, null);
    }

    public JerotesPlayerTargetGoal(Mob mob, Class<T> clazz, int n, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> p_199894_) {
        super(mob, bl, bl2);
        this.targetType = clazz;
        this.randomInterval = NearestAttackableTargetGoal.reducedTickDelay(n);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(p_199894_);
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        }
        this.findTarget();
        return this.target != null;
    }

    protected AABB getTargetSearchArea(double d) {
        return this.mob.getBoundingBox().inflate(d, d, d);
    }

    protected void findTarget() {
        //注意
        this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (find) -> {
            return find instanceof Mob mobs && mobs.getTarget() == this.mob && this.mob.canBeSeenAsEnemy() && !AttackFind.FindCanNotAttack(this.mob, find) &&
                    !AttackFind.FindCanNotAttack(find, this.mob) &&
                    !AttackFind.SameFactionAvoidDamage(this.mob, find) &&
                    Main.canSeeAngle(this.mob, find.getEyePosition(), this.mob instanceof JerotesPlayerEntity jerotesPlayerEntity ? (160 + (Math.min(200, jerotesPlayerEntity.getLookLevel() * 10f))) : 180f);
        }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity livingEntity) {
        this.target = livingEntity;
    }

    private TargetingConditions getTargetConditions() {
        return this.targetConditions.range(this.getFollowDistance());
    }
}

