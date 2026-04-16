package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.client.layer.TruesightLayer;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Shadow
    @Final
    protected List<RenderLayer<T, M>> layers;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectConstructor(EntityRendererProvider.Context context, M model, float shadowRadius, CallbackInfo ci) {
        //真实视觉
        this.layers.add(new TruesightLayer<>(this));
    }


    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void render(T t, CallbackInfoReturnable<Boolean> cir) {
        if (EntityAndItemFind.isTrueInvisible(t)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}