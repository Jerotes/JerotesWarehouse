package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class ParalyzedMobEffect extends BaseMobEffectAllTick {
	public ParalyzedMobEffect() {
		super(MobEffectCategory.HARMFUL, 0xdde63e);
		}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.paralyzed";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		//失能
		if (!livingEntity.level().isClientSide) {
			if (!livingEntity.level().isClientSide()) {
				livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.SPELL_REACTION_LOCK.get(), 5, n, false, false));
			}
		}
		//行动
		livingEntity.setJumping(false);
		livingEntity.stopUsingItem();
		if (livingEntity.onGround() || livingEntity.isInWater() || livingEntity.isNoGravity()) {
			livingEntity.makeStuckInBlock(livingEntity.getBlockStateOn(), new Vec3(0, 0, 0));
		}
		else if (!livingEntity.noPhysics && livingEntity.getDeltaMovement().y > -1d && !livingEntity.onGround()) {
			livingEntity.makeStuckInBlock(livingEntity.getBlockStateOn(), new Vec3(0, 1, 0));
			livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().x, -1d, livingEntity.getDeltaMovement().z);
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
