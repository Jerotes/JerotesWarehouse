package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Interface.MagicSpellLivingEntity;
import com.jerotes.jerotes.network.JerotesPlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public enum SpellType implements SpellTypeInterface {
	JEROTES_EMPTY("jerotes_empty"),
	JEROTES_MAGIC_MISSILE("jerotes_magic_missile"),
	JEROTES_POISON_BREATH("jerotes_poison_breath"),
	JEROTES_RAY_OF_FIRE("jerotes_ray_of_fire"),
	JEROTES_RAY_OF_FROST("jerotes_ray_of_frost"),
	JEROTES_RAY_OF_LIGHTNING("jerotes_ray_of_lightning"),
	JEROTES_RAY_OF_SICKNESS("jerotes_ray_of_sickness"),
	JEROTES_RAY_OF_ENFEEBLEMENT("jerotes_ray_of_enfeeblement"),
	JEROTES_DISINTEGRATE("jerotes_disintegrate"),
	JEROTES_FINGER_OF_DEATH("jerotes_finger_of_death"),
	JEROTES_LIGHTNING_BOLT("jerotes_lightning_bolt"),
	JEROTES_VICIOUS_MOCKERY("jerotes_vicious_mockery"),
	JEROTES_HOLD_PERSON("jerotes_hold_person"),
	JEROTES_BESTOW_CURSE("jerotes_bestow_curse"),
	JEROTES_PHANTASMAL_KILLER("jerotes_phantasmal_killer"),
	JEROTES_EYEBITE("jerotes_eyebite"),
	JEROTES_FIRE_ABSORPTION("jerotes_fire_absorption"),
	JEROTES_COLD_ABSORPTION("jerotes_cold_absorption"),
	JEROTES_LIGHTNING_ABSORPTION("jerotes_lightning_absorption"),
	JEROTES_LIFE_MEND("jerotes_life_mend"),
	JEROTES_CURE_WOUNDS("jerotes_cure_wounds"),
	JEROTES_MISTY_STEP("jerotes_misty_step"),
	JEROTES_MIRROR_IMAGE("jerotes_mirror_image"),
	JEROTES_INVISIBLE_PASSAGE("jerotes_invisible_passage"),
	JEROTES_COUNTERSPELL("jerotes_counterspell"),
	JEROTES_MAGIC_ABSORPTION("jerotes_magic_absorption"),
	JEROTES_CLOUD_OF_DAGGERS("jerotes_cloud_of_daggers"),
	JEROTES_INSECT_PLAGUE("jerotes_insect_plague"),
	JEROTES_FIREBALL("jerotes_fireball"),
	JEROTES_LINEAR_EVOKER_FANG("jerotes_linear_evoker_fang"),
	JEROTES_CIRCULAR_EVOKER_FANG("jerotes_circular_evoker_fang"),
	JEROTES_CONJURE_VEXES("jerotes_conjure_vexes");

	private final String id;
	SpellType(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}

	public MagicSpell magicSpellGet(int level, LivingEntity caster, Entity target) {
		return switch (this) {
			case JEROTES_EMPTY -> SpellList.Empty(level, caster, target);
			case JEROTES_MAGIC_MISSILE -> SpellList.MagicMissile(level, caster, target);
			case JEROTES_POISON_BREATH -> SpellList.PoisonBreath(level, caster, target);
			case JEROTES_RAY_OF_FIRE -> SpellList.RayofFire(level, caster, target);
			case JEROTES_RAY_OF_FROST -> SpellList.RayofFrost(level, caster, target);
			case JEROTES_RAY_OF_LIGHTNING -> SpellList.RayofLightning(level, caster, target);
			case JEROTES_RAY_OF_SICKNESS -> SpellList.RayofSickness(level, caster, target);
			case JEROTES_RAY_OF_ENFEEBLEMENT -> SpellList.RayofEnfeeblement(level, caster, target);
			case JEROTES_DISINTEGRATE -> SpellList.Disintegrate(level, caster, target);
			case JEROTES_FINGER_OF_DEATH -> SpellList.FingerOfDeath(level, caster, target);
			case JEROTES_LIGHTNING_BOLT -> SpellList.LightningBolt(level, caster, target);
			case JEROTES_VICIOUS_MOCKERY -> SpellList.ViciousMockery(level, caster, target);
			case JEROTES_HOLD_PERSON -> SpellList.HoldPerson(level, caster, target);
			case JEROTES_BESTOW_CURSE -> SpellList.BestowCurse(level, caster, target);
			case JEROTES_PHANTASMAL_KILLER -> SpellList.PhantasmalKiller(level, caster, target);
			case JEROTES_EYEBITE -> SpellList.Eyebite(level, caster, target);
			case JEROTES_FIRE_ABSORPTION -> SpellList.FireAbsorption(level, caster, target);
			case JEROTES_COLD_ABSORPTION -> SpellList.ColdAbsorption(level, caster, target);
			case JEROTES_LIGHTNING_ABSORPTION -> SpellList.LightningAbsorption(level, caster, target);
			case JEROTES_LIFE_MEND -> SpellList.LifeMend(level, caster, target);
			case JEROTES_CURE_WOUNDS -> SpellList.CureWounds(level, caster, target);
			case JEROTES_MISTY_STEP -> SpellList.MistyStep(level, caster, target);
			case JEROTES_MIRROR_IMAGE -> SpellList.MirrorImage(level, caster, target);
			case JEROTES_INVISIBLE_PASSAGE -> SpellList.InvisiblePassage(level, caster, target);
			case JEROTES_COUNTERSPELL -> SpellList.Counterspell(level, caster, target);
			case JEROTES_MAGIC_ABSORPTION -> SpellList.MagicAbsorption(level, caster, target);
			case JEROTES_CLOUD_OF_DAGGERS -> SpellList.CloudOfDaggers(level, caster, target);
			case JEROTES_INSECT_PLAGUE -> SpellList.InsectPlague(level, caster, target);
			case JEROTES_FIREBALL -> SpellList.Fireball(level, caster, target);
			case JEROTES_LINEAR_EVOKER_FANG -> SpellList.LinearEvokerFang(level, caster, target);
			case JEROTES_CIRCULAR_EVOKER_FANG -> SpellList.CircularEvokerFang(level, caster, target);
			case JEROTES_CONJURE_VEXES -> SpellList.ConjureVexes(level, caster, target);
		};
	}


	public static void setLastSpellLevelJerotes(LivingEntity livingEntity, int n) {
		if (!livingEntity.level().isClientSide())
			if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
				magicSpellLivingEntity.setLastSpellLevelJerotes(n);
	}
	public static int getLastSpellLevelJerotes(LivingEntity livingEntity) {
		if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
			return magicSpellLivingEntity.getLastSpellLevelJerotes();
		return 0;
	}
	public static void setLastSpellTypeJerotes(LivingEntity livingEntity, String string) {
		if (!livingEntity.level().isClientSide())
			if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
				magicSpellLivingEntity.setLastSpellTypeJerotes(string);
	}
	public static String getLastSpellTypeJerotes(LivingEntity livingEntity) {
		if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
			return magicSpellLivingEntity.getLastSpellTypeJerotes();
		return "jerotes_empty";
	}
	public static void setLastSpellTargetJerotes(LivingEntity livingEntity, UUID uuid) {
		if (!livingEntity.level().isClientSide())
			if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
				magicSpellLivingEntity.setLastSpellTargetJerotes(uuid);
	}
	public static UUID getLastSpellTargetJerotes(LivingEntity livingEntity) {
		if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
			return magicSpellLivingEntity.getLastSpellTargetJerotes();
		return livingEntity.getUUID();
	}


	public static void setConcentrationSpellLevelJerotes(LivingEntity livingEntity, int n) {
		if (!livingEntity.level().isClientSide())
			if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
				magicSpellLivingEntity.setConcentrationSpellLevelJerotes(n);
	}
	public static int getConcentrationSpellLevelJerotes(LivingEntity livingEntity) {
		if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
			return magicSpellLivingEntity.getConcentrationSpellLevelJerotes();
		return 0;
	}
	public static void setConcentrationSpellTypeJerotes(LivingEntity livingEntity, String string) {
		if (!livingEntity.level().isClientSide())
			if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
				magicSpellLivingEntity.setConcentrationSpellTypeJerotes(string);
	}
	public static String getConcentrationSpellTypeJerotes(LivingEntity livingEntity) {
		if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
			return magicSpellLivingEntity.getConcentrationSpellTypeJerotes();
		return "";
	}
	public static void setConcentrationSpellTargetJerotes(LivingEntity livingEntity, UUID uuid) {
		if (!livingEntity.level().isClientSide())
			if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
				magicSpellLivingEntity.setConcentrationSpellTargetJerotes(uuid);
	}
	public static UUID getConcentrationSpellTargetJerotes(LivingEntity livingEntity) {
		if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
			return magicSpellLivingEntity.getConcentrationSpellTargetJerotes();
		return livingEntity.getUUID();
	}

	public static void setCastCooldownJerotes(LivingEntity livingEntity, int n) {
		if (!livingEntity.level().isClientSide())
			if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
				magicSpellLivingEntity.setCastCooldownJerotes(n);
	}
	public static int getCastCooldownJerotes(LivingEntity livingEntity) {
		if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
			return magicSpellLivingEntity.getCastCooldownJerotes();
		return 0;
	}
	public static void setCastingTickJerotes(LivingEntity livingEntity, int n) {
		if (!livingEntity.level().isClientSide())
			if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
				magicSpellLivingEntity.setCastingTickJerotes(n);
	}
	public static int getCastingTickJerotes(LivingEntity livingEntity) {
		if (livingEntity instanceof MagicSpellLivingEntity magicSpellLivingEntity)
			return magicSpellLivingEntity.getCastingTickJerotes();
		return 0;
	}


	public static void endConcentration(LivingEntity caster) {
		boolean bl = false;
		if (caster.level() instanceof ServerLevel serverLevel) {
			if (serverLevel.getEntity(SpellType.getConcentrationSpellTargetJerotes(caster)) instanceof ConcentrationSpellEntity concentrationSpellEntity) {
				bl = concentrationSpellEntity.endConcentration();
			}
		}
		if (!bl) {
			if (!SpellType.getConcentrationSpellTypeJerotes(caster).isEmpty()) {
				SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(SpellType.getConcentrationSpellTypeJerotes(caster));
				SpellType.endConcentrationMessage(caster, spellTypeInterface.magicSpellGet(1, null, null).getSpellName());
			}
			SpellType.setConcentrationSpellTypeJerotes(caster, "");
			if (caster instanceof Player player) {
				player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
					capability.setConcentrationSpellTarget("");
				});
			}
			SpellType.setConcentrationSpellTargetJerotes(caster, UUID.fromString("00000000-0000-0000-0000-000000000000"));
		}
	}
	public static void endConcentrationMessage(LivingEntity caster, Component components) {
		if (caster instanceof ServerPlayer serverPlayer) {
			Component component = Component.empty()
					.append(Component.translatable("message.jerotes.end_concentration").withStyle(ChatFormatting.GOLD))
					.append(Component.literal(" "))
					.append(components.copy().withStyle(ChatFormatting.AQUA));
			serverPlayer.sendSystemMessage(component);
			serverPlayer.displayClientMessage(component, true);
		}
	}
	public static void startConcentration(LivingEntity caster, ConcentrationSpellEntity concentrationSpellEntity) {
		if (concentrationSpellEntity instanceof Entity entity)
			SpellType.setConcentrationSpellTargetJerotes(caster, entity.getUUID());
	}
	public static void startConcentrationMessage(LivingEntity caster, Component components) {
		if (caster instanceof ServerPlayer serverPlayer) {
			Component component = Component.empty()
					.append(Component.translatable("message.jerotes.start_concentration").withStyle(ChatFormatting.GOLD))
					.append(Component.literal(" "))
					.append(components.copy().withStyle(ChatFormatting.AQUA));
			serverPlayer.sendSystemMessage(component);
			serverPlayer.displayClientMessage(component, true);
		}
	}

	public void stop(LivingEntity caster, int level, boolean must) {
		stopCasting(caster, level, must);
	}
	public static void stopCasting(LivingEntity caster, int level, boolean must) {
		//停止
		if (must || level > SpellType.getLastSpellLevelJerotes(caster)) {
			if (SpellType.getCastingTickJerotes(caster) > -1) {
				if (caster.level() instanceof ServerLevel serverLevel) {
					SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(SpellType.getLastSpellTypeJerotes(caster));
					MagicSpell magicSpell = spellTypeInterface.magicSpellGet(1, caster, serverLevel.getEntity(getLastSpellTargetJerotes(caster)));
					magicSpell.spellCastingStop(0);
				}
			}
			SpellType.setCastingTickJerotes(caster, -1);
			if (must) {
				SpellType.setLastSpellLevelJerotes(caster, 1);
				SpellType.setLastSpellTypeJerotes(caster, "jerotes_empty");
				SpellType.setLastSpellTargetJerotes(caster, caster.getUUID());
			}
		}
	}
}