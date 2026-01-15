package com.jerotes.jerotes.item.Interface;

import com.jerotes.jerotes.spell.SpellTypeInterface;
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
}