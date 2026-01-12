package com.jerotes.jerotes.entity.magic;

public interface MagicAbout {
    default boolean isHelp() {
        return false;
    }
    int getSpellLevel();
}

