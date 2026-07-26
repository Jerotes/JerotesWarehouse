package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesVexEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.HoldMobEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.HoldPersonMainEntity;
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
				return (2 + getSpellLevel()) * 5 + 4;
			}
			public boolean CastingAboutBase(int n) {
				if (n % 5 == 0 && n != 0) {
					getCaster().swing(InteractionHand.MAIN_HAND, true);
					Entity entity = getTarget();
					if (getCaster() instanceof Player player && Main.getTargetedEntity(player, getSpellDistance()) != null && canUseToEntity(Main.getTargetedEntity(player, getSpellDistance()))) {
						entity = Main.getTargetedEntity(player, getSpellDistance());
					}
					SpellFind.MagicMissile(getCaster(), (entity instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSpellAccuracy(), 1, 1, 1, getCaster() instanceof Player);
					if (!getCaster().isSilent()) {
						getCaster().level().playSound(null, getCaster().getX(), getCaster().getY(), getCaster().getZ(), getSound(),
								getCaster().getSoundSource(), 1.0f, 1.0F);
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
			public float getSpellDistanceBase() {
				return 36;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.EVOCATION;
			}
		};
	}
	//毒性吐息
	public static MagicSpell PoisonBreath(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "poison_breath", JerotesParticleTypes.POISON_BREATH_DISPLAY.get(), JerotesSoundEvents.MAGIC_POISON_BREATH){
			public boolean spellFindUse() {
				return SpellFind.PoisonBreath(getCaster(), getTarget(), 2 * getSpellLevel(), getSpellLevel() * 2, getSpellLevel() * 6, getSpellLevel(), getSpellAccuracy(), getSpellLevel(), 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
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
	//致病射线
	public static MagicSpell RayofSickness(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ray_of_sickness", JerotesParticleTypes.RAY_OF_SICKNESS_DISPLAY.get(), JerotesSoundEvents.MAGIC_RAY_OF_SICKNESS){
			public boolean spellFindUse() {
				return SpellFind.RayofSickness(getCaster(), getTarget(), getSpellLevel(), getSpellLevel() * 4, getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
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
				return SpellFind.RayofEnfeeblement(getCaster(), getTarget(), getSpellLevel() * 6, getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
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
	//虚弱射线
	public static MagicSpell Disintegrate(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "disintegrate", JerotesParticleTypes.DISINTEGRATE_DISPLAY.get(), JerotesSoundEvents.MAGIC_DISINTEGRATE){
			public boolean spellFindUse() {
				return SpellFind.Disintegrate(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
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
		};
	}
	//死亡一指
	public static MagicSpell FingerOfDeath(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "finger_of_death", JerotesParticleTypes.FINGER_OF_DEATH_DISPLAY.get(), JerotesSoundEvents.MAGIC_FINGER_OF_DEATH){
			public boolean spellFindUse() {
				return SpellFind.FingerOfDeath(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
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
				return SpellFind.HoldPerson(getCaster(), getTarget(), getSpellLevel(), getSpellLevel() * 6, 1, Math.max(1, getSpellLevel() - 1), true);
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
										getCaster().getSoundSource(), 1.0f, 1.0F);
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
	//降咒
	public static MagicSpell BestowCurse(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "bestow_curse", JerotesParticleTypes.BESTOW_CURSE_DISPLAY.get(), JerotesSoundEvents.MAGIC_BESTOW_CURSE){
			public boolean spellFindUse() {
				return SpellFind.BestowCurse(getCaster(), getTarget(), getSpellLevel() * 6, getSpellLevel());
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
			public float getSpellDistanceBase() {
				return 6;
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
				return SpellFind.Eyebite(getCaster(), getTarget(), getSpellLevel() * 6, getSpellLevel());
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
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.FIRE_ABSORPTION.get(), getSpellLevel() * 6 * 20, getSpellLevel() - 1));
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
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.COLD_ABSORPTION.get(), getSpellLevel() * 6 * 20, getSpellLevel() - 1));
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
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.LIGHTNING_ABSORPTION.get(), getSpellLevel() * 6 * 20, getSpellLevel() - 1));
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
			public float shouldBeSelf() {
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
				return SpellSchool.CONJURATION;
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
			public float shouldBeSelf() {
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
				return SpellSchool.CONJURATION;
			}
		};
	}
	//迷踪步
	public static MagicSpell MistyStep(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "misty_step", JerotesParticleTypes.MISTY_STEP_DISPLAY.get(), JerotesSoundEvents.MAGIC_MISTY_STEP){
			public boolean spellFindUse() {
				return SpellFind.MistyStep(getCaster(), 30, 16, getCaster() instanceof Player);
			}
			public boolean isHelp() {
				return true;
			}
			public float getSpellDistanceBase() {
				return 30;
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
					if (Main.getJerotesPersistentData(getCaster()).get("jerotes_has_mirror_image_1_tick") != null && Main.getJerotesPersistentData(getCaster()).getDouble("jerotes_has_mirror_image_1_tick") > 0) {
						return false;
					}
					if (Main.getJerotesPersistentData(getCaster()).get("jerotes_has_mirror_image_2_tick") != null && Main.getJerotesPersistentData(getCaster()).getDouble("jerotes_has_mirror_image_2_tick") > 0) {
						return false;
					}
					if (Main.getJerotesPersistentData(getCaster()).get("jerotes_has_mirror_image_3_tick") != null && Main.getJerotesPersistentData(getCaster()).getDouble("jerotes_has_mirror_image_3_tick") > 0) {
						return false;
					}
					return super.canUse();
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
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get()) && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get())).getAmplifier() >= getSpellLevel() - 1 && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get())).getDuration() > 6 * 20 && !(getCaster() instanceof Player)) {
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
	//火球术
	public static MagicSpell Fireball(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "fireball", JerotesParticleTypes.FIREBALL_DISPLAY.get(), JerotesSoundEvents.MAGIC_FIREBALL){
			public boolean spellFindUse() {
				return SpellFind.Fireball(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
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
		};
	}

	//线形唤魔者尖牙
	public static MagicSpell LinearEvokerFang(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "linear_evoker_fang", JerotesParticleTypes.LINEAR_EVOKER_FANG_DISPLAY.get(), JerotesSoundEvents.MAGIC_LINEAR_EVOKER_FANG){
			public boolean spellFindUse() {
				return SpellFind.LinearEvokerFang(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSpellLevel() * 4, 2f);
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
				return 6;
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
				return SpellFind.ConjureVexs(getCaster(), getSpellLevel(), getSpellLevel(), 16);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int getBaseSpellLevel() {
				return 3;
			}
			public float getSpellDistanceBase() {
				return 16;
			}
			public boolean canUse() {
				if (getCaster() != null) {
					List<JerotesVexEntity> list = getCaster().level().getEntitiesOfClass(JerotesVexEntity.class, getCaster().getBoundingBox().inflate(128, 128, 128));
					list.removeIf(summon -> summon.getOwner() != getCaster());
					return super.canUse() && list.size() <= getSpellLevel() * 1.5f;
				}
				return super.canUse();
			}
			public SpellSchool getSpellSchool() {
				return SpellSchool.CONJURATION;
			}
		};
	}
}