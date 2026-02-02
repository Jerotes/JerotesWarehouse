package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public abstract class NameTagItemMixin extends Item {
    public NameTagItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "interactLivingEntity", at = @At("HEAD"))
    private void interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (itemStack.hasCustomHoverName() && !(livingEntity instanceof Player)) {
            if (!player.level().isClientSide() && livingEntity.isAlive()) {
                if (livingEntity instanceof JerotesEntity jerotes) {
                    jerotes.setCustomNameUseNameTag(itemStack.getHoverName(), livingEntity, player, interactionHand);
                }
            }
        }
    }
}