package com.jerotes.jerotes.entity;

import net.minecraft.world.entity.Entity;

public interface JerotesChangeLivingEntity {
    boolean canAddPassengerJerotes(Entity entity);
    boolean wasRecentlyStabbedJerotes(Entity entity, int n);
    void rememberStabbedEntityJerotes(Entity entity);
    float getTicksSinceLastKineticHitFeedbackJerotes(float f);
}

