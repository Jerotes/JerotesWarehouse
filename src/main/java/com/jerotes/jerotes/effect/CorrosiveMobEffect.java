package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEnchantments;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;

public class CorrosiveMobEffect extends BaseMobEffectTick {
	public CorrosiveMobEffect() {
		super(MobEffectCategory.HARMFUL, 44988);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.corrosive";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		int corrosiveStop = MainConfig.CorrosiveStopDurabilityValue;
		int effectLevel = n + 1;
		float damageLevel = 1;
		int breakLevel = 1;
		ItemStack head = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
		ItemStack chest = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
		ItemStack legs = livingEntity.getItemBySlot(EquipmentSlot.LEGS);
		ItemStack feet = livingEntity.getItemBySlot(EquipmentSlot.FEET);
		ItemStack mainHand = livingEntity.getItemBySlot(EquipmentSlot.MAINHAND);
		ItemStack offHand = livingEntity.getItemBySlot(EquipmentSlot.OFFHAND);

		float breakHeadLevel = 0.25f;
		float breakChestLevel = 0.25f;
		float breakLegsLevel = 0.25f;
		float breakFeetLevel = 0.25f;

		boolean gameRuleArmor = !(livingEntity instanceof Player) && JerotesGameRules.JEROTES_ARMOR_CAN_BREAK != null && !livingEntity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_ARMOR_CAN_BREAK);
		boolean gameRuleMelee = !(livingEntity instanceof Player) && JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && !livingEntity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK);
		boolean gameRuleRange = !(livingEntity instanceof Player) && JerotesGameRules.JEROTES_RANGE_CAN_BREAK != null && !livingEntity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_RANGE_CAN_BREAK);
		boolean gameRuleShield = !(livingEntity instanceof Player) && JerotesGameRules.JEROTES_SHIELD_CAN_BREAK != null && !livingEntity.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_SHIELD_CAN_BREAK);
		boolean gameRuleMainHand =
						(mainHand.getItem() instanceof ArmorItem && gameRuleArmor) ||
						(mainHand.getItem() instanceof ShieldItem && gameRuleShield) ||
						((mainHand.getItem() instanceof BowItem || mainHand.getItem() instanceof CrossbowItem) && gameRuleRange) ||
						(!(mainHand.getItem() instanceof ArmorItem) && !(mainHand.getItem() instanceof ShieldItem) && !(mainHand.getItem() instanceof BowItem) && !(mainHand.getItem() instanceof CrossbowItem) && gameRuleMelee);
		boolean gameRuleOffHand =
				(offHand.getItem() instanceof ArmorItem && gameRuleArmor) ||
						(offHand.getItem() instanceof ShieldItem && gameRuleShield) ||
						((offHand.getItem() instanceof BowItem || offHand.getItem() instanceof CrossbowItem) && gameRuleRange) ||
						(!(offHand.getItem() instanceof ArmorItem) && !(offHand.getItem() instanceof ShieldItem) && !(offHand.getItem() instanceof BowItem) && !(offHand.getItem() instanceof CrossbowItem) && gameRuleMelee);

		if (head.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) > 0) {
			breakHeadLevel = 0;
		}
		if (chest.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) > 0) {
			breakChestLevel = 0;
		}
		if (legs.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) > 0) {
			breakLegsLevel = 0;
		}
		if (feet.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) > 0) {
			breakFeetLevel = 0;
		}
		float damageEnchantLevel = breakHeadLevel + breakChestLevel + breakLegsLevel + breakFeetLevel;

		if (EntityFactionFind.isMachine(livingEntity) || EntityFactionFind.isConstruct(livingEntity)) {
			//默认伤害倍率额外*2
			damageLevel *= 2f;
		}
		if (EntityFactionFind.isOoze(livingEntity)) {
			//默认伤害倍率额外/2
			damageLevel /= 2f;
		}

		//伤害
		if (livingEntity.getHealth() > 0f) {
			DamageSource damageSource = new DamageSource(livingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.CORROSIVE));
			livingEntity.hurt(damageSource, effectLevel * damageLevel * damageEnchantLevel);
		}
		//耐久
		int min = Math.min(corrosiveStop - 1, effectLevel * breakLevel);
		if (livingEntity.getRandom().nextInt( 1, 5) <= effectLevel && !gameRuleArmor) {
			if (head.isDamageableItem() && head.getRarity() != Rarity.EPIC && head.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0 && head.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) <= 0 && head.getDamageValue() < head.getMaxDamage() - corrosiveStop) {
				head.hurtAndBreak(min, livingEntity, livingEntitys -> livingEntity.broadcastBreakEvent(EquipmentSlot.HEAD));
			}
		}
		if (livingEntity.getRandom().nextInt( 1, 5) <= effectLevel && !gameRuleArmor) {
			if (chest.isDamageableItem() && chest.getRarity() != Rarity.EPIC && chest.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0 && chest.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) <= 0 && chest.getDamageValue() < chest.getMaxDamage() - corrosiveStop) {
				chest.hurtAndBreak(min, livingEntity, livingEntitys -> livingEntity.broadcastBreakEvent(EquipmentSlot.CHEST));
			}
		}
		if (livingEntity.getRandom().nextInt( 1, 5) <= effectLevel && !gameRuleArmor) {
			if (legs.isDamageableItem() && legs.getRarity() != Rarity.EPIC && legs.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0 && legs.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) <= 0 && legs.getDamageValue() < legs.getMaxDamage() - corrosiveStop) {
				legs.hurtAndBreak(min, livingEntity, livingEntitys -> livingEntity.broadcastBreakEvent(EquipmentSlot.LEGS));
			}
		}
		if (livingEntity.getRandom().nextInt( 1, 5) <= effectLevel && !gameRuleArmor) {
			if (feet.isDamageableItem() && feet.getRarity() != Rarity.EPIC && feet.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0 && feet.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) <= 0 && feet.getDamageValue() < feet.getMaxDamage() - corrosiveStop) {
				feet.hurtAndBreak(min, livingEntity, livingEntitys -> livingEntity.broadcastBreakEvent(EquipmentSlot.FEET));
			}
		}
		if (livingEntity.getRandom().nextInt( 1, 5) <= effectLevel && !gameRuleMainHand) {
			if (mainHand.isDamageableItem() && mainHand.getRarity() != Rarity.EPIC && mainHand.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0 && mainHand.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) <= 0 && mainHand.getDamageValue() < mainHand.getMaxDamage() - corrosiveStop) {
				mainHand.hurtAndBreak(min, livingEntity, livingEntitys -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}
		}
		if (livingEntity.getRandom().nextInt( 1, 5) <= effectLevel && !gameRuleOffHand) {
			if (offHand.isDamageableItem() && offHand.getRarity() != Rarity.EPIC && offHand.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0 && offHand.getEnchantmentLevel(JerotesEnchantments.CORROSION_RESISTANCE.get()) <= 0 && offHand.getDamageValue() < offHand.getMaxDamage() - corrosiveStop) {
				offHand.hurtAndBreak(min, livingEntity, livingEntitys -> livingEntity.broadcastBreakEvent(EquipmentSlot.OFFHAND));
			}
		}
	}
}
