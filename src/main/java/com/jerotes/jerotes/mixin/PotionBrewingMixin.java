package com.jerotes.jerotes.mixin;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {
    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private static void mix(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if (input.hasTag()
                && input.getTag().getBoolean("JerotesCanBrewChange")
                && ((ingredient.is(Items.GUNPOWDER) && input.is(Items.POTION))
                || (ingredient.is(Items.DRAGON_BREATH) && input.is(Items.SPLASH_POTION)))) {

            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}