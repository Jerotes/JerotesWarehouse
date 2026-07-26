package com.jerotes.jerotes.entity.Interface;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface ConcentrationSpellEntity {
     boolean endConcentration();
     default boolean attackEndConcentration() {
          return false;
     }
     default boolean spellEndConcentration() {
          return false;
     }
}