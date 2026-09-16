package com.jerotes.jerotes.util;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.*;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.forge.JerotesMerorDamageEvent;
import com.jerotes.jerotes.forge.JerotesMeleeDamageFromMainHandIsOffHandEvent;
import com.jerotes.jerotes.init.JerotesDamageTypeTags;
import com.jerotes.jerotes.init.JerotesMobEffectTags;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.network.JerotesPlayerData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityAndItemFind {
	//近战攻击
	public static boolean isMeleeDamage(DamageSource damageSource) {
		return (damageSource.getEntity() == damageSource.getDirectEntity() ||
				damageSource.getEntity() != null && damageSource.getDirectEntity() == null)
				&& damageSource.is(JerotesDamageTypeTags.IS_MELEE);
	}
	public static boolean MeleeDamageFromMainHandNotOffHand(LivingEntity livingEntity) {
		//event
		JerotesMeleeDamageFromMainHandIsOffHandEvent event = new JerotesMeleeDamageFromMainHandIsOffHandEvent(livingEntity);
		MinecraftForge.EVENT_BUS.post(event);
		return !event.isOffHand();
	}
	//是否Boss
	public static boolean isBoss(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge:bosses")));
	}
	//是否传奇
	public static boolean isLegendary(Entity entity) {
		if (isExalted(entity))
			return true;
		if (entity instanceof JerotesEntity jerotes && jerotes.isLegendary())
			return true;
		if (entity instanceof JerotesEntity jerotes && !jerotes.isLegendary())
			return false;
		if (entity instanceof Player player && player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).IsLegend)
			return true;
		return entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:legendary")));
	}
	//是否至高
	public static boolean isExalted(Entity entity) {
		if (entity instanceof JerotesEntity jerotes && jerotes.isExalted())
			return true;
		if (entity instanceof JerotesEntity jerotes && !jerotes.isExalted())
			return false;
		return entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:exalted")));
	}
	//是否免疫特殊击退
	public static boolean isNoSpecialKnockback(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:no_special_knockback")));
	}
	//是否忽略特殊伤害间隔
	public static boolean isIgnoreSpecialHurtCooldown(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:ignore_special_hurt_cooldown")));
	}
	//是否无法缴械
	public static boolean isUnableDisarm(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:unable_disarm")));
	}
	public static boolean isUnableDisarm(Entity entity) {
		return isUnableDisarm(entity.getType()) || isLegendary(entity);
	}
	//选择长矛物品
	public static boolean targetJavelinWeapon(ItemStack javelin) {
		return javelin.getItem() instanceof TridentItem || javelin.is(ItemTags.create(new ResourceLocation("forge:tools/tridents")));
	}
	//选择火把
	public static boolean targetTorch(ItemStack torch) {
		return torch.is(ItemTags.create(new ResourceLocation("jerotes:torchs")));
	}
	//是否震撼免疫
	public static boolean isAbackAwayImmune(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:aback_away_immune")));
	}
	//是否魔法抵抗
	public static boolean MagicResistance(DamageSource damagesource) {
		return  damagesource.is(JerotesDamageTypeTags.IS_MAGIC)
				|| (damagesource.getDirectEntity() != null && damagesource.getDirectEntity().getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:magic_shoots"))))
				|| (damagesource.getEntity() != null && damagesource.getEntity().getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:magic_shoots"))))
				|| damagesource.getDirectEntity() instanceof MagicAbout || damagesource.getEntity() instanceof MagicAbout
				|| damagesource.getDirectEntity() instanceof ThrownPotion || damagesource.getDirectEntity() instanceof AreaEffectCloud
				|| damagesource.getEntity() instanceof Warden && damagesource.is(DamageTypes.SONIC_BOOM);
	}
	//是否金属盔甲
	public static boolean isMetalArmors(ItemStack stack) {
		return stack.is(ItemTags.create(new ResourceLocation("jerotes:metal_armors")));
	}
	//是否莫厄武器
	public static boolean isMerorWeapon(ItemStack stack) {
		return stack.is(ItemTags.create(new ResourceLocation("jerotes:meror")));
	}
	//是否莫厄相关实体
	public static boolean isMerorAttackEntity(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:meror_damage")));
	}
	//是否莫厄破防攻击
	public static boolean isMerorAttack(DamageSource damageSource) {
		//event
		JerotesMerorDamageEvent event = new JerotesMerorDamageEvent(damageSource);
		MinecraftForge.EVENT_BUS.post(event);
		return event.isMerorDamage();
	}
	//是否无法缴械
	public static boolean isLoreUndying(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:lore_undying")));
	}
	public static boolean isLoreUndying(Entity entity) {
		if (entity instanceof LoreUndyingEntity loreUndyingEntity)
			return loreUndyingEntity.isLoreUndying();
		return isLoreUndying(entity.getType());
	}
	//是否雪球伤害
	public static boolean isSnowballCanAttack(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:snowball_can_attack")));
	}
	//是否目盲
	public static boolean targetBlindness(LivingEntity livingEntity) {
		return livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:blindness")));
	}
	public static boolean targetBlindnessTrue(LivingEntity livingEntity) {
		AtomicBoolean can = new AtomicBoolean(false);
		livingEntity.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTagOrEmpty(JerotesMobEffectTags.BLINDNESS).forEach(effect -> {
			if (livingEntity.hasEffect(effect.get())) {
				can.set(true);
			}
		});
		return can.get() || EntityAndItemFind.targetBlindness(livingEntity);
	}
	//是否发亮
	public static boolean targetGlowing(LivingEntity livingEntity) {
		return livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("jerotes:glowing")));
	}
	public static boolean targetGlowingTrue(LivingEntity livingEntity) {
		AtomicBoolean can = new AtomicBoolean(false);
		livingEntity.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT).getTagOrEmpty(JerotesMobEffectTags.GLOWING).forEach(effect -> {
			if (livingEntity.hasEffect(effect.get())) {
				can.set(true);
			}
		});
		return can.get() || targetGlowing(livingEntity);
	}
	//是否真实隐身
	public static boolean isTrueInvisible(Entity entity) {
		return entity instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity && jerotesChangeLivingEntity.isTrueInvisibleJerotes();
	}
	public static boolean isTrueInvisibleBuff(Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			boolean can = false;
			var registry = livingEntity.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT);
			var trueInvisibleOpt = registry.getTag(JerotesMobEffectTags.TRUE_INVISIBLE);
			var mustCrouchOpt = registry.getTag(JerotesMobEffectTags.TRUE_INVISIBLE_MUST_CROUCHING);var trueInvisibleTag = trueInvisibleOpt.orElse(null);
			if (trueInvisibleTag == null) {
				return false;
			}
			var mustCrouchTag = mustCrouchOpt.orElse(null);
			int level = 0;
			for (Holder<MobEffect> holder : trueInvisibleTag) {
				MobEffectInstance effectInstance = livingEntity.getEffect(holder.get());
				if (effectInstance == null) {
					continue;
				}
				if (mustCrouchTag != null && mustCrouchTag.contains(holder) && !livingEntity.isCrouching()) {
					continue;
				}
				can = true;
			}
			return can;
		}
		return false;
	}
	public static int getTrueInvisibleLevel(Entity entity) {
		if (!(entity instanceof LivingEntity livingEntity)) {
			return 0;
		}
		var registry = livingEntity.level().registryAccess().registryOrThrow(Registries.MOB_EFFECT);
		var trueInvisibleOpt = registry.getTag(JerotesMobEffectTags.TRUE_INVISIBLE);
		var mustCrouchOpt = registry.getTag(JerotesMobEffectTags.TRUE_INVISIBLE_MUST_CROUCHING);
		var trueInvisibleTag = trueInvisibleOpt.orElse(null);
		if (trueInvisibleTag == null) {
			return 0;
		}
		var mustCrouchTag = mustCrouchOpt.orElse(null);
		int level = 0;
		for (Holder<MobEffect> holder : trueInvisibleTag) {
			MobEffectInstance effectInstance = livingEntity.getEffect(holder.get());
			if (effectInstance == null) {
				continue;
			}
			if (mustCrouchTag != null && mustCrouchTag.contains(holder) && !livingEntity.isCrouching()) {
				continue;
			}

			int amp = effectInstance.getAmplifier() + 1;
			if (amp > level) {
				level = amp;
			}
		}
		return level;
	}


	//是否更好的附魔
	public static boolean isEnchantLevelBetter(ItemStack self, ItemStack other) {
		return getTotalEnchantmentLevel(self) > getTotalEnchantmentLevel(other);
	}


	//附魔等级
	public static float getTotalEnchantmentLevel(ItemStack stack) {
		if (stack.isEmpty()) return 0;
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		float totalValue = 0;
		//计算附魔总等级
		for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			Enchantment enchantment = entry.getKey();
			//成本计算
			int add = Math.min(enchantment.getMinCost(entry.getValue()), enchantment.getMaxCost(entry.getValue()));
			//默认权重
			float weight = 1;
			//宝藏
			if (enchantment.isTreasureOnly()) {
				weight *= 1.25f;
			}
			//诅咒
			if (enchantment.isCurse()) {
				weight *= -1;
			}
			totalValue += add * weight;
		}
		return totalValue;
	}
	//抵挡魔法
	public static boolean canSelfResistMagic(DamageSource damageSource, float amount, LivingEntity entity, int selfLevel) {
		if (!(entity instanceof SpellUseEntity spellUse))
			return false;
		if (!MagicResistance(damageSource))
			return false;
		if (damageSource.getEntity() instanceof SpellUseEntity ||
				damageSource.getDirectEntity() instanceof SpellUseEntity ||
				damageSource.getEntity() instanceof MagicAbout ||
				damageSource.getDirectEntity() instanceof MagicAbout) {
			int otherLevl = getOtherLevl(damageSource);
			return selfLevel <= otherLevl;
		}
		else {
			int spelllevel = Math.max(0, spellUse.getSpellLevel() - 1);
			float maxResistance = spelllevel * spelllevel * 10;
			float afterDamage = amount * Math.max(0.05f, ((100f - spelllevel * spelllevel)/100f));
			if (Float.isNaN(afterDamage) || Float.isInfinite(afterDamage)) {
				return false;
			}
			return afterDamage <= maxResistance;
		}
	}
	public static boolean canSelfResistMagic(DamageSource damageSource, float amount, LivingEntity entity) {
		if (!(entity instanceof SpellUseEntity spellUse))
			return false;
		return canSelfResistMagic(damageSource, amount, entity, spellUse.getSpellLevel());
	}
	public static int getOtherLevl(DamageSource damageSource) {
		int ownerSpellUseLevel = damageSource.getEntity() instanceof SpellUseEntity spellUse1 ? spellUse1.getSpellLevel() : 1;
		int magicSpellUseLevel = damageSource.getDirectEntity() instanceof SpellUseEntity spellUse1 ? spellUse1.getSpellLevel() : 1;
		int ownerMagicAboutLevel = damageSource.getEntity() instanceof MagicAbout magicAbout ? magicAbout.getSpellLevel() : 1;
		int magicMagicAboutLevel = damageSource.getDirectEntity() instanceof MagicAbout magicAbout ? magicAbout.getSpellLevel() : 1;
		int otherLevl = Math.max(Math.max(ownerSpellUseLevel, magicSpellUseLevel),Math.max(ownerMagicAboutLevel, magicMagicAboutLevel));
		return otherLevl;
	}

	//dnd属性值获得
	//力量
	public static int getStrength(LivingEntity livingEntity) {
		return 10;
	}
	//敏捷
	public static int getDexterity(LivingEntity livingEntity) {
		return 10;
	}
	//体质
	public static int getConstitution(LivingEntity livingEntity) {
		return 10;
	}
	//智力
	public static int getIntelligence(LivingEntity livingEntity) {
		return 10;
	}
	//感知
	public static int getWisdom(LivingEntity livingEntity) {
		return 10;
	}
	//魅力
	public static int getCharisma(LivingEntity livingEntity) {
		return 10;
	}
	public static int getNormalSaving(LivingEntity livingEntity) {
		if (livingEntity == null) return 10;
		int n = 10;
		n += livingEntity instanceof Player player ? 2 + Math.min(player.experienceLevel/40, 7) : 0;
		n += livingEntity instanceof SpellUseEntity spellUse && spellUse.getSpellLevel() >= 5 ? (spellUse.getSpellLevel() - 5)/2 : 0;
		n += livingEntity instanceof UseBowEntity useBowEntity && useBowEntity.getBowLevel() >= 20 ? 1 : 0;
		n += livingEntity instanceof UseShieldEntity useShieldEntity && useShieldEntity.getShieldLevel() >= 20 ? 1 : 0;
		n += livingEntity instanceof BossEntity ? 1 : 0;
		n += livingEntity instanceof Player player && player.isCreative() ? 30 : 0;
		n += isLegendary(livingEntity) ? 3 : 0;
		n += isExalted(livingEntity) ? 3 : 0;
		n += livingEntity instanceof Player ? 0 : Math.min(livingEntity.getExperienceReward()/200, 3);
		n += Math.min((int) Main.luck(livingEntity), 5);
		n += livingEntity.hasEffect(JerotesMobEffects.PARALYZED.get()) ? -3 : 0;
		n += livingEntity.hasEffect(JerotesMobEffects.ANESTHETIZED_HOLD.get()) ? -2 : 0;
		n += livingEntity.hasEffect(JerotesMobEffects.ENSLAVEMENT.get()) ? -2 : 0;
		n += livingEntity.hasEffect(JerotesMobEffects.PRONE.get()) ? -2 : 0;
		return Math.min(n, 30);
	}
	public static int getModifier(int n) {
		return (n - 10) / 2;
	}
	public static boolean savingSpellThrow(LivingEntity livingEntity, int add) {
		return savingSpellThrow(livingEntity, add, false);
	}
	public static boolean savingSpellThrow(LivingEntity livingEntity, int add, boolean mustThrow) {
		if (livingEntity == null)
			return true;
		if (!MainConfig.HaveSavingThrow && !mustThrow)
			return true;
		if (MainConfig.HaveSavingThrowOnlyPlayerAndLegendary && !(livingEntity instanceof Player) && !isLegendary(livingEntity) && !mustThrow)
			return false;
		int n = (Main.randomReach(livingEntity.getRandom(),1, 20));
		if (n == 1)
			return false;
		if (n == 20)
			return true;
		return n >= EntityAndItemFind.getModifier(EntityAndItemFind.getNormalSaving(livingEntity)) + 8f + add;
	}
	public static boolean savingSpellThrow(LivingEntity livingEntity) {
		return savingSpellThrow(livingEntity, 0);
	}


	public static boolean savingNormalThrow(LivingEntity livingEntity, int add, boolean mustThrow) {
		if (livingEntity == null)
			return true;
		if (!MainConfig.HaveSavingThrow && !mustThrow)
			return true;
		if (MainConfig.HaveSavingThrowOnlyPlayerAndLegendary && !(livingEntity instanceof Player) && !isLegendary(livingEntity) && !mustThrow)
			return false;
		int n = (Main.randomReach(livingEntity.getRandom(),1, 20));
		if (n == 1)
			return false;
		if (n == 20)
			return true;
		return n >= EntityAndItemFind.getModifier(EntityAndItemFind.getNormalSaving(livingEntity)) + add;
	}

	public static float getItemAttackSpeedFromDPS(float DPS, float speedMultiplier, WeaponCategory weaponCategory) {
		float speed;
		switch (weaponCategory) {
			case DAGGER -> speed = 2.2f;
			case SWORD -> speed = 1.6f;
			case AXE -> speed = 1.2f;
			case HAMMER -> speed = 1.0f;
			case TWO_HANDED_SWORD -> speed = 0.8f;
			case TWO_HANDED_AXE -> speed = 0.7f;
			case TWO_HANDED_HAMMER -> speed = 0.6f;
			case PIKE -> speed = 0.7f;
			default -> speed = 4.0f;
		}
		speed *= speedMultiplier;
		speed = Math.round(speed / 0.05f) * 0.05f;
		return speed;
	}
	public static float getItemAttackDamageFromDPS(float DPS, float speedMultiplier, WeaponCategory weaponCategory) {
		float speed = getItemAttackSpeedFromDPS(DPS, speedMultiplier, weaponCategory);
		float damage = DPS/speed;
		damage = Math.round(damage / 0.5f) * 0.5f;
		return damage;
	}
	public static enum WeaponCategory {
		SWORD,
		DAGGER,
		HAMMER,
		AXE,
		TWO_HANDED_SWORD,
		TWO_HANDED_AXE,
		PIKE,
		TWO_HANDED_HAMMER;
	}


	public static boolean hasColor(LivingEntity livingEntity) {
		CompoundTag compoundTag = new CompoundTag();
		livingEntity.addAdditionalSaveData(compoundTag);
		if (compoundTag.get("Color") != null && compoundTag.contains("Color", Tag.TAG_BYTE)) {
			return true;
		}
		return false;
	}
	public static void setColor(LivingEntity livingEntity, DyeColor dyeColor) {
		CompoundTag compoundTag = livingEntity.getPersistentData();
		livingEntity.addAdditionalSaveData(compoundTag);
		if (compoundTag.get("Color") != null && compoundTag.contains("Color", Tag.TAG_BYTE)) {
			compoundTag.putByte("Color", (byte)dyeColor.getId());
		}
		livingEntity.readAdditionalSaveData(compoundTag);
	}
}