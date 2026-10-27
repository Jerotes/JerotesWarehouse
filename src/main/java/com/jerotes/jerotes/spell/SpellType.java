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
	JEROTES_BLAZE_FIREBALL("jerotes_blaze_fireball"),
	JEROTES_BREEZE_WIND_CHARGE("jerotes_breeze_wind_charge"),
	JEROTES_GHAST_FIREBALL("jerotes_ghast_fireball"),
	JEROTES_ENDERDRAGON_FIREBALL("jerotes_enderdragon_fireball"),
	JEROTES_POISON_BREATH("jerotes_poison_breath"),
	JEROTES_COLOR_SPRAY("jerotes_color_spray"),
	JEROTES_FIRE_BOLT("jerotes_fire_bolt"),
	JEROTES_FROST_BOLT("jerotes_frost_bolt"),
	JEROTES_SHOCK_BOLT("jerotes_shock_bolt"),
	JEROTES_FLAMES_SPRAY("jerotes_flames_spray"),
	JEROTES_FROSTBITE_SPRAY("jerotes_frostbite_spray"),
	JEROTES_SPARKS_SPRAY("jerotes_sparks_spray"),
	JEROTES_WITCH_BOLT("jerotes_witch_bolt"),
	JEROTES_ICE_KNIFE("jerotes_ice_knife"),
	JEROTES_RAY_OF_FIRE("jerotes_ray_of_fire"),
	JEROTES_RAY_OF_FROST("jerotes_ray_of_frost"),
	JEROTES_RAY_OF_LIGHTNING("jerotes_ray_of_lightning"),
	JEROTES_ELDRITCH_BLAST("jerotes_eldritch_blast"),
	JEROTES_SCORCHING_RAY("jerotes_scorching_ray"),
	JEROTES_RAY_OF_SICKNESS("jerotes_ray_of_sickness"),
	JEROTES_RAY_OF_ENFEEBLEMENT("jerotes_ray_of_enfeeblement"),
	JEROTES_DISINTEGRATE("jerotes_disintegrate"),
	JEROTES_FINGER_OF_DEATH("jerotes_finger_of_death"),
	JEROTES_LIGHTNING_BOLT("jerotes_lightning_bolt"),
	JEROTES_CHAIN_LIGHTNING("jerotes_chain_lightning"),
	JEROTES_SUNBEAM("jerotes_sunbeam"),
	JEROTES_INFESTATION("jerotes_infestation"),
	JEROTES_SHOCKING_GRASP("jerotes_shocking_grasp"),
	JEROTES_GUARDIAN_LASER("jerotes_guardian_laser"),
	JEROTES_VICIOUS_MOCKERY("jerotes_vicious_mockery"),
	JEROTES_HOLD_PERSON("jerotes_hold_person"),
	JEROTES_BESTOW_CURSE("jerotes_bestow_curse"),
	JEROTES_REMOVE_CURSE("jerotes_remove_curse"),
	JEROTES_BLINDNESS_CURSE("jerotes_blindness_curse"),
	JEROTES_FATIGUE_CURSE("jerotes_fatigue_curse"),
	JEROTES_DARKNESS_CURSE("jerotes_darkness_curse"),
	JEROTES_PHANTASMAL_KILLER("jerotes_phantasmal_killer"),
	JEROTES_EYEBITE("jerotes_eyebite"),
	JEROTES_HARM("jerotes_harm"),
	JEROTES_FIRE_ABSORPTION("jerotes_fire_absorption"),
	JEROTES_COLD_ABSORPTION("jerotes_cold_absorption"),
	JEROTES_LIGHTNING_ABSORPTION("jerotes_lightning_absorption"),
	JEROTES_LIFE_MEND("jerotes_life_mend"),
	JEROTES_CURE_WOUNDS("jerotes_cure_wounds"),
	JEROTES_MENDING("jerotes_mending"),
	JEROTES_FEATHER_FALL("jerotes_feather_fall"),
	JEROTES_DARKVISION("jerotes_darkvision"),
	JEROTES_MISTY_STEP("jerotes_misty_step"),
	JEROTES_ENDER_STEP("jerotes_ender_step"),
	JEROTES_MIRROR_IMAGE("jerotes_mirror_image"),
	JEROTES_INVISIBILITY("jerotes_invisibility"),
	JEROTES_INVISIBLE_PASSAGE("jerotes_invisible_passage"),
	JEROTES_COUNTERSPELL("jerotes_counterspell"),
	JEROTES_MAGIC_ABSORPTION("jerotes_magic_absorption"),
	JEROTES_CLOUD_OF_DAGGERS("jerotes_cloud_of_daggers"),
	JEROTES_CREATE_BONFIRE("jerotes_create_bonfire"),
	JEROTES_INSECT_PLAGUE("jerotes_insect_plague"),
	JEROTES_PLANT_GROWTH("jerotes_plant_growth"),
	JEROTES_DIMINISH_PLANTS("jerotes_diminish_plants"),
	JEROTES_FIREBALL("jerotes_fireball"),
	JEROTES_ICE_STORM("jerotes_ice_storm"),
	JEROTES_THUNDERWAVE("jerotes_thunderwave"),
	JEROTES_LINEAR_EVOKER_FANG("jerotes_linear_evoker_fang"),
	JEROTES_CIRCULAR_EVOKER_FANG("jerotes_circular_evoker_fang"),
	JEROTES_CONJURE_VEXES("jerotes_conjure_vexes"),
	JEROTES_POWER_WORD_KILL("jerotes_power_word_kill"),
	JEROTES_WAIL_OF_THE_BANSHEE("jerotes_wail_of_the_banshee"),
	JEROTES_PROBING_EYE("jerotes_probing_eye"),
	JEROTES_ANALYTICAL_EYE("jerotes_analytical_eye"),
	JEROTES_ELEMENTAL_CONTRACT("jerotes_elemental_contract"),
	JEROTES_FEY_CONTRACT("jerotes_fey_contract"),
	JEROTES_CONSTRUCT_CONTRACT("jerotes_construct_contract"),
	JEROTES_CREATE_WATER("jerotes_create_water"),
	JEROTES_DESTROY_WATER("jerotes_destroy_water"),
	JEROTES_WITHER_SKULL("jerotes_wither_skull"),
	JEROTES_DANGEROUS_WITHER_SKULL("jerotes_dangerous_wither_skull"),
	JEROTES_EVOKERCRAFT("jerotes_evokercraft"),
	JEROTES_WARDEN_SNOIC_BOOM("jerotes_warden_snoic_boom"),
	JEROTES_REDSTONE_LASER("jerotes_redstone_laser"),
	JEROTES_INSTANT_FORGE("jerotes_instant_forge"),
	JEROTES_ENDER_CHEST("jerotes_ender_chest"),
	JEROTES_LOCK_AIM("jerotes_lock_aim");

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
			case JEROTES_BLAZE_FIREBALL -> SpellList.BlazeFireball(level, caster, target);
			case JEROTES_BREEZE_WIND_CHARGE -> SpellList.BreezeWindCharge(level, caster, target);
			case JEROTES_GHAST_FIREBALL -> SpellList.GhastFireball(level, caster, target);
			case JEROTES_ENDERDRAGON_FIREBALL -> SpellList.EnderdragonFireball(level, caster, target);
			case JEROTES_POISON_BREATH -> SpellList.PoisonBreath(level, caster, target);
			case JEROTES_COLOR_SPRAY -> SpellList.ColorSpray(level, caster, target);
			case JEROTES_FIRE_BOLT -> SpellList.FireBolt(level, caster, target);
			case JEROTES_FROST_BOLT -> SpellList.FrostBolt(level, caster, target);
			case JEROTES_SHOCK_BOLT -> SpellList.ShockBolt(level, caster, target);
			case JEROTES_FLAMES_SPRAY -> SpellList.FlamesSpray(level, caster, target);
			case JEROTES_FROSTBITE_SPRAY -> SpellList.FrostbiteSpray(level, caster, target);
			case JEROTES_SPARKS_SPRAY -> SpellList.SparksSpray(level, caster, target);
			case JEROTES_WITCH_BOLT -> SpellList.WitchBolt(level, caster, target);
			case JEROTES_RAY_OF_FIRE -> SpellList.RayofFire(level, caster, target);
			case JEROTES_ICE_KNIFE -> SpellList.IceKnife(level, caster, target);
			case JEROTES_RAY_OF_FROST -> SpellList.RayofFrost(level, caster, target);
			case JEROTES_RAY_OF_LIGHTNING -> SpellList.RayofLightning(level, caster, target);
			case JEROTES_ELDRITCH_BLAST -> SpellList.EldritchBlast(level, caster, target);
			case JEROTES_SCORCHING_RAY -> SpellList.ScorchingRay(level, caster, target);
			case JEROTES_RAY_OF_SICKNESS -> SpellList.RayofSickness(level, caster, target);
			case JEROTES_RAY_OF_ENFEEBLEMENT -> SpellList.RayofEnfeeblement(level, caster, target);
			case JEROTES_DISINTEGRATE -> SpellList.Disintegrate(level, caster, target);
			case JEROTES_FINGER_OF_DEATH -> SpellList.FingerOfDeath(level, caster, target);
			case JEROTES_LIGHTNING_BOLT -> SpellList.LightningBolt(level, caster, target);
			case JEROTES_CHAIN_LIGHTNING -> SpellList.ChainLightning(level, caster, target);
			case JEROTES_SUNBEAM -> SpellList.Sunbeam(level, caster, target);
			case JEROTES_INFESTATION -> SpellList.Infestation(level, caster, target);
			case JEROTES_SHOCKING_GRASP -> SpellList.ShockingGrasp(level, caster, target);
			case JEROTES_GUARDIAN_LASER -> SpellList.GuardianLaser(level, caster, target);
			case JEROTES_VICIOUS_MOCKERY -> SpellList.ViciousMockery(level, caster, target);
			case JEROTES_HOLD_PERSON -> SpellList.HoldPerson(level, caster, target);
			case JEROTES_BESTOW_CURSE -> SpellList.BestowCurse(level, caster, target);
			case JEROTES_REMOVE_CURSE -> SpellList.RemoveCurse(level, caster, target);
			case JEROTES_BLINDNESS_CURSE -> SpellList.BlindnessCurse(level, caster, target);
			case JEROTES_DARKNESS_CURSE -> SpellList.DarknessCurse(level, caster, target);
			case JEROTES_FATIGUE_CURSE -> SpellList.FatigueCurse(level, caster, target);
			case JEROTES_PHANTASMAL_KILLER -> SpellList.PhantasmalKiller(level, caster, target);
			case JEROTES_EYEBITE -> SpellList.Eyebite(level, caster, target);
			case JEROTES_HARM -> SpellList.Harm(level, caster, target);
			case JEROTES_FIRE_ABSORPTION -> SpellList.FireAbsorption(level, caster, target);
			case JEROTES_COLD_ABSORPTION -> SpellList.ColdAbsorption(level, caster, target);
			case JEROTES_LIGHTNING_ABSORPTION -> SpellList.LightningAbsorption(level, caster, target);
			case JEROTES_LIFE_MEND -> SpellList.LifeMend(level, caster, target);
			case JEROTES_CURE_WOUNDS -> SpellList.CureWounds(level, caster, target);
			case JEROTES_MENDING -> SpellList.Mending(level, caster, target);
			case JEROTES_FEATHER_FALL -> SpellList.FeatherFall(level, caster, target);
			case JEROTES_DARKVISION -> SpellList.Darkvision(level, caster, target);
			case JEROTES_MISTY_STEP -> SpellList.MistyStep(level, caster, target);
			case JEROTES_ENDER_STEP -> SpellList.EnderStep(level, caster, target);
			case JEROTES_MIRROR_IMAGE -> SpellList.MirrorImage(level, caster, target);
			case JEROTES_INVISIBILITY -> SpellList.Invisibility(level, caster, target);
			case JEROTES_INVISIBLE_PASSAGE -> SpellList.InvisiblePassage(level, caster, target);
			case JEROTES_COUNTERSPELL -> SpellList.Counterspell(level, caster, target);
			case JEROTES_MAGIC_ABSORPTION -> SpellList.MagicAbsorption(level, caster, target);
			case JEROTES_CLOUD_OF_DAGGERS -> SpellList.CloudOfDaggers(level, caster, target);
			case JEROTES_CREATE_BONFIRE -> SpellList.CreateBonfire(level, caster, target);
			case JEROTES_INSECT_PLAGUE -> SpellList.InsectPlague(level, caster, target);
			case JEROTES_PLANT_GROWTH -> SpellList.PlantGrowth(level, caster, target);
			case JEROTES_DIMINISH_PLANTS -> SpellList.DiminishPlants(level, caster, target);
			case JEROTES_FIREBALL -> SpellList.Fireball(level, caster, target);
			case JEROTES_ICE_STORM -> SpellList.IceStorm(level, caster, target);
			case JEROTES_THUNDERWAVE -> SpellList.Thunderwave(level, caster, target);
			case JEROTES_LINEAR_EVOKER_FANG -> SpellList.LinearEvokerFang(level, caster, target);
			case JEROTES_CIRCULAR_EVOKER_FANG -> SpellList.CircularEvokerFang(level, caster, target);
			case JEROTES_CONJURE_VEXES -> SpellList.ConjureVexes(level, caster, target);
			case JEROTES_POWER_WORD_KILL -> SpellList.PowerWordKill(level, caster, target);
		case JEROTES_WAIL_OF_THE_BANSHEE -> SpellList.WailOfTheBanshee(level, caster, target);
			case JEROTES_PROBING_EYE -> SpellList.ProbingEye(level, caster, target);
			case JEROTES_ANALYTICAL_EYE -> SpellList.AnalyticalEye(level, caster, target);
			case JEROTES_ELEMENTAL_CONTRACT -> SpellList.ElementalContract(level, caster, target);
			case JEROTES_FEY_CONTRACT -> SpellList.FeyContract(level, caster, target);
			case JEROTES_CONSTRUCT_CONTRACT -> SpellList.ConstructContract(level, caster, target);
			case JEROTES_CREATE_WATER -> SpellList.CreateWater(level, caster, target);
			case JEROTES_DESTROY_WATER -> SpellList.DestroyWater(level, caster, target);
			case JEROTES_WITHER_SKULL -> SpellList.WitherSkull(level, caster, target);
			case JEROTES_DANGEROUS_WITHER_SKULL -> SpellList.DangerousWitherSkull(level, caster, target);
			case JEROTES_EVOKERCRAFT -> SpellList.Evokercraft(level, caster, target);
			case JEROTES_WARDEN_SNOIC_BOOM -> SpellList.WardenSonicBoom(level, caster, target);
			case JEROTES_REDSTONE_LASER -> SpellList.RedstoneLaser(level, caster, target);
			case JEROTES_INSTANT_FORGE -> SpellList.InstantForge(level, caster, target);
			case JEROTES_ENDER_CHEST -> SpellList.EnderChest(level, caster, target);
			case JEROTES_LOCK_AIM -> SpellList.LockAim(level, caster, target);
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