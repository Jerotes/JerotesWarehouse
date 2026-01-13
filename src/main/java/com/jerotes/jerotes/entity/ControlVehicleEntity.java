package com.jerotes.jerotes.entity;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ControlVehicleEntity {
     boolean changeControlType(Container container);
     boolean isManuallyControlCombat();
     void setManuallyControlCombat(boolean bl);
     float getManuallyControlCombatCameraChange();
     float canBeControl();
     float pressMain(Player player);
     boolean canPressMain();
     float pressAdd(Player player);
     boolean canPressAdd();
}