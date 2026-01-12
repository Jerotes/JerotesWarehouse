package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class FireAbsorptionMobEffect extends BaseMobEffect {
	public FireAbsorptionMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xcf3000);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.fire_absorption";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
