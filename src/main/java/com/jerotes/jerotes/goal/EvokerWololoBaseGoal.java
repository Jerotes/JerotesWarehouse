package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.EvokerAbout;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Evoker;

import javax.annotation.Nullable;

public abstract class EvokerWololoBaseGoal extends Goal {
    protected int attackWarmupDelay;
    protected int nextAttackTickCount;
    public final Evoker livingEntity;

    protected EvokerWololoBaseGoal(Evoker livingEntity) {
        this.livingEntity = livingEntity;
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.livingEntity.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        if (this.livingEntity.isCastingSpell()) {
            return false;
        }
        return this.livingEntity.tickCount >= this.nextAttackTickCount;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.livingEntity.getTarget();
        return livingEntity != null && livingEntity.isAlive() && this.attackWarmupDelay > 0;
    }

    @Override
    public void start() {
        this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
        if (livingEntity instanceof EvokerAbout evokerAbout) {
            evokerAbout.jerotes1_20_4$setEvokerAboutSpellCastingTickCount(this.getCastingTime());
        }
        this.nextAttackTickCount = this.livingEntity.tickCount + this.getCastingInterval();
        SoundEvent soundEvent = this.getSpellPrepareSound();
        if (soundEvent != null) {
            this.livingEntity.playSound(soundEvent, 1.0f, 1.0f);
        }
        if (livingEntity instanceof EvokerAbout evokerAbout) {
            evokerAbout.jerotes1_20_4$spellEvokerAboutSpell();
        }
    }

    @Override
    public void tick() {
        --this.attackWarmupDelay;
        if (this.attackWarmupDelay == 0) {
            this.performSpellCasting();
            if (livingEntity instanceof EvokerAbout evokerAbout) {
                evokerAbout.jerotes1_20_4$setEvokerAboutSpellCastingTickCount(this.getCastingTime());
                this.livingEntity.playSound(evokerAbout.jerotes1_20_4$getEvokerAboutCastingSoundEvent(), 1.0f, 1.0f);
            }
        }
    }

    protected abstract void performSpellCasting();

    protected int getCastWarmupTime() {
        return 20;
    }

    protected abstract int getCastingTime();

    protected abstract int getCastingInterval();

    @Nullable
    protected abstract SoundEvent getSpellPrepareSound();
}