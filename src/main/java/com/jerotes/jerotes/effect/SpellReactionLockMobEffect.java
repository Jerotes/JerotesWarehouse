package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;


public class SpellReactionLockMobEffect extends BaseMobEffect {
	public SpellReactionLockMobEffect() {
		super(MobEffectCategory.NEUTRAL, 0x56a9c5);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.spell_reaction_lock";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
	}
}
