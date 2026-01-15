package com.jerotes.jerotes.entity.Interface;

import net.minecraft.world.entity.LivingEntity;

public interface TameMobEntity {
    boolean isOrderedToSit();
    boolean isInSittingPose();
    boolean isTame();
    void setInSittingPose(boolean bl);
    void setOrderedToSit(boolean bl);
    boolean wantsToAttack(LivingEntity livingEntity, LivingEntity livingEntity2);
}

