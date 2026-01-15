package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.item.Interface.JerotesItemThrownJavelinUse;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public interface UseThrownJavelinEntity {
    default int getThrowing() {
        return 1;
    }

    default boolean UseThrownJavelinEntityStopUse() {
        return true;
    }

    //发射
    default void useTridentShoot(LivingEntity livingEntity, LivingEntity livingEntityTarget, float f) {
        if (livingEntityTarget == null) {
            return;
        }
        ItemStack itemStackJavelin = livingEntity.getMainHandItem();
        if ((livingEntity.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(livingEntity.getMainHandItem()) && !InventoryEntity.isRangeJavelin(livingEntity.getMainHandItem())) && !livingEntity.getOffhandItem().isEmpty()) {
            itemStackJavelin = livingEntity.getOffhandItem();
        }
        if (itemStackJavelin.getItem() instanceof JerotesItemThrownJavelinUse jerotesItemThrownJavelinUse && jerotesItemThrownJavelinUse.isJerotesThrownJavelin() && jerotesItemThrownJavelinUse.JerotesThrownJavelinCanRange()) {
            int n3 = EnchantmentHelper.getRiptide(itemStackJavelin);
            if (n3 > 0 && livingEntity.isInWaterOrRain() && jerotesItemThrownJavelinUse.JerotesNormalThrownJavelinRush()) {
                float f1 = livingEntity.getYRot();
                float f2 = livingEntity.getXRot();
                float f3 = -Mth.sin(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
                float f4 = -Mth.sin(f2 * 0.017453292f);
                float f5 = Mth.cos(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
                float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
                float f7 = 3.0f * ((1.0f + (float)n3) / 4.0f);
                livingEntity.push(f3 *= f7 / f6, f4 *= f7 / f6, f5 *= f7 / f6);
                if (livingEntity instanceof Player player) {
                    player.startAutoSpinAttack(20);
                }
                if (livingEntity.onGround()) {
                    float f8 = 1.1999999f;
                    livingEntity.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
                }
                SoundEvent soundEvent = n3 >= 3 ? SoundEvents.TRIDENT_RIPTIDE_3 : (n3 == 2 ? SoundEvents.TRIDENT_RIPTIDE_2 : SoundEvents.TRIDENT_RIPTIDE_1);
                livingEntity.level().playSound(null, livingEntity, soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            else {
                if (jerotesItemThrownJavelinUse.JerotesNormalThrownJavelin()) {
                    Projectile Javelin = jerotesItemThrownJavelinUse.JerotesThrownJavelin(livingEntity, itemStackJavelin);
                    if (Javelin == null) {
                        return;
                    }
                    double d = livingEntityTarget.getX() - livingEntity.getX();
                    double d2 = livingEntityTarget.getY(0.3333333333333333) - Javelin.getY();
                    double d3 = livingEntityTarget.getZ() - livingEntity.getZ();
                    double d4 = Math.sqrt(d * d + d3 * d3);
                    Javelin.shoot(d, d2 + d4 * 0.20000000298023224, d3, f, 2);
                    if (!livingEntity.isSilent()) {
                        livingEntity.playSound(SoundEvents.TRIDENT_THROW, 1.0f, 1.0f / (livingEntity.getRandom().nextFloat() * 0.4f + 0.8f));
                    }
                    livingEntity.level().addFreshEntity(Javelin);
                }
                else {
                    jerotesItemThrownJavelinUse.JerotesNotNormalThrownJavelinUse();
                }
            }
        }
        if (itemStackJavelin.getItem() instanceof TridentItem && itemStackJavelin.getItem() == Items.TRIDENT) {
            int n3 = EnchantmentHelper.getRiptide(itemStackJavelin);
            if (n3 > 0 && livingEntity.isInWaterOrRain()) {
                float f1 = livingEntity.getYRot();
                float f2 = livingEntity.getXRot();
                float f3 = -Mth.sin(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
                float f4 = -Mth.sin(f2 * 0.017453292f);
                float f5 = Mth.cos(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
                float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
                float f7 = 3.0f * ((1.0f + (float)n3) / 4.0f);
                livingEntity.push(f3 *= f7 / f6, f4 *= f7 / f6, f5 *= f7 / f6);
                if (livingEntity instanceof Player player) {
                    player.startAutoSpinAttack(20);
                }
                if (livingEntity.onGround()) {
                    float f8 = 1.1999999f;
                    livingEntity.move(MoverType.SELF, new Vec3(0.0, 1.1999999284744263, 0.0));
                }
                SoundEvent soundEvent = n3 >= 3 ? SoundEvents.TRIDENT_RIPTIDE_3 : (n3 == 2 ? SoundEvents.TRIDENT_RIPTIDE_2 : SoundEvents.TRIDENT_RIPTIDE_1);
                livingEntity.level().playSound(null, livingEntity, soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            else {
                ThrownTrident Javelin = new ThrownTrident(livingEntity.level(), livingEntity, itemStackJavelin);
                double d = livingEntityTarget.getX() - livingEntity.getX();
                double d2 = livingEntityTarget.getY(0.3333333333333333) - Javelin.getY();
                double d3 = livingEntityTarget.getZ() - livingEntity.getZ();
                double d4 = Math.sqrt(d * d + d3 * d3);
                Javelin.shoot(d, d2 + d4 * 0.20000000298023224, d3, f, 2);
                if (!livingEntity.isSilent()) {
                    livingEntity.playSound(SoundEvents.TRIDENT_THROW, 1.0f, 1.0f / (livingEntity.getRandom().nextFloat() * 0.4f + 0.8f));
                }
                livingEntity.level().addFreshEntity(Javelin);
            }
        }
    }
}

