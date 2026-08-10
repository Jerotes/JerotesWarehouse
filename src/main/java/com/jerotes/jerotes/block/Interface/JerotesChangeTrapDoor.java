package com.jerotes.jerotes.block.Interface;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public interface JerotesChangeTrapDoor {
    default boolean isJerotesWoodenDoor() {
        return false;
    }

    default SoundEvent getOpenSoundJerotes() {
        return null;
    }
    default SoundEvent getCloseSoundJerotes() {
        return null;
    }

    default void playSoundJerotes(@Nullable Player p_57528_, Level p_57529_, BlockPos p_57530_, boolean p_57531_) {
        p_57529_.playSound(p_57528_, p_57530_, p_57531_ ? getOpenSoundJerotes() : getCloseSoundJerotes(),
                SoundSource.BLOCKS, 1.0F, p_57529_.getRandom().nextFloat() * 0.1F + 0.9F);
        p_57529_.gameEvent(p_57528_, p_57531_ ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, p_57530_);
    }
}

