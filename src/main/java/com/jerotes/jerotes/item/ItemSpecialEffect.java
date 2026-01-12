package com.jerotes.jerotes.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface ItemSpecialEffect {
    default void attackUse(Entity self, Entity attackTo, boolean bl) {
        if (bl) {
            attackUse(self, attackTo);
        }
    }
    default void attackUse(Entity self, Entity attackTo) {
    }

    default void blockUse(Entity self, Entity attackBy, DamageSource damageSource) {
    }
}

