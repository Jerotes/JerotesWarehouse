package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;


public class InvisiblePassageMobEffect extends BaseMobEffectAllTick {
	public InvisiblePassageMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xc5c5c5);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.invisible_passage";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		if (!livingEntity.level().isClientSide()) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 0, false, false));
		}
	}
}
