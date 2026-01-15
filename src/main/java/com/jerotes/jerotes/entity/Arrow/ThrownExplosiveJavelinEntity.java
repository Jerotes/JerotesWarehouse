package com.jerotes.jerotes.entity.Arrow;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownExplosiveJavelinEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.EXPLOSIVE_JAVELIN.get());
	private static final EntityType<ThrownExplosiveJavelinEntity> type = JerotesEntityType.THROWN_EXPLOSIVE_JAVELIN.get();
	public ThrownExplosiveJavelinEntity(EntityType<? extends ThrownExplosiveJavelinEntity> entityType, Level level) {
		super(entityType, level, item, 3.0f);
	}
	public ThrownExplosiveJavelinEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 3.0f);
	}

	@Override
	public boolean canLoyalty() {
		return false;
	}

	public boolean isExploded;
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putBoolean("IsExploded", this.isExploded);
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.isExploded = compoundTag.getBoolean("IsExploded");
	}
	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);
		if (!this.isExploded && !this.isRemoved()) {
			if (!this.level().isClientSide) {
				this.level().explode(this.getOwner() != null ? this.getOwner() : this, this.getX(), this.getY(), this.getZ(), 1, Level.ExplosionInteraction.MOB);
				this.level().explode(this.getOwner() != null ? this.getOwner() : this, this.getX(), this.getY(), this.getZ(), 2, Level.ExplosionInteraction.NONE);
				this.isExploded = true;
			}
		}
	}
}
