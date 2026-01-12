package com.jerotes.jerotes.item.tool;

import com.jerotes.jerotes.item.ItemSpecialEffect;
import com.jerotes.jerotes.item.MeleeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseAxe extends AxeItem implements ItemSpecialEffect, MeleeItem {
    public ItemToolBaseAxe(Tier tier, float damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.axe").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}

