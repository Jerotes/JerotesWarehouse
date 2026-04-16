package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.enchantment.Interface.MeleeEnchantment;
import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseAxe extends AxeItem implements ItemSpecialEffect, MeleeItem {
    public ItemToolBaseAxe(Tier tier, float damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment instanceof DamageEnchantment || enchantment instanceof FireAspectEnchantment || enchantment instanceof LootBonusEnchantment lootBonusEnchantment && lootBonusEnchantment.category == EnchantmentCategory.WEAPON || enchantment instanceof KnockbackEnchantment || enchantment instanceof MeleeEnchantment) {
            return this.isMeleeWeapon();
        }
        if (enchantment instanceof SweepingEdgeEnchantment) {
            return this instanceof ItemTwoHanded;
        }
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.axe").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}

