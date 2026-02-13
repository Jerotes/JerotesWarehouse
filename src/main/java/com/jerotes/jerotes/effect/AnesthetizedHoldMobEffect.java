package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class AnesthetizedHoldMobEffect extends BaseMobEffectAllTick {
	public AnesthetizedHoldMobEffect() {
		super(MobEffectCategory.HARMFUL, -14890162);
		}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.anesthetized_hold";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		if (EntityAndItemFind.isLegendary(livingEntity))
			return;
		float headXRot;
		if (livingEntity.getXRot() < 45){
			headXRot = livingEntity.getXRot() + (n + 1);
		}
		else {
			headXRot = 45;
		}
		livingEntity.setXRot(headXRot);
		livingEntity.xRotO = livingEntity.getXRot();
		livingEntity.setJumping(false);
		livingEntity.stopUsingItem();
		if (livingEntity.onGround() || livingEntity.isInWater()) {
			livingEntity.makeStuckInBlock(livingEntity.getBlockStateOn(), new Vec3(0.2, 0, 0.2));
		}
		else if (!livingEntity.noPhysics && livingEntity.getDeltaMovement().y > -1d) {
			livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, -1d, livingEntity.getDeltaMovement().z);
		}
		if (livingEntity instanceof Mob mob) {
			mob.setAggressive(false);
			mob.setTarget(null);
			mob.getNavigation().stop();
			if (mob instanceof NeutralMob neutralMob) {
				neutralMob.stopBeingAngry();
			}
			if (!mob.level().isClientSide) {
				mob.goalSelector.setControlFlag(Goal.Flag.MOVE, false);
				mob.goalSelector.setControlFlag(Goal.Flag.JUMP, false);
				mob.goalSelector.setControlFlag(Goal.Flag.LOOK, false);
				mob.goalSelector.setControlFlag(Goal.Flag.TARGET, false);
			}
		}
		super.applyEffectTick(livingEntity, n);
	}
}
