package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.ControlVehicleEntity;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin extends CapabilityProvider<ItemStack> {
    @Shadow public abstract Item getItem();

    protected ItemStackMixin(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    @Inject(method = "onUseTick", at = @At(value = "HEAD"), cancellable = true)
    public void onUseTick(Level level, LivingEntity livingEntity, int n, CallbackInfo ci) {
        if (this.getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase) {
            itemToolBaseSpearBase.onUseTickSpecial(level, livingEntity, (ItemStack)(Object)this, n);
            if (!level.isClientSide()) {
                ci.cancel();
            }
        }
    }
    @Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
    public void useOn(Level p_41683_, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player != null && player.getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity && controlVehicleEntity.canNotUseItemWhenControlVehicleJerotes() &&
                controlVehicleEntity.isManuallyControlCombatJerotes()) {
            cir.setReturnValue(InteractionResultHolder.fail(player.getItemInHand(interactionHand)));
        }
    }
    @Inject(method = "use", at = @At(value = "HEAD"), cancellable = true)
    public void use(Level p_41683_, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player != null && player.getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity && controlVehicleEntity.canNotUseItemWhenControlVehicleJerotes() &&
                controlVehicleEntity.isManuallyControlCombatJerotes()) {
            cir.setReturnValue(InteractionResultHolder.fail(player.getItemInHand(interactionHand)));
        }
    }
}