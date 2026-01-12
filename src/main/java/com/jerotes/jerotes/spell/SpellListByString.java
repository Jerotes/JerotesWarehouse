package com.jerotes.jerotes.spell;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SpellListByString {

	public static MagicSpell getSpell(int level, LivingEntity caster, Entity target, SpellTypeInterface spellTypes) {
		return spellTypes.magicSpellGet(level, caster, target);
	}
	public static MagicSpell getSpellEasy(SpellTypeInterface spellTypes) {
		return getSpell(getSpell(1, null, null, spellTypes).baseSpellLevel(), null, null, spellTypes);
	}
}