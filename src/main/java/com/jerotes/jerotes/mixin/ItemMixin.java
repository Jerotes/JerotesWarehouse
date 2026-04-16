package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.ControlVehicleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
//    @Inject(method = "canAttackBlock", at = @At(value = "HEAD"), cancellable = true)
//    public void canAttackBlock(BlockState p_41441_, Level p_41442_, BlockPos p_41443_, Player player, CallbackInfoReturnable<Boolean> cir) {
//        if (player != null && player.getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity &&
//                controlVehicleEntity.canNotUseItemWhenControlVehicleJerotes() &&
//                controlVehicleEntity.isManuallyControlCombatJerotes()) {
//            cir.setReturnValue(false);
//        }
//    }
}