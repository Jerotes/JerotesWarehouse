package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FogMobEffect extends BaseMobEffectTick {
	public FogMobEffect() {
		super(MobEffectCategory.NEUTRAL, 0xececec);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.fog";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
