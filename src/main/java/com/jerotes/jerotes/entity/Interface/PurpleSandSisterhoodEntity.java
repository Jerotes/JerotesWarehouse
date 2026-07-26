package com.jerotes.jerotes.entity.Interface;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public interface PurpleSandSisterhoodEntity {
    default int PurpleSandSisterhoodLevel() {
        return 2;
    }
}

