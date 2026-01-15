package com.jerotes.jerotes.entity.Arrow;

import com.jerotes.jerotes.entity.Interface.AnesthetizedAttackEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ThrownAnestheticJavelinEntity extends BaseJavelinEntity implements AnesthetizedAttackEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.ANESTHETIC_JAVELIN.get());
	private static final EntityType<ThrownAnestheticJavelinEntity> type = JerotesEntityType.THROWN_ANESTHETIC_JAVELIN.get();
	public ThrownAnestheticJavelinEntity(EntityType<? extends ThrownAnestheticJavelinEntity> entityType, Level level) {
		super(entityType, level, item, 3.0f);
	}
	public ThrownAnestheticJavelinEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 3.0f);
	}

	@Override
	public int getAnesthetized() {
		return 360;
	}

	public boolean isVillager;
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putBoolean("IsVillager", this.isVillager);
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.isVillager = compoundTag.getBoolean("IsVillager");
	}
	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		Entity entity2 = entityHitResult.getEntity();
		if (this.isVillager && this.getOwner() != null && this.getOwner() instanceof Mob mob && !(entity2 == mob.getTarget() || entity2 == mob.getLastHurtByMob() || entity2 == mob.getLastHurtMob())) {
			return;
		}
		super.onHitEntity(entityHitResult);
	}
	@Override
	protected void doPostHurtEffects(LivingEntity livingEntity) {
		super.doPostHurtEffects(livingEntity);
		if (!livingEntity.level().isClientSide) {
			livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 440, 4));
			livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 440, 2));
		}
	}
}
