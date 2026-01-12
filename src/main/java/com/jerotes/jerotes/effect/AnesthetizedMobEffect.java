package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;

public class AnesthetizedMobEffect extends BaseMobEffectTick {
	public AnesthetizedMobEffect() {
		super(MobEffectCategory.HARMFUL, -14890162);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.anesthetized";
	}
}
