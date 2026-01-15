package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.CanBeIllagerFactionEntity;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class JerotesIllagerFactionEntityHelpIllagerGoal extends NearestAttackableTargetGoal<LivingEntity> {
    @Nullable
    private LivingEntity attack;

    public JerotesIllagerFactionEntityHelpIllagerGoal(Mob mob, Class<LivingEntity> class_, boolean bl, @Nullable boolean bl2, Predicate<LivingEntity> predicate) {
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
        if (!(this.mob instanceof CanBeIllagerFactionEntity canBeIllagerFaction && canBeIllagerFaction.isIllagerFaction())) {
            return false;
        }
        List<Mob> list = mob.level().getEntitiesOfClass(Mob.class, mob.getBoundingBox().inflate(32.0, 32.0, 32.0));
        double d = Double.MAX_VALUE;
        //对方目标
        for (Mob findMob : list) {
            if (findMob == null) continue;
            if ((mob.distanceToSqr(findMob)) > d) continue;
            if (findMob.getTarget() == null) continue;
            if (!EntityFactionFind.isRaider(findMob) || EntityFactionFind.isRaider(findMob.getTarget())) continue;
            if ((findMob.getTeam() != null || this.mob.getTeam() != null) && !this.mob.isAlliedTo(findMob)) continue;
            if (AttackFind.FindCanNotAttack(this.mob, findMob.getTarget())) continue;
            if (!AttackFind.FindCanNotAttack(this.mob, findMob) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(findMob)) continue;
            if ((findMob.getTarget() != null && mob.canAttack(findMob.getTarget()) && mob.canAttackType(findMob.getTarget().getType()))) {
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
                    if (findMob.getTarget().isAlliedTo(ownable.getOwner())){
                        break;
                    }
                }
                //不攻击同阵营的驯服生物
                if (findMob.getTarget() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                    if (EntityFactionFind.isRaider(ownable.getOwner())) {
                        break;
                    }
                }
                //不攻击队友及内讧
                if (findMob.getTarget().isAlliedTo(mob) || findMob.getTarget().isAlliedTo(findMob)) {
                    break;
                }
                attack = findMob.getLastHurtByMob();
                return true;
            }
        }
        //对方受伤
        for (Mob findMob : list) {
            if (findMob == null) continue;
            if ((mob.distanceToSqr(findMob)) > d) continue;
            if (findMob.getLastHurtByMob() == null) continue;
            if (!EntityFactionFind.isRaider(findMob) || EntityFactionFind.isRaider(findMob.getLastHurtByMob())) continue;
            if ((findMob.getTeam() != null || this.mob.getTeam() != null) && !this.mob.isAlliedTo(findMob)) continue;
            if (AttackFind.FindCanNotAttack(this.mob, findMob.getLastHurtByMob())) continue;
            if (!AttackFind.FindCanNotAttack(this.mob, findMob) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(findMob)) continue;
            if ((findMob.getLastHurtByMob() != null && mob.canAttack(findMob.getLastHurtByMob()) && mob.canAttackType(findMob.getLastHurtByMob().getType()))) {
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
                    if (findMob.getLastHurtByMob().isAlliedTo(ownable.getOwner())){
                        break;
                    }
                }
                //不攻击同阵营的驯服生物
                if (findMob.getLastHurtByMob() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                    if (EntityFactionFind.isRaider(ownable.getOwner())) {
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

