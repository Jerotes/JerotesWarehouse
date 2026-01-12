package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;


public class PhantasmalKillerEffect extends BaseMobEffectAllTick {
	public PhantasmalKillerEffect() {
		super(MobEffectCategory.HARMFUL, 0xa023ba);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.phantasmal_killer";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		int effectLevel = n + 1;
		//震慑
		if (!livingEntity.level().isClientSide()) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 5, 0, false, false));
			livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.ABACK.get(), 5, 0, false, false));
		}
		if (livingEntity.level() instanceof ServerLevel serverLevel) {
			for (int i = 0; i < 7; ++i) {
				double d = livingEntity.getRandom().nextGaussian() * 0.02;
				double d2 = livingEntity.getRandom().nextGaussian() * 0.02;
				double d3 = livingEntity.getRandom().nextGaussian() * 0.02;
				serverLevel.sendParticles(JerotesParticleTypes.PHANTASMAL_KILLER.get(), livingEntity.getRandomX(1.0), livingEntity.getRandomY() + 0.5, livingEntity.getRandomZ(1.0), 0, d, d2, d3, 1);
			}
		}
		int time = livingEntity.getEffect(JerotesMobEffects.PHANTASMAL_KILLER.get()).getDuration();
		if ((time % 120 == 0) && time != 0 && time != 6) {
			//几率移除
			if (livingEntity.getRandom().nextFloat() > 0.8f - Main.luck(livingEntity) * 0.2f) {
				if (!livingEntity.level().isClientSide) {
					livingEntity.removeEffect(JerotesMobEffects.PHANTASMAL_KILLER.get());
				}
			}
			else {
				DamageSource damageSource = livingEntity.level().damageSources().wither();
				livingEntity.hurt(damageSource, effectLevel * Main.randomReach(RandomSource.create(), 1, 10));
			}
		}
	}
}
