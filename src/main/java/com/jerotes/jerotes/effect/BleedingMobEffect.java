package com.jerotes.jerotes.effect;

import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;

public class BleedingMobEffect extends BaseMobEffectSlowTick {
	public BleedingMobEffect() {
		super(MobEffectCategory.HARMFUL, 11468800);
	}

	@Override
	public String getDescriptionId() {
		return "effect.jerotes.bleeding";
	}

	@Override
	public void applyEffectTick(LivingEntity livingEntity, int n) {
		super.applyEffectTick(livingEntity, n);
		if (EntityFactionFind.isMachine(livingEntity)) {
			return;
		}
		int effectLevel = n + 1;
		if (livingEntity.getHealth() > 0f) {
			float maxHealth = livingEntity.getMaxHealth();
			float base = 1f;
			if (livingEntity.getMobType() == MobType.UNDEAD) {
				base /= 2;
			}
			if (EntityFactionFind.isConstruct(livingEntity) || EntityFactionFind.isMachine(livingEntity)) {
				base /= 2;
			}
			if (EntityFactionFind.isOoze(livingEntity)) {
				base /= 2;
			}
			if (EntityFactionFind.isPlant(livingEntity)) {
				base /= 2;
			}
			float damage = maxHealth * 0.005f * effectLevel * base;
			float maxDamage = 5 * effectLevel;
			if (damage > maxDamage) {
				damage = maxDamage;
			}
			DamageSource damageSources = new DamageSource(livingEntity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(JerotesDamageTypes.BLEEDING));
			livingEntity.hurt(damageSources, damage);
		}
		int num = effectLevel;
		if (num > 10) {
			num = 10;
		}
		if (livingEntity.getMobType() != MobType.UNDEAD && !EntityFactionFind.isOoze(livingEntity) && !EntityFactionFind.isPlant(livingEntity) && !EntityFactionFind.isConstruct(livingEntity)) {
			if (livingEntity.level() instanceof ServerLevel serverLevel) {
				for (int j = 0; j < num; ++j) {
					serverLevel.sendParticles(JerotesParticleTypes.BLOOD.get(), livingEntity.getRandomX(0.5), livingEntity.getRandomY(), livingEntity.getRandomZ(0.5), 0, 0, -0.2f, 0, 0.5);
				}
			}
		}
	}
}
