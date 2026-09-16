package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesVexEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.HoldMobEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.HoldPersonMainEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.MirrorImageMainEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;

public class SpellList {
	public static MagicSpell Empty(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "empty", JerotesParticleTypes.NULL.get(), JerotesSoundEvents.SPELL){
			public boolean spellFindUse() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUseTargetNone() {
				return true;
			}
		};
	}
	//魔法飞弹
	public static MagicSpell MagicMissile(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "magic_missile", JerotesParticleTypes.MAGIC_MISSILE_DISPLAY.get(), JerotesSoundEvents.MAGIC_MAGIC_MISSILE){
			public boolean spellFindUse() {
				return SpellFind.MagicMissile(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSpellAccuracy(), 1, 1, 3 + getSpellLevel(), getCaster() instanceof Player);
			}
			public int getCastingTicks() {
				return (1 + getSpellLevel()) * 5 + 4;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 5 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					Entity entity = getTarget();
					if (getCaster() instanceof Player player && Main.getTargetedEntity(player, getSpellDistance()) != null && canUseToEntity(Main.getTargetedEntity(player, getSpellDistance()))) {
						entity = Main.getTargetedEntity(player, getSpellDistance());
					}
					if (!(entity != null && entity.isAlive())) {
						entity = getCaster();
					}
					SpellFind.MagicMissile(getCaster(), (entity instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSpellAccuracy(), 1, 1, 1, getCaster() instanceof Player);
					if (!getCaster().isSilent()) {
						getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
								getCaster().getSoundSource(), getSoundLevel(), 1.0F);
					}
				}
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public float getSpellDistanceBase() {
				return 36;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
		};
	}
	//烈焰人火球
	public static MagicSpell BlazeFireball(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "blaze_fireball", JerotesParticleTypes.BLAZE_FIREBALL_DISPLAY.get(), JerotesSoundEvents.MAGIC_BLAZE_FIREBALL){
			public boolean spellFindUse() {
				return SpellFind.BlazeFireball(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
			public float getSpellDistanceBase() {
				return 240;
			}
		};
	}
	//旋风人风弹
	public static MagicSpell BreezeWindCharge(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "breeze_wind_charge", JerotesParticleTypes.BREEZE_WIND_CHARGE_DISPLAY.get(), JerotesSoundEvents.MAGIC_BREEZE_WIND_CHARGE){
			public boolean spellFindUse() {
				return SpellFind.BreezeWindCharge(getCaster(), getTarget(), getSpellLevel(), 3 + (getSpellLevel() - 1) * 0.25f, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
			public float getSpellDistanceBase() {
				return 240;
			}
		};
	}
	//恶魂火球
	public static MagicSpell GhastFireball(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ghast_fireball", JerotesParticleTypes.GHAST_FIREBALL_DISPLAY.get(), JerotesSoundEvents.MAGIC_GHAST_FIREBALL){
			public boolean spellFindUse() {
				return SpellFind.GhastFireball(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, 1 + (getSpellLevel() - 1) * 0.25f, getSpellAccuracy(), 1, 5, getCaster() instanceof Player, getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public float getSpellDistanceBase() {
				return 240;
			}
		};
	}
	//末影龙火球
	public static MagicSpell EnderdragonFireball(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "enderdragon_fireball", JerotesParticleTypes.ENDERDRAGON_FIREBALL_DISPLAY.get(), JerotesSoundEvents.MAGIC_ENDERDRAGON_FIREBALL){
			public boolean spellFindUse() {
				return SpellFind.EnderdragonFireball(getCaster(), getTarget(), getSpellLevel(), 1, (int)Math.ceil((getSpellLevel()) / 2.0), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public int getBaseSpellLevel() {
				return 5;
			}
			public float getSpellDistanceBase() {
				return 60;
			}
		};
	}
	//毒性吐息
	public static MagicSpell PoisonBreath(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "poison_breath", JerotesParticleTypes.POISON_BREATH_DISPLAY.get(), JerotesSoundEvents.MAGIC_POISON_BREATH){
			public boolean spellFindUse() {
				return SpellFind.PoisonBreath(getCaster(), getTarget(), 2 * getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, (int)Math.ceil((getSpellLevel()) / 2.0), getSpellAccuracy(), getSpellLevel(), 5, getCaster() instanceof Player, getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public float getSpellDistanceBase() {
				int level = getSpellLevel();
				if (level <= 3) return 4.5f;
				if (level <= 6) return 9.0f;
				if (level <= 8) return 18.0f;
				return 27.0f;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//火焰箭
	public static MagicSpell FireBolt(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "fire_bolt", JerotesParticleTypes.FIRE_BOLT_DISPLAY.get(), JerotesSoundEvents.MAGIC_FIRE_BOLT){
			public boolean spellFindUse() {
				return SpellFind.FireBolt(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
			public float getSpellDistanceBase() {
				return (int) (36 * MainConfig.SomeBulletSpellReachMultiplier);
			}
		};
	}
	//寒冰箭
	public static MagicSpell FrostBolt(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "frost_bolt", JerotesParticleTypes.FROST_BOLT_DISPLAY.get(), JerotesSoundEvents.MAGIC_FROST_BOLT){
			public boolean spellFindUse() {
				return SpellFind.FrostBolt(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
			public float getSpellDistanceBase() {
				return (int) (36 * MainConfig.SomeBulletSpellReachMultiplier);
			}
		};
	}
	//电击箭
	public static MagicSpell ShockBolt(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "shock_bolt", JerotesParticleTypes.SHOCK_BOLT_DISPLAY.get(), JerotesSoundEvents.MAGIC_SHOCK_BOLT){
			public boolean spellFindUse() {
				return SpellFind.ShockBolt(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
			public float getSpellDistanceBase() {
				return (int) (36 * MainConfig.SomeBulletSpellReachMultiplier);
			}
		};
	}
	//喷涌火舌
	public static MagicSpell FlamesSpray(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "flames_spray", JerotesParticleTypes.FLAMES_SPRAY_DISPLAY.get(), JerotesSoundEvents.MAGIC_FLAMES_SPRAY){
			public boolean spellFindUse() {
				return SpellFind.FlamesSpray(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, getSpellAccuracy(), getCaster() instanceof Player);
			}
			public int getCastingTicks() {
				return 59;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 2 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					SpellFind.FlamesSpray(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, getSpellAccuracy(), getCaster() instanceof Player);
					if (!getCaster().isSilent()) {
						getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
								getCaster().getSoundSource(), getSoundLevel(), 1.0F);
					}
				}
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public float getSpellDistanceBase() {
				return 9;
			}
		};
	}
	//喷涌霜噬
	public static MagicSpell FrostbiteSpray(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "frostbite_spray", JerotesParticleTypes.FROSTBITE_SPRAY_DISPLAY.get(), JerotesSoundEvents.MAGIC_FROSTBITE_SPRAY){
			public boolean spellFindUse() {
				return SpellFind.FrostbiteSpray(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, getSpellAccuracy(), getCaster() instanceof Player);
			}
			public int getCastingTicks() {
				return 59;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 2 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					SpellFind.FrostbiteSpray(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, getSpellAccuracy(), getCaster() instanceof Player);
					if (!getCaster().isSilent()) {
						getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
								getCaster().getSoundSource(), getSoundLevel(), 1.0F);
					}
				}
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public float getSpellDistanceBase() {
				return 9;
			}
		};
	}
	//喷涌电花
	public static MagicSpell SparksSpray(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "sparks_spray", JerotesParticleTypes.SPARKS_SPRAY_DISPLAY.get(), JerotesSoundEvents.MAGIC_SPARKS_SPRAY){
			public boolean spellFindUse() {
				return SpellFind.SparksSpray(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), getCaster() instanceof Player);
			}
			public int getCastingTicks() {
				return 59;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 2 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					SpellFind.SparksSpray(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), getCaster() instanceof Player);
					if (!getCaster().isSilent()) {
						getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
								getCaster().getSoundSource(), getSoundLevel(), 1.0F);
					}
				}
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public float getSpellDistanceBase() {
				return 9;
			}
		};
	}
	//巫术箭
	public static MagicSpell WitchBolt(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "witch_bolt", JerotesParticleTypes.WITCH_BOLT_DISPLAY.get(), JerotesSoundEvents.MAGIC_WITCH_BOLT){
			public boolean spellFindUse() {
				return SpellFind.WitchBolt(getCaster(), getTarget(), getSpellLevel(), 2.25f, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isConcentration() {
				return true;
			}
			public float getSpellDistanceBase() {
				return (int) (18 * MainConfig.SomeBulletSpellReachMultiplier);
			}
		};
	}
	//冰刃
	public static MagicSpell IceKnife(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ice_knife", JerotesParticleTypes.ICE_KNIFE_DISPLAY.get(), JerotesSoundEvents.MAGIC_ICE_KNIFE){
			public boolean spellFindUse() {
				return SpellFind.IceKnife(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public float getSpellDistanceBase() {
				return (int) (36 * MainConfig.SomeBulletSpellReachMultiplier);
			}
		};
	}
	//火焰射线
	public static MagicSpell RayofFire(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ray_of_fire", JerotesParticleTypes.RAY_OF_FIRE_DISPLAY.get(), JerotesSoundEvents.MAGIC_RAY_OF_FIRE){
			public boolean spellFindUse() {
				return SpellFind.RayofFire(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
		};
	}
	//冷冻射线
	public static MagicSpell RayofFrost(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ray_of_frost", JerotesParticleTypes.RAY_OF_FROST_DISPLAY.get(), JerotesSoundEvents.MAGIC_RAY_OF_FROST){
			public boolean spellFindUse() {
				return SpellFind.RayofFrost(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
		};
	}
	//闪电射线
	public static MagicSpell RayofLightning(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ray_of_lightning", JerotesParticleTypes.RAY_OF_LIGHTNING_DISPLAY.get(), JerotesSoundEvents.MAGIC_RAY_OF_LIGHTNING){
			public boolean spellFindUse() {
				return SpellFind.RayofLightning(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
		};
	}
	//魔能爆
	public static MagicSpell EldritchBlast(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "eldritch_blast", JerotesParticleTypes.ELDRITCH_BLAST_DISPLAY.get(), JerotesSoundEvents.MAGIC_ELDRITCH_BLAST){
			public boolean spellFindUse() {
				return SpellFind.EldritchBlast(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public int getCastingTicks() {
				int count = 1 + (getSpellLevel() - 1) / 2;
				return count <= 1 ? 0 : (count - 1) * 5 + 4;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 5 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					SpellFind.EldritchBlast(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
					if (!getCaster().isSilent()) {
						getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
								getCaster().getSoundSource(), getSoundLevel(), 1.0F);
					}
				}
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 36;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
		};
	}
	//灼热射线
	public static MagicSpell ScorchingRay(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "scorching_ray", JerotesParticleTypes.SCORCHING_RAY_DISPLAY.get(), JerotesSoundEvents.MAGIC_SCORCHING_RAY){
			public boolean spellFindUse() {
				return SpellFind.ScorchingRay(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public int getCastingTicks() {
				int count = 3 + (getSpellLevel() - 2);
				return count <= 1 ? 0 : (count - 1) * 5 + 4;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 5 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					SpellFind.ScorchingRay(getCaster(), getTarget(), getSpellLevel(), 6 + getSpellLevel() * 3, getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
					if (!getCaster().isSilent()) {
						getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
								getCaster().getSoundSource(), getSoundLevel(), 1.0F);
					}
				}
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 36;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
		};
	}
	//致病射线
	public static MagicSpell RayofSickness(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ray_of_sickness", JerotesParticleTypes.RAY_OF_SICKNESS_DISPLAY.get(), JerotesSoundEvents.MAGIC_RAY_OF_SICKNESS){
			public boolean spellFindUse() {
				return SpellFind.RayofSickness(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, (int)Math.ceil((getSpellLevel()) / 2.0), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
		};
	}
	//虚弱射线
	public static MagicSpell RayofEnfeeblement(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ray_of_enfeeblement", JerotesParticleTypes.RAY_OF_ENFEEBLEMENT_DISPLAY.get(), JerotesSoundEvents.MAGIC_RAY_OF_ENFEEBLEMENT){
			public boolean spellFindUse() {
				return SpellFind.RayofEnfeeblement(getCaster(), getTarget(), 6 + (getSpellLevel() - 1) * 3, (int)Math.ceil((getSpellLevel()) / 2.0), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
		};
	}
	//解离术
	public static MagicSpell Disintegrate(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "disintegrate", JerotesParticleTypes.DISINTEGRATE_DISPLAY.get(), JerotesSoundEvents.MAGIC_DISINTEGRATE){
			public boolean spellFindUse() {
				return SpellFind.Disintegrate(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player, getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 6;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 1;
			}
			//
		};
	}
	//死亡一指
	public static MagicSpell FingerOfDeath(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "finger_of_death", JerotesParticleTypes.FINGER_OF_DEATH_DISPLAY.get(), JerotesSoundEvents.MAGIC_FINGER_OF_DEATH){
			public boolean spellFindUse() {
				return SpellFind.FingerOfDeath(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player, getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 7;
			}
			public float getSpellDistanceBase() {
				return 30;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//闪电束
	public static MagicSpell LightningBolt(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "lightning_bolt", JerotesParticleTypes.LIGHTNING_BOLT_DISPLAY.get(), JerotesSoundEvents.MAGIC_LIGHTNING_BOLT){
			public boolean spellFindUse() {
				return SpellFind.LightningBolt(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public float getSpellDistanceBase() {
				return 30;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
		};
	}
	//连锁闪电
	public static MagicSpell ChainLightning(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "chain_lightning", JerotesParticleTypes.CHAIN_LIGHTNING_DISPLAY.get(), JerotesSoundEvents.MAGIC_CHAIN_LIGHTNING){
			public boolean spellFindUse() {
				return SpellFind.ChainLightning(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player, getSubcategory(), true);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 6;
			}
			public float getSpellDistanceBase() {
				return 45;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//阳炎射线
	public static MagicSpell Sunbeam(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "sunbeam", JerotesParticleTypes.SUNBEAM_DISPLAY.get(), JerotesSoundEvents.MAGIC_SUNBEAM){
			public boolean spellFindUse() {
				return SpellFind.Sunbeam(getCaster(), getSpellLevel(), getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 6;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public boolean isConcentration() {
				return true;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 1;
			}
			//
		};
	}
	//虫群孳生
	public static MagicSpell Infestation(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "infestation", JerotesParticleTypes.INFESTATION_DISPLAY.get(), JerotesSoundEvents.MAGIC_INFESTATION){
			public boolean spellFindUse() {
				return SpellFind.Infestation(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 9;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
			public boolean isCantrip() {
				return true;
			}
		};
	}
	//电爪
	public static MagicSpell ShockingGrasp(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "shocking_grasp", JerotesParticleTypes.SHOCKING_GRASP_DISPLAY.get(), JerotesSoundEvents.MAGIC_SHOCKING_GRASP){
			public boolean spellFindUse() {
				return SpellFind.ShockingGrasp(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean isMelee() {
				return true;
			}
			public float getSpellDistanceBase() {
				return 6;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public boolean isCantrip() {
				return true;
			}
		};
	}
	//守卫者光束
	public static MagicSpell GuardianLaser(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "guardian_laser", JerotesParticleTypes.GUARDIAN_LASER_DISPLAY.get(), JerotesSoundEvents.MAGIC_GUARDIAN_LASER){
			public boolean spellFindUse() {
				return SpellFind.GuardianLaser(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 16;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ENCHANTMENT;
			}
			public boolean isCantrip() {
				return true;
			}
		};
	}
	//恶毒嘲笑
	public static MagicSpell ViciousMockery(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "vicious_mockery", JerotesParticleTypes.VICIOUS_MOCKERY_DISPLAY.get(), JerotesSoundEvents.MAGIC_VICIOUS_MOCKERY){
			public boolean spellFindUse() {
				return SpellFind.ViciousMockery(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ENCHANTMENT;
			}
			public boolean isCantrip() {
				return true;
			}
		};
	}
	//人类定身术
	public static MagicSpell HoldPerson(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "hold_person", JerotesParticleTypes.HOLD_PERSON_DISPLAY.get(), JerotesSoundEvents.MAGIC_HOLD_PERSON){
			public boolean spellFindUse() {
				return SpellFind.HoldPerson(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, 1, Math.max(1, getSpellLevel() - 1), true);
			}
			public int getCastingTicks() {
				return getCaster() instanceof Player ? Math.max(0, getSpellLevel() - 2) * 5 + 4 : 0;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 5 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					if (getCaster().level() instanceof ServerLevel serverLevel) {
						Entity target = null;
						if (getCaster() instanceof Player player && Main.getTargetedEntity(player, 18) != null) {
							boolean bl = false;
							if (serverLevel.getEntity(SpellType.getConcentrationSpellTargetJerotes(getCaster())) instanceof HoldPersonMainEntity holdPersonMainEntity) {
								bl = holdPersonMainEntity.distanceTo(Main.getTargetedEntity(player, 18)) <= 18;
							}
							if (bl) {
								target = Main.getTargetedEntity(player, 18);
							}
						}
						if (target instanceof LivingEntity livingEntity && canUseToEntity(target)) {
							HoldMobEntity spell = null;
							//感知判定
							if (!EntityAndItemFind.savingSpellThrow(livingEntity)) {
								spell = new HoldMobEntity(serverLevel, getCaster());
								spell.setSpellLevelDamage(getSpellLevel());
								spell.setPos(livingEntity.getX(), livingEntity.getY() + 2 / 16f, livingEntity.getZ());
								spell.setOwnerUUID(getCaster().getUUID());
								spell.setTarget(livingEntity);
								spell.setSize(Main.mobWidth(livingEntity));
								spell.setTargetHeight(Main.mobHeight(livingEntity));
								serverLevel.addFreshEntity(spell);
								DamageSource damageSource = new DamageSource(getCaster().level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.MAGIC_EFFECT), spell, caster);
								livingEntity.hurt(damageSource, 0f);
							}
							if (serverLevel.getEntity(SpellType.getConcentrationSpellTargetJerotes(getCaster())) instanceof HoldPersonMainEntity holdPersonMainEntity) {
								if (spell != null) {
									holdPersonMainEntity.add(spell.getUUID());
								}
							}
							if (!getCaster().isSilent()) {
								getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
										getCaster().getSoundSource(), getSoundLevel(), 1.0F);
							}
						}
					}
				}
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public boolean canUseToEntity(Entity entity) {
				return entity instanceof LivingEntity livingEntity && EntityFactionFind.isHumanoid(livingEntity) && super.canUseToEntity(entity);
			}
			public boolean isConcentration() {
				return true;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ENCHANTMENT;
			}
		};
	}
	//降咒术
	public static MagicSpell BestowCurse(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "bestow_curse", JerotesParticleTypes.BESTOW_CURSE_DISPLAY.get(), JerotesSoundEvents.MAGIC_BESTOW_CURSE){
			public boolean spellFindUse() {
				return SpellFind.BestowCurse(getCaster(), getTarget(), getSpellLevel(), getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public boolean isMelee() {
				return true;
			}
			public boolean isConcentration() {
				return true;
			}
			public float getSpellDistanceBase() {
				return 6;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
			public int getConcentrationSpellMaxLevel() {
				return 5;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 3;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return Main.randomReach(livingEntity.getRandom(), 0 ,3);
			}
			//
		};
	}
	//解咒术
	public static MagicSpell RemoveCurse(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "remove_curse", JerotesParticleTypes.REMOVE_CURSE_DISPLAY.get(), JerotesSoundEvents.MAGIC_REMOVE_CURSE){
			public boolean spellFindUse() {
				return SpellFind.RemoveCurse(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, getSpellLevel(), getSubcategory());
			}
			public boolean canTargetNonLivingEntity() {
				return true;
			}
			public boolean mustTargetPickable() {
				return false;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public boolean isMelee() {
				return true;
			}
			public boolean isHelp() {
				return true;
			}
			public float shouldBeSelfChance() {
				return 0.7f;
			}
			public boolean mobUseInCombat() {
				return false;
			}
			public boolean canUseToEntity(Entity entity) {
				if (getCaster() instanceof Player)
					return super.canUseToEntity(entity);
				boolean hasNegative = false;
				if (entity instanceof LivingEntity livingEntity)  {
					for (MobEffectInstance inst : livingEntity.getActiveEffects()) {
						if (!inst.getEffect().isBeneficial()) {
							hasNegative = true;
							break;
						}
					}
				}
				return hasNegative && super.canUseToEntity(entity);
			}
			public float getSpellDistanceBase() {
				return 6;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//失明诅咒
	public static MagicSpell BlindnessCurse(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "blindness_curse", JerotesParticleTypes.BLINDNESS_CURSE_DISPLAY.get(), JerotesSoundEvents.MAGIC_BLINDNESS_CURSE){
			public boolean spellFindUse() {
				return SpellFind.BlindnessCurse(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, 1);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
		};
	}
	//黑暗诅咒
	public static MagicSpell DarknessCurse(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "darkness_curse", JerotesParticleTypes.DARKNESS_CURSE_DISPLAY.get(), JerotesSoundEvents.MAGIC_DARKNESS_CURSE){
			public boolean spellFindUse() {
				return SpellFind.DarknessCurse(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, 1);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
		};
	}
	//疲劳诅咒
	public static MagicSpell FatigueCurse(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "fatigue_curse", JerotesParticleTypes.FATIGUE_CURSE_DISPLAY.get(), JerotesSoundEvents.MAGIC_FATIGUE_CURSE){
			public boolean spellFindUse() {
				return SpellFind.FatigueCurse(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, (int)Math.ceil(getSpellLevel() / 3.0));
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
		};
	}
	//魅影杀手
	public static MagicSpell PhantasmalKiller(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "phantasmal_killer", JerotesParticleTypes.PHANTASMAL_KILLER_DISPLAY.get(), JerotesSoundEvents.MAGIC_PHANTASMAL_KILLER){
			public boolean spellFindUse() {
				return SpellFind.PhantasmalKiller(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 4;
			}
			public float getSpellDistanceBase() {
				return 36;
			}
			public boolean isConcentration() {
				return true;
			}
			public boolean canUseToEntity(Entity entity) {
				if (entity instanceof LivingEntity livingEntity &&
						livingEntity.hasEffect(JerotesMobEffects.PHANTASMAL_KILLER.get()) &&
						Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.PHANTASMAL_KILLER.get())).getAmplifier() >= getSpellLevel() - 1) {
					return false;
				}
				return super.canUseToEntity(entity);
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ILLUSION;
			}
		};
	}
	//摄心目光
	public static MagicSpell Eyebite(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "eyebite", JerotesParticleTypes.EYEBITE_DISPLAY.get(), JerotesSoundEvents.MAGIC_EYEBITE){
			public boolean spellFindUse() {
				return SpellFind.Eyebite(getCaster(), getTarget(), 6 + (getSpellLevel() - 1) * 3, getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 6;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
		};
	}
	//重伤术
	public static MagicSpell Harm(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "harm", JerotesParticleTypes.HARM_DISPLAY.get(), JerotesSoundEvents.MAGIC_HARM){
			public boolean spellFindUse() {
				return SpellFind.Harm(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 6;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.NECROMANCY;
			}
		};
	}
	//火焰吸收
	public static MagicSpell FireAbsorption(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "fire_absorption", JerotesParticleTypes.FIRE_ABSORPTION_DISPLAY.get(), JerotesSoundEvents.MAGIC_FIRE_ABSORPTION){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.FIRE_ABSORPTION.get(), 6 + (getSpellLevel() - 1) * 3 * 20, getSpellLevel() - 1));
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.FIRE_ABSORPTION.get()) && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.FIRE_ABSORPTION.get())).getAmplifier() >= getSpellLevel() - 1 && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.FIRE_ABSORPTION.get())).getDuration() > 6 * 20 && !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
		};
	}
	//冰霜吸收
	public static MagicSpell ColdAbsorption(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "cold_absorption", JerotesParticleTypes.COLD_ABSORPTION_DISPLAY.get(), JerotesSoundEvents.MAGIC_COLD_ABSORPTION){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.COLD_ABSORPTION.get(), 6 + (getSpellLevel() - 1) * 3 * 20, getSpellLevel() - 1));
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.COLD_ABSORPTION.get()) && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.COLD_ABSORPTION.get())).getAmplifier() >= getSpellLevel() - 1 && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.COLD_ABSORPTION.get())).getDuration() > 6 * 20 && !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
		};
	}
	//闪电吸收
	public static MagicSpell LightningAbsorption(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "lightning_absorption", JerotesParticleTypes.LIGHTNING_ABSORPTION_DISPLAY.get(), JerotesSoundEvents.MAGIC_LIGHTNING_ABSORPTION){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.LIGHTNING_ABSORPTION.get(), 6 + (getSpellLevel() - 1) * 3 * 20, getSpellLevel() - 1));
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get()) && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get())).getAmplifier() >= getSpellLevel() - 1 && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get())).getDuration() > 6 * 20 && !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
		};
	}
	//修补自身
	public static MagicSpell LifeMend(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.MAIN, "life_mend", JerotesParticleTypes.LIFE_MEND_DISPLAY.get(), JerotesSoundEvents.MAGIC_LIFE_MEND){
			public boolean spellFindUse() {
				return SpellFind.LifeMend(getCaster(), getCaster(), getSpellLevel());
			}
			public boolean isHelp() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public float shouldBeSelfChance() {
				return 0.7f;
			}
			public boolean mobUseInCombat() {
				return false;
			}
			public boolean canUseToEntity(Entity entity) {
				return entity instanceof LivingEntity livingEntity &&
						(livingEntity.getHealth() < livingEntity.getMaxHealth() || getCaster() instanceof Player) &&
						super.canUseToEntity(entity);
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
		};
	}
	//疗伤术
	public static MagicSpell CureWounds(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "cure_wounds", JerotesParticleTypes.CURE_WOUNDS_DISPLAY.get(), JerotesSoundEvents.MAGIC_CURE_WOUNDS){
			public boolean spellFindUse() {
				return SpellFind.CureWounds(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
			}
			public boolean isHelp() {
				return true;
			}
			public boolean isMelee() {
				return true;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 6;
			}
			public float shouldBeSelfChance() {
				return 0.7f;
			}
			public boolean mobUseInCombat() {
				return false;
			}
			public boolean canUseToEntity(Entity entity) {
				return entity instanceof LivingEntity livingEntity &&
						(livingEntity.getHealth() < livingEntity.getMaxHealth() || getCaster() instanceof Player) &&
						livingEntity.getMobType() != MobType.UNDEAD &&
						!EntityFactionFind.isMachine(livingEntity) &&
						!EntityFactionFind.isConstruct(livingEntity) && super.canUseToEntity(entity);
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
		};
	}
	//修补术
	public static MagicSpell Mending(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "mending", JerotesParticleTypes.MENDING_DISPLAY.get(), JerotesSoundEvents.MAGIC_MENDING){
			public boolean spellFindUse() {
				return SpellFind.Mending(getCaster(), getTarget(), getSpellLevel(), getSubcategory());
			}
			public boolean canTargetNonLivingEntity() {
				return true;
			}
			public boolean mustTargetPickable() {
				return false;
			}
			public boolean isHelp() {
				return true;
			}
			public boolean isMelee() {
				return true;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 6;
			}
			public float shouldBeSelfChance() {
				return 0.7f;
			}
			public boolean mobUseInCombat() {
				return false;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
			public boolean isCantrip() {
				return true;
			}
			public boolean canUseToEntity(Entity entity) {
				return !(getSubcategory() == 1 && (entity instanceof LivingEntity livingEntity && !EntityFactionFind.isMachine(livingEntity) && !EntityFactionFind.isConstruct(livingEntity)));
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//羽落术
	public static MagicSpell FeatherFall(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "feather_fall", JerotesParticleTypes.FEATHER_FALL_DISPLAY.get(), JerotesSoundEvents.MAGIC_FEATHER_FALL){
			public boolean spellFindUse() {
				return SpellFind.FeatherFall(getCaster(), getTarget(), getSpellLevel(), 20 * 60, 1, getSubcategory());
			}
			public int getCastingTicks() {
				return 24;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 5 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					Entity entity = getCaster();
					if (getCaster() instanceof Player player && Main.getTargetedEntity(player, getSpellDistance()) != null &&
							canUseToEntity(Main.getTargetedEntity(player, getSpellDistance()))) {
						entity = Main.getTargetedEntity(player, getSpellDistance());
					}
					SpellFind.FeatherFall(getCaster(), entity, getSpellLevel(), 20 * 60, 1, 2);
					if (!getCaster().isSilent()) {
						getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
								getCaster().getSoundSource(), getSoundLevel(), 1.0F);
					}
				}
				return true;
			}
			public void spellAfterUse() {
				super.spellAfterUse();
				if (getSubcategory() != 2)
					SpellType.stopCasting(caster, 999, false);
			}
			public boolean isHelp() {
				return true;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
			public boolean isCombat() {
				return false;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 2;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//黑暗视觉
	public static MagicSpell Darkvision(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "darkvision", JerotesParticleTypes.DARKVISION_DISPLAY.get(), JerotesSoundEvents.MAGIC_DARKVISION){
			public boolean spellFindUse() {
				return SpellFind.Darkvision(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), 600 * 20, 1);
			}
			public boolean isHelp() {
				return true;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isMelee() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 6;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
		};
	}
	//迷踪步
	public static MagicSpell MistyStep(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "misty_step", JerotesParticleTypes.MISTY_STEP_DISPLAY.get(), JerotesSoundEvents.MAGIC_MISTY_STEP){
			public boolean spellFindUse() {
				return SpellFind.MistyStep(getCaster(), 9, 16, getCaster() instanceof Player);
			}
			public boolean isHelp() {
				return true;
			}
			public float getSpellDistanceBase() {
				return 9;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUse() {
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
	//末影步
	public static MagicSpell EnderStep(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.MAIN, "ender_step", JerotesParticleTypes.ENDER_STEP_DISPLAY.get(), JerotesSoundEvents.MAGIC_ENDER_STEP){
			public boolean spellFindUse() {
				return SpellFind.EnderStep(getCaster(), Math.max(6, getSpellLevel() * getSpellLevel()), 16, getCaster() instanceof Player);
			}
			public boolean isHelp() {
				return true;
			}
			public float getSpellDistanceBase() {
				return Math.max(6, getSpellLevel() * getSpellLevel());
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUse() {
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
	//镜影术
	public static MagicSpell MirrorImage(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.MAIN, "mirror_image", JerotesParticleTypes.MIRROR_IMAGE_DISPLAY.get(), JerotesSoundEvents.MAGIC_MIRROR_IMAGE){
			public boolean spellFindUse() {
				return SpellFind.MirrorImage(getCaster(), 3, getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public boolean canUse() {
				if (getCaster() != null) {
					List<MirrorImageMainEntity> list = getCaster().level().getEntitiesOfClass(MirrorImageMainEntity.class, getCaster().getBoundingBox().inflate(128, 128, 128));
					list.removeIf(summon -> summon.getOwner() != getCaster());
					return super.canUse() && list.isEmpty();
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ILLUSION;
			}
		};
	}
	//隐形术
	public static MagicSpell Invisibility(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "invisibility", JerotesParticleTypes.INVISIBILITY_DISPLAY.get(), JerotesSoundEvents.MAGIC_INVISIBILITY){
			public boolean spellFindUse() {
				return SpellFind.Invisibility(getCaster(), getTarget(), getSpellLevel());
			}
			public float getSpellDistanceBase() {
				return 6;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean isHelp() {
				return true;
			}
			public boolean isMelee() {
				return true;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isConcentration() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.MAGIC_INVISIBILITY.get()) && !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ILLUSION;
			}
		};
	}
	//隐形通道
	public static MagicSpell InvisiblePassage(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "invisible_passage", JerotesParticleTypes.INVISIBLE_PASSAGE_DISPLAY.get(), JerotesSoundEvents.MAGIC_INVISIBLE_PASSAGE){
			public boolean spellFindUse() {
				return SpellFind.InvisiblePassage(getCaster(), getTarget(), getSpellLevel());
			}
			public float getSpellDistanceBase() {
				return 6;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean isHelp() {
				return true;
			}
			public boolean isMelee() {
				return true;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isConcentration() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get()) && !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ILLUSION;
			}
		};
	}
	//法术反制
	public static MagicSpell Counterspell(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "counterspell", JerotesParticleTypes.COUNTERSPELL_DISPLAY.get(), JerotesSoundEvents.MAGIC_COUNTERSPELL){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.COUNTERSPELL.get(), 12 * 20, getSpellLevel() - 1));
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canBeBreak() {
				return false;
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public boolean canUse() {
				if (getCaster() != null &&
						getCaster().hasEffect(JerotesMobEffects.COUNTERSPELL.get()) &&
						Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.COUNTERSPELL.get())).getAmplifier() >= getSpellLevel() - 1
						&& Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.COUNTERSPELL.get())).getDuration() > 6 * 20
						&& !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
		};
	}
	//魔法吸收
	public static MagicSpell MagicAbsorption(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "magic_absorption", JerotesParticleTypes.MAGIC_ABSORPTION_DISPLAY.get(), JerotesSoundEvents.MAGIC_MAGIC_ABSORPTION){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.MAGIC_ABSORPTION.get(), getSpellLevel() * 3 * 20, (getSpellLevel() - 1)/3));
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.MAGIC_ABSORPTION.get()) && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.MAGIC_ABSORPTION.get())).getAmplifier() >= getSpellLevel() - 1 && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.MAGIC_ABSORPTION.get())).getDuration() > 6 * 20 && !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ABJURATION;
			}
		};
	}
	//匕首之云
	public static MagicSpell CloudOfDaggers(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "cloud_of_daggers", JerotesParticleTypes.CLOUD_OF_DAGGERS_DISPLAY.get(), JerotesSoundEvents.MAGIC_CLOUD_OF_DAGGERS){
			public boolean spellFindUse() {
				return SpellFind.CloudOfDaggers(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isConcentration() {
				return true;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
	//创造篝火
	public static MagicSpell CreateBonfire(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "create_bonfire", JerotesParticleTypes.CREATE_BONFIRE_DISPLAY.get(), JerotesSoundEvents.MAGIC_CREATE_BONFIRE){
			public boolean spellFindUse() {
				return SpellFind.CreateBonfire(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isConcentration() {
				return true;
			}
			public boolean isCantrip() {
				return true;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
	//疫病虫群
	public static MagicSpell InsectPlague(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "insect_plague", JerotesParticleTypes.INSECT_PLAGUE_DISPLAY.get(), JerotesSoundEvents.MAGIC_INSECT_PLAGUE){
			public boolean spellFindUse() {
				return SpellFind.InsectPlague(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
			}
			public int getBaseSpellLevel() {
				return 5;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 90;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isConcentration() {
				return true;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
	//植物滋长
	public static MagicSpell PlantGrowth(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "plant_growth", JerotesParticleTypes.PLANT_GROWTH_DISPLAY.get(), JerotesSoundEvents.MAGIC_PLANT_GROWTH){
			public boolean spellFindUse() {
				return SpellFind.PlantGrowth(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSubcategory());
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 45;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean canUseToEntity(Entity entity) {
				if (!(entity instanceof Player player)) {
					return false;
				}
				return super.canUseToEntity(entity);
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 2;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//植物凋零
	public static MagicSpell DiminishPlants(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "diminish_plants", JerotesParticleTypes.DIMINISH_PLANTS_DISPLAY.get(), JerotesSoundEvents.MAGIC_DIMINISH_PLANTS){
			public boolean spellFindUse() {
				return SpellFind.DiminishPlants(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSubcategory());
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 45;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean canUseToEntity(Entity entity) {
				if (!(entity instanceof Player player)) {
					return false;
				}
				return super.canUseToEntity(entity);
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 2;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//火球术
	public static MagicSpell Fireball(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "fireball", JerotesParticleTypes.FIREBALL_DISPLAY.get(), JerotesSoundEvents.MAGIC_FIREBALL){
			public boolean spellFindUse() {
				return SpellFind.Fireball(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSubcategory());
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 45;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//冰风暴
	public static MagicSpell IceStorm(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "ice_storm", JerotesParticleTypes.ICE_STORM_DISPLAY.get(), JerotesSoundEvents.MAGIC_ICE_STORM){
			public boolean spellFindUse() {
				return SpellFind.IceStorm(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSubcategory());
			}
			public int getBaseSpellLevel() {
				return 4;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 90;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
		};
	}
	//雷鸣波
	public static MagicSpell Thunderwave(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "thunderwave", JerotesParticleTypes.THUNDERWAVE_DISPLAY.get(), JerotesSoundEvents.MAGIC_THUNDERWAVE){
			public boolean spellFindUse() {
				return SpellFind.Thunderwave(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 4.5f;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public float getSoundLevel() {
				return 5.625f;
			}
		};
	}
	//线形唤魔者尖牙
	public static MagicSpell LinearEvokerFang(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "linear_evoker_fang", JerotesParticleTypes.LINEAR_EVOKER_FANG_DISPLAY.get(), JerotesSoundEvents.MAGIC_LINEAR_EVOKER_FANG){
			public boolean spellFindUse() {
				return SpellFind.LinearEvokerFang(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
			public float getSpellDistanceBase() {
				return 20;
			}
		};
	}
	//环形唤魔者尖牙
	public static MagicSpell CircularEvokerFang(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "circular_evoker_fang", JerotesParticleTypes.CIRCULAR_EVOKER_FANG_DISPLAY.get(), JerotesSoundEvents.MAGIC_CIRCULAR_EVOKER_FANG){
			public boolean spellFindUse() {
				return SpellFind.CircularEvokerFang(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public float getSpellDistanceBase() {
				return 3;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
	//召唤恼鬼
	public static MagicSpell ConjureVexes(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.MAIN, "conjure_vexes", JerotesParticleTypes.CONJURE_VEXES_DISPLAY.get(), JerotesSoundEvents.MAGIC_CONJURE_VEXES){
			public boolean spellFindUse() {
				List<JerotesVexEntity> list = getCaster().level().getEntitiesOfClass(JerotesVexEntity.class, getCaster().getBoundingBox().inflate(128, 128, 128));
				list.removeIf(summon -> summon.getOwner() != getCaster());
				list.removeIf(summon -> !summon.isNotPersistent());
				return SpellFind.ConjureVexs(getCaster(), getSpellLevel() - list.size(), getSpellLevel() - list.size(), 2);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean canUse() {
				if (getCaster() != null) {
					List<JerotesVexEntity> list = getCaster().level().getEntitiesOfClass(JerotesVexEntity.class, getCaster().getBoundingBox().inflate(128, 128, 128));
					list.removeIf(summon -> summon.getOwner() != getCaster());
					list.removeIf(summon -> !summon.isNotPersistent());
					return super.canUse() && list.size() < getSpellLevel();
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
	//律令死亡
	public static MagicSpell PowerWordKill(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "power_word_kill", JerotesParticleTypes.POWER_WORD_KILL_DISPLAY.get(), JerotesSoundEvents.MAGIC_POWER_WORD_KILL){
			public boolean spellFindUse() {
				return SpellFind.PowerWordKill(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 9;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ENCHANTMENT;
			}
		};
	}
	//探查之眼
	public static MagicSpell ProbingEye(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.ADD, "probing_eye", JerotesParticleTypes.PROBING_EYE_DISPLAY.get(), JerotesSoundEvents.MAGIC_PROBING_EYE){
			public boolean spellFindUse() {
				return SpellFind.ProbingEye(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 6;
			}
			public boolean isCombat() {
				return false;
			}
			public float getSpellDistanceBase() {
				return 90;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.DIVINATION;
			}
			public boolean canUseTargetNone() {
				return true;
			}
		};
	}
	//识别之眼
	public static MagicSpell AnalyticalEye(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "analytical_eye", JerotesParticleTypes.ANALYTICAL_EYE_DISPLAY.get(), JerotesSoundEvents.MAGIC_ANALYTICAL_EYE){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.ANALYTICAL_EYE.get(), (6 + (getSpellLevel() - 1) * 3) * 200, 0));
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 6;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public boolean isCombat() {
				return false;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.ANALYTICAL_EYE.get()) &&
						Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.ANALYTICAL_EYE.get())).getDuration() > 6 * 20 &&
						!(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.DIVINATION;
			}
		};
	}
	//元素契约术
	public static MagicSpell ElementalContract(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "elemental_contract", JerotesParticleTypes.ELEMENTAL_CONTRACT_DISPLAY.get(), JerotesSoundEvents.MAGIC_ELEMENTAL_CONTRACT){
			public boolean spellFindUse() {
				return SpellFind.ElementalContract(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 5;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ENCHANTMENT;
			}
			public boolean canUseTargetNone() {
				return false;
			}
			public boolean isCombat() {
				return false;
			}
		};
	}
	//妖精契约术
	public static MagicSpell FeyContract(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "fey_contract", JerotesParticleTypes.FEY_CONTRACT_DISPLAY.get(), JerotesSoundEvents.MAGIC_FEY_CONTRACT){
			public boolean spellFindUse() {
				return SpellFind.FeyContract(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 5;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ENCHANTMENT;
			}
			public boolean canUseTargetNone() {
				return false;
			}
			public boolean isCombat() {
				return false;
			}
		};
	}
	//构造契约术
	public static MagicSpell ConstructContract(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "construct_contract", JerotesParticleTypes.CONSTRUCT_CONTRACT_DISPLAY.get(), JerotesSoundEvents.MAGIC_CONSTRUCT_CONTRACT){
			public boolean spellFindUse() {
				return SpellFind.ConstructContract(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 5;
			}
			public float getSpellDistanceBase() {
				return 18;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.ENCHANTMENT;
			}
			public boolean canUseTargetNone() {
				return false;
			}
			public boolean isCombat() {
				return false;
			}
		};
	}
	//造水术
	public static MagicSpell CreateWater(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "create_water", JerotesParticleTypes.CREATE_WATER_DISPLAY.get(), JerotesSoundEvents.MAGIC_CREATE_WATER){
			public boolean spellFindUse() {
				return SpellFind.CreateWater(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public float getSpellDistanceBase() {
				return 9;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isCombat() {
				return false;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 5;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//枯水术
	public static MagicSpell DestroyWater(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "destroy_water", JerotesParticleTypes.DESTROY_WATER_DISPLAY.get(), JerotesSoundEvents.MAGIC_DESTROY_WATER){
			public boolean spellFindUse() {
				return SpellFind.DestroyWater(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public float getSpellDistanceBase() {
				return 9;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isCombat() {
				return false;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 2;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//凋灵之首
	public static MagicSpell WitherSkull(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "wither_skull", JerotesParticleTypes.WITHER_SKULL_DISPLAY.get(), JerotesSoundEvents.MAGIC_WITHER_SKULL){
			public boolean spellFindUse() {
				return SpellFind.WitherSkull(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, (int) (getSpellLevel()/2f), 1 + (getSpellLevel() - 1) * 0.125f, getSpellAccuracy(), 1, 5, getCaster() instanceof Player, getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
			public float getSpellDistanceBase() {
				return 60;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//危险凋灵之首
	public static MagicSpell DangerousWitherSkull(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "dangerous_wither_skull", JerotesParticleTypes.DANGEROUS_WITHER_SKULL_DISPLAY.get(), JerotesSoundEvents.MAGIC_DANGEROUS_WITHER_SKULL){
			public boolean spellFindUse() {
				return SpellFind.DangerousWitherSkull(getCaster(), getTarget(), getSpellLevel(), 6 + (getSpellLevel() - 1) * 3, (int) (getSpellLevel()/2f), 1 + (getSpellLevel() - 1) * 0.125f, getSpellAccuracy(), 1, 5, getCaster() instanceof Player, getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
			public float getSpellDistanceBase() {
				return 36;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 1;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//唤魔者伎俩
	public static MagicSpell Evokercraft(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "evokercraft", JerotesParticleTypes.EVOKERCRAFT_DISPLAY.get(), JerotesSoundEvents.MAGIC_EVOKERCRAFT){
			public boolean spellFindUse() {
				return SpellFind.Evokercraft(getCaster(), getTarget(), getSpellLevel(), getSubcategory());
			}
			public float getSpellDistanceBase() {
				return 30;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public boolean isCantrip() {
				return true;
			}
			public boolean isCombat() {
				return false;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 5;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//监守者音爆
	public static MagicSpell WardenSonicBoom(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "warden_sonic_boom", JerotesParticleTypes.WARDEN_SONIC_BOOM_DISPLAY.get(), JerotesSoundEvents.MAGIC_WARDEN_SONIC_BOOM){
			public boolean spellFindUse() {
				return SpellFind.WardenSonicBoom(getCaster(), getTarget(), getSpellLevel());
			}
			public float getSpellDistanceBase() {
				return 20;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 2;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
			public float getSoundLevel() {
				return 3f;
			}
		};
	}
	//红石激光
	public static MagicSpell RedstoneLaser(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "redstone_laser", JerotesParticleTypes.REDSTONE_LASER_DISPLAY.get(), JerotesSoundEvents.MAGIC_REDSTONE_LASER){
			public boolean spellFindUse() {
				return SpellFind.RedstoneLaser(getCaster(), getTarget(), getSpellLevel());
			}
			public float getSpellDistanceBase() {
				return 36;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUseTargetNone() {
				return true;
			}
			public boolean isCantrip() {
				return true;
			}
			public boolean isCombat() {
				return false;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
		};
	}
	//即刻锻造
	public static MagicSpell InstantForge(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.MAIN, "instant_forge", JerotesParticleTypes.INSTANT_FORGE_DISPLAY.get(), JerotesSoundEvents.MAGIC_INSTANT_FORGE){
			public boolean spellFindUse() {
				return SpellFind.InstantForge(getCaster(), getSpellLevel(), getSubcategory());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public boolean isCombat() {
				return false;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.TRANSMUTATION;
			}
			//
			public int getCustomSubcategoryReachAdd() {
				return 7;
			}
			public int usedByNonPlayerFindSubcategory(LivingEntity livingEntity) {
				return 0;
			}
			//
		};
	}
	//末影箱
	public static MagicSpell EnderChest(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "ender_chest", JerotesParticleTypes.ENDER_CHEST_DISPLAY.get(), JerotesSoundEvents.MAGIC_ENDER_CHEST){
			public boolean spellFindUse() {
				return SpellFind.EnderChest(getCaster(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistanceBase() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public boolean isCombat() {
				return false;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
	//锁定目标
	public static MagicSpell LockAim(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "lock_aim", JerotesParticleTypes.LOCK_AIM_DISPLAY.get(), JerotesSoundEvents.MAGIC_LOCK_AIM){
			public boolean spellFindUse() {
				return SpellFind.LockAim(getCaster(), getTarget(), getSpellLevel());
			}
			public float getSpellDistanceBase() {
				return 60;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 1;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.DIVINATION;
			}
			public boolean isConcentration() {
				return true;
			}
		};
	}
}