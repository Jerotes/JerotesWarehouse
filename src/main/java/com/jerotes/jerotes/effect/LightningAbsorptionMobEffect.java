package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class LightningAbsorptionMobEffect extends BaseMobEffect {
	public LightningAbsorptionMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xb858e8);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.lightning_absorption";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
