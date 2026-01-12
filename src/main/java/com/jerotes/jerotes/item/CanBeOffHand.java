package com.jerotes.jerotes.item;

import net.minecraft.world.entity.LivingEntity;

public interface CanBeOffHand {
    default boolean isOffHandItem(LivingEntity livingEntity) {
        return false;
    }
}

