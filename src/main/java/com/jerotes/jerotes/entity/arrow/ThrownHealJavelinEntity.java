package com.jerotes.jerotes.entity.arrow;

import com.jerotes.jerotes.entity.arrow.BaseJavelinEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownHealJavelinEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.HEAL_JAVELIN.get());
	private static final EntityType<ThrownHealJavelinEntity> type = JerotesEntityType.THROWN_HEAL_JAVELIN.get();
	public ThrownHealJavelinEntity(EntityType<? extends ThrownHealJavelinEntity> entityType, Level level) {
		super(entityType, level, item, 0.0f);
	}
	public ThrownHealJavelinEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 0.0f);
	}

	public DamageSource getDamageSource(Entity entity) {
		if (entity instanceof Mob mob && mob.getMobType() == MobType.UNDEAD) {
			return this.damageSources().trident(this, this.getOwner() == null ? this : this.getOwner());
		}
		else {
			return this.damageSources().trident(this, this);
		}
	}
	@Override
	protected void doPostHurtEffects(LivingEntity livingEntity) {
		super.doPostHurtEffects(livingEntity);
		if (!livingEntity.level().isClientSide) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 240, 2));
			livingEntity.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 3));
		}
	}
}
