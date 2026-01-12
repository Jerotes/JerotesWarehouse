package com.jerotes.jerotes.entity.arrow;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownJavelinNetheriteEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.NETHERITE_JAVELIN.get());
	private static final EntityType<ThrownJavelinNetheriteEntity> type = JerotesEntityType.THROWN_NETHERITE_JAVELIN.get();
	public ThrownJavelinNetheriteEntity(EntityType<? extends ThrownJavelinNetheriteEntity> entityType, Level level) {
		super(entityType, level, item, 8.0f);
	}
	public ThrownJavelinNetheriteEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 8.0f);
	}
}
