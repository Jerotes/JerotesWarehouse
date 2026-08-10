package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.EliteEntity;
import com.jerotes.jerotes.entity.Interface.FireAbsorptionEntity;
import com.jerotes.jerotes.entity.Interface.ColdAbsorptionEntity;
import com.jerotes.jerotes.entity.Interface.LightningAbsorptionEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.MirrorImageEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.spell.SpellUtil;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class EffectEvent {

	//魔法吸收
	@SubscribeEvent
	public static void MagicAbsorption(LivingHurtEvent event) {
		DamageSource damagesource = event.getSource();
		LivingEntity entity = event.getEntity();
		if (damagesource == null || entity == null)
			return;
		if (AttackFind.MagicResistance(damagesource)){
			float damages = 1;
			if (!entity.level().isClientSide()) {
				if (entity.hasEffect(JerotesMobEffects.MAGIC_ABSORPTION.get())) {
					int effectLevel = Objects.requireNonNull(entity.getEffect(JerotesMobEffects.MAGIC_ABSORPTION.get())).getAmplifier() + 1;
					damages -= effectLevel * 0.1f;
				}
			}
			event.setAmount(event.getAmount() * damages);
		}
	}
	//元素吸收
	@SubscribeEvent
	public static void OtherAbsorption(LivingAttackEvent event) {
		DamageSource damagesource = event.getSource();
		LivingEntity entity = event.getEntity();
		if (damagesource == null || entity == null)
			return;
		double damages = 1;
		if (AttackFind.isFireDamage(damagesource)){
			if (!entity.level().isClientSide()) {
				if (entity.hasEffect(JerotesMobEffects.FIRE_ABSORPTION.get()) || entity instanceof FireAbsorptionEntity) {
					double AbsorptionPercentage = Math.max(
							(entity instanceof FireAbsorptionEntity absorptionEntity) ? absorptionEntity.FireAbsorptionPercentage() : 0,
							(entity.hasEffect(JerotesMobEffects.FIRE_ABSORPTION.get())) ? (Objects.requireNonNull(entity.getEffect(JerotesMobEffects.FIRE_ABSORPTION.get())).getAmplifier() + 1) * 20 : 0);
					damages -= AbsorptionPercentage / 100f;
					boolean bl = SpellFind.FireAbsorption(entity, event.getAmount(), AbsorptionPercentage, 0.25f);
					if (bl) {
						if (!entity.isSilent()) {
							entity.playSound(JerotesSoundEvents.MAGIC_FIRE_ABSORPTION, EntityAndItemFind.isBoss(entity.getType()) ? 5f : (entity instanceof EliteEntity ? 2f : 1f) * 1, 1.0F);
						}
					}
				}
			}
		}
		if (AttackFind.isColdDamage(damagesource)){
			if (!entity.level().isClientSide()) {
				if (entity.hasEffect(JerotesMobEffects.COLD_ABSORPTION.get()) || entity instanceof ColdAbsorptionEntity) {
					double AbsorptionPercentage = Math.max(
							(entity instanceof ColdAbsorptionEntity absorptionEntity) ? absorptionEntity.ColdAbsorptionPercentage() : 0,
							(entity.hasEffect(JerotesMobEffects.COLD_ABSORPTION.get())) ? (Objects.requireNonNull(entity.getEffect(JerotesMobEffects.COLD_ABSORPTION.get())).getAmplifier() + 1) * 20 : 0);
					damages -= AbsorptionPercentage / 100f;
					boolean bl = SpellFind.ColdAbsorption(entity, event.getAmount(), AbsorptionPercentage, 0.25f);
					if (bl) {
						if (!entity.isSilent()) {
							entity.playSound(JerotesSoundEvents.MAGIC_COLD_ABSORPTION, EntityAndItemFind.isBoss(entity.getType()) ? 5f : (entity instanceof EliteEntity ? 2f : 1f) * 1, 1.0F);
						}
					}
				}
			}
		}
		if (AttackFind.isLightningDamage(damagesource)){
			if (!entity.level().isClientSide()) {
				if (entity.hasEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get()) || entity instanceof LightningAbsorptionEntity) {
					double AbsorptionPercentage = Math.max(
							(entity instanceof LightningAbsorptionEntity absorptionEntity) ? absorptionEntity.LightningAbsorptionPercentage() : 0,
							(entity.hasEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get())) ? (Objects.requireNonNull(entity.getEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get())).getAmplifier() + 1) * 20 : 0);
					damages -= AbsorptionPercentage / 100f;
					boolean bl = SpellFind.LightningAbsorption(entity, event.getAmount(), AbsorptionPercentage, 0.25f);
					if (bl) {
						if (!entity.isSilent()) {
							entity.playSound(JerotesSoundEvents.MAGIC_LIGHTNING_ABSORPTION, EntityAndItemFind.isBoss(entity.getType()) ? 5f : (entity instanceof EliteEntity ? 2f : 1f) * 1, 1.0F);
						}
					}
				}
			}
		}
		if (damages <= 0) {
			event.setCanceled(true);
		}
	}
	@SubscribeEvent
	public static void OtherAbsorption(LivingHurtEvent event) {
		DamageSource damagesource = event.getSource();
		LivingEntity entity = event.getEntity();
		if (damagesource == null || entity == null)
			return;
		double damages = 1;
		if (AttackFind.isFireDamage(damagesource)){
			if (!entity.level().isClientSide()) {
				if (entity.hasEffect(JerotesMobEffects.FIRE_ABSORPTION.get()) || entity instanceof FireAbsorptionEntity) {
					double AbsorptionPercentage = Math.max(
							(entity instanceof FireAbsorptionEntity absorptionEntity) ? absorptionEntity.FireAbsorptionPercentage() : 0,
							(entity.hasEffect(JerotesMobEffects.FIRE_ABSORPTION.get())) ? (Objects.requireNonNull(entity.getEffect(JerotesMobEffects.FIRE_ABSORPTION.get())).getAmplifier() + 1) * 20 : 0);
					damages -= AbsorptionPercentage / 100f;
				}
			}
		}
		if (AttackFind.isColdDamage(damagesource)){
			if (!entity.level().isClientSide()) {
				if (entity.hasEffect(JerotesMobEffects.COLD_ABSORPTION.get()) || entity instanceof ColdAbsorptionEntity) {
					double AbsorptionPercentage = Math.max(
							(entity instanceof ColdAbsorptionEntity absorptionEntity) ? absorptionEntity.ColdAbsorptionPercentage() : 0,
							(entity.hasEffect(JerotesMobEffects.COLD_ABSORPTION.get())) ? (Objects.requireNonNull(entity.getEffect(JerotesMobEffects.COLD_ABSORPTION.get())).getAmplifier() + 1) * 20 : 0);
					damages -= AbsorptionPercentage / 100f;
				}
			}
		}
		if (AttackFind.isLightningDamage(damagesource)){
			if (!entity.level().isClientSide()) {
				if (entity.hasEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get()) || entity instanceof LightningAbsorptionEntity) {
					double AbsorptionPercentage = Math.max(
							(entity instanceof LightningAbsorptionEntity absorptionEntity) ? absorptionEntity.LightningAbsorptionPercentage() : 0,
							(entity.hasEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get())) ? (Objects.requireNonNull(entity.getEffect(JerotesMobEffects.LIGHTNING_ABSORPTION.get())).getAmplifier() + 1) * 20 : 0);
					damages -= AbsorptionPercentage / 100f;
				}
			}
		}
		event.setAmount((float) (event.getAmount() * Math.max(0, damages)));
	}

	//定身术
	@SubscribeEvent
	public static void MobHold(LivingEvent.LivingTickEvent event) {
		LivingEntity livingEntity = event.getEntity();
		if (livingEntity != null) {
			if (livingEntity.hasEffect(JerotesMobEffects.HOLD_MOB.get())) {
				if (Main.getJerotesPersistentData(livingEntity).getFloat("jerotes_hold_mob_x_rot") < 500) {
					Main.getJerotesPersistentData(livingEntity).putFloat("jerotes_hold_mob_x_rot", livingEntity.getXRot() + 500);
				} else {
					livingEntity.setXRot(Main.getJerotesPersistentData(livingEntity).getFloat("jerotes_hold_mob_x_rot") - 500);
					livingEntity.xRotO = Main.getJerotesPersistentData(livingEntity).getFloat("jerotes_hold_mob_x_rot") - 500;
				}
				if (Main.getJerotesPersistentData(livingEntity).getFloat("jerotes_hold_mob_y_rot") < 500) {
					Main.getJerotesPersistentData(livingEntity).putFloat("jerotes_hold_mob_y_rot", livingEntity.getYRot() + 500);
				} else {
					livingEntity.setYRot(Main.getJerotesPersistentData(livingEntity).getFloat("jerotes_hold_mob_y_rot") - 500);
					livingEntity.yRotO = Main.getJerotesPersistentData(livingEntity).getFloat("jerotes_hold_mob_y_rot") - 500;
					livingEntity.setYHeadRot(Main.getJerotesPersistentData(livingEntity).getFloat("jerotes_hold_mob_y_rot") - 500);
					livingEntity.setYBodyRot(Main.getJerotesPersistentData(livingEntity).getFloat("jerotes_hold_mob_y_rot") - 500);

				}
			}
			if (!livingEntity.hasEffect(JerotesMobEffects.HOLD_MOB.get())) {
				Main.persistentDataRemove(livingEntity, "jerotes_hold_mob_x_rot");
				Main.persistentDataRemove(livingEntity, "jerotes_hold_mob_y_rot");
			}
		}
	}

	@SubscribeEvent
	public static void MirrorImage(LivingAttackEvent event) {
		DamageSource damagesource = event.getSource();
		LivingEntity entity = event.getEntity();
		if (damagesource == null || entity == null)
			return;
		if (!entity.hasEffect(JerotesMobEffects.MIRROR_IMAGE.get())) {
			return;
		}
		if (damagesource.getEntity() instanceof LivingEntity living && (EntityAndItemFind.targetBlindnessTrue(living) ||
				living.hasEffect(MobEffects.NIGHT_VISION) ||
				living.hasEffect(JerotesMobEffects.TRUESIGHT.get()))) {
			return;
		}
		if (damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return;
		}
		if (!(entity.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		List<MirrorImageEntity> list2 = serverLevel.getEntitiesOfClass(MirrorImageEntity.class, entity.getBoundingBox().inflate(32, 32, 32));
		list2.removeIf(mirrorImageEntity -> mirrorImageEntity.getOwner() != entity);
		if (list2.isEmpty()) {
			return;
		}

		boolean can = false;
		for (int i = 0; i < list2.size(); i++) {
			if (Main.rollDice(1, 6) >= 3) {
				can = true;
				break;
			}
		}

		if (can) {
			MirrorImageEntity toDiscard = list2.get(0);
			toDiscard.discard();
			event.setCanceled(true);
		}
	}

	//法术反制
	@SubscribeEvent
	public static void Counterspell(JerotesStopSpellEvent event) {
		Entity otherCaster = event.getOtherCaster();
		Entity otherMagic = event.getSpellEntity();
		Entity caster = event.getCaster();
		if (otherMagic instanceof MagicAboutEntity magicAbout && otherCaster != null && !magicAbout.isHelp()) {
			boolean bl = SpellUtil.SpellReaction(caster, otherCaster, otherMagic, magicAbout.getSpellLevel());
			event.setCanceled(bl);
		}
		//存在危害类
		if (event.getType() == 1) {
			boolean bl = SpellUtil.SpellReaction(caster, otherCaster, otherMagic, event.getLevel());
			event.setCanceled(bl);
		}
	}
}

