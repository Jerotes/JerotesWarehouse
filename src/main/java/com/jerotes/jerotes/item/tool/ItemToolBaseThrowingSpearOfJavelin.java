package com.jerotes.jerotes.item.tool;

import com.jerotes.jerotes.init.JerotesItems;
import com.jerotes.jerotes.init.JerotesSounds;
import com.jerotes.jerotes.item.ItemToolBaseThrowingSpear;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

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
            return InteractionResultHolder.success(itemStack);
        }
        return super.use(level, player, interactionHand);
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.jerotes.throwing_spear").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}

