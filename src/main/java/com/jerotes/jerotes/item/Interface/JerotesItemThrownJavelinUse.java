package com.jerotes.jerotes.item.Interface;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

public interface JerotesItemThrownJavelinUse {
    //是否特殊远程
    default boolean isJerotesThrownJavelin() {
        return true;
    }
    Projectile JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack);
    //是否常规远程
    default boolean JerotesNormalThrownJavelin() {
        return true;
    }
    //能否附魔冲刺
    default boolean JerotesNormalThrownJavelinRush() {
        return true;
    }
    //能否远程
    default boolean JerotesThrownJavelinCanRange() {
        return true;
    }
    //默认动作
    default void JerotesNormalThrownJavelinAnim(ModelPart main, ModelPart off, ModelPart add, boolean bl) {
        main.xRot = main.xRot * 0.5f - 3.1415927f;
        main.yRot = 0.0f;
    }
    default boolean JerotesUseSpecialThrownJavelinAnim() {
        return false;
    }
    //特殊远程
    default void JerotesNotNormalThrownJavelinUse() {
    }
}

