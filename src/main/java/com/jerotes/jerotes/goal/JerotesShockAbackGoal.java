package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.ShockEntity;
import com.jerotes.jerotes.entity.TameMobEntity;
import com.jerotes.jerotes.init.JerotesMobEffectTags;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JerotesShockAbackGoal extends Goal {
    private final PathfinderMob shock;
    @Nullable
    private LivingEntity find;
    private double speedModifier;
    private int timeToRecalcPath;

    public JerotesShockAbackGoal(PathfinderMob shock, double d) {
        this.shock = shock;
        this.speedModifier = d;
    }

    @Override
    public boolean canUse() {
        if (!MainConfig.MobHasShockAback) {
            return false;
        }
        if (this.shock.getRandom().nextInt(20) != 1) {
            return false;
        }
        //坐姿
        if (shock instanceof TamableAnimal tamable && tamable.isInSittingPose() || shock instanceof TameMobEntity tamables && tamables.isInSittingPose()) {
            return false;
        }
        //无视震撼
        if (shock instanceof TamableAnimal tamable && tamable.isInSittingPose() || shock instanceof TameMobEntity tamables && tamables.isInSittingPose()) {
            return false;
        }
        //震撼免疫
        if (EntityAndItemFind.isAbackAwayImmune(shock.getType())) {
            return false;
        }
        //失能
        if (shock.isNoAi() || !shock.isAlive()) {
            return false;
        }

        List<LivingEntity> list = this.shock.level().getEntitiesOfClass(LivingEntity.class, this.shock.getBoundingBox().inflate(64.0, 64.0, 64.0));
        list.removeIf(livingEntity -> !(livingEntity instanceof ShockEntity shocked && shocked.useShock()));
        if (list.isEmpty()) {
            return false;
        }
        LivingEntity find = null;
        double speed = this.speedModifier;
        for (LivingEntity shockUser : list) {
            //失能
            if (shockUser instanceof Mob mob && mob.isNoAi() || !shockUser.isAlive()) continue;
            //震撼生物
            if (!(shockUser instanceof ShockEntity shocked && shocked.useShock())) continue;
            if (this.shock.distanceTo(shockUser) > shocked.ShockLevel() * 5) continue;
            if (!(shocked.useShockTo(shock))) continue;
            //排除不可伤害
            if (AttackFind.FindCanNotAttack(shockUser, this.shock)) continue;
            if (shockUser instanceof OwnableEntity ownable && ownable.getOwner() != null && AttackFind.FindCanNotAttack(ownable.getOwner(), this.shock)) continue;
            //震撼免疫
            if (this.shock.getHealth() > shockUser.getHealth() / 4) continue;
            AtomicBoolean can = new AtomicBoolean(false);
            this.shock.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTagOrEmpty(JerotesMobEffectTags.ABACK_AWAY_IMMUNE).forEach(effect -> {
                if (this.shock.hasEffect(effect.get())) {
                    can.set(true);
                }
            });
            if (can.get()) continue;
            find = shockUser;
        }
        if (find == null) {
            return false;
        }
        //起床
        if (shock.isSleeping()) {
            shock.stopSleeping();
        }
        //目标清除
        if (shock.getTarget() != find && !(find instanceof Mob mob && mob.getTarget() == shock)) {
            shock.setTarget(null);
        }
        this.speedModifier = speed;
        this.find = find;
        shock.getNavigation().stop();
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (!MainConfig.MobHasShockAback) {
            return false;
        }
        //坐姿
        if (shock instanceof TamableAnimal tamableFlower && tamableFlower.isInSittingPose() || shock instanceof TameMobEntity tamableFlowers && tamableFlowers.isInSittingPose()) {
            return false;
        }
        //无视震撼
        if (shock instanceof TamableAnimal tamableFlower && tamableFlower.isInSittingPose() || shock instanceof TameMobEntity tamableFlowers && tamableFlowers.isInSittingPose()) {
            return false;
        }
        //震撼免疫
        if (EntityAndItemFind.isAbackAwayImmune(shock.getType())) {
            return false;
        }
        //失能
        if (shock.isNoAi() || !shock.isAlive()) {
            return false;
        }

        double d = 0;
        if (this.find != null) {
            d = this.shock.distanceToSqr(this.find);
        }
        return !(d < 4.0) && !(d > 1024.0);
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.find = null;
    }

    @Override
    public void tick() {
        if (this.find != null) {
            if (--this.timeToRecalcPath > 0) {
                return;
            }
            this.timeToRecalcPath = this.adjustedTickDelay(10);

            for (int i = 0; i < 4; ++i) {
                Vec3 vec = LandRandomPos.getPosAway(shock, 32, 24, this.find.position());
                if (vec != null) {
                    boolean blNow = true;
                    if (shock.getNavigation().getTargetPos() != null) {
                        Vec3 now = new Vec3(shock.getNavigation().getTargetPos().getX(), shock.getNavigation().getTargetPos().getY(), shock.getNavigation().getTargetPos().getZ());
                        if (vec.distanceTo(this.find.position()) <= now.distanceTo(this.find.position())) {
                            blNow = false;
                        }
                    }
                    //当前与目标位置 要比 当前与被逃离位置 更远
                    if (blNow
                            //目标与被逃离位置 要比 当前与被逃离位置 更远
                            && (vec.distanceTo(this.find.position()) > shock.distanceTo(this.find))
                            //被逃离与目标位置 要比 当前与目标位置 更远
                            && (vec.distanceTo(this.find.position()) > vec.distanceTo(shock.position()) || i < 2)) {
                        shock.getNavigation().moveTo(vec.x, vec.y, vec.z, 1.4d);
                        break;
                    }
                }
            }
        }
    }
}

