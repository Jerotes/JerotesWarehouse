package com.jerotes.jerotes.entity;

import net.minecraft.core.BlockPos;

public interface HasHomePosEntity {
    void setHomePos(BlockPos blockPos);
    BlockPos getHomePos();
    boolean isGoingHome();
   void setGoingHome(boolean bl);
}

