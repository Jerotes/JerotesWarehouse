package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class EnslavementMobEffect extends BaseMobEffectAllTick {
	public EnslavementMobEffect() {
		super(MobEffectCategory.HARMFUL, 1644059);
		}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.enslavement";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		if (EntityFactionFind.isOoze(livingEntity)) {
			return;
		}
		livingEntity.setJumping(false);
		if (livingEntity.onGround() || livingEntity.isInWater()) {
			livingEntity.makeStuckInBlock(livingEntity.getBlockStateOn(), new Vec3(0.2, 0, 0.2));
		}
		else if (!livingEntity.noPhysics && livingEntity.getDeltaMovement().y > -1d) {
			livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, -1d, livingEntity.getDeltaMovement().z);
		}
		if (livingEntity instanceof Mob mob) {
			if (!mob.level().isClientSide) {
				mob.goalSelector.setControlFlag(Goal.Flag.MOVE, false);
				mob.goalSelector.setControlFlag(Goal.Flag.JUMP, false);
			}
		}
	}
}
