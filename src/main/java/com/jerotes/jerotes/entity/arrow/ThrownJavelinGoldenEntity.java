package com.jerotes.jerotes.entity.arrow;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownJavelinGoldenEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.GOLDEN_JAVELIN.get());
	private static final EntityType<ThrownJavelinGoldenEntity> type = JerotesEntityType.THROWN_GOLDEN_JAVELIN.get();
	public ThrownJavelinGoldenEntity(EntityType<? extends ThrownJavelinGoldenEntity> entityType, Level level) {
		super(entityType, level, item, 4.0f);
	}
	public ThrownJavelinGoldenEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 4.0f);
	}
}
