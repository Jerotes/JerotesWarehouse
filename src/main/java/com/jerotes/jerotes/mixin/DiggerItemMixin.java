package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.item.Interface.MeleeItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin extends TieredItem {
    public DiggerItemMixin(Tier p_43308_, Properties p_43309_) {
        super(p_43308_, p_43309_);
    }

    @Inject(method = "hurtEnemy", at = @At(value = "HEAD"), cancellable = true)
    public void hurtEnemy(ItemStack itemStack, LivingEntity p_40995_, LivingEntity p_40996_, CallbackInfoReturnable<Boolean> cir) {
        if (itemStack.getItem() instanceof MeleeItem meleeItem && meleeItem.isMeleeWeapon()) {
            itemStack.hurtAndBreak(1, p_40996_, (p_41007_) -> {
                p_41007_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}