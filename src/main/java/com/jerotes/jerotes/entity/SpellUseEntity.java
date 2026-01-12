package com.jerotes.jerotes.entity;

public interface SpellUseEntity {
    //法术等级
    default int getSpellLevel() {
        return 0;
    }
    default boolean isSpellHumanoid() {
        return false;
    }
    default boolean isSpellHumanoidTwoHanded() {
        return false;
    }
    default boolean isMagicUseStyle() {
        return false;
    }
}

