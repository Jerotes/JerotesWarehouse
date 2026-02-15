package com.jerotes.jerotes.entity.Interface;

import net.minecraft.world.entity.player.Abilities;

public interface JerotesPlayerBaseEntity {
    default boolean beTargetAsPlayer() {
        return false;
    }
    default boolean beLookAsPlayer() {
        return false;
    }
    default boolean beAnesthetizedAsPlayer() {
        return false;
    }
    default boolean otherAttackReachAsPlayer() {
        return false;
    }
    default boolean usePikeAsPlayer() {
        return false;
    }
    default boolean useSpearAsPlayer() {
        return false;
    }
    default boolean useBeaconAsPlayer() {
        return false;
    }
    default boolean attackDamageSourceAsPlayer() {
        return false;
    }
    default boolean hurtByEnderPearlAsPlayer() {
        return false;
    }
    default Abilities getAbilities() {
        return null;
    }
    default void playerJump() {
    }
    default int getAttackSpeed() {
        return 20;
    }
}
