package com.jerotes.jerotes.entity.Interface;

import net.minecraft.sounds.SoundEvent;

public interface EvokerAbout {
    default void jerotes1_20_4$setEvokerAboutSpellCastingTickCount(int n) {

    }
    default SoundEvent jerotes1_20_4$getEvokerAboutCastingSoundEvent() {
        return null;
    }
    default void jerotes1_20_4$spellEvokerAboutSpell() {
    }
}
