package com.jerotes.jerotes.item;

import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ItemSpecialAttack {
    default void jerotesSpecialAttackClient(LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            player.resetAttackStrengthTicker();
        }
    }
    default void jerotesSpecialAttack(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
    }
    default boolean jerotesSpecialAttackNeed(LivingEntity livingEntity) {
        return !(livingEntity instanceof Player player && player.getAttackStrengthScale(5F) < 1);
    }
}

