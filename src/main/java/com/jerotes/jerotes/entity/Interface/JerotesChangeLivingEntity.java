package com.jerotes.jerotes.entity.Interface;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public interface JerotesChangeLivingEntity {
    boolean canAddPassengerJerotes(Entity entity);
    boolean wasRecentlyStabbedJerotes(Entity entity, int n);
    void rememberStabbedEntityJerotes(Entity entity);
    float getTicksSinceLastKineticHitFeedbackJerotes(float f);
    long getlastKineticHitFeedbackTimeJerotes();
    boolean isTrueInvisibleJerotes();
    int getCurrentSwingDurationJerotes();
    void setLastDamageSourceJerotes(DamageSource damageSource);

    float getMaxHealthReduceCountJerotes();
    void setMaxHealthReduceCountJerotes(float count);
    float getMaxHealthReduceTickJerotes();
    void setMaxHealthReduceTickJerotes(float tick);
    void applyMaxHealthReductionJerotes(float tick);

    int getAnesthetizedTickJerotes();
    void setAnesthetizedTickJerotes(int tick);

    int getProneTickJerotes();
    void setProneTickJerotes(int tick);

    float getThisTickRenderTimeJerotes();
    void setThisTickRenderTimeJerotes(float tick);
    float getLastTickRenderTimeJerotes();
    void setLastTickRenderTimeJerotes(float tick);
    float getProneTickProgressJerotes();
    void setProneTickProgressJerotes(float tick);
}

