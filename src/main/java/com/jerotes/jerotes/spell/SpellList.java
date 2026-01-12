package com.jerotes.jerotes.spell;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSounds;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class SpellList {
	//魔法飞弹
	public static MagicSpell MagicMissile(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "magic_missile", JerotesParticleTypes.MAGIC_MISSILE_DISPLAY.get(), JerotesSounds.MAGIC_MAGIC_MISSILE){
			public boolean spellFindUse() {
				return SpellFind.MagicMissile(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel(), getSpellAccuracy(), 1, 1, 3 + getSpellLevel(), getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUseTargetNone() {
				return true;
			}
		};
	}
	//毒性吐息
	public static MagicSpell PoisonBreath(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "poison_breath", JerotesParticleTypes.POISON_BREATH_DISPLAY.get(), JerotesSounds.MAGIC_POISON_BREATH){
			public boolean spellFindUse() {
				return SpellFind.PoisonBreath(getCaster(), getTarget(), 2 * getSpellLevel(), getSpellLevel() * 2, getSpellLevel() * 6, getSpellLevel(), getSpellAccuracy(), getSpellLevel(), 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
		};
	}
	//致病射线
	public static MagicSpell RayofSickness(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ray_of_sickness", JerotesParticleTypes.RAY_OF_SICKNESS_DISPLAY.get(), JerotesSounds.MAGIC_RAY_OF_SICKNESS){
			public boolean spellFindUse() {
				return SpellFind.RayofSickness(getCaster(), getTarget(), getSpellLevel(), getSpellLevel() * 4, getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
		};
	}
	//虚弱射线
	public static MagicSpell RayofEnfeeblement(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "ray_of_enfeeblement", JerotesParticleTypes.RAY_OF_ENFEEBLEMENT_DISPLAY.get(), JerotesSounds.MAGIC_RAY_OF_ENFEEBLEMENT){
			public boolean spellFindUse() {
				return SpellFind.RayofEnfeeblement(getCaster(), getTarget(), getSpellLevel() * 6, getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int baseSpellLevel() {
				return 2;
			}
		};
	}
	//闪电束
	public static MagicSpell LightningBolt(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SHOOT, MagicType.MAIN, "lightning_bolt", JerotesParticleTypes.LIGHTNING_BOLT_DISPLAY.get(), JerotesSounds.MAGIC_LIGHTNING_BOLT){
			public boolean spellFindUse() {
				return SpellFind.LightningBolt(getCaster(), getTarget(), getSpellLevel(), getSpellAccuracy(), 1, 5, getCaster() instanceof Player);
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int baseSpellLevel() {
				return 3;
			}
		};
	}
	//恶毒嘲笑
	public static MagicSpell ViciousMockery(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "vicious_mockery", JerotesParticleTypes.VICIOUS_MOCKERY_DISPLAY.get(), JerotesSounds.MAGIC_VICIOUS_MOCKERY){
			public boolean spellFindUse() {
				return SpellFind.ViciousMockery(getCaster(), getTarget(), getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistance() {
				return 32;
			}
		};
	}
	//人类定身术
	public static MagicSpell HoldPerson(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "hold_person", JerotesParticleTypes.HOLD_PERSON_DISPLAY.get(), JerotesSounds.MAGIC_HOLD_PERSON){
			public boolean spellFindUse() {
				return SpellFind.HoldPerson(getCaster(), getTarget(), getSpellLevel() * 6, 1, Math.max(1, getSpellLevel() - 1));
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int baseSpellLevel() {
				return 2;
			}
			public float getSpellDistance() {
				return 32;
			}
			public boolean canUseToEntity(Entity entity) {
				return entity instanceof LivingEntity livingEntity && EntityFactionFind.isHumanoid(livingEntity) && super.canUseToEntity(entity);
			}
		};
	}
	//降咒
	public static MagicSpell BestowCurse(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "bestow_curse", JerotesParticleTypes.BESTOW_CURSE_DISPLAY.get(), JerotesSounds.MAGIC_BESTOW_CURSE){
			public boolean spellFindUse() {
				return SpellFind.BestowCurse(getCaster(), getTarget(), getSpellLevel() * 6, getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int baseSpellLevel() {
				return 3;
			}
			public boolean isMelee() {
				return true;
			}
			public float getSpellDistance() {
				return 6;
			}
		};
	}
	//魅影杀手
	public static MagicSpell PhantasmalKiller(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "phantasmal_killer", JerotesParticleTypes.PHANTASMAL_KILLER_DISPLAY.get(), JerotesSounds.MAGIC_PHANTASMAL_KILLER){
			public boolean spellFindUse() {
				return SpellFind.PhantasmalKiller(getCaster(), getTarget(), getSpellLevel(), getSpellLevel() * 6, getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int baseSpellLevel() {
				return 4;
			}
			public float getSpellDistance() {
				return 32;
			}
			public boolean canUseToEntity(Entity entity) {
				if (entity instanceof LivingEntity livingEntity &&
						livingEntity.hasEffect(JerotesMobEffects.PHANTASMAL_KILLER.get()) &&
						Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.PHANTASMAL_KILLER.get())).getAmplifier() >= getSpellLevel() - 1) {
					return false;
				}
				return super.canUseToEntity(entity);
			}
		};
	}
	//摄心目光
	public static MagicSpell Eyebite(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "eyebite", JerotesParticleTypes.EYEBITE_DISPLAY.get(), JerotesSounds.MAGIC_EYEBITE){
			public boolean spellFindUse() {
				return SpellFind.Eyebite(getCaster(), getTarget(), getSpellLevel() * 6, getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public int baseSpellLevel() {
				return 6;
			}
			public float getSpellDistance() {
				return 32;
			}
		};
	}
	//火焰吸收
	public static MagicSpell FireAbsorption(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "fire_absorption", JerotesParticleTypes.FIRE_ABSORPTION_DISPLAY.get(), JerotesSounds.MAGIC_FIRE_ABSORPTION){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.FIRE_ABSORPTION.get(), getSpellLevel() * 6 * 20, getSpellLevel() - 1));
			}
			public float getSpellDistance() {
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
		};
	}
	//冰霜吸收
	public static MagicSpell FreezeAbsorption(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "freeze_absorption", JerotesParticleTypes.FREEZE_ABSORPTION_DISPLAY.get(), JerotesSounds.MAGIC_FREEZE_ABSORPTION){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.FREEZE_ABSORPTION.get(), getSpellLevel() * 6 * 20, getSpellLevel() - 1));
			}
			public float getSpellDistance() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.FREEZE_ABSORPTION.get()) && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.FREEZE_ABSORPTION.get())).getAmplifier() >= getSpellLevel() - 1 && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.FREEZE_ABSORPTION.get())).getDuration() > 6 * 20 && !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
		};
	}
	//闪电吸收
	public static MagicSpell LightningAbsorption(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "lightning_absorption", JerotesParticleTypes.LIGHTNING_ABSORPTION_DISPLAY.get(), JerotesSounds.MAGIC_LIGHTNING_ABSORPTION){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.LIGHTNING_ABSORPTION.get(), getSpellLevel() * 6 * 20, getSpellLevel() - 1));
			}
			public float getSpellDistance() {
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
		};
	}
	//疗伤术
	public static MagicSpell CureWounds(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.TARGET, MagicType.MAIN, "cure_wounds", JerotesParticleTypes.CURE_WOUNDS_DISPLAY.get(), JerotesSounds.MAGIC_CURE_WOUNDS){
			public boolean spellFindUse() {
				return SpellFind.CureWounds(getCaster(), (getTarget() instanceof LivingEntity livingEntity) ? livingEntity : getCaster(), getSpellLevel());
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
			public float getSpellDistance() {
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
						livingEntity.getHealth() < livingEntity.getMaxHealth() &&
						livingEntity.getMobType() != MobType.UNDEAD &&
						!EntityFactionFind.isMachine(livingEntity) &&
						!EntityFactionFind.isConstruct(livingEntity) && super.canUseToEntity(entity);
			}
		};
	}
	//迷踪步
	public static MagicSpell MistyStep(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "misty_step", JerotesParticleTypes.MISTY_STEP_DISPLAY.get(), JerotesSounds.MAGIC_MISTY_STEP){
			public boolean spellFindUse() {
				return SpellFind.MistyStep(getCaster(), 30, 16, getCaster() instanceof Player);
			}
			public boolean isHelp() {
				return true;
			}
			public float getSpellDistance() {
				return 30;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canUse() {
				if (getCaster() != null && !(getCaster().onGround() || Main.isInFluid(getCaster()))) {
					return false;
				}
				return super.canUse();
			}
		};
	}
	//镜影术
	public static MagicSpell MirrorImage(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.MAIN, "mirror_image", JerotesParticleTypes.MIRROR_IMAGE_DISPLAY.get(), JerotesSounds.MAGIC_MIRROR_IMAGE){
			public boolean spellFindUse() {
				return SpellFind.MirrorImage(getCaster(), 3, getSpellLevel());
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public float getSpellDistance() {
				return 2;
			}
			public boolean isHelp() {
				return true;
			}
			public int baseSpellLevel() {
				return 2;
			}
			public boolean canUse() {
				if (getCaster() != null) {
					if (getCaster().getPersistentData().get("jerotes_has_mirror_image_1_tick") != null && getCaster().getPersistentData().getDouble("jerotes_has_mirror_image_1_tick") > 0) {
						return false;
					}
					if (getCaster().getPersistentData().get("jerotes_has_mirror_image_2_tick") != null && getCaster().getPersistentData().getDouble("jerotes_has_mirror_image_2_tick") > 0) {
						return false;
					}
					if (getCaster().getPersistentData().get("jerotes_has_mirror_image_3_tick") != null && getCaster().getPersistentData().getDouble("jerotes_has_mirror_image_3_tick") > 0) {
						return false;
					}
					return super.canUse();
				}
				return super.canUse();
			}
		};
	}
	//隐形通道
	public static MagicSpell InvisiblePassage(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "invisible_passage", JerotesParticleTypes.INVISIBLE_PASSAGE_DISPLAY.get(), JerotesSounds.MAGIC_INVISIBLE_PASSAGE){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.INVISIBLE_PASSAGE.get(), getSpellLevel() * 24 * 20, getSpellLevel() - 1, false, false));
			}
			public float getSpellDistance() {
				return 2;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean isHelp() {
				return true;
			}
			public boolean canUse() {
				if (getCaster() != null && getCaster().hasEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get()) && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get())).getAmplifier() >= getSpellLevel() - 1 && Objects.requireNonNull(getCaster().getEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get())).getDuration() > 6 * 20 && !(getCaster() instanceof Player)) {
					return false;
				}
				return super.canUse();
			}
		};
	}
	//法术反制
	public static MagicSpell Counterspell(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "counterspell", JerotesParticleTypes.COUNTERSPELL_DISPLAY.get(), JerotesSounds.MAGIC_COUNTERSPELL){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.COUNTERSPELL.get(), 12 * 20, getSpellLevel() - 1));
			}
			public float getSpellDistance() {
				return 2;
			}
			public String getSpellModId() {
				return JerotesWarehouse.MODID;
			}
			public boolean canBeBreak() {
				return false;
			}
			public int baseSpellLevel() {
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
		};
	}
	//魔法吸收
	public static MagicSpell MagicAbsorption(int n, LivingEntity caster, Entity target) {
		return new MagicSpell(n, caster, target, MagicType.SELF, MagicType.ADD, "magic_absorption", JerotesParticleTypes.MAGIC_ABSORPTION_DISPLAY.get(), JerotesSounds.MAGIC_MAGIC_ABSORPTION){
			public boolean spellFindUse() {
				return !caster.level().isClientSide() && caster.addEffect(new MobEffectInstance(JerotesMobEffects.MAGIC_ABSORPTION.get(), getSpellLevel() * 3 * 20, getSpellLevel() - 1));
			}
			public float getSpellDistance() {
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
		};
	}
}