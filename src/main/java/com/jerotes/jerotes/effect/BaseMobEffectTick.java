package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class BaseMobEffectTick extends MobEffect {
	public BaseMobEffectTick(MobEffectCategory mobEffectCategory, int n) {
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
		int n3 = 5 >> n2;
		if (n3 > 0) {
			return n % n3 == 0;
		}
		return true;
	}
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int n3 = 5 >> amplifier;
		if (n3 > 0) {
			return duration % n3 == 0;
		}
		return true;
	}
}
