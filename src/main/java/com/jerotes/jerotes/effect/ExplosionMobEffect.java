package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ExplosionMobEffect extends BaseMobEffectInstantenous {
	public ExplosionMobEffect() {
		super(MobEffectCategory.HARMFUL, 0xdd3a11);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.explosion";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		livingEntity.level().explode(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), n + 1, Level.ExplosionInteraction.NONE);
		livingEntity.removeEffect(JerotesMobEffects.EXPLOSION.get());
	}


	@Override
	public void applyInstantenousEffect(@Nullable Entity p_19462_, @Nullable Entity p_19463_, LivingEntity p_19464_, int p_19465_, double p_19466_) {

		p_19464_.level().explode(null, p_19464_.getX(), p_19464_.getY(), p_19464_.getZ(), p_19465_ + 1, Level.ExplosionInteraction.NONE);
		p_19464_.removeEffect(JerotesMobEffects.EXPLOSION.get());

	}
}
