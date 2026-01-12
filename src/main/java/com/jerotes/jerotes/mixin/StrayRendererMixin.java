package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.StrayAbout;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StrayRenderer.class)
public abstract class StrayRendererMixin extends SkeletonRenderer {
    public StrayRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "getTextureLocation*", at = @At("HEAD"), cancellable = true)
    public void getTextureLocation(AbstractSkeleton abstractSkeleton, CallbackInfoReturnable<ResourceLocation> cir) {
        if (abstractSkeleton instanceof StrayAbout strayAbout && strayAbout.isJerotesParched()) {
            cir.setReturnValue(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skeleton/parched.png"));
        }
    }
}