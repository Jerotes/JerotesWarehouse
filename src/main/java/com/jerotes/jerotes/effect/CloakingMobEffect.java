package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;


public class CloakingMobEffect extends BaseMobEffectAllTick {
	public CloakingMobEffect() {
		super(MobEffectCategory.BENEFICIAL, 6902855);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.cloaking";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		if (Main.getJerotesPersistentData(livingEntity).get("jerotes_cloaking_shift_key_down") != null
				&& !Main.getJerotesPersistentData(livingEntity).getBoolean("jerotes_cloaking_shift_key_down")
		&& livingEntity.isShiftKeyDown()) {
			for (int i = 0; i < 7; ++i) {
				double d = livingEntity.getRandom().nextGaussian() * 0.02;
				double d2 = livingEntity.getRandom().nextGaussian() * 0.02;
				double d3 = livingEntity.getRandom().nextGaussian() * 0.02;
				livingEntity.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, livingEntity.getRandomX(1.0), livingEntity.getRandomY() + 0.5, livingEntity.getRandomZ(1.0), d, d2, d3);
			}
			livingEntity.level().playSound(null, livingEntity.getOnPos(), JerotesSoundEvents.CLOAKING, livingEntity.getSoundSource(), 1.0f, 1.0f);
		}

		if (livingEntity.isShiftKeyDown()) {
			if (!livingEntity.level().isClientSide) {
				livingEntity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 0, false, false));
			}
		}


		Main.getJerotesPersistentData(livingEntity).putBoolean("jerotes_cloaking_shift_key_down", livingEntity.isShiftKeyDown());
	}
}
