package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class CounterspellMobEffect extends BaseMobEffect {
	public CounterspellMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0x7ccde8);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.counterspell";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
