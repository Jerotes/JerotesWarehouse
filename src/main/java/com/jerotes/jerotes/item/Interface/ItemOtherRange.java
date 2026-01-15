package com.jerotes.jerotes.item.Interface;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ItemOtherRange {
    default boolean isRange(LivingEntity living, ItemStack itemStack) {
        return true;
    }
}


