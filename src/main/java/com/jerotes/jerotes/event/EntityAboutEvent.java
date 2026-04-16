package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.ControlVehicleEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Mob.TestEntity;
import com.jerotes.jerotes.entity.Interface.UseBowEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.forge.JerotesFactionEvent;
import com.jerotes.jerotes.forge.JerotesMerorDamageEvent;
import com.jerotes.jerotes.forge.JerotesMeleeDamageFromMainHandIsOffHandEvent;
import com.jerotes.jerotes.goal.JerotesPikeUseGoal;
import com.jerotes.jerotes.goal.JerotesShockAbackGoal;
import com.jerotes.jerotes.goal.JerotesSpearUseGoal;
import com.jerotes.jerotes.goal.TaczGunAttackGoal;
import com.jerotes.jerotes.init.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class EntityAboutEvent {

	//阵营免伤
	@SubscribeEvent
	public static void FactionHurt(LivingAttackEvent event) {
		LivingEntity hurt = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (!MainConfig.SameFactionAvoidDamage)
			return;
		if (AttackFind.SameFactionAvoidDamage(attacker, hurt))
			event.setCanceled(true);
		if (AttackFind.SameFactionAvoidDamage(attacker, hurt.getControllingPassenger()) && (hurt instanceof Mob mob && mob.getTarget() != attacker))
			event.setCanceled(true);
	}
	//是盟友
	@SubscribeEvent
	public static void JerotesFaction(JerotesFactionEvent event) {
		LivingEntity livingEntity = event.getLiving1();
		LivingEntity livingEntity2 = event.getLiving2();
		if (livingEntity == null || livingEntity2 == null)
			return;


		boolean faction = livingEntity instanceof JerotesEntity jerotes && jerotes.isFactionWith(livingEntity2) ||
				livingEntity2 instanceof JerotesEntity jerotes2 && jerotes2.isFactionWith(livingEntity);
		boolean owner1 = livingEntity instanceof OwnableEntity ownable && ownable.getOwner() != null;
		boolean owner2 = livingEntity2 instanceof OwnableEntity ownable && ownable.getOwner() != null;
		boolean factionWitch = faction && !owner1 && !owner2;
		boolean trueFaction = !Objects.equals(EntityFactionFind.getTrueFaction(livingEntity), "") && !Objects.equals(EntityFactionFind.getTrueFaction(livingEntity2), "") && Objects.equals(EntityFactionFind.getTrueFaction(livingEntity), EntityFactionFind.getTrueFaction(livingEntity2));
		if (factionWitch
				|| trueFaction
				|| EntityFactionFind.isRaider(livingEntity) && EntityFactionFind.isRaider(livingEntity2) && !owner1 && !owner2
				|| EntityFactionFind.isPiglin(livingEntity) && EntityFactionFind.isPiglin(livingEntity2) && !owner1 && !owner2) {
			event.setFriend(true);
			return;
		}
		boolean enmey = livingEntity instanceof JerotesEntity jerotes && jerotes.isHateFaction(livingEntity2) ||
				livingEntity2 instanceof JerotesEntity jerotes2 && jerotes2.isHateFaction(livingEntity);
		if (enmey) {
			event.setEnemy(true);
        }
	}


	@SubscribeEvent
	public static void JerotesMerorDamage(JerotesMerorDamageEvent event) {
		DamageSource damageSource = event.getDamageSource();
		if (damageSource == null)
			return;
		//莫厄攻击类型
		if (damageSource.getEntity() != null && EntityAndItemFind.isMerorAttackEntity(damageSource.getEntity().getType()))
			event.setMerorDamage(true);
		if (damageSource.getDirectEntity() != null && EntityAndItemFind.isMerorAttackEntity(damageSource.getDirectEntity().getType()))
			event.setMerorDamage(true);
		//近战
		if (EntityAndItemFind.isMeleeDamage(damageSource) && damageSource.getEntity() instanceof LivingEntity livingEntity) {
			ItemStack handItem = livingEntity.getMainHandItem();
			if (!EntityAndItemFind.MeleeDamageFromMainHandNotOffHand(livingEntity))
				handItem = livingEntity.getOffhandItem();
			if (EntityAndItemFind.isMerorWeapon(handItem))
				event.setMerorDamage(true);
		}
	}

	@SubscribeEvent
	public static void JerotesNotOffHand(JerotesMeleeDamageFromMainHandIsOffHandEvent event) {
		LivingEntity livingEntity = event.getLivingEntity();
		if (livingEntity == null)
			return;
		if (livingEntity.swingingArm == InteractionHand.MAIN_HAND && livingEntity.swingTime > 0 ||
				livingEntity.getOffhandItem().isEmpty())
			return;
		if (livingEntity.isUsingItem() && livingEntity.getUsedItemHand() == InteractionHand.OFF_HAND)
			event.setOffHand(true);
		if (livingEntity.getUseItem() != livingEntity.getMainHandItem() && livingEntity.getUseItem() == livingEntity.getOffhandItem())
			event.setOffHand(true);
	}

	@SubscribeEvent
	public static void OtherVisibility(LivingEvent.LivingVisibilityEvent event) {
		LivingEntity self = event.getEntity();
		Entity lookingEntity = event.getLookingEntity();
		if (lookingEntity == null || self == null)
			return;
		{
			if (EntityAndItemFind.isTrueInvisible(self)) {
				event.modifyVisibility(event.getVisibilityModifier()/2);
			}
			if (self.hasEffect(JerotesMobEffects.CLOAKING.get())) {
				int cloakingLevel = (Objects.requireNonNull(self.getEffect(JerotesMobEffects.CLOAKING.get())).getAmplifier() + 1);
				if (self.distanceTo(lookingEntity) > 48 - cloakingLevel * 8) {
					event.modifyVisibility(0);
				}
			}
		}
	}

	//其他
	@SubscribeEvent
	public static void OtherHurt(LivingAttackEvent event) {
		LivingEntity hurt = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		Entity attackerUse = event.getSource().getDirectEntity();
		if (attacker instanceof LivingEntity livingEntity) {
			attacker.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTagOrEmpty(JerotesMobEffectTags.REMOVE_AFTER_ATTACK).forEach(effect -> {
				if (livingEntity.hasEffect(effect.get())) {
					if (!livingEntity.level().isClientSide()) {
						livingEntity.removeEffect(effect.get());
					}
				}
			});
		}
		if (!((attackerUse instanceof MagicAbout magicShoot) && magicShoot instanceof Projectile projectile))
			return;
		if (hurt == projectile.getOwner()) {
			event.setCanceled(true);
		}
	}

	//附魔
	@SubscribeEvent
	public static void EnchantHurt(LivingHurtEvent event) {
		if (event != null && event.getEntity() != null) {
			LivingEntity livingEntity = event.getEntity();
			DamageSource damagesource = event.getSource();
			if (damagesource == null || livingEntity == null)
				return;
			if (!livingEntity.level().isClientSide()) {
				if (damagesource.is(JerotesDamageTypes.CORROSIVE)) {
					float damages = 1;
					ItemStack head = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
					ItemStack chest = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
					ItemStack legs = livingEntity.getItemBySlot(EquipmentSlot.LEGS);
					ItemStack feet = livingEntity.getItemBySlot(EquipmentSlot.FEET);
					damages -= head.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) * 0.08f;
					damages -= chest.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) * 0.08f;
					damages -= legs.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) * 0.08f;
					damages -= feet.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) * 0.08f;
					event.setAmount(event.getAmount() * damages);
				}
			}
		}
	}

	//附魔增伤
	@SubscribeEvent
	public static void addWeaponEnchant(LivingDamageEvent event) {
		LivingEntity entity = event.getEntity();
		Entity attackBy = event.getSource().getEntity();
		float amount = event.getAmount();
		if (entity == null || !entity.isAlive())
			return;
		if (attackBy != null) {
			if (attackBy instanceof LivingEntity living) {
				if (((attackBy instanceof Mob || attackBy instanceof Player)) && event.getSource().getDirectEntity() == living && EntityAndItemFind.isMeleeDamage(event.getSource())) {
					ItemStack handItem = living.getMainHandItem();
					if (!EntityAndItemFind.MeleeDamageFromMainHandNotOffHand(living))
						handItem = living.getOffhandItem();

					if (JerotesEnchantments.BANE_OF_HUMANOIDS != null && EntityFactionFind.isHumanoid(entity) && attackBy instanceof LivingEntity livingEntity && !handItem.isEmpty() && handItem.getEnchantmentLevel(JerotesEnchantments.BANE_OF_HUMANOIDS.get()) > 0) {
						if (living.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
							float baseDamage = (float) living.getAttributeValue(Attributes.ATTACK_DAMAGE);
							event.setAmount(amount + amount / baseDamage * (livingEntity.getMainHandItem().getEnchantmentLevel(JerotesEnchantments.BANE_OF_HUMANOIDS.get()) * 2.5f));
						}
					}
				}
			}
		}
	}


	//阵营减伤
	@SubscribeEvent
	public static void FactionHurtHaveDamage(LivingDamageEvent event) {
		LivingEntity hurt = event.getEntity();
		Entity attacker = event.getSource().getEntity();
		if (!(attacker instanceof LivingEntity livingEntityAttacker))
			return;
		if (!MainConfig.TamedMobHurtByOwnerHasReduce)
			return;
		//驯服生物
		if (livingEntityAttacker instanceof OwnableEntity ownable && ownable.getOwner() == hurt) {
			if (livingEntityAttacker instanceof JerotesEntity || hurt instanceof JerotesEntity || MainConfig.AffectsNonThisModEntities) {
				event.setAmount(event.getAmount() / 3);
				event.setCanceled(true);
			}
		}
		if (hurt instanceof OwnableEntity ownable && ownable.getOwner() == livingEntityAttacker) {
			if (livingEntityAttacker instanceof JerotesEntity || hurt instanceof JerotesEntity || MainConfig.AffectsNonThisModEntities)
				event.setAmount(event.getAmount() / 3);
		}
		if (hurt instanceof Mob mob && mob.getTarget() != livingEntityAttacker) {
			if (hurt instanceof JerotesEntity || MainConfig.AffectsNonThisModEntities) {
				if (Main.isTrusted(hurt, livingEntityAttacker, true)) {
					event.setAmount(event.getAmount() / 3);
				}
			}
		}
	}

	//行为
	@SubscribeEvent
	public static void GoalAdd(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof PathfinderMob pathfinderMob) {
			//威慑
			if (!EntityAndItemFind.isAbackAwayImmune(pathfinderMob.getType()) && MainConfig.MobHasShockAback) {
				pathfinderMob.goalSelector.addGoal(1, new JerotesShockAbackGoal(pathfinderMob, 1.2));
			}
			//矛
			if (pathfinderMob instanceof Zombie && MainConfig.MobManuallyControlCombatCameraChange ||
					pathfinderMob.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:can_use_spears")))) {
				pathfinderMob.goalSelector.addGoal(1, new JerotesSpearUseGoal<>(pathfinderMob, 1.0, 1.0, 10.0f, 2.0f,
						!pathfinderMob.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:can_use_spears_and_can_not_normal_attack"))),
						!pathfinderMob.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:can_use_spears_and_can_not_charge_attack")))
				));
			}
			//长枪
			if (pathfinderMob instanceof Zombie && MainConfig.MobManuallyControlCombatCameraChange ||
					pathfinderMob.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:can_use_pikes")))) {
				pathfinderMob.goalSelector.addGoal(1, new JerotesPikeUseGoal(pathfinderMob, 1.2, true));
			}
			//tacz
			if (entity instanceof UseBowEntity useBowEntity && !useBowEntity.justBow()) {
				if (ModList.get().isLoaded("tacz")) {
					pathfinderMob.goalSelector.addGoal(useBowEntity.getBowUsePriority(), new TaczGunAttackGoal<>(pathfinderMob, 1.0D, 32.0f));
				}
			}
		}
	}

	//计时
	@SubscribeEvent
	public static void Tick(LivingEvent.LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity == null || !entity.isAlive())
			return;
		//镜影术
		Main.persistentDataDoubleReduceToZero(entity, "jerotes_has_mirror_image_1_tick", true);
		Main.persistentDataDoubleReduceToZero(entity, "jerotes_has_mirror_image_2_tick", true);
		Main.persistentDataDoubleReduceToZero(entity, "jerotes_has_mirror_image_3_tick", true);
		if (entity.getPersistentData().get("jerotes_has_mirror_image_1_tick") == null || entity.getPersistentData().getDouble("jerotes_has_mirror_image_1_tick") <= 0) {
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_1_x");
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_1_y");
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_1_z");
		}
		else {
			if (!entity.level().isClientSide()) {
				entity.addEffect(new MobEffectInstance(JerotesMobEffects.MIRROR_IMAGE.get(), 5, 0, false, false));
			}
		}
		if (entity.getPersistentData().get("jerotes_has_mirror_image_2_tick") == null && entity.getPersistentData().getDouble("jerotes_has_mirror_image_2_tick") <= 0) {
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_2_x");
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_2_y");
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_2_z");
		}
		else {
			if (!entity.level().isClientSide()) {
				entity.addEffect(new MobEffectInstance(JerotesMobEffects.MIRROR_IMAGE.get(), 5, 0, false, false));
			}
		}
		if (entity.getPersistentData().get("jerotes_has_mirror_image_3_tick") == null && entity.getPersistentData().getDouble("jerotes_has_mirror_image_3_tick") <= 0) {
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_3_x");
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_3_y");
			Main.persistentDataRemove(entity, "jerotes_has_mirror_image_3_z");
		}
		else {
			if (!entity.level().isClientSide()) {
				entity.addEffect(new MobEffectInstance(JerotesMobEffects.MIRROR_IMAGE.get(), 5, 0, false, false));
			}
		}
		//伤害间隔
		if (entity.getPersistentData().getFloat("jerotes_boss_hurt_cooldown") > 0) {
			entity.getPersistentData().putFloat("jerotes_boss_hurt_cooldown", entity.getPersistentData().getFloat("jerotes_boss_hurt_cooldown") - 1);
		}
		//弹反
		Main.persistentDataDoubleReduceToZero(entity, "jerotes_shield_parry_cooldown", true);
		Main.persistentDataDoubleReduceToZero(entity, "jerotes_shield_parry_tick", true);
	}

	//战斗
	@SubscribeEvent
	public static void BossAttack(LivingAttackEvent event) {
		Entity entity = event.getSource().getEntity();
		float damage = event.getAmount();
		LivingEntity boss = event.getEntity();
		//间隔
		//系列
		if (boss instanceof JerotesEntity jerotes) {
			if (jerotes.hasDamageCooldownTick()) {
				if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !boss.isInvulnerableTo(event.getSource())) {
					boolean breakThis = damage > boss.getPersistentData().getFloat("jerotes_boss_hurt_cooldown_last_damage") * jerotes.BreakHurtCooldownMultiple(event.getSource(), entity);
					boolean noCooldown = boss.getPersistentData().getFloat("jerotes_boss_hurt_cooldown") <= 0;
					if (!breakThis && !noCooldown) {
						event.setCanceled(true);
					} else {
						boss.getPersistentData().putFloat("jerotes_boss_hurt_cooldown", jerotes.DamageCooldownTick(event.getSource(), entity));
						boss.getPersistentData().putFloat("jerotes_boss_hurt_cooldown_last_damage", damage);
					}
				} else {
					boss.getPersistentData().putFloat("jerotes_boss_hurt_cooldown", jerotes.DamageCooldownTick(event.getSource(), entity));
					boss.getPersistentData().putFloat("jerotes_boss_hurt_cooldown_last_damage", damage);
				}
			}
		}
		else {
			if (MainConfig.HasDamageCooldownTick.contains(boss.getEncodeId()) || EntityAndItemFind.isLegendary(boss)) {
				float base = 1;
				if (event.getSource().is(DamageTypeTags.BYPASSES_COOLDOWN)) {
					base *= 0.5f;
				}
				if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !boss.isInvulnerableTo(event.getSource())) {
					boolean breakThis = damage > boss.getPersistentData().getFloat("jerotes_boss_hurt_cooldown_last_damage") * (float) MainConfig.BaseBreakHurtCooldownMultiple;
					boolean noCooldown = boss.getPersistentData().getFloat("jerotes_boss_hurt_cooldown") <= 0;
					if (!breakThis && !noCooldown) {
						event.setCanceled(true);
					} else {
						boss.getPersistentData().putFloat("jerotes_boss_hurt_cooldown", (float) MainConfig.BaseDamageCooldownTick * base);
						boss.getPersistentData().putFloat("jerotes_boss_hurt_cooldown_last_damage", damage);
					}
				} else {
					boss.getPersistentData().putFloat("jerotes_boss_hurt_cooldown", (float) MainConfig.BaseDamageCooldownTick * base);
					boss.getPersistentData().putFloat("jerotes_boss_hurt_cooldown_last_damage", damage);
				}
			}
		}
	}
	@SubscribeEvent
	public static void BossHurt(LivingHurtEvent event) {
		Entity entity = event.getSource().getEntity();
		float damage = event.getAmount();
		if (entity instanceof LivingEntity bossAttacker) {
			//百分比伤害
			//系列
			if (bossAttacker instanceof JerotesEntity jerotes) {
				if (jerotes.hasPercentageDamage() && damage > 0) {
					float damageAdd = jerotes.PercentageDamage(event.getSource());
					if (bossAttacker.getAttribute(Attributes.ATTACK_DAMAGE) != null &&
							event.getAmount() < bossAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE) / 0.75f) {
						damageAdd = (float) Math.max(damageAdd / 5, damageAdd * event.getAmount() / bossAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
					}
					damage = damage + (damageAdd / 100 * event.getEntity().getMaxHealth());
				}
			}
			else {
				if ((MainConfig.HasPercentageDamage.contains(bossAttacker.getEncodeId()) || EntityAndItemFind.isLegendary(bossAttacker)) && damage > 0) {
					float f = (float) MainConfig.BaseAttackPercentage;
					if (EntityAndItemFind.MagicResistance(event.getSource())) {
						f = (float) MainConfig.BaseMagicAttackPercentage;
					}
					float damageAdd = f;
					if (bossAttacker.getAttribute(Attributes.ATTACK_DAMAGE) != null &&
							event.getAmount() < bossAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE) / 0.75f) {
						damageAdd = (float) Math.max(damageAdd / 5, damageAdd * event.getAmount() / bossAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE));
					}
					damage = damage + (damageAdd / 100 * event.getEntity().getMaxHealth());
				}
			}
		}

		LivingEntity boss = event.getEntity();
		if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !boss.isInvulnerableTo(event.getSource())) {
			//限伤
			//系列
			if (boss instanceof JerotesEntity jerotes) {
				if (jerotes.hasDamageCap()) {
					if (damage > jerotes.DamageCap(event.getSource(), entity)) {
						damage = jerotes.DamageCap(event.getSource(), entity);
					}
				}
			}
			else {
				if (MainConfig.HasDamageCap.contains(boss.getEncodeId()) || EntityAndItemFind.isLegendary(boss)) {
					if (damage > (float) MainConfig.BaseDamageCap / 100 * boss.getMaxHealth()) {
						damage = (float) MainConfig.BaseDamageCap / 100 * boss.getMaxHealth();
					}
				}
			}
		}
		event.setAmount(damage);
	}
	@SubscribeEvent
	public static void BossDamage(LivingDamageEvent event) {
		Entity entity = event.getSource().getEntity();
		float damage = event.getAmount();
		LivingEntity boss = event.getEntity();
		if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !boss.isInvulnerableTo(event.getSource())) {
			//限伤
			//系列
			if (boss instanceof JerotesEntity jerotes) {
				if (jerotes.hasDamageCap()) {
					if (damage > jerotes.DamageCap(event.getSource(), entity)) {
						damage = jerotes.DamageCap(event.getSource(), entity);
					}
				}
			}
			else {
				if (MainConfig.HasDamageCap.contains(boss.getEncodeId()) || EntityAndItemFind.isLegendary(boss)) {
					if (damage > (float) MainConfig.BaseDamageCap / 100 * boss.getMaxHealth()) {
						damage = (float) MainConfig.BaseDamageCap / 100 * boss.getMaxHealth();
					}
				}
			}
		}
		event.setAmount(damage);
	}


	@SubscribeEvent
	public static void isManuallyControlCombatJerotes(AttackEntityEvent event) {
		if (event.getEntity() != null && event.getEntity().getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity &&
                controlVehicleEntity.canNotUseItemWhenControlVehicleJerotes() &&
                controlVehicleEntity.isManuallyControlCombatJerotes()) {
			event.setCanceled(true);
        }
	}
	@SubscribeEvent
	public static void isManuallyControlCombatJerotes(PlayerEvent.BreakSpeed event) {
		if (event.getEntity() != null && event.getEntity().getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity &&
				controlVehicleEntity.canNotUseItemWhenControlVehicleJerotes() &&
				controlVehicleEntity.isManuallyControlCombatJerotes()) {
			event.setNewSpeed(0);
		}
	}
	@SubscribeEvent
	public static void isManuallyControlCombatJerotes(PlayerInteractEvent.RightClickItem event) {
		if (event.getEntity() != null && event.getEntity().getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity &&
				controlVehicleEntity.canNotUseItemWhenControlVehicleJerotes() &&
				controlVehicleEntity.isManuallyControlCombatJerotes()) {
			event.setCanceled(true);
		}
	}
	@SubscribeEvent
	public static void isManuallyControlCombatJerotes(PlayerInteractEvent.RightClickBlock event) {
		if (event.getEntity() != null && event.getEntity().getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity &&
				controlVehicleEntity.canNotUseItemWhenControlVehicleJerotes() &&
				controlVehicleEntity.isManuallyControlCombatJerotes()) {
			event.setCanceled(true);
		}
	}
	@SubscribeEvent
	public static void isManuallyControlCombatJerotes(PlayerInteractEvent.EntityInteract event) {
		if (event.getEntity() != null && event.getEntity().getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity &&
				controlVehicleEntity.canNotUseItemWhenControlVehicleJerotes() &&
				controlVehicleEntity.isManuallyControlCombatJerotes()) {
			event.setCanceled(true);
		}
	}
}