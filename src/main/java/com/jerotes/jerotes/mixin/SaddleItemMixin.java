package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.ArmorEntity;
import com.jerotes.jerotes.entity.CamelAbout;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SaddleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SaddleItem.class)
public abstract class SaddleItemMixin extends Item {
    public SaddleItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    protected void interactLivingEntity(ItemStack p_43055_, Player p_43056_, LivingEntity p_43057_, InteractionHand p_43058_, CallbackInfoReturnable<InteractionResult> cir) {
        if (p_43057_ instanceof ArmorEntity armorEntity && armorEntity.notBaseSaddle())
            cir.setReturnValue(InteractionResult.PASS);
    }
}