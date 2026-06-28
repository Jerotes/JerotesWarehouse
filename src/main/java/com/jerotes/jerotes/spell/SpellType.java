package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.util.Main;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public enum SpellType implements SpellTypeInterface {
	JEROTES_MAGIC_MISSILE("jerotes_magic_missile"),
	JEROTES_POISON_BREATH("jerotes_poison_breath"),
	JEROTES_RAY_OF_SICKNESS("jerotes_ray_of_sickness"),
	JEROTES_RAY_OF_ENFEEBLEMENT("jerotes_ray_of_enfeeblement"),
	JEROTES_LIGHTNING_BOLT("jerotes_lightning_bolt"),
	JEROTES_VICIOUS_MOCKERY("jerotes_vicious_mockery"),
	JEROTES_HOLD_PERSON("jerotes_hold_person"),
	JEROTES_BESTOW_CURSE("jerotes_bestow_curse"),
	JEROTES_PHANTASMAL_KILLER("jerotes_phantasmal_killer"),
	JEROTES_EYEBITE("jerotes_eyebite"),
	JEROTES_FIRE_ABSORPTION("jerotes_fire_absorption"),
	JEROTES_FREEZE_ABSORPTION("jerotes_freeze_absorption"),
	JEROTES_LIGHTNING_ABSORPTION("jerotes_lightning_absorption"),
	JEROTES_CURE_WOUNDS("jerotes_cure_wounds"),
	JEROTES_MISTY_STEP("jerotes_misty_step"),
	JEROTES_MIRROR_IMAGE("jerotes_mirror_image"),
	JEROTES_INVISIBLE_PASSAGE("jerotes_invisible_passage"),
	JEROTES_COUNTERSPELL("jerotes_counterspell"),
	JEROTES_MAGIC_ABSORPTION("jerotes_magic_absorption"),
	JEROTES_CLOUD_OF_DAGGERS("jerotes_cloud_of_daggers");

	private final String id;
	SpellType(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}

	public MagicSpell magicSpellGet(int level, LivingEntity caster, Entity target) {
		return switch (this) {
			case JEROTES_MAGIC_MISSILE -> SpellList.MagicMissile(level, caster, target);
			case JEROTES_POISON_BREATH -> SpellList.PoisonBreath(level, caster, target);
			case JEROTES_RAY_OF_SICKNESS -> SpellList.RayofSickness(level, caster, target);
			case JEROTES_RAY_OF_ENFEEBLEMENT -> SpellList.RayofEnfeeblement(level, caster, target);
			case JEROTES_LIGHTNING_BOLT -> SpellList.LightningBolt(level, caster, target);
			case JEROTES_VICIOUS_MOCKERY -> SpellList.ViciousMockery(level, caster, target);
			case JEROTES_HOLD_PERSON -> SpellList.HoldPerson(level, caster, target);
			case JEROTES_BESTOW_CURSE -> SpellList.BestowCurse(level, caster, target);
			case JEROTES_PHANTASMAL_KILLER -> SpellList.PhantasmalKiller(level, caster, target);
			case JEROTES_EYEBITE -> SpellList.Eyebite(level, caster, target);
			case JEROTES_FIRE_ABSORPTION -> SpellList.FireAbsorption(level, caster, target);
			case JEROTES_FREEZE_ABSORPTION -> SpellList.FreezeAbsorption(level, caster, target);
			case JEROTES_LIGHTNING_ABSORPTION -> SpellList.LightningAbsorption(level, caster, target);
			case JEROTES_CURE_WOUNDS -> SpellList.CureWounds(level, caster, target);
			case JEROTES_MISTY_STEP -> SpellList.MistyStep(level, caster, target);
			case JEROTES_MIRROR_IMAGE -> SpellList.MirrorImage(level, caster, target);
			case JEROTES_INVISIBLE_PASSAGE -> SpellList.InvisiblePassage(level, caster, target);
			case JEROTES_COUNTERSPELL -> SpellList.Counterspell(level, caster, target);
			case JEROTES_MAGIC_ABSORPTION -> SpellList.MagicAbsorption(level, caster, target);
			case JEROTES_CLOUD_OF_DAGGERS -> SpellList.CloudOfDaggers(level, caster, target);
		};
	}

	public void stop(LivingEntity caster, int level, boolean must) {
		stops(caster, level, must);
	}
	public static void stops(LivingEntity caster, int level, boolean must) {
		//魔法飞弹
		if (must || level > Main.getJerotesPersistentData(caster).getInt("jerotes_magic_missile_spellLevelDamage")) {
			Main.persistentDataRemove(caster, "jerotes_magic_missile");
			Main.persistentDataRemove(caster, "jerotes_magic_missile_spellLevelDamage");
			Main.persistentDataRemove(caster, "jerotes_magic_missile_target");
			Main.persistentDataRemove(caster, "jerotes_magic_missile_spellLevelAccuracy");
			Main.persistentDataRemove(caster, "jerotes_magic_missile_count");
			Main.persistentDataRemove(caster, "jerotes_magic_missile_distance");
		}
	}
}