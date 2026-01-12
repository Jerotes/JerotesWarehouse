package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}