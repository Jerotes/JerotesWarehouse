package com.jerotes.jerotes.entity.Interface;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;

public interface JerotesChangeEntity {
    default CompoundTag getJerotesPersistentData() {
        return null;
    }
}
