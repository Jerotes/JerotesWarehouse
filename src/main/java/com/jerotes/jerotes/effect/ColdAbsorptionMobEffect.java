package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class ColdAbsorptionMobEffect extends BaseMobEffect {
	public ColdAbsorptionMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0x6e98ff);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.cold_absorption";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
