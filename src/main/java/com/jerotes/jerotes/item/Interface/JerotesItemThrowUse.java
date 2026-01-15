package com.jerotes.jerotes.item.Interface;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;

public interface JerotesItemThrowUse {
    default boolean isJerotesThrow() {
        return true;
    }
    default boolean isJerotesThrow(LivingEntity livingEntity) {
        return isJerotesThrow();
    }
    default boolean isJerotesThrowShrinkSelf() {
        return true;
    }
    default boolean isJerotesThrowShrinkOther() {
        return false;
    }
    Projectile useJerotesThrowShoot(LivingEntity livingEntity, InteractionHand interactionHand);
    float useJerotesThrowShootSpeed(LivingEntity livingEntity, InteractionHand interactionHand);
}

