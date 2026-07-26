package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;


public class TrueInvisibilityMobEffect extends BaseMobEffectAllTick {
	public TrueInvisibilityMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xdfdfdf);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.true_invisbility";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		if (!livingEntity.level().isClientSide()) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 0, false, false));
		}
	}
}
