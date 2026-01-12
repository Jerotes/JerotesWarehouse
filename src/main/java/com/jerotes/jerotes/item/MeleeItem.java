package com.jerotes.jerotes.item;

public interface MeleeItem {
    default boolean isMeleeWeapon() {
        return true;
    }
    default int swingTimes() {
        return 6;
    }
}