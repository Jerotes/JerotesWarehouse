package com.jerotes.jerotes.entity.Interface;

public interface UseDaggerEntity {
     default boolean asUseDagger() {
          return false;
     }
     default float daggerShiftKeyDownReach() {
          return 5;
     }
}

