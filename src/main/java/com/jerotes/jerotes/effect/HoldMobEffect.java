package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;


public class HoldMobEffect extends BaseMobEffectAllTick {
	public HoldMobEffect() {
		super(MobEffectCategory.HARMFUL, 5013390);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.hold_mob";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		if (!livingEntity.level().isClientSide) {
			if (!livingEntity.level().isClientSide()) {
				livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.PARALYZED.get(), 5, n, false, false));
			}
		}
	}
}
