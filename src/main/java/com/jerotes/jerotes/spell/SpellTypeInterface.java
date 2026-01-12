package com.jerotes.jerotes.spell;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public interface SpellTypeInterface {
	String getId();
	MagicSpell magicSpellGet(int level, LivingEntity caster, Entity target);

	void stop(LivingEntity caster, int level, boolean must);
}