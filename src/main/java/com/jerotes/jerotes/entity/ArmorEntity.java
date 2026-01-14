package com.jerotes.jerotes.entity;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public interface ArmorEntity {
     boolean hasInventoryChanged(Container container);
     boolean isArmor(ItemStack itemStack);
     default boolean isSaddle(ItemStack itemStack) {
          return itemStack.is(Items.SADDLE);
     }
     boolean canWearArmor();
     default int getInventoryColumns() {
          return 5;
     }
     default int getInventorySize() {
          return getInventoryColumns() * 3;
     }
     default boolean hasChest() {
          return false;
     }
     SimpleContainer inventory();
     boolean isWearingArmor();

     default int getAddNumber() {
          return 400;
     }

     boolean isWarBeastArmor();
     boolean isGiantBeastArmor();

     default boolean notBaseSaddle() {
          return false;
     }
     default boolean notBaseBeastArmor() {
          return false;
     }
     default ResourceLocation notBaseSaddleResourceLocation() {
          return new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/horse/saddle_slot.png");
     }
     default ResourceLocation notBaseBeastArmorResourceLocation() {
          return new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/horse/armor_slot.png");
     }
}