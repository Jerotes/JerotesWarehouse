package com.jerotes.jerotes.entity.Interface;

public interface UseSpearSpecialEntity {
    default float getJerotesSpearNeedReach() {
        return 0.5f;
    }
    default float getJerotesSpearNeedSpeed() {
        return 0.2f;
    }
    default float getJerotesSpearDamageMultiple() {
        return 1.0f;
    }

    default boolean isJerotesSpearGetMotionLikePlayer() {
        return false;
    }

    default boolean isJerotesSpearCauseExtraKnockbackPlayer() {
        return false;
    }
}

