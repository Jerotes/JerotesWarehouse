package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;


public class HoldMobEffect extends BaseMobEffectAllTick {
	public HoldMobEffect() {
		super(MobEffectCategory.HARMFUL, 5013390);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.hold_mob";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		int time = livingEntity.getEffect(JerotesMobEffects.HOLD_MOB.get()).getDuration();
		if ((time % 120 == 0) && time != 0 && time != 6 && (livingEntity.getRandom().nextFloat() > 0.8f - Main.luck(livingEntity) * 0.2f)) {
			if (!livingEntity.level().isClientSide) {
				livingEntity.removeEffect(JerotesMobEffects.HOLD_MOB.get());
			}
		}
		if ((time % 60 == 0) && time != 0 && (livingEntity.getRandom().nextFloat() > 0.6f - Main.luck(livingEntity) * 0.2f) && EntityAndItemFind.isLegendary(livingEntity)) {
			if (!livingEntity.level().isClientSide) {
				livingEntity.removeEffect(JerotesMobEffects.HOLD_MOB.get());
			}
		}
		if(livingEntity.getDeltaMovement().y > 0){
			livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(0.2, 0, 0.2));
		}
		livingEntity.setJumping(false);
		livingEntity.stopUsingItem();
		if (livingEntity.onGround() || livingEntity.isInWater() || livingEntity.isNoGravity()) {
			livingEntity.makeStuckInBlock(livingEntity.getBlockStateOn(), new Vec3(0.25, 0.05, 0.25));
		}
		if (livingEntity instanceof Mob mob) {
			mob.getNavigation().stop();
			if (!mob.level().isClientSide) {
				mob.goalSelector.setControlFlag(Goal.Flag.MOVE, false);
				mob.goalSelector.setControlFlag(Goal.Flag.JUMP, false);
				mob.goalSelector.setControlFlag(Goal.Flag.LOOK, false);
			}
		}
	}
}
