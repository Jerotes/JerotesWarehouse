package com.jerotes.jerotes.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseArrow extends ArrowItem {
    public ItemToolBaseArrow(Properties properties) {
        super(properties);
    }

    public float getBaseDamage() {
        return 2.0f;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        list.add(Component.translatable("item.jerotes.arrow", this.getBaseDamage()).withStyle(ChatFormatting.YELLOW));
    }
}

