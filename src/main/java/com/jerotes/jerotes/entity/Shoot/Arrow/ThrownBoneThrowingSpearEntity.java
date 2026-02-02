package com.jerotes.jerotes.entity.Shoot.Arrow;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownBoneThrowingSpearEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.BONE_THROWING_SPEAR_OF_JAVELIN.get());
	private static final EntityType<ThrownBoneThrowingSpearEntity> type = JerotesEntityType.THROWN_BONE_THROWING_SPEAR.get();
	public ThrownBoneThrowingSpearEntity(EntityType<? extends ThrownBoneThrowingSpearEntity> entityType, Level level) {
		super(entityType, level, item, 5.0f);
	}
	public ThrownBoneThrowingSpearEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 5.0f);
	}
}
