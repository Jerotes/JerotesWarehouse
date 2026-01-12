package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AbackMobEffect extends BaseMobEffect {
	public AbackMobEffect() {
		super(MobEffectCategory.HARMFUL, -8257536);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.aback";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
