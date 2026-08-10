package com.jerotes.jerotes.block.Interface;

import net.minecraft.sounds.SoundEvent;

public interface JerotesChangeFenceGate {
    default boolean isJerotesWoodenDoor() {
        return false;
    }
    default SoundEvent getOpenSoundJerotes() {
        return null;
    }
    default SoundEvent getCloseSoundJerotes() {
        return null;
    }
}

