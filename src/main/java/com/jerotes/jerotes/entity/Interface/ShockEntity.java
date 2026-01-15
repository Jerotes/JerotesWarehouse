package com.jerotes.jerotes.entity.Interface;

import net.minecraft.world.entity.LivingEntity;

public interface ShockEntity {
    default boolean useShock() {
        return false;
    }
    default boolean useShockTo(LivingEntity livingEntity) {
        return true;
    }
    default int ShockLevel() {
        return 5;
    }
}

