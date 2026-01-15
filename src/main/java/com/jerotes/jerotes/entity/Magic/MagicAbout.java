package com.jerotes.jerotes.entity.Magic;

public interface MagicAbout {
    default boolean isHelp() {
        return false;
    }
    int getSpellLevel();
}

