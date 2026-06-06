package com.jerotes.jerotes.util;

import com.jerotes.jerotes.entity.Interface.JerotesChangeLivingEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.forge.JerotesMerorDamageEvent;
import com.jerotes.jerotes.forge.JerotesMeleeDamageFromMainHandIsOffHandEvent;
import com.jerotes.jerotes.init.JerotesDamageTypeTags;
import com.jerotes.jerotes.init.JerotesMobEffectTags;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.network.JerotesPlayerData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
			if (livingEntity.hasEffect(JerotesMobEffects.CLOAKING.get()) && (livingEntity.isCrouching() || livingEntity.isShiftKeyDown())) {
				return true;
			}
			if (livingEntity.hasEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get())) {
				return true;
			}
			if (livingEntity.hasEffect(JerotesMobEffects.MIRROR_IMAGE.get())) {
				return true;
			}
		}
		return false;
	}
	public static int getTrueInvisibleLevel(Entity entity) {
		int level = 0;
		if (entity instanceof LivingEntity livingEntity) {
			if (livingEntity.hasEffect(JerotesMobEffects.CLOAKING.get()) && (livingEntity.isCrouching() || livingEntity.isShiftKeyDown())) {
				if (level < Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.CLOAKING.get())).getAmplifier() + 1)
					level = Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.CLOAKING.get())).getAmplifier() + 1;
			}
			if (livingEntity.hasEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get())) {
				if (level < Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get())).getAmplifier() + 1)
					level = Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.INVISIBLE_PASSAGE.get())).getAmplifier() + 1;
			}
			if (livingEntity.hasEffect(JerotesMobEffects.MIRROR_IMAGE.get())) {
				if (level < Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.MIRROR_IMAGE.get())).getAmplifier() + 1)
					level = Objects.requireNonNull(livingEntity.getEffect(JerotesMobEffects.MIRROR_IMAGE.get())).getAmplifier() + 1;
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
}