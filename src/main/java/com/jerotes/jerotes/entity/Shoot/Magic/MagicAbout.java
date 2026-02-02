package com.jerotes.jerotes.entity.Shoot.Magic;

public interface MagicAbout {
    default boolean isHelp() {
        return false;
    }
    int getSpellLevel();
}

