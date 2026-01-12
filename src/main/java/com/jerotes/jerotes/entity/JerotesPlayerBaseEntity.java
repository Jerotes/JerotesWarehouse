package com.jerotes.jerotes.entity;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.List;

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
    default Abilities getAbilities() {
        return null;
    }
    default void playerJump() {
    }
    default int getAttackSpeed() {
        return 20;
    }
}
