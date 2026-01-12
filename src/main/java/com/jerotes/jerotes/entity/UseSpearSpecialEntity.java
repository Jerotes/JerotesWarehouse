package com.jerotes.jerotes.entity;

public interface UseSpearSpecialEntity {
    default float getNeedReach() {
        return 0.5f;
    }
    default float getNeedSpeed() {
        return 0.2f;
    }
    default float getDamageMultiple() {
        return 1.0f;
    }

    default boolean isGetMotionLikePlayer() {
        return false;
    }

    default boolean isCauseExtraKnockbackPlayer() {
        return false;
    }
}

