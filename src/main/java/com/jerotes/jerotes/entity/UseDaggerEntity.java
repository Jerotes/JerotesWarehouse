package com.jerotes.jerotes.entity;

public interface UseDaggerEntity {
     default boolean asUseDagger() {
          return false;
     }
     default float daggerShiftKeyDownReach() {
          return 5;
     }
}

