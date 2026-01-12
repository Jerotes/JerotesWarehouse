package com.jerotes.jerotes.entity.arrow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class BaseAbstractArrowEntity extends AbstractArrow {

    protected BaseAbstractArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level, ItemStack itemStack) {
        super(entityType, level);
    }

    protected BaseAbstractArrowEntity(EntityType<? extends AbstractArrow> entityType, double d, double d2, double d3, Level level, ItemStack itemStack) {
        super(entityType, d, d2, d3, level);
    }

    protected BaseAbstractArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity livingEntity, Level level, ItemStack itemStack) {
        super(entityType, livingEntity, level);
    }
}


