package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class QuakeMobEffect extends BaseMobEffect {
	public QuakeMobEffect() {
		super(MobEffectCategory.NEUTRAL, 16771451);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.quake";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
