package com.jerotes.jerotes.entity.Interface;

import java.util.UUID;

public interface CarvedEntity {
    default boolean trusts(UUID uUID) {
        return false;
    }
    default boolean isVexRace() {
        return false;
    }

    default boolean isCarvedAlly() {
        return false;
    }
}
