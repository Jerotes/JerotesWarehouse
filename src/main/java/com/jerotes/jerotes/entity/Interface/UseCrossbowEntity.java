package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.item.Tool.ItemToolBaseCrossbow;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

public interface UseCrossbowEntity {
    default boolean isChargingCrossbow() {
        return false;
    }

    default void onCrossbowAttackPerformed() {
    }

    //发射
    default void useCrossbowShoot(LivingEntity livingEntity, float f) {
        InteractionHand interactionhand = ProjectileUtil.getWeaponHoldingHand(livingEntity, item -> item instanceof CrossbowItem);
        ItemStack itemstack = livingEntity.getItemInHand(interactionhand);

        if (livingEntity.isHolding(is -> is.getItem() instanceof ItemToolBaseCrossbow) &&
                itemstack.getItem() instanceof ItemToolBaseCrossbow crossbowItem && crossbowItem.useCustomShoot() && livingEntity instanceof Mob mob) {
            crossbowItem.customShoot(livingEntity, itemstack);
        }
        else if (livingEntity.isHolding(is -> is.getItem() instanceof CrossbowItem) &&
                itemstack.getItem() instanceof CrossbowItem) {

            if (itemstack.getItem() instanceof ItemToolBaseCrossbow itemToolBaseCrossbow) {
                ItemToolBaseCrossbow.performShooting(livingEntity.level(), livingEntity, interactionhand, itemstack,
                        itemToolBaseCrossbow.getShootingPower(itemstack),
                        livingEntity instanceof UseBowEntity useBowEntity ?
                                Math.max(0, 20 - useBowEntity.getBowLevel()) / 5f
                                :
                                (float)(14 - livingEntity.level().getDifficulty().getId() * 4));
            }
            else {
                CrossbowItem.performShooting(livingEntity.level(), livingEntity, interactionhand, itemstack,
                        itemstack.getItem() instanceof ItemToolBaseCrossbow itemToolBaseCrossbow ? itemToolBaseCrossbow.getShootingPower(itemstack) : f,
                        livingEntity instanceof UseBowEntity useBowEntity ?
                                Math.max(0, 20 - useBowEntity.getBowLevel()) / 5f
                                :
                                (float)(14 - livingEntity.level().getDifficulty().getId() * 4));
            }
        }
        this.onCrossbowAttackPerformed();
    }
}

