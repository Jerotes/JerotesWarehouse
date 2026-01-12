package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class MagicAbsorptionMobEffect extends BaseMobEffectTick {
	public MagicAbsorptionMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -13434829);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.magic_absorption";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		int effectLevel = n + 1;
		float effectBase = 0;
		if (effectLevel <= 3) {
			effectBase = effectLevel;
		}
		else {
			effectBase = 3 + (effectLevel - 3) * 0.35f;
		}
		if (livingEntity.getHealth() > livingEntity.getMaxHealth()/2) {
			livingEntity.heal( effectBase * 0.1f);
		}
		if (livingEntity.getHealth() <= livingEntity.getMaxHealth()/2) {
			livingEntity.heal( effectBase * 0.2f);
		}
	}
}
