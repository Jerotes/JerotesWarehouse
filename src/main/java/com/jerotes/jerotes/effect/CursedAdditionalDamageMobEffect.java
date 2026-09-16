package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.init.JerotesParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class CursedAdditionalDamageMobEffect extends BaseMobEffectAllTick {
	public CursedAdditionalDamageMobEffect() {
		super(MobEffectCategory.HARMFUL, 0x4f4a74);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.cursed_additional_damage";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
