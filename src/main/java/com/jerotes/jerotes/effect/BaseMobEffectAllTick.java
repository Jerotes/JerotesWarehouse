package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class BaseMobEffectAllTick extends MobEffect {
	public BaseMobEffectAllTick(MobEffectCategory mobEffectCategory, int n) {
		super(mobEffectCategory, n);
	}

	@Override
	public String getDescriptionId() {
		return this.getOrCreateDescriptionId();
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}

	public boolean shouldApplyEffectTickThisTick(int n, int n2) {
		return true;
	}
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
