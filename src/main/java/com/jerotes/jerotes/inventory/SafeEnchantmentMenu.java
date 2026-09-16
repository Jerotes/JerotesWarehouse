package com.jerotes.jerotes.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SafeEnchantmentMenu extends EnchantmentMenu {
    public SafeEnchantmentMenu(int id, Player player) {
        super(id, player.getInventory(), ContainerLevelAccess.NULL);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        for (int i = 0; i < 2; i++) {
            Slot slot = this.slots.get(i);
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty()) {
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                slot.set(ItemStack.EMPTY);
            }
        }
    }
}