package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.util.EntityAndItemFind;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EyesLayer.class)
public abstract class EyesLayerMixin<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public EyesLayerMixin(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack var1, MultiBufferSource var2, int var3, T t, float var5, float var6, float var7, float var8, float var9, float var10, CallbackInfo ci) {
        if (EntityAndItemFind.isTrueInvisible(t)) {
            ci.cancel();
        }
    }
}