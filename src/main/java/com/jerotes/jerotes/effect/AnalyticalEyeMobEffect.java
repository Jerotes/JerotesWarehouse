package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;

public class AnalyticalEyeMobEffect extends BaseMobEffectTick {
	public AnalyticalEyeMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -14890162);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.analytical_eye";
	}
}
