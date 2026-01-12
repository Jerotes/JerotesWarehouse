package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class TruesightMobEffect extends BaseMobEffectTick {
	public TruesightMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 10878931);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.truesight";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
