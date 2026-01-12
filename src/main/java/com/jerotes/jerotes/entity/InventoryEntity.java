package com.jerotes.jerotes.entity;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.item.*;
import com.jerotes.jerotes.item.tool.ItemToolBasePike;
import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public interface InventoryEntity {
    SimpleContainer mobInventory();
    int inventoryCount();
    default boolean hasCanChangeMeleeOrRange() {
        return true;
    }
    default boolean hasCanChangeInventory() {
        return true;
    }
    boolean isCanChangeInventory();
    void setCanChangeInventory(boolean bl);
    boolean isCanChangeMeleeOrRange();
    void setCanChangeMeleeOrRange(boolean bl);
    default float meleeOrRangeDistance() {
        return 8.0f;
    }
    default int changeInventoryCooldownTick() {
        return 50;
    }
    default boolean canEatOrDrinkHand() {
        return false;
    }
    default boolean howToEatOrDrinkHand(Mob mob, ItemStack itemStack) {
        //例
        //防火
        if (itemStack.getItem() instanceof PotionItem && !(itemStack.getItem() instanceof ThrowablePotionItem) && (PotionUtils.getPotion(itemStack) == Potions.LONG_FIRE_RESISTANCE || PotionUtils.getPotion(itemStack) == Potions.FIRE_RESISTANCE)) {
            return true;
        }
        //水肺
        else if (itemStack.getItem() instanceof PotionItem && !(itemStack.getItem() instanceof ThrowablePotionItem) && (PotionUtils.getPotion(itemStack) == Potions.WATER_BREATHING || PotionUtils.getPotion(itemStack) == Potions.LONG_WATER_BREATHING)) {
            return true;
        }
        return false;
    }
    default boolean NonCombatEmptyWeapon() {
        return false;
    }
    default boolean NonCombatEmptyShield() {
        return false;
    }
    default boolean canUseMeleeWeapon() {
        return true;
    }
    default boolean canUseRangeJavelin() {
        return true;
    }
    default boolean canUseCrossbow() {
        return true;
    }
    default boolean canUseThrow() {
        return true;
    }
    default boolean canUseBow() {
        return true;
    }
    default boolean canUseShield() {
        return true;
    }
    default boolean canUseHelmet() {
        return true;
    }
    default boolean canUseChestplate() {
        return true;
    }
    default boolean canUseLeggings() {
        return true;
    }
    default boolean canUseBoots() {
        return true;
    }
    default boolean canUseMagicItem() {
        return true;
    }

    //额外

    default Component componentInventoryIYes() {
        return Component.translatable("message.jerotes.mob_inventory_inventory_yes");
    }
    default Component componentInventoryINo() {
        return Component.translatable("message.jerotes.mob_inventory_inventory_no");
    }
    default Component componentInventoryIIYes() {
        return Component.translatable("message.jerotes.mob_inventory_melee_or_range_yes");
    }
    default Component componentInventoryIINo() {
        return Component.translatable("message.jerotes.mob_inventory_melee_or_range_no");
    }

    static boolean canReplaceMeleeWeapon(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isMeleeWeapon(inventory, newItem) || isMeleeWeapon(inventory, oldItem))) {
            if (!inventory.canUseMeleeWeapon() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isMeleeWeapon(inventory, newItem) && isMeleeWeapon(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isMeleeWeapon(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceBow(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isBow(inventory, newItem) || isBow(inventory, oldItem))) {
            if (!inventory.canUseBow() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isBow(inventory, newItem) && isBow(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isBow(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceOtherRange(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isOtherRange(inventory, newItem) || isOtherRange(inventory, oldItem))) {
            if (!inventory.canUseBow() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isOtherRange(inventory, newItem) && isOtherRange(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isOtherRange(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceCrossbow(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isCrossbow(inventory, newItem) || isCrossbow(inventory, oldItem))) {
            if (!inventory.canUseCrossbow() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isCrossbow(inventory, newItem) && isCrossbow(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isCrossbow(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceMagicItem(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isMagicItem(inventory, newItem) || isMagicItem(inventory, oldItem))) {
            if (!inventory.canUseMagicItem() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isMagicItem(inventory, newItem) && isMagicItem(inventory, oldItem)) {
                if (newItem.getItem() instanceof MagicItem magicItemNew && oldItem.getItem() instanceof MagicItem magicItemOld && (magicItemNew.getSpellLevel(newItem) > magicItemOld.getSpellLevel(oldItem))){
                    return true;
                }
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isMagicItem(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }

    static boolean canReplaceThrow(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isThrow(inventory, newItem) || isThrow(inventory, oldItem))) {
            if (!inventory.canUseCrossbow() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isThrow(inventory, newItem) && isThrow(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isThrow(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceShield(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isShield(inventory, newItem) || isShield(inventory, oldItem))) {
            //可以进食
            if (inventory instanceof Mob mob &&
                    inventory.canEatOrDrinkHand() && isFoodOrPotion(inventory, newItem) && inventory.howToEatOrDrinkHand(mob, newItem)) {
                //原本是副手物品
                if ((isShield(inventory, oldItem))) {
                    //原本是食物
                    if (isFoodOrPotion(inventory, oldItem) && inventory.howToEatOrDrinkHand(mob, oldItem)) {
                        return false;
                    }
                    return true;
                }
                //原本不是副手物品
                return true;
            }

            if (oldItem.getItem() instanceof ItemTwoHanded && newItem.getItem() instanceof ShieldItem) {
                return false;
            }
            if (!inventory.canUseShield() && newItem.getItem() instanceof ShieldItem || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (oldItem.getItem() instanceof CanBeOffHand) {
                return false;
            }
            else if (newItem.getItem() instanceof CanBeOffHand) {
                return true;
            }
            if (isShield(inventory, newItem) && isShield(inventory, oldItem)) {
                if (!(oldItem.getItem() instanceof ShieldItem)) {
                    return false;
                }
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isShield(inventory, newItem) && !(isShield(inventory, oldItem) && inventory.canUseShield())) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceHelmet(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isHelmet(inventory, newItem) || isHelmet(inventory, oldItem))) {
            if (!inventory.canUseHelmet() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isHelmet(inventory, newItem) && isHelmet(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isHelmet(inventory, newItem) && !isHelmet(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceChestplate(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isChestplate(inventory, newItem) || isChestplate(inventory, oldItem))) {
            if (!inventory.canUseChestplate() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isChestplate(inventory, newItem) && isChestplate(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isChestplate(inventory, newItem) && !isChestplate(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceLeggings(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isLeggings(inventory, newItem) || isLeggings(inventory, oldItem))) {
            if (!inventory.canUseLeggings() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isLeggings(inventory, newItem) && isLeggings(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isLeggings(inventory, newItem) && !isLeggings(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }
    static boolean canReplaceBoots(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isBoots(inventory, newItem) || isBoots(inventory, oldItem))) {
            if (!inventory.canUseBoots() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isBoots(inventory, newItem) && isBoots(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isBoots(inventory, newItem) && !isBoots(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return false;
    }

    static boolean canReplaceCurrentItem(InventoryEntity inventory, ItemStack newItem, ItemStack oldItem) {
        if ((isMeleeWeapon(inventory, newItem) || isMeleeWeapon(inventory, oldItem))) {
            if (!inventory.canUseMeleeWeapon() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isMeleeWeapon(inventory, newItem) && isMeleeWeapon(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isMeleeWeapon(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isBow(inventory, newItem) || isBow(inventory, oldItem))) {
            if (!inventory.canUseBow() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isBow(inventory, newItem) && isBow(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isBow(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isOtherRange(inventory, newItem) || isOtherRange(inventory, oldItem))) {
            if (!inventory.canUseBow() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isOtherRange(inventory, newItem) && isOtherRange(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isOtherRange(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isMagicItem(inventory, newItem) || isMagicItem(inventory, oldItem))) {
            if (!inventory.canUseMagicItem() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isMagicItem(inventory, newItem) && isMagicItem(inventory, oldItem)) {
                if (newItem.getItem() instanceof MagicItem magicItemNew && oldItem.getItem() instanceof MagicItem magicItemOld && (magicItemNew.getSpellLevel(newItem) > magicItemOld.getSpellLevel(oldItem))){
                    return true;
                }
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isMagicItem(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isCrossbow(inventory, newItem) || isCrossbow(inventory, oldItem))) {
            if (!inventory.canUseCrossbow() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isCrossbow(inventory, newItem) && isCrossbow(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isCrossbow(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isThrow(inventory, newItem) || isThrow(inventory, oldItem))) {
            if (!inventory.canUseThrow() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isThrow(inventory, newItem) && isThrow(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isThrow(inventory, newItem) && !(isMeleeWeapon(inventory, oldItem) && inventory.canUseMeleeWeapon()) && !(isCrossbow(inventory, oldItem) && inventory.canUseCrossbow()) && !(isMagicItem(inventory, oldItem) && inventory.canUseMagicItem()) && !(isThrow(inventory, oldItem) && inventory.canUseThrow()) && !(isBow(inventory, oldItem) && inventory.canUseBow()) && !isOtherRange(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isShield(inventory, newItem) || isShield(inventory, oldItem))) {
            if (oldItem.getItem() instanceof ItemTwoHanded && newItem.getItem() instanceof ShieldItem) {
                return false;
            }
            if (!inventory.canUseShield() && newItem.getItem() instanceof ShieldItem || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (oldItem.getItem() instanceof CanBeOffHand) {
                return false;
            }
            else if (newItem.getItem() instanceof CanBeOffHand) {
                return true;
            }
            if (isShield(inventory, newItem) && isShield(inventory, oldItem)) {
                if (!(oldItem.getItem() instanceof ShieldItem)) {
                    return false;
                }
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isShield(inventory, newItem) && !(isShield(inventory, oldItem) && inventory.canUseShield())) {
                return true;
            }
            return false;
        }
        if ((isHelmet(inventory, newItem) || isHelmet(inventory, oldItem))) {
            if (!inventory.canUseHelmet() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isHelmet(inventory, newItem) && isHelmet(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isHelmet(inventory, newItem) && !isHelmet(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isChestplate(inventory, newItem) || isChestplate(inventory, oldItem))) {
            if (!inventory.canUseChestplate() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isChestplate(inventory, newItem) && isChestplate(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isChestplate(inventory, newItem) && !isChestplate(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isLeggings(inventory, newItem) || isLeggings(inventory, oldItem))) {
            if (!inventory.canUseLeggings() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isLeggings(inventory, newItem) && isLeggings(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isLeggings(inventory, newItem) && !isLeggings(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        if ((isBoots(inventory, newItem) || isBoots(inventory, oldItem))) {
            if (!inventory.canUseBoots() || oldItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0) {
                return false;
            }
            if (isBoots(inventory, newItem) && isBoots(inventory, oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isBoots(inventory, newItem) && !isBoots(inventory, oldItem)) {
                return true;
            }
            return false;
        }
        return canReplaceCurrentItemBase(newItem, oldItem);
    }
    static boolean canReplaceCurrentItem(ItemStack newItem, ItemStack oldItem, boolean canUseMelee, boolean canUseBow, boolean canUseCrossbow, boolean canUseMagicItem, boolean canUseThrow, boolean canUseShield, boolean canUseHelmet, boolean canUseChestplate, boolean canUseLeggings, boolean canUseBoots) {
        if ((isMeleeWeapon(newItem) || isMeleeWeapon(oldItem))) {
            if (!canUseMelee) {
                return false;
            }
            if (isMeleeWeapon(newItem) && isMeleeWeapon(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isMeleeWeapon(newItem) && !(isMeleeWeapon(oldItem) && canUseMelee) && !(isCrossbow(oldItem) && canUseCrossbow && !(isMagicItem(oldItem) && canUseMagicItem)) && !(isThrow(oldItem) && canUseThrow) && !(isBow(oldItem) && canUseThrow) && !isOtherRange(oldItem)) {
                return true;
            }
            return false;
        }
        if ((isBow(newItem) || isBow(oldItem))) {
            if (!canUseBow) {
                return false;
            }
            if (isBow(newItem) && isBow(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isBow(newItem) && !(isMeleeWeapon(oldItem) && canUseMelee) && !(isCrossbow(oldItem) && canUseCrossbow) && !(isMagicItem(oldItem) && canUseMagicItem) && !(isThrow(oldItem) && canUseThrow) && !(isBow(oldItem) && canUseBow) && !isOtherRange(oldItem)) {
                return true;
            }
            return false;
        }
        if ((isOtherRange(newItem) || isOtherRange(oldItem))) {
            if (!canUseBow) {
                return false;
            }
            if (isOtherRange(newItem) && isOtherRange(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isOtherRange(newItem) && !(isMeleeWeapon(oldItem) && canUseMelee) && !(isCrossbow(oldItem) && canUseCrossbow) && !(isMagicItem(oldItem) && canUseMagicItem) && !(isThrow(oldItem) && canUseThrow) && !(isBow(oldItem) && canUseBow) && !isOtherRange(oldItem)) {
                return true;
            }
            return false;
        }
        if ((isCrossbow(newItem) || isCrossbow(oldItem))) {
            if (!canUseCrossbow) {
                return false;
            }
            if (isCrossbow(newItem) && isCrossbow(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isCrossbow(newItem) && !(isMeleeWeapon(oldItem) && canUseMelee) && !(isCrossbow(oldItem) && canUseCrossbow) && !(isMagicItem(oldItem) && canUseMagicItem) && !(isThrow(oldItem) && canUseThrow) && !(isBow(oldItem) && canUseBow) && !isOtherRange(oldItem)) {
                return true;
            }
            return false;
        }

        if ((isMagicItem(newItem) || isMagicItem(oldItem))) {
            if (!canUseMagicItem) {
                return false;
            }
            if (isMagicItem(newItem) && isMagicItem(oldItem)) {
                if (newItem.getItem() instanceof MagicItem magicItemNew && oldItem.getItem() instanceof MagicItem magicItemOld && (magicItemNew.getSpellLevel(newItem) > magicItemOld.getSpellLevel(oldItem))){
                    return true;
                }
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isMagicItem(newItem) && !(isMeleeWeapon(oldItem) && canUseMelee) && !(isCrossbow(oldItem) && canUseCrossbow) && !(isMagicItem(oldItem) && canUseMagicItem) && !(isThrow(oldItem) && canUseThrow) && !(isBow(oldItem) && canUseBow)&& !isOtherRange(oldItem)) {
                return true;
            }
            return false;
        }
        if ((isThrow(newItem) || isThrow(oldItem))) {
            if (!canUseThrow) {
                return false;
            }
            if (isThrow(newItem) && isThrow(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isThrow(newItem) && !(isMeleeWeapon(oldItem) && canUseMelee) && !(isCrossbow(oldItem) && canUseCrossbow) && !(isMagicItem(oldItem) && canUseMagicItem) && !(isThrow(oldItem) && canUseThrow) && !(isBow(oldItem) && canUseBow)&& !isOtherRange(oldItem)) {
                return true;
            }
            return false;
        }
        if ((isShield(newItem) || isShield(oldItem))) {
            if (oldItem.getItem() instanceof ItemTwoHanded && newItem.getItem() instanceof ShieldItem) {
                return false;
            }
            if (!canUseShield && newItem.getItem() instanceof ShieldItem) {
                return false;
            }
            if (isShield(newItem) && isShield(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isShield(newItem) && !(isShield(oldItem) && (canUseShield || !(newItem.getItem() instanceof ShieldItem)))) {
                return true;
            }
            return false;
        }
        if ((isHelmet(newItem) || isHelmet(oldItem))) {
            if (!canUseHelmet) {
                return false;
            }
            if (isHelmet(newItem) && isHelmet(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isHelmet(newItem) && !isHelmet(oldItem)) {
                return true;
            }
            return false;
        }
        if ((isChestplate(newItem) || isChestplate(oldItem))) {
            if (!canUseChestplate) {
                return false;
            }
            if (isChestplate(newItem) && isChestplate(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isChestplate(newItem) && !isChestplate(oldItem)) {
                return true;
            }
            return false;
        }
        if ((isLeggings(newItem) || isLeggings(oldItem))) {
            if (!canUseLeggings) {
                return false;
            }
            if (isLeggings(newItem) && isLeggings(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isLeggings(newItem) && !isLeggings(oldItem)) {
                return true;
            }
            return false;
        }
        if ((isBoots(newItem) || isBoots(oldItem))) {
            if (!canUseBoots) {
                return false;
            }
            if (isBoots(newItem) && isBoots(oldItem)) {
                if (newItem.getMaxDamage() > oldItem.getMaxDamage()){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && EntityAndItemFind.isEnchantLevelBetter(newItem, oldItem)){
                    return true;
                }
                else if (newItem.getMaxDamage() == oldItem.getMaxDamage() && newItem.getDamageValue() < oldItem.getDamageValue()){
                    return true;
                }
            }
            if (isBoots(newItem) && !isBoots(oldItem)) {
                return true;
            }
            return false;
        }
        return canReplaceCurrentItemBase(newItem, oldItem);
    }

    static boolean canReplaceCurrentItemBase(ItemStack p_21428_, ItemStack p_21429_) {
        if (p_21429_.isEmpty()) {
            return true;
        } else if (p_21428_.getItem() instanceof SwordItem) {
            if (!(p_21429_.getItem() instanceof SwordItem)) {
                return true;
            } else {
                SwordItem sworditem = (SwordItem)p_21428_.getItem();
                SwordItem sworditem1 = (SwordItem)p_21429_.getItem();
                if (sworditem.getDamage() != sworditem1.getDamage()) {
                    return sworditem.getDamage() > sworditem1.getDamage();
                } else {
                    return canReplaceEqualItem(p_21428_, p_21429_);
                }
            }
        } else if (p_21428_.getItem() instanceof BowItem && p_21429_.getItem() instanceof BowItem) {
            return canReplaceEqualItem(p_21428_, p_21429_);
        } else if (p_21428_.getItem() instanceof CrossbowItem && p_21429_.getItem() instanceof CrossbowItem) {
            return canReplaceEqualItem(p_21428_, p_21429_);
        } else {
            Item $$6 = p_21428_.getItem();
            if ($$6 instanceof ArmorItem) {
                ArmorItem armoritem = (ArmorItem)$$6;
                if (EnchantmentHelper.hasBindingCurse(p_21429_)) {
                    return false;
                } else if (!(p_21429_.getItem() instanceof ArmorItem)) {
                    return true;
                } else {
                    ArmorItem armoritem1 = (ArmorItem)p_21429_.getItem();
                    if (armoritem.getDefense() != armoritem1.getDefense()) {
                        return armoritem.getDefense() > armoritem1.getDefense();
                    } else if (armoritem.getToughness() != armoritem1.getToughness()) {
                        return armoritem.getToughness() > armoritem1.getToughness();
                    } else {
                        return canReplaceEqualItem(p_21428_, p_21429_);
                    }
                }
            } else {
                if (p_21428_.getItem() instanceof DiggerItem) {
                    if (p_21429_.getItem() instanceof BlockItem) {
                        return true;
                    }

                    Item $$7 = p_21429_.getItem();
                    if ($$7 instanceof DiggerItem) {
                        DiggerItem diggeritem = (DiggerItem)$$7;
                        DiggerItem diggeritem1 = (DiggerItem)p_21428_.getItem();
                        if (diggeritem1.getAttackDamage() != diggeritem.getAttackDamage()) {
                            return diggeritem1.getAttackDamage() > diggeritem.getAttackDamage();
                        }

                        return canReplaceEqualItem(p_21428_, p_21429_);
                    }
                }

                return false;
            }
        }
    }
    static boolean canReplaceEqualItem(ItemStack p_21478_, ItemStack p_21479_) {
        if (p_21478_.getDamageValue() < p_21479_.getDamageValue() || p_21478_.hasTag() && !p_21479_.hasTag()) {
            return true;
        } else if (p_21478_.hasTag() && p_21479_.hasTag()) {
            return p_21478_.getTag().getAllKeys().stream().anyMatch((p_21513_) -> {
                return !p_21513_.equals("Damage");
            }) && !p_21479_.getTag().getAllKeys().stream().anyMatch((p_21503_) -> {
                return !p_21503_.equals("Damage");
            });
        } else {
            return false;
        }
    }

    static boolean isFoodOrPotion(InventoryEntity inventory, ItemStack itemStack) {
        return itemStack.getItem().isEdible() || itemStack.getItem() instanceof PotionItem && !(itemStack.getItem() instanceof ThrowablePotionItem);
    }
    static boolean isMeleeWeapon(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseMeleeWeapon()) {
            return isMeleeWeapon(itemStack) || isSpear(inventory, itemStack);
        }
        return false;
    }
    static boolean isMeleeWeapon(ItemStack itemStack) {
        if (itemStack.getItem() instanceof MeleeItem meleeItem) {
            return meleeItem.isMeleeWeapon();
        }
        return itemStack.getItem() instanceof AxeItem
                || itemStack.getItem() instanceof SwordItem
                || itemStack.getItem() instanceof TridentItem
                || itemStack.getItem() instanceof ItemToolBaseSpearBase;
    }
    static boolean isSpear(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseMeleeWeapon()) {
            return isSpear(itemStack);
        }
        return false;
    }
    static boolean isSpear(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemToolBaseSpearBase;
    }
    static boolean isPike(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseMeleeWeapon()) {
            return isPike(itemStack);
        }
        return false;
    }
    static boolean isPike(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemToolBasePike;
    }
    static boolean isCrossbow(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseCrossbow()) {
            return isCrossbow(itemStack);
        }
        return false;
    }
    static boolean isCrossbow(ItemStack itemStack) {
        return itemStack.getItem() instanceof CrossbowItem;
    }
    static boolean isMagicItem(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseMagicItem()) {
            return isMagicItem(itemStack);
        }
        return false;
    }
    static boolean isMagicItem(ItemStack itemStack) {
        return itemStack.getItem() instanceof MagicItem;
    }
    static boolean isThrow(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory instanceof LivingEntity livingEntity &&
                itemStack.getItem() instanceof JerotesItemThrowUse jerotesItemThrowUse && !jerotesItemThrowUse.isJerotesThrow(livingEntity))
            return false;
        if (inventory.canUseThrow()) {
            return isThrow(itemStack);
        }
        return false;
    }
    static boolean isThrow(ItemStack itemStack) {
        return itemStack.getItem() instanceof JerotesItemThrowUse jerotesItemThrowUse && jerotesItemThrowUse.isJerotesThrow()
                || itemStack.getItem() instanceof EggItem
                || itemStack.getItem() instanceof SnowballItem
                || itemStack.getItem() instanceof ThrowablePotionItem
                || itemStack.getItem() instanceof EnderpearlItem;
    }
    static boolean isBow(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseBow()) {
            return isBow(itemStack);
        }
        return false;
    }
    static boolean isBow(ItemStack itemStack) {
        return itemStack.getItem() instanceof BowItem;
    }
    static boolean isRangeJavelin(InventoryEntity inventory, ItemStack itemStack) {
        if ((itemStack.getDamageValue() < itemStack.getMaxDamage() - 1 || !(inventory instanceof Mob mob && mob.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_RANGE_CAN_BREAK))) && inventory.canUseRangeJavelin()) {
            return isRangeJavelin(itemStack);
        }
        return false;
    }
    static boolean isRangeJavelin(ItemStack itemStack) {
        return (itemStack.getItem() instanceof JerotesItemThrownJavelinUse jerotesItemThrownJavelinUse && jerotesItemThrownJavelinUse.isJerotesThrownJavelin() && jerotesItemThrownJavelinUse.JerotesThrownJavelinCanRange() || itemStack.getItem() == Items.TRIDENT);
    }
    static boolean isOtherRange(InventoryEntity inventory, ItemStack itemStack) {
        if (!(inventory instanceof LivingEntity livingEntity)) {
            return false;
        }
        if (itemStack.getItem().getDescriptionId().contains("modern_kinetic_gun"))
            return true;
        return itemStack.getItem() instanceof ItemOtherRange itemOtherRange && itemOtherRange.isRange(livingEntity, itemStack) && inventory.isSelfOtherRange(itemStack);
    }
    static boolean isOtherRange(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemOtherRange;
    }
    default boolean isSelfOtherRange(ItemStack itemStack) {
        return true;
    }
    static boolean isShield(InventoryEntity inventory, ItemStack itemStack) {
        //对该生物是副手
        if (inventory instanceof JerotesEntity jerotes && jerotes.isOffHandItem(itemStack)) {
            return true;
        }
        //对该生物绝对不能是副手
        if (inventory instanceof Mob mob && inventory instanceof JerotesEntity jerotes &&
                jerotes.isNotOffHandItem(mob.getOffhandItem()) && !jerotes.isNotOffHandItem(itemStack)) {
            return true;
        }
        //长枪拒绝副手
        if (inventory instanceof Mob mob && mob.getMainHandItem().getItem() instanceof ItemToolBasePike) {
            return itemStack.isEmpty() && !mob.getOffhandItem().isEmpty();
        }
        //对该主手物品是副手
        if (inventory instanceof LivingEntity livingEntity &&
                itemStack.getItem() instanceof CanBeOffHand canBeOffHand &&
                canBeOffHand.isOffHandItem(livingEntity)) {
            return true;
        }
        if (inventory instanceof LivingEntity livingEntity &&
                livingEntity.getMainHandItem().getItem() instanceof CanGetOffHand canGetOffHand &&
                canGetOffHand.getOffHandItem(livingEntity)) {
            return true;
        }
        //弩
        if (inventory.canUseCrossbow() &&
                inventory instanceof LivingEntity livingEntity &&
                isCrossbow(inventory, livingEntity.getMainHandItem())
                &&
                livingEntity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof FireworkRocketItem) {
            return true;
        }
        if (inventory.canUseShield()) {
            return isShield(itemStack);
        }
        return false;
    }
    static boolean isShield(ItemStack itemStack) {
        return itemStack.getItem() instanceof ShieldItem || itemStack.getItem() == Items.TOTEM_OF_UNDYING;
    }
    static boolean isHelmet(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseHelmet()) {
            return isHelmet(itemStack);
        }
        return false;
    }
    static boolean isHelmet(ItemStack itemStack) {
        return itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.HEAD;
    }
    static boolean isChestplate(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseChestplate()) {
            return isChestplate(itemStack);
        }
        return false;
    }
    static boolean isChestplate(ItemStack itemStack) {
        return itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.CHEST;
    }
    static boolean isLeggings(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseLeggings()) {
            return isLeggings(itemStack);
        }
        return false;
    }
    static boolean isLeggings(ItemStack itemStack) {
        return itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.LEGS;
    }
    static boolean isBoots(InventoryEntity inventory, ItemStack itemStack) {
        if (inventory.canUseBoots()) {
            return isBoots(itemStack);
        }
        return false;
    }
    static boolean isBoots(ItemStack itemStack) {
        return itemStack.getItem() instanceof ArmorItem armorItem && armorItem.getEquipmentSlot() == EquipmentSlot.FEET;
    }

    default void changeInventory(Mob mob) {
        if (mob instanceof InventoryEntity inventory && inventory.isCanChangeInventory() && !mob.isNoAi()) {
            //吃食物
            if (inventory.canEatOrDrinkHand()) {
                int tick = inventory.changeInventoryCooldownTick();
                if (mob.tickCount % tick == 0) {
                    ItemStack mainHandItem = mob.getMainHandItem();
                    ItemStack offHandItem = mob.getOffhandItem();
                    if (isFoodOrPotion(inventory, offHandItem) && !mob.isUsingItem()) {
                        if (inventory.howToEatOrDrinkHand(mob, offHandItem)) {
                            mob.startUsingItem(InteractionHand.OFF_HAND);
                        }
                    }
                    if (isFoodOrPotion(inventory, mainHandItem) && !mob.isUsingItem()) {
                        if (inventory.howToEatOrDrinkHand(mob, mainHandItem)) {
                            mob.startUsingItem(InteractionHand.MAIN_HAND);
                        }
                    }
                }
            }
            //物品buff
            if (MainConfig.InventoryMobAboutItemstackInventoryTick) {
                for (int n = 0; n < inventoryCount(); ++n) {
                    ItemStack itemStack = inventory.mobInventory().getItem(n);
                    int slot = n + 1;
                    if (slot >= 100)
                        slot += 4;
                    if (slot >= 150)
                        slot += 1;
                    itemStack.inventoryTick(mob.level(), mob, slot, false);
                }
                mob.getItemBySlot(EquipmentSlot.MAINHAND).inventoryTick(mob.level(), mob,  0, true);
                mob.getItemBySlot(EquipmentSlot.OFFHAND).inventoryTick(mob.level(), mob,  150, false);
                mob.getItemBySlot(EquipmentSlot.HEAD).inventoryTick(mob.level(), mob,  100, false);
                mob.getItemBySlot(EquipmentSlot.CHEST).inventoryTick(mob.level(), mob,  101, false);
                mob.getItemBySlot(EquipmentSlot.LEGS).inventoryTick(mob.level(), mob,  102, false);
                mob.getItemBySlot(EquipmentSlot.FEET).inventoryTick(mob.level(), mob,  103, false);
            }
            //更换相关
            int tick = inventory.changeInventoryCooldownTick();
            if (mob.tickCount % tick == 0) {
                //选择更好的武器
                if (!mob.level().isClientSide()) {
                    ItemStack mainHandItem = mob.getMainHandItem();
                    ItemStack offHandItem = mob.getOffhandItem();
                    ItemStack headItem = mob.getItemBySlot(EquipmentSlot.HEAD);
                    ItemStack chestItem = mob.getItemBySlot(EquipmentSlot.CHEST);
                    ItemStack legsItem = mob.getItemBySlot(EquipmentSlot.LEGS);
                    ItemStack feetItem = mob.getItemBySlot(EquipmentSlot.FEET);
                    ItemStack findNestMainHandItem = mainHandItem;
                    ItemStack findNestOffHandItem = offHandItem;
                    ItemStack findNestHeadItem = headItem;
                    ItemStack findNestChestItem = chestItem;
                    ItemStack findNestLegsItem = legsItem;
                    ItemStack findNestFeetItem = feetItem;
                    int findNumMainHandItem = -1;
                    int findNumOffHandItem = -1;
                    int findNumHeadItem = -1;
                    int findNumChestItem = -1;
                    int findNumLegsItem = -1;
                    int findNumFeetItem = -1;
                    for (int n = 0; n < inventoryCount(); ++n) {
                        ItemStack itemStack = inventory.mobInventory().getItem(n);
                        //副手
                        if (!(inventory.canEatOrDrinkHand() && isFoodOrPotion(inventory, findNestOffHandItem)) && !itemStack.isEmpty() && (canReplaceShield(inventory, itemStack, findNestOffHandItem)
                                || findNestOffHandItem.isEmpty()) && isShield(inventory, itemStack) && (!inventory.NonCombatEmptyShield() || mob.isAggressive())) {
                            findNestOffHandItem = itemStack;
                            findNumOffHandItem = n;
                        }
                        else if (!(inventory.canEatOrDrinkHand() && isFoodOrPotion(inventory, findNestOffHandItem)) && !findNestOffHandItem.isEmpty() && itemStack.isEmpty() && inventory.NonCombatEmptyShield() && !mob.isAggressive()) {
                            findNestOffHandItem = itemStack;
                            findNumOffHandItem = n;
                        }
                        //头盔
                        else if (!itemStack.isEmpty() && (canReplaceHelmet(inventory, itemStack, findNestHeadItem)
                                || findNestHeadItem.isEmpty()) && isHelmet(inventory, itemStack)) {
                            findNestHeadItem = itemStack;
                            findNumHeadItem = n;
                        }
                        //胸甲
                        else if (!itemStack.isEmpty() && (canReplaceChestplate(inventory, itemStack, findNestChestItem)
                                || findNestChestItem.isEmpty() && isHelmet(inventory, itemStack))) {
                            findNestChestItem = itemStack;
                            findNumChestItem = n;
                        }
                        //护腿
                        else if (!itemStack.isEmpty() && (canReplaceLeggings(inventory, itemStack, findNestLegsItem)
                                || findNestLegsItem.isEmpty() && isLeggings(inventory, itemStack))) {
                            findNestLegsItem = itemStack;
                            findNumLegsItem = n;
                        }
                        //靴子
                        else if (!itemStack.isEmpty() && (canReplaceBoots(inventory, itemStack, findNestFeetItem)
                                || findNestFeetItem.isEmpty() && isBoots(inventory, itemStack))) {
                            findNestFeetItem = itemStack;
                            findNumFeetItem = n;
                        }
                        //主手
                        else {
                            //战斗选择对应类型
                            if (mob.getTarget() != null && inventory.isCanChangeMeleeOrRange() && !itemStack.isEmpty() && !(inventory.canEatOrDrinkHand() && isFoodOrPotion(inventory, findNestMainHandItem))) {
                                //应该近战时
                                if (mob.distanceTo(mob.getTarget()) <= this.meleeOrRangeDistance() && !(mob.distanceTo(mob.getTarget()) > this.meleeOrRangeDistance() / 2 && !mob.getTarget().onGround())) {
                                    //战斗物品
                                    if (isMeleeWeapon(inventory, itemStack) || isBow(inventory, itemStack) || isOtherRange(inventory, itemStack) || isCrossbow(inventory, itemStack) || isMagicItem(inventory, itemStack) || isThrow(inventory, itemStack)) {
                                        //当前为近战
                                        if (isMeleeWeapon(inventory, mob.getMainHandItem()) || (isMagicItem(inventory, mob.getMainHandItem()) && mob.getMainHandItem().getItem() instanceof MagicItem magicItem && magicItem.isMelee(mob.getMainHandItem()) && !magicItem.isHelp(mob.getMainHandItem()))) {
                                            //可替换近战
                                            if (canReplaceMeleeWeapon(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceBow(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceOtherRange(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceCrossbow(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceMagicItem(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceThrow(inventory, itemStack, findNestMainHandItem)
                                                    || findNestMainHandItem.isEmpty()) {
                                                findNestMainHandItem = itemStack;
                                                findNumMainHandItem = n;
                                            }
                                        }
                                        //当前为远程
                                        else {
                                            //是近战或者可替换远程
                                            if (isMeleeWeapon(inventory, itemStack) || (isMagicItem(inventory, itemStack) && itemStack.getItem() instanceof MagicItem magicItem && magicItem.isMelee(itemStack) && !magicItem.isHelp(itemStack))
                                                    || canReplaceBow(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceOtherRange(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceCrossbow(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceMagicItem(inventory, itemStack, findNestMainHandItem) && (isMagicItem(inventory, itemStack) && itemStack.getItem() instanceof MagicItem magicItems && (!magicItems.isMelee(itemStack) || !magicItems.isHelp(itemStack)))
                                                    || canReplaceThrow(inventory, itemStack, findNestMainHandItem)
                                                    || findNestMainHandItem.isEmpty()) {
                                                findNestMainHandItem = itemStack;
                                                findNumMainHandItem = n;
                                            }
                                        }
                                    }
                                }
                                //应该远程时
                                else {
                                    //战斗物品
                                    if (isMeleeWeapon(inventory, itemStack) || isBow(inventory, itemStack) || isOtherRange(inventory, itemStack) || isCrossbow(inventory, itemStack) || isMagicItem(inventory, itemStack) || isThrow(inventory, itemStack)) {
                                        //当前为远程
                                        if (isBow(inventory, mob.getMainHandItem()) || isOtherRange(inventory, mob.getMainHandItem()) || isCrossbow(inventory, mob.getMainHandItem()) || isThrow(inventory, mob.getMainHandItem()) || isRangeJavelin(inventory, mob.getMainHandItem()) && inventory.canUseRangeJavelin() || (isMagicItem(inventory, mob.getMainHandItem()) && mob.getMainHandItem().getItem() instanceof MagicItem magicItem && !magicItem.isMelee(mob.getMainHandItem()) && !magicItem.isHelp(mob.getMainHandItem()))) {
                                            //可替换远程
                                            if (InventoryEntity.canReplaceMeleeWeapon(inventory, itemStack, findNestMainHandItem) && InventoryEntity.isRangeJavelin(inventory, itemStack) && InventoryEntity.isRangeJavelin(inventory, findNestMainHandItem)
                                                    || canReplaceBow(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceOtherRange(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceCrossbow(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceMagicItem(inventory, itemStack, findNestMainHandItem)
                                                    || canReplaceThrow(inventory, itemStack, findNestMainHandItem)
                                                    || findNestMainHandItem.isEmpty()) {
                                                findNestMainHandItem = itemStack;
                                                findNumMainHandItem = n;
                                            }
                                        }
                                        //当前为近战
                                        else {
                                            //是远程或者可替换近战
                                            if (canReplaceMeleeWeapon(inventory, itemStack, findNestMainHandItem) && (isRangeJavelin(inventory, findNestMainHandItem) && inventory.canUseRangeJavelin())
                                                    || canReplaceMagicItem(inventory, itemStack, findNestMainHandItem) && (isMagicItem(inventory, itemStack) && itemStack.getItem() instanceof MagicItem magicItems && (magicItems.isMelee(itemStack) || magicItems.isHelp(itemStack)))
                                                    || (isMagicItem(inventory, itemStack) && itemStack.getItem() instanceof MagicItem magicItem && !magicItem.isMelee(itemStack) && !magicItem.isHelp(itemStack))
                                                    || isBow(inventory, itemStack)
                                                    || isOtherRange(inventory, itemStack)
                                                    || isCrossbow(inventory, itemStack)
                                                    || isRangeJavelin(inventory, itemStack) && inventory.canUseRangeJavelin()
                                                    || isThrow(inventory, itemStack)
                                                    || findNestMainHandItem.isEmpty()) {
                                                findNestMainHandItem = itemStack;
                                                findNumMainHandItem = n;
                                            }
                                        }
                                    }
                                }
                            }
                            //非战斗
                            else {
                                //非战斗空手
                                if (inventory.NonCombatEmptyWeapon() && !mob.isAggressive() && mob.getTarget() == null && !(inventory.canEatOrDrinkHand() && isFoodOrPotion(inventory, findNestMainHandItem))) {
                                    if (!findNestMainHandItem.isEmpty() && itemStack.isEmpty()) {
                                        findNestMainHandItem = itemStack;
                                        findNumMainHandItem = n;
                                    }
                                }
                                //非战斗选择同类物品
                                else if (!itemStack.isEmpty() && !(inventory.canEatOrDrinkHand() && isFoodOrPotion(inventory, findNestMainHandItem))) {
                                    if (isMeleeWeapon(inventory, itemStack) || isBow(inventory, itemStack) || isOtherRange(inventory, itemStack) || isCrossbow(inventory, itemStack) || isMagicItem(inventory, itemStack) || isThrow(inventory, itemStack)) {
                                        if (canReplaceMeleeWeapon(inventory, itemStack, findNestMainHandItem)
                                                || canReplaceBow(inventory, itemStack, findNestMainHandItem)
                                                || canReplaceCrossbow(inventory, itemStack, findNestMainHandItem)
                                                || canReplaceThrow(inventory, itemStack, findNestMainHandItem)
                                                || canReplaceMagicItem(inventory, itemStack, findNestMainHandItem)
                                                || findNestMainHandItem.isEmpty()) {
                                            findNestMainHandItem = itemStack;
                                            findNumMainHandItem = n;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (findNumOffHandItem != -1 && offHandItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0) {
                        mob.setItemSlot(EquipmentSlot.OFFHAND, inventory.mobInventory().getItem(findNumOffHandItem));
                        inventory.mobInventory().setItem(findNumOffHandItem, offHandItem);
                    } else if (findNumHeadItem != -1 && headItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0) {
                        mob.setItemSlot(EquipmentSlot.HEAD, inventory.mobInventory().getItem(findNumHeadItem));
                        inventory.mobInventory().setItem(findNumHeadItem, headItem);
                    } else if (findNumChestItem != -1 && chestItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0) {
                        mob.setItemSlot(EquipmentSlot.CHEST, inventory.mobInventory().getItem(findNumChestItem));
                        inventory.mobInventory().setItem(findNumChestItem, chestItem);
                    } else if (findNumLegsItem != -1 && legsItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0) {
                        mob.setItemSlot(EquipmentSlot.LEGS, inventory.mobInventory().getItem(findNumLegsItem));
                        inventory.mobInventory().setItem(findNumLegsItem, legsItem);
                    } else if (findNumFeetItem != -1 && feetItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0) {
                        mob.setItemSlot(EquipmentSlot.FEET, inventory.mobInventory().getItem(findNumFeetItem));
                        inventory.mobInventory().setItem(findNumFeetItem, feetItem);
                    } else if (findNumMainHandItem != -1 && mainHandItem.getEnchantmentLevel(Enchantments.BINDING_CURSE) <= 0) {
                        mob.setItemSlot(EquipmentSlot.MAINHAND, inventory.mobInventory().getItem(findNumMainHandItem));
                        inventory.mobInventory().setItem(findNumMainHandItem, mainHandItem);
                    }
                }
            }
        }
    }
}

