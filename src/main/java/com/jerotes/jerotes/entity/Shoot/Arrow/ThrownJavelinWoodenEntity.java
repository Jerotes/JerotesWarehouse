package com.jerotes.jerotes.entity.Shoot.Arrow;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownJavelinWoodenEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.WOODEN_JAVELIN.get());
	private static final EntityType<ThrownJavelinWoodenEntity> type = JerotesEntityType.THROWN_WOODEN_JAVELIN.get();
	public ThrownJavelinWoodenEntity(EntityType<? extends ThrownJavelinWoodenEntity> entityType, Level level) {
		super(entityType, level, item, 4.0f);
	}
	public ThrownJavelinWoodenEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 4.0f);
	}
}
