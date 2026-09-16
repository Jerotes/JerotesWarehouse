package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ProneMobEffect extends BaseMobEffect {
	public ProneMobEffect() {
		super(MobEffectCategory.HARMFUL, 0x008080);
		}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.prone";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
