package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.entity.Mob.JerotesPlayerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LookAtPlayerGoal.class)
public abstract class LookAtPlayerGoalMixin extends Goal {

    @Shadow @Final protected Class<? extends LivingEntity> lookAtType;

    @Shadow @Final protected Mob mob;

    @Shadow @Final protected float probability;

    @Shadow @Final protected TargetingConditions lookAtContext;

    @Shadow @Final protected float lookDistance;

    @Shadow @Nullable protected Entity lookAt;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    protected void canUse(CallbackInfoReturnable<Boolean> cir) {
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return;
        }
        if (this.mob.getTarget() != null) {
            return;
        }
        if (this.lookAtType == Player.class) {
            LivingEntity find = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(LivingEntity.class, this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0, (double)this.lookDistance), (finds) -> {
                return finds instanceof JerotesPlayerBaseEntity jerotesPlayerBaseEntity && jerotesPlayerBaseEntity.beLookAsPlayer();
            }), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

            if (find == null)
                return;
            Player findPlayer = this.mob.level().getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

            if (findPlayer == null || find.distanceTo(this.mob) < findPlayer.distanceTo(this.mob)) {
                this.lookAt = find;
                cir.setReturnValue(true);
            }
        }

    }
}