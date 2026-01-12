package com.jerotes.jerotes.item;

import net.minecraft.world.entity.LivingEntity;

public interface CanGetOffHand {
    default boolean getOffHandItem(LivingEntity livingEntity) {
        return false;
    }
}

