package com.jerotes.jerotes.entity.Arrow;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownJavelinCopperEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.COPPER_JAVELIN.get());
	private static final EntityType<ThrownJavelinCopperEntity> type = JerotesEntityType.THROWN_COPPER_JAVELIN.get();
	public ThrownJavelinCopperEntity(EntityType<? extends ThrownJavelinCopperEntity> entityType, Level level) {
		super(entityType, level, item, 5.0f);
	}
	public ThrownJavelinCopperEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 5.0f);
	}
}
