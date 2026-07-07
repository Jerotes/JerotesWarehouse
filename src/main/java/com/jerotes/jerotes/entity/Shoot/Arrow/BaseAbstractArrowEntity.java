package com.jerotes.jerotes.entity.Shoot.Arrow;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseAbstractArrowEntity extends AbstractArrow {
    @Nullable
    public List<Entity> allayEntities;

    protected BaseAbstractArrowEntity(EntityType<? extends AbstractArrow> entityType, Level level, ItemStack itemStack) {
        super(entityType, level);
    }

    protected BaseAbstractArrowEntity(EntityType<? extends AbstractArrow> entityType, double d, double d2, double d3, Level level, ItemStack itemStack) {
        super(entityType, d, d2, d3, level);
    }

    protected BaseAbstractArrowEntity(EntityType<? extends AbstractArrow> entityType, LivingEntity livingEntity, Level level, ItemStack itemStack) {
        super(entityType, livingEntity, level);
    }

    public void resetAllayEntities() {
        if (this.allayEntities != null) {
            this.allayEntities.clear();
        }
    }
}


