package com.jerotes.jerotes.entity;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.item.JerotesItemThrowUse;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;

public interface UseThrowEntity {
    //发射
    default void useThrowShoot(LivingEntity livingEntity, LivingEntity livingEntityTarget) {
        if (livingEntityTarget == null) {
            return;
        }
        ItemStack itemStackThrow = livingEntity.getMainHandItem();
        if ((livingEntity.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(livingEntity.getMainHandItem()) && !InventoryEntity.isThrow(livingEntity.getMainHandItem())) && !livingEntity.getOffhandItem().isEmpty()) {
            itemStackThrow = livingEntity.getOffhandItem();
        }
        InteractionHand interactionHand = InteractionHand.MAIN_HAND;
        if (itemStackThrow != livingEntity.getMainHandItem()) {
            interactionHand = InteractionHand.OFF_HAND;
        }
        Projectile projectile = new Snowball(livingEntity.level(), livingEntity);
        float speed = 1.6f;
        //鸡蛋
        if (itemStackThrow.getItem() instanceof EggItem) {
            speed = 1.5f;
            ThrownEgg thrownEgg = new ThrownEgg(livingEntity.level(), livingEntity);
            thrownEgg.setItem(itemStackThrow);
            projectile = thrownEgg;
            double d = livingEntityTarget.getX() - livingEntity.getX();
            double d2 = livingEntityTarget.getY(0.3333333333333333) - projectile.getY();
            double d3 = livingEntityTarget.getZ() - livingEntity.getZ();
            double d4 = Math.sqrt(d * d + d3 * d3);
            projectile.shoot(d, d2 + d4 * (0.2 - speed * 0.03f), d3, speed, 2);
            livingEntity.level().addFreshEntity(projectile);
        }
        //雪球
        if (itemStackThrow.getItem() instanceof SnowballItem) {
            speed = 1.5f;
            Snowball snowball = new Snowball(livingEntity.level(), livingEntity);
            snowball.setItem(itemStackThrow);
            projectile = snowball;
            double d = livingEntityTarget.getX() - livingEntity.getX();
            double d2 = livingEntityTarget.getY(0.3333333333333333) - projectile.getY();
            double d3 = livingEntityTarget.getZ() - livingEntity.getZ();
            double d4 = Math.sqrt(d * d + d3 * d3);
            projectile.shoot(d, d2 + d4 * (0.2 - speed * 0.03f), d3, speed, 2);
            livingEntity.level().addFreshEntity(projectile);
        }
        //药水
        if (itemStackThrow.getItem() instanceof ThrowablePotionItem) {
            speed = 0.f;
            ThrownPotion thrownPotion = new ThrownPotion(livingEntity.level(), livingEntity);
            thrownPotion.setItem(itemStackThrow);
            projectile = thrownPotion;
            projectile.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), -20.0F, 0.5F, 1.0F);
        }
        //末影珍珠
        if (itemStackThrow.getItem() instanceof EnderpearlItem) {
            speed = 1.5f;
            ThrownEnderpearl thrownEnderpearl = new ThrownEnderpearl(livingEntity.level(), livingEntity);
            thrownEnderpearl.setItem(itemStackThrow);
            projectile = thrownEnderpearl;
            double d = livingEntityTarget.getX() - livingEntity.getX();
            double d2 = livingEntityTarget.getY(0.3333333333333333) - projectile.getY();
            double d3 = livingEntityTarget.getZ() - livingEntity.getZ();
            double d4 = Math.sqrt(d * d + d3 * d3);
            projectile.shoot(d, d2 + d4 * (0.2 - speed * 0.03f), d3, speed, 2);
            livingEntity.level().addFreshEntity(projectile);
        }
        //特殊投掷
        if (itemStackThrow.getItem() instanceof JerotesItemThrowUse jerotesItemThrowUse && jerotesItemThrowUse.isJerotesThrow(livingEntity)) {
            if (!livingEntity.level().isClientSide()) {
                speed = jerotesItemThrowUse.useJerotesThrowShootSpeed(livingEntity, interactionHand);
                projectile = jerotesItemThrowUse.useJerotesThrowShoot(livingEntity, interactionHand);
            }
            if (projectile == null) return;
            double d = livingEntityTarget.getX() - livingEntity.getX();
            double d2 = livingEntityTarget.getY(0.3333333333333333) - projectile.getY();
            double d3 = livingEntityTarget.getZ() - livingEntity.getZ();
            double d4 = Math.sqrt(d * d + d3 * d3);
            projectile.shoot(d, d2 + d4 * (0.2 - speed * 0.03f), d3, speed, 2);
            livingEntity.level().addFreshEntity(projectile);
        }

        livingEntity.swing(interactionHand);
        if (MainConfig.MobUseThrowShrinkItem &&
                !(itemStackThrow.getItem() instanceof JerotesItemThrowUse jerotesItemThrowUse && !jerotesItemThrowUse.isJerotesThrowShrinkSelf())) {
            if (interactionHand == InteractionHand.OFF_HAND) {
                livingEntity.getOffhandItem().shrink(1);
            }
            else {
                livingEntity.getMainHandItem().shrink(1);
            }
        }
        if (MainConfig.MobUseThrowShrinkItem && itemStackThrow.getItem() instanceof JerotesItemThrowUse jerotesItemThrowUse && jerotesItemThrowUse.isJerotesThrowShrinkOther()) {
           if (interactionHand == InteractionHand.OFF_HAND) {
               livingEntity.getMainHandItem().shrink(1);
           }
           else {
               livingEntity.getOffhandItem().shrink(1);
           }
        }
    }
}

