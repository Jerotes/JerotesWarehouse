package com.jerotes.jerotes.entity;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.List;

public interface JerotesEntity {
    //协助所有同伴
    default boolean helpAllSameFaction() {
        return true;
    }
    //被所有同伴协助
    default boolean helpByAllSameFaction() {
        return true;
    }
    //同类协助
    default boolean helpSameType() {
        return true;
    }
    //特例协助
    default boolean canBeHelp(Entity entity) {
        return true;
    }


    //可以跳跃攻击
    default boolean canAttackJump() {
        return false;
    }
    default AABB getAttackBoundingBox() {
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

    //是同一阵营
    default boolean isFactionWith(Entity entity) {
        return false;
    }
    //憎恨阵营
    default boolean isHateFaction(Entity entity) {
        return false;
    }

    //阵营
    default String getFactionTypeName() {
        return "";
    }
    default String getMobTypeNameModId() {
        return "";
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


    default void setSprintingCooldown(int n) {
    }
    default void setCanNotAttackTargetTick(int n) {
    }
}
