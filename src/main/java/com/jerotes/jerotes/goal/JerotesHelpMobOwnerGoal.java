package com.jerotes.jerotes.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class JerotesHelpMobOwnerGoal extends NearestAttackableTargetGoal<LivingEntity> {
    @Nullable
    private LivingEntity attack;

    public JerotesHelpMobOwnerGoal(Mob mob, Class<LivingEntity> class_, boolean bl, @Nullable boolean bl2, Predicate<LivingEntity> predicate) {
        super(mob, class_, 10, bl, bl2, predicate);
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        }
        if (this.mob.getTarget() != null) {
            return false;
        }
        if (this.mob.getTarget() instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return false;
        }
        List<LivingEntity> list = this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate(64.0, 64.0, 64.0));
        double d = Double.MAX_VALUE;
        for (LivingEntity owner : list) {
            if (owner == null) continue;
            if (!(owner instanceof Mob mobOwner)) continue;
            if (!(this.mob instanceof OwnableEntity ownable)) continue;
            if (mobOwner.getUUID() != ownable.getOwnerUUID()) continue;
            if ((this.mob.distanceToSqr(mobOwner)) > d) continue;
            if (this.mob.getTarget() != null) continue;
            if (mobOwner.getTarget() != null && this.mob.canAttack(mobOwner.getTarget()) && this.mob.canAttackType(mobOwner.getTarget().getType())) {
                if (!(mobOwner.getTarget().isAlliedTo(this.mob)) && !(mobOwner.getTarget().isAlliedTo(mobOwner))) {
                    attack = mobOwner.getTarget();
                    return true;
                }
            }
            else if (mobOwner.getLastHurtByMob() != null && this.mob.canAttack(mobOwner.getLastHurtByMob()) && this.mob.canAttackType(mobOwner.getLastHurtByMob().getType())) {
                if (!(mobOwner.getLastHurtByMob().isAlliedTo(this.mob)) && !(mobOwner.getLastHurtByMob().isAlliedTo(mobOwner))) {
                    attack = mobOwner.getLastHurtByMob();
                    return true;
                }
            }
            else if (mobOwner.getLastHurtByMob() != null && this.mob.canAttack(mobOwner.getLastHurtByMob()) && this.mob.canAttackType(mobOwner.getLastHurtByMob().getType())) {
                if (!(mobOwner.getLastHurtByMob().isAlliedTo(this.mob)) && !(mobOwner.getLastHurtByMob().isAlliedTo(mobOwner))) {
                    attack = mobOwner.getLastHurtByMob();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.setTarget(attack);
        super.start();
    }
}

