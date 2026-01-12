package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class JerotesHelpAlliesGoal extends NearestAttackableTargetGoal<LivingEntity> {
    @Nullable
    private LivingEntity attack;

    public JerotesHelpAlliesGoal(Mob mob, Class<LivingEntity> class_, boolean bl, boolean bl2, Predicate<LivingEntity> predicate) {
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
        List<Mob> list = mob.level().getEntitiesOfClass(Mob.class, mob.getBoundingBox().inflate(32.0, 32.0, 32.0));
        double d = Double.MAX_VALUE;
        for (Mob findMob : list) {
            if (findMob == null) continue;
            if ((this.mob.distanceToSqr(findMob)) > d) continue;
            if (this.mob.getTeam() == null || findMob.getTeam() == null) continue;
            if (findMob.getTarget() == null) continue;
            if (!findMob.isAlliedTo(this.mob) || this.mob.isAlliedTo(findMob.getTarget())) continue;
            if (AttackFind.FindCanNotAttack(this.mob, findMob.getTarget())) continue;
            if (!AttackFind.FindCanNotAttack(this.mob, findMob) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(findMob)) continue;
            if ((this.mob.canAttack(findMob.getTarget()) && this.mob.canAttackType(findMob.getTarget().getType()))) {
                //如果自身为可驯服生物
                if (this.mob instanceof OwnableEntity ownable && ownable.getOwner() != null){
                    //不攻击主人
                    if (findMob.getTarget() == ownable.getOwner()) {
                        break;
                    }
                    //不攻击同主人
                    if (findMob.getTarget() instanceof OwnableEntity ownableHurt) {
                        if (ownableHurt.getOwner() == ownable.getOwner()){
                            break;
                        }
                    }
                    //不攻击主人队友
                    if (Objects.requireNonNull(findMob.getTarget()).isAlliedTo(ownable.getOwner())){
                        break;
                    }
                }
                //不攻击同类的驯服生物
                if (findMob.getTarget() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                    if (this.mob.isAlliedTo(ownable.getOwner())) {
                        break;
                    }
                }
                //不攻击队友及内讧
                if (findMob.getTarget().isAlliedTo(mob) || findMob.getTarget().isAlliedTo(findMob)) {
                    break;
                }
                attack = findMob.getTarget();
                return true;
            }
        }
        for (Mob findMob : list) {
            if (findMob == null) continue;
            if ((this.mob.distanceToSqr(findMob)) > d) continue;
            if (this.mob.getTeam() == null || findMob.getTeam() == null) continue;
            if (findMob.getLastHurtByMob() == null) continue;
            if (!findMob.isAlliedTo(this.mob) || this.mob.isAlliedTo(findMob.getLastHurtByMob())) continue;
            if (AttackFind.FindCanNotAttack(this.mob, findMob.getLastHurtByMob())) continue;
            if (!AttackFind.FindCanNotAttack(this.mob, findMob) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(findMob)) continue;
            if ((this.mob.canAttack(findMob.getLastHurtByMob()) && this.mob.canAttackType(findMob.getLastHurtByMob().getType()))) {
                //如果自身为可驯服生物
                if (this.mob instanceof OwnableEntity ownable && ownable.getOwner() != null){
                    //不攻击主人
                    if (findMob.getLastHurtByMob() == ownable.getOwner()) {
                        break;
                    }
                    //不攻击同主人
                    if (findMob.getLastHurtByMob() instanceof OwnableEntity ownableHurt) {
                        if (ownableHurt.getOwner() == ownable.getOwner()){
                            break;
                        }
                    }
                    //不攻击主人队友
                    if (Objects.requireNonNull(findMob.getLastHurtByMob()).isAlliedTo(ownable.getOwner())){
                        break;
                    }
                }
                //不攻击同类的驯服生物
                if (findMob.getLastHurtByMob() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                    if (this.mob.isAlliedTo(ownable.getOwner())) {
                        break;
                    }
                }
                //不攻击队友及内讧
                if (findMob.getLastHurtByMob().isAlliedTo(mob) || findMob.getLastHurtByMob().isAlliedTo(findMob)) {
                    break;
                }
                attack = findMob.getLastHurtByMob();
                return true;
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

