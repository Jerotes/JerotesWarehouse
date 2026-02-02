package com.jerotes.jerotes.entity.Shoot.Arrow;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownJavelinDiamondEntity extends BaseJavelinEntity {
	private static final ItemStack item = new ItemStack(JerotesItems.DIAMOND_JAVELIN.get());
	private static final EntityType<ThrownJavelinDiamondEntity> type = JerotesEntityType.THROWN_DIAMOND_JAVELIN.get();
	public ThrownJavelinDiamondEntity(EntityType<? extends ThrownJavelinDiamondEntity> entityType, Level level) {
		super(entityType, level, item, 7.0f);
	}
	public ThrownJavelinDiamondEntity(Level level, LivingEntity livingEntity, ItemStack itemStack) {
		super(level, livingEntity, itemStack, type, 7.0f);
	}
}
