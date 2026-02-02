package com.jerotes.jerotes.entity.Interface;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public interface ControlVehicleEntity {
     boolean isManuallyControlCombatJerotes();
     void setManuallyControlCombatJerotes(boolean bl);
     float getManuallyControlCombatCameraChangeJerotes();
     boolean canBeControlJerotes(Player player);
     void pressMainJerotes(Player player);
     boolean canPressMainJerotes();
     void pressAddJerotes(Player player);
     boolean canPressAddJerotes();
     default boolean canNotUseItemWhenControlVehicleJerotes() { return true;}
     boolean isTrueManuallyControlCombatJerotes();
     default void individualAttackJerotes(LivingEntity livingEntity, int type) {
     }
     default void individualAttackJerotes(LivingEntity livingEntity) {
     }


     default boolean canChangeSteeringControl(Player player) {
          return false;
     }
     default void setNormalSteeringControlJerotes(boolean bl) {
     }
     default boolean isNormalSteeringControlJerotes() {
          return false;
     }

     default void pressLeftJerotes(Player player) {}
     default boolean canPressLeftJerotes() {return true;}
     default void pressRightJerotes(Player player) {}
     default boolean canPressRightJerotes() {return true;}
}