package com.jerotes.jerotes.item.Interface;

import com.jerotes.jerotes.spell.SpellTypeInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface MagicItem {
    int getSpellLevel(ItemStack itemStack);
    boolean isMelee(ItemStack itemStack);
    boolean isHelp(ItemStack itemStack);
    void damageMagicItem(LivingEntity livingEntity, ItemStack itemStack);
    float getSpellDistance(ItemStack itemStack);
    List<SpellTypeInterface> getMainSpellType(ItemStack itemStack);
    List<SpellTypeInterface> getAddSpellType(ItemStack itemStack);

    default boolean hasSpell() {
        return false;
    }
    default String getSpellId(ItemStack itemStack) {
        return "jerotes_empty";
    }
    default boolean canChangeSubcategory(ItemStack itemStack) {
        return false;
    }
    default int getSubcategory(ItemStack itemStack) {
        return 0;
    }
    default ItemStack setSubcategory(ItemStack itemStack, int n) {
        return itemStack;
    }
}