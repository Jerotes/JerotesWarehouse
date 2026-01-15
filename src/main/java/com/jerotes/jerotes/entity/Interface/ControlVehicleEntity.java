package com.jerotes.jerotes.entity.Interface;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface ControlVehicleEntity {
     boolean isManuallyControlCombat();
     void setManuallyControlCombat(boolean bl);
     float getManuallyControlCombatCameraChange();
     boolean canBeControl(Player player);
     void pressMain(Player player);
     boolean canPressMain();
     void pressAdd(Player player);
     boolean canPressAdd();
     boolean isTrueManuallyControlCombat();
     default void individualAttack(LivingEntity livingEntity, int type) {
     }
     default void individualAttack(LivingEntity livingEntity) {
     }
}