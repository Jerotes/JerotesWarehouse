package com.jerotes.jerotes.entity;

import net.minecraft.world.entity.Mob;

public interface CanNotControlEntity {
     default boolean isCanNotControl() {
          return this instanceof Mob mob && mob.getVehicle() != null && mob.getTarget() != null && mob.getTarget() == mob.getVehicle();
     }
}

