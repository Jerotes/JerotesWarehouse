package com.jerotes.jerotes.entity;

import com.jerotes.jerotes.item.tool.ItemToolBaseDagger;
import net.minecraft.world.entity.Mob;

public interface ShiftKeyDownEntity {
     default boolean shouldShiftKeyDown() {
          return baseShouldShiftKeyDown();
     }
     default boolean baseShouldShiftKeyDown() {
          if (this instanceof UseDaggerEntity useDaggerEntity && this instanceof Mob mob) {
               if (mob.getTarget() != null && mob.distanceTo(mob.getTarget()) <= useDaggerEntity.daggerShiftKeyDownReach()) {
                    return mob.getMainHandItem().getItem() instanceof ItemToolBaseDagger itemToolBaseDagger && itemToolBaseDagger.canShiftKeyDownUse(mob) && mob.onGround();
               }
          }
          return false;
     }
}

