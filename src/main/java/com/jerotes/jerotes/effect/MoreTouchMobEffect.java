package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class MoreTouchMobEffect extends BaseMobEffectTick {
	public MoreTouchMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0x066b6b);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.more_touch";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
