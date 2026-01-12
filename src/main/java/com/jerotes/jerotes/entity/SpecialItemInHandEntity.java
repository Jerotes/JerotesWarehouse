package com.jerotes.jerotes.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

public interface SpecialItemInHandEntity {
    //禁用特殊动画物品
    default boolean stopSpecialItemInHand(ItemStack itemStack) {
        return false;
    }
}

