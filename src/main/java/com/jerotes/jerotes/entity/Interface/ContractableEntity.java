package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.item.Interface.*;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
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

public interface ContractableEntity {
    //能否契约
    default boolean isCanContract() {
        return true;
    }
    //契约需要法术等级
    default int contractShouldLevel() {
        return contractShouldBaseLevel();
    }
    default int contractShouldBaseLevel() {
        return 5;
    }
    //契约
    default boolean contractUse(LivingEntity livingEntity) {
        return true;
    }
}