package com.jerotes.jerotes.entity.Interface;

import net.minecraft.sounds.SoundEvent;

public interface BossEntity {
    //Boss音乐
    default SoundEvent getBossMusic() {
        return null;
    }
}

