package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEnchantments;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;

public class DeadlyPoisonMobEffect extends BaseMobEffectTick {
	public DeadlyPoisonMobEffect() {
		super(MobEffectCategory.HARMFUL, 0x3a6f1a);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.deadly_poison";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		int effectLevel = n + 1;
		float base = 1.0f;
		//默认伤害倍率额外/4
		if (EntityFactionFind.isConstruct(livingEntity) || EntityFactionFind.isMachine(livingEntity)) {
			return;
		}
		//默认伤害倍率额外/2
		if (livingEntity.getMobType() == MobType.UNDEAD) {
			base /= 2;
		}
		//默认伤害倍率额外/2
		if (EntityFactionFind.isOoze(livingEntity)) {
			base /= 2;
		}
		DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.POISON);
		if (livingEntity.getHealth() > 0f) {
			livingEntity.hurt(damageSource, effectLevel * base);
		}
	}
}
