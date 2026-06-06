package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.block.Interface.JerotesChangeFenceGate;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FenceGateBlock.class)
public abstract class FenceGateBlockMixin extends HorizontalDirectionalBlock implements JerotesChangeFenceGate {

    protected FenceGateBlockMixin(Properties p_54120_) {
        super(p_54120_);
    }

    public boolean isJerotesWoodenDoor() {
        return true;
    }
}