package com.jerotes.jerotes.entity.Arrow;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownJavelinIronEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.IRON_JAVELIN.get());
	private static final EntityType<ThrownJavelinIronEntity> type = JerotesEntityType.THROWN_IRON_JAVELIN.get();
	public ThrownJavelinIronEntity(EntityType<? extends ThrownJavelinIronEntity> entityType, Level level) {
		super(entityType, level, item, 6.0f);
	}
	public ThrownJavelinIronEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 6.0f);
	}
}
