package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.CarvedEntity;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.allay.Allay;

import javax.annotation.Nullable;
import java.util.List;

public class JerotesHelpCarvedGoal extends NearestAttackableTargetGoal<LivingEntity> {
    @Nullable
    private LivingEntity attack;

    public JerotesHelpCarvedGoal(Mob mob) {
        super(mob, LivingEntity.class, 10, false, false, livingEntity -> livingEntity instanceof LivingEntity);
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        }
        if (!(this.mob instanceof CarvedEntity self)) {
            return false;
        }
        if (this.mob.getTarget() != null) {
            return false;
        }
        if (this.mob instanceof OwnableEntity carvedHound && carvedHound.getOwner() != null
           //     && MainConfig.CarvedHoundRetire
        ) {
            return false;
        }
        List<Mob> list = this.mob.level().getEntitiesOfClass(Mob.class, this.mob.getBoundingBox().inflate(32.0, 32.0, 32.0));
        double d = Double.MAX_VALUE;
        for (Mob carved : list) {
            if (carved == null) continue;
            if ((this.mob.distanceToSqr(carved)) > d) continue;
            if (carved.getTarget() == null) continue;
            if ((carved.getTeam() != null || this.mob.getTeam() != null) && !this.mob.isAlliedTo(carved)) continue;
            if (AttackFind.FindCanNotAttack(this.mob, carved.getTarget())) continue;
            if (!AttackFind.SameFactionAvoidDamage(this.mob, carved, false)) continue;
            if (this.mob instanceof Allay && carved instanceof Allay) continue;
            if (EntityFactionFind.isCarved(carved.getType())) {
                if (this.mob.canAttack(carved.getTarget()) && this.mob.canAttackType(carved.getTarget().getType())) {
                    //如果自身为可驯服生物
                    if (this.mob instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                        //不攻击主人
                        if (carved.getTarget() == ownable.getOwner()) {
                            continue;
                        }
                        //不攻击同主人
                        if (carved.getTarget() instanceof OwnableEntity ownableHurt) {
                            if (ownableHurt.getOwner() == ownable.getOwner()) {
                                continue;
                            }
                            if (ownableHurt.getOwner() == ownable.getOwner()) {
                                continue;
                            }
                        }
                        //不攻击主人队友
                        if (carved.getTarget().isAlliedTo(ownable.getOwner())) {
                            continue;
                        }
                    }
                    //不攻击信任者
                    if (self.trusts(carved.getTarget().getUUID())) {
                        continue;
                    }
                    //不攻击信任者的驯服生物
                    if (carved.getTarget() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                        if (self.trusts(ownable.getOwner().getUUID())) {
                            continue;
                        }
                    }
                    //不攻击队友及内讧
                    if (carved.getTarget().isAlliedTo(carved) || carved.getTarget().isAlliedTo(this.mob)) {
                        continue;
                    }
                    attack = carved.getTarget();
                    return true;
                }
            }
        }
        for (Mob carved : list) {
            if (carved == null) continue;
            if ((this.mob.distanceToSqr(carved)) > d) continue;
            if (carved.getLastHurtByMob() == null) continue;
            if ((carved.getTeam() != null || this.mob.getTeam() != null) && !this.mob.isAlliedTo(carved)) continue;
            if (AttackFind.FindCanNotAttack(this.mob, carved.getLastHurtByMob())) continue;
            if (!AttackFind.SameFactionAvoidDamage(this.mob, carved, false)) continue;
            if (EntityFactionFind.isCarved(carved.getType())) {
                if (this.mob.canAttack(carved.getLastHurtByMob()) && this.mob.canAttackType(carved.getLastHurtByMob().getType())) {
                    //如果自身为可驯服生物
                    if (this.mob instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                        //不攻击主人
                        if (carved.getLastHurtByMob() == ownable.getOwner()) {
                            continue;
                        }
                        //不攻击同主人
                        if (carved.getLastHurtByMob() instanceof OwnableEntity ownableHurt) {
                            if (ownableHurt.getOwner() == ownable.getOwner()) {
                                continue;
                            }
                            if (ownableHurt.getOwner() == ownable.getOwner()) {
                                continue;
                            }
                        }
                        //不攻击主人队友
                        if (carved.getLastHurtByMob().isAlliedTo(ownable.getOwner())) {
                            continue;
                        }
                    }
                    //不攻击信任者
                    if (self.trusts(carved.getLastHurtByMob().getUUID())) {
                        continue;
                    }
                    //不攻击信任者的驯服生物
                    if (carved.getLastHurtByMob() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                        if (self.trusts(ownable.getOwner().getUUID())) {
                            continue;
                        }
                    }
                    //不攻击队友及内讧
                    if (carved.getLastHurtByMob().isAlliedTo(carved) || carved.getLastHurtByMob().isAlliedTo(this.mob)) {
                        continue;
                    }
                    attack = carved.getLastHurtByMob();
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

