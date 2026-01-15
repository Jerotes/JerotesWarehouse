package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.init.JerotesMobEffectTags;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class JerotesHelpSameFactionGoal extends NearestAttackableTargetGoal<LivingEntity> {
    @Nullable
    private LivingEntity attack;

    public JerotesHelpSameFactionGoal(Mob mob, Class<LivingEntity> class_, boolean bl, boolean bl2, Predicate<LivingEntity> predicate) {
        super(mob, class_, 10, bl, bl2, predicate);
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        }
        if (mob.getTarget() != null) {
            return false;
        }
        List<Mob> list = mob.level().getEntitiesOfClass(Mob.class, mob.getBoundingBox().inflate(32.0, 32.0, 32.0));
        double d = Double.MAX_VALUE;
        for (Mob findMob : list) {
            if (findMob == null) continue;
            if ((mob.distanceToSqr(findMob)) > d) continue;
            if (findMob.getTarget() == null) continue;
            if (!isCanAttack(mob, findMob, findMob.getTarget())) continue;
            if ((findMob.getTeam() != null || mob.getTeam() != null) && !mob.isAlliedTo(findMob)) continue;
            //如果自己不是帮助所有人 也不是帮助同类的人 并且不帮助对方
            if (mob instanceof JerotesEntity jerotesEntity && !jerotesEntity.helpAllSameFaction() && !jerotesEntity.helpSameType() && !jerotesEntity.canBeHelp(findMob)) continue;
            //如果自己是只帮助同类类型
            if (mob instanceof JerotesEntity jerotesEntity && !jerotesEntity.helpAllSameFaction() && jerotesEntity.helpSameType() && mob.getType() != findMob.getType()) continue;
            //如果对方是不被所有帮助 也不是被帮助同类类型 并且不被帮助对方
            if (findMob instanceof JerotesEntity jerotesEntity && !jerotesEntity.helpByAllSameFaction() && !(jerotesEntity.helpSameType() && mob.getType() != findMob.getType()) && !(jerotesEntity.canBeHelp(mob))) continue;
            if (AttackFind.FindCanNotAttack(this.mob, findMob.getTarget())) continue;
            if (!AttackFind.FindCanNotAttack(this.mob, findMob) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(findMob)) continue;
            if ((mob.canAttack(findMob.getTarget()) && mob.canAttackType(findMob.getTarget().getType()))) {
                //如果自身为可驯服生物
                if (mob instanceof OwnableEntity ownable && ownable.getOwner() != null){
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
                //不攻击同阵营的驯服生物
                if (findMob.getTarget() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                    if (!isCanAttack(mob, findMob, ownable.getOwner())){
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
            if ((mob.distanceToSqr(findMob)) > d) continue;
            if (findMob.getLastHurtByMob() == null) continue;
            if (!isCanAttack(mob, findMob, findMob.getLastHurtByMob())) continue;
            if ((findMob.getTeam() != null || mob.getTeam() != null) && !mob.isAlliedTo(findMob)) continue;
            //如果自己不是帮助所有人 也不是帮助同类的人 并且不帮助对方
            if (mob instanceof JerotesEntity jerotesEntity && !jerotesEntity.helpAllSameFaction() && !jerotesEntity.helpSameType() && !jerotesEntity.canBeHelp(findMob)) continue;
            //如果自己是只帮助同类类型
            if (mob instanceof JerotesEntity jerotesEntity && !jerotesEntity.helpAllSameFaction() && jerotesEntity.helpSameType() && mob.getType() != findMob.getType()) continue;
            //如果对方是不被所有帮助 也不是被帮助同类类型 并且不被帮助对方
            if (findMob instanceof JerotesEntity jerotesEntity && !jerotesEntity.helpByAllSameFaction() && !(jerotesEntity.helpSameType() && mob.getType() != findMob.getType()) && !(jerotesEntity.canBeHelp(mob))) continue;
            if (AttackFind.FindCanNotAttack(this.mob, findMob.getLastHurtByMob())) continue;
            if (!AttackFind.FindCanNotAttack(this.mob, findMob) || !EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(findMob)) continue;
            if ((mob.canAttack(findMob.getLastHurtByMob()) && mob.canAttackType(findMob.getLastHurtByMob().getType()))) {
                //如果自身为可驯服生物
                if (mob instanceof OwnableEntity ownable && ownable.getOwner() != null){
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
                //不攻击同阵营的驯服生物
                if (findMob.getLastHurtByMob() instanceof OwnableEntity ownable && ownable.getOwner() != null) {
                    if (!isCanAttack(mob, findMob, ownable.getOwner())){
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

    public boolean isCanAttacks(Mob mob, Mob findMob, LivingEntity findMobTarget) {
        //蛇龙阵营
        if (EntityFactionFind.isFactionSerponCombatTeam(mob)) {
            AtomicBoolean canNot = new AtomicBoolean(false);
            findMobTarget.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTagOrEmpty(JerotesMobEffectTags.SERPON_FACTION).forEach(effect -> {
                if (findMobTarget.hasEffect(effect.get())) {
                    canNot.set(true);
                }
            });
            return !canNot.get();
        }
        return true;
    }

    public boolean isCanAttack(Mob mob, Mob findMob, LivingEntity findMobTarget) {
        //与队友为同伴
        return EntityFactionFind.isFaction(mob, findMob)
                //额外判定
                && isCanAttacks(mob, findMob, findMobTarget)
                //目标与队友并非内讧
                && !(EntityFactionFind.isFaction(findMobTarget, findMob))
                //目标并非自己同伴
                && !(EntityFactionFind.isFaction(mob, findMobTarget));
    }
}

