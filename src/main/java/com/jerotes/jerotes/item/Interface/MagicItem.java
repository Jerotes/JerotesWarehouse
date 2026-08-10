package com.jerotes.jerotes.item.Interface;

import com.jerotes.jerotes.spell.SpellTypeInterface;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface MagicItem {
    default boolean showItemIcon() {
        return false;
    }
    default boolean showTooltipIcon() {
        return true;
    }


    int getSpellLevelMagicItem(ItemStack itemStack);
    boolean isMeleeMagicItem(ItemStack itemStack);
    boolean isHelpMagicItem(ItemStack itemStack);
    void damageMagicItem(LivingEntity livingEntity, ItemStack itemStack);
    float getSpellDistanceMagicItem(ItemStack itemStack);
    List<SpellTypeInterface> getMainSpellTypeMagicItem(ItemStack itemStack);
    List<SpellTypeInterface> getAddSpellTypeMagicItem(ItemStack itemStack);

    default boolean hasSpellMagicItem() {
        return false;
    }
    default String getSpellIdMagicItem(ItemStack itemStack) {
        return "jerotes_empty";
    }
    default boolean canChangeSubcategoryMagicItem(ItemStack itemStack) {
        return false;
    }
    default int getSubcategoryMagicItem(ItemStack itemStack) {
        return 0;
    }
    default ItemStack setSubcategoryMagicItem(ItemStack itemStack, int n) {
        return itemStack;
    }
}