package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.item.Interface.ItemToolBaseThrowingSpear;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseThrowingSpearOfJavelin extends ItemToolBaseJavelin implements ItemToolBaseThrowingSpear {
    public ItemToolBaseThrowingSpearOfJavelin(Properties properties, float f, float f2, float f3) {
        super(properties, f, f2, f3);
    }
    public ItemToolBaseThrowingSpearOfJavelin(Properties properties, float f, float f2) {
        super(properties, f, f2);
    }

    public Item getOtherMode() {
        return null;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player.isShiftKeyDown() && getOtherMode() != null) {
            ItemStack itemStack1 = new ItemStack(getOtherMode());
            itemStack1.deserializeNBT(itemStack.getOrCreateTag());
            itemStack1.setTag(itemStack.getOrCreateTag());
            itemStack1.setDamageValue(itemStack.getDamageValue());
            player.setItemInHand(interactionHand, itemStack1);
            player.getCooldowns().addCooldown(itemStack.getItem(), 20);
            player.getCooldowns().addCooldown(itemStack1.getItem(), 20);
            return InteractionResultHolder.success(itemStack);
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.throwing_spear").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}

