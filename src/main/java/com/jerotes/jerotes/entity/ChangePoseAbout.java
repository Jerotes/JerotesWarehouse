package com.jerotes.jerotes.entity;

import net.minecraft.world.entity.player.Player;

public interface ChangePoseAbout {
    int getChangeType();
    boolean isWander();
    void setChangeType(int n);
    void setChangeType(int n, Player player);
}
