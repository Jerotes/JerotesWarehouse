package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.block.Interface.JerotesChangeTrapDoor;
import com.jerotes.jerotes.entity.Interface.JerotesChangeCamel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrapDoorBlock.class)
public abstract class TrapDoorBlockMixin extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, JerotesChangeTrapDoor {
    @Shadow
    @Final
    private BlockSetType type;

    protected TrapDoorBlockMixin(Properties p_54120_) {
        super(p_54120_);
    }

    public boolean isJerotesWoodenDoor() {
        return this.type.canOpenByHand();
    }
}