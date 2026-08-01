package com.jerotes.jerotes.entity.Interface;

public interface CameraLockEntity {
    default boolean canNotAttackPlayerWhenCameraLock() {
        return true;
    }
    default boolean canNotAttackWhenCameraLock() {
        return true;
    }
}
