package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public interface JerotesEntity {

    default void setCustomNameUseNameTag(@Nullable Component component, Entity self, Entity source, InteractionHand interactionHand) {
    }

    //可以跳跃攻击
    default boolean canAttackJump() {
        return false;
    }
    default AABB getAttackBoundingBox() {
        if (this instanceof Mob mob) {
            Entity entity = mob.getVehicle();
            AABB aabb;
            if (entity != null) {
                AABB aabb1 = entity.getBoundingBox();
                AABB aabb2 = mob.getBoundingBox();
                aabb = new AABB(Math.min(aabb2.minX, aabb1.minX), aabb2.minY, Math.min(aabb2.minZ, aabb1.minZ), Math.max(aabb2.maxX, aabb1.maxX), aabb2.maxY, Math.max(aabb2.maxZ, aabb1.maxZ));
            } else {
                aabb = mob.getBoundingBox();
            }
            AABB aabb1 = aabb.inflate(Math.sqrt(2.04F) - (double) 0.6F, 0.0D, Math.sqrt(2.04F) - (double) 0.6F);
            return aabb1.inflate(0d, 0d, 0d);
        }
        return null;
    }

    //飞行驯服生物
    default boolean isJerotesFlyingMob() {
        return false;
    }

    //命令攻击
    default boolean OwnerCanOrderAttack() {
        return true;
    }



    default boolean isOffHandItem(ItemStack itemStack) {
        return false;
    }
    default boolean isNotOffHandItem(ItemStack itemStack) {
        return false;
    }

    //额外介绍
    default void MobInventoryAddTooltip(List<Component> tooltip, LivingEntity livingEntity) {
    }

    //百分比
    default boolean hasPercentageDamage() {
        if (!(this instanceof LivingEntity livingEntity)) {
            return false;
        }
        return MainConfig.HasPercentageDamage.contains(livingEntity.getEncodeId());
    }
    default float PercentageDamage(DamageSource damageSource) {
        if (EntityAndItemFind.MagicResistance(damageSource)) {
            return(float) MainConfig.BaseMagicAttackPercentage;
        }
        return (float) MainConfig.BaseAttackPercentage;
    }
    //限伤
    default boolean hasDamageCap() {
        if (!(this instanceof LivingEntity livingEntity)) {
            return false;
        }
        return (MainConfig.HasDamageCap.contains(livingEntity.getEncodeId()));
    }
    default float DamageCap(DamageSource damageSource, Entity entity) {
        if (!(this instanceof LivingEntity self)) {
            return Float.MAX_VALUE;
        }
        return (float) MainConfig.BaseDamageCap / 100 * self.getMaxHealth();
    }
    //间隔
    default boolean hasDamageCooldownTick() {
        if (!(this instanceof LivingEntity livingEntity)) {
            return false;
        }
        return (MainConfig.HasDamageCooldownTick.contains(livingEntity.getEncodeId()));
    }
    default float DamageCooldownTick(DamageSource damageSource, Entity entity) {
        float base = 1;
        if (damageSource.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
            base *= 0.5f;
        }
        return (float) MainConfig.BaseDamageCooldownTick * base;
    }
    default float BreakHurtCooldownMultiple(DamageSource damageSource, Entity entity) {
        return (float) MainConfig.BaseBreakHurtCooldownMultiple;
    }

    default boolean isLegendary() {
        return this.hasDamageCap() && this.hasDamageCooldownTick() && this.hasPercentageDamage();
    }
    default boolean isExalted() {
        return false;
    }

    default void setSprintingCooldown(int n) {
    }
    default void setCanNotAttackTargetTick(int n) {
    }
}
