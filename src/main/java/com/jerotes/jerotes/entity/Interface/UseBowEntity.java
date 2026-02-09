package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.item.Tool.ItemToolBaseBow;
import com.jerotes.jerotes.util.Main;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public interface UseBowEntity {

    AbstractArrow getCustomArrow(ItemStack itemStack, float f);
    int getBowLevel();

    default int getBowUsePriority() {
        return 1;
    }
    default boolean justBow() {
        return false;
    }

    static AbstractArrow getMobArrow(LivingEntity livingEntity, ItemStack itemStack, float f) {
        ArrowItem arrowitem = (ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
        AbstractArrow abstractarrow = arrowitem.createArrow(livingEntity.level(), itemStack, livingEntity);
        abstractarrow.setEnchantmentEffectsFromEntity(livingEntity, f);
        if (itemStack.is(Items.TIPPED_ARROW) && abstractarrow instanceof Arrow) {
            ((Arrow)abstractarrow).setEffectsFromItem(itemStack);
        }
        return abstractarrow;
    }
    //发射
    default void useBowShoot(LivingEntity livingEntity, LivingEntity livingEntityTarget, float f, int n, int n2) {
        if (livingEntityTarget == null) {
            return;
        }
        ItemStack itemStackBow = livingEntity.getMainHandItem();
        Item off = livingEntity.getOffhandItem().getItem();
        if (off instanceof BowItem && (livingEntity.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(livingEntity.getMainHandItem()))) {
            itemStackBow = livingEntity.getOffhandItem();
        }
        //特殊射弹
        if (itemStackBow.getItem() instanceof ItemToolBaseBow itemToolBaseBow
                && itemToolBaseBow.specialShootBullet(livingEntity, livingEntityTarget, f, n, n2)) {
            return;
        }
        //消耗弹药
        ItemStack itemStackArrow = livingEntity.getProjectile(livingEntity.getItemInHand(ProjectileUtil.getWeaponHoldingHand(livingEntity, itemStackBow.getItem())));
         if (itemStackBow.getItem() instanceof ItemToolBaseBow itemToolBaseBow) {
             if (MainConfig.MobUseBowShrinkArrow) {
                 itemStackArrow.shrink(1);
             }
         }
         //箭
        AbstractArrow abstractArrow = getCustomArrow(itemStackArrow, f);
        if (abstractArrow.getType() == EntityType.ARROW && abstractArrow instanceof Arrow arrow && PotionUtils.getPotion(itemStackArrow) == Potions.EMPTY
                && itemStackBow.getItem() instanceof ItemToolBaseBow customBaseShootBow && customBaseShootBow.useBaseShootArrow()
                && livingEntity.getRandom().nextFloat() <= customBaseShootBow.customBaseShootArrowChance(livingEntity)) {
            abstractArrow = customBaseShootBow.customBaseShootArrow(livingEntity, itemStackArrow);
        }
        if (n2 < n) {
            n = n2;
        }
        int i = Main.randomReach(livingEntity.getRandom(), n, n2);
        float powerTime = getPowerForTime(i);
        if (itemStackBow.getItem() instanceof BowItem) {
            powerTime = BowItem.getPowerForTime(i);
            if (itemStackBow.getItem() instanceof ItemToolBaseBow itemToolBaseBow) {
                powerTime = itemToolBaseBow.getPowerForTimeJerotes(i);
            }
        }
        double d = livingEntityTarget.getX() - livingEntity.getX();
        int speed1 = Math.min(i, 15);
        int speed2 = Math.max(10, speed1);
        int speedOfPowerTime = speed2 - 10;
        double d2 = livingEntityTarget.getY(0.35) - abstractArrow.getY();
        double d3 = livingEntityTarget.getZ() - livingEntity.getZ();
        double d4 = Math.sqrt(d * d + d3 * d3);
        //力度
        float power = powerTime * 3.0F;
        if (itemStackBow.getItem() instanceof ItemToolBaseBow itemToolBaseBow) {
            power = itemToolBaseBow.getArrowSpeed(powerTime);
        }
        //不精准度
        float inaccuracy = Math.max(0, 20 - getBowLevel()) / 5f + getBowLevel() >= 20 ? 0 : 1;
        if (itemStackBow.getItem() instanceof ItemToolBaseBow itemToolBaseBow) {
            inaccuracy = Math.max(0, 20 - getBowLevel()) / 5f + getBowLevel() >= 20 ? 0 : itemToolBaseBow.getArrowInaccuracy();
        }
        //abstractArrow.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 0.0F, f * 3.0F, 1.0F);
        abstractArrow.shoot(d, d2 + d4 * (0.2 - speedOfPowerTime * 0.02f), d3,
                power, inaccuracy);
        Vec3 vec3 = livingEntity.getDeltaMovement();
        abstractArrow.setDeltaMovement(abstractArrow.getDeltaMovement().add(vec3.x, livingEntity.onGround() ? 0.0D : vec3.y, vec3.z));
        //JerotesWarehouse.LOGGER.info("bow power" + power);
        //暴击
        if (powerTime == 1.0F) {
            abstractArrow.setCritArrow(true);
        }
        //附魔
        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemStackBow);
        if (j > 0) {
            abstractArrow.setBaseDamage(abstractArrow.getBaseDamage() + (double)j * 0.5D + 0.5D);
        }
        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemStackBow);
        if (k > 0) {
            abstractArrow.setKnockback(k);
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemStackBow) > 0) {
            abstractArrow.setSecondsOnFire(100);
        }
        if (MainConfig.MobUseBowShrinkArrow) {
            itemStackArrow.shrink(1);
       }

        if (!livingEntity.isSilent()) {
            livingEntity.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (livingEntityTarget.level().getRandom().nextFloat() * 0.4F + 1.2F) + powerTime * 0.5F);
        }
        livingEntity.level().addFreshEntity(abstractArrow);
    }

    static float getPowerForTime(int n) {
        float f = (float)n / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    static double calculateY(int x) {
        double a = 0.006;
        double b = -0.15;
        double c = 0.4;
        return a * x * x + b * x + c;
    }
}

