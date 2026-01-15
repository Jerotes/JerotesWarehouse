package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal {

    @Shadow @Final protected Class<T> targetType;

    @Shadow protected TargetingConditions targetConditions;

    @Shadow protected abstract AABB getTargetSearchArea(double p_26069_);

    @Shadow @Nullable protected LivingEntity target;

    public NearestAttackableTargetGoalMixin(Mob p_26140_, boolean p_26141_) {
        super(p_26140_, p_26141_);
    }

    @Inject(method = "findTarget", at = @At("HEAD"), cancellable = true)
    protected void findTarget(CallbackInfo ci) {
        if (this.targetType == Player.class ||this.targetType == ServerPlayer.class) {
            LivingEntity find = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(this.getFollowDistance()), (finds) -> {
                return finds.canBeSeenAsEnemy() && this.mob.canAttack(finds) && finds instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.beTargetAsPlayer();
            }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

            Player findPlayer = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

            if (find == null)
                return;
            if (findPlayer == null || find.distanceTo(this.mob) < findPlayer.distanceTo(this.mob)) {
                this.target = find;
                ci.cancel();
            }
        }

    }
}