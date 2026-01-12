package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render*", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack var1, MultiBufferSource var2, int var3, T t, float var5, float var6, float var7, float var8, float var9, float var10, CallbackInfo ci) {
        if (t.hasEffect(JerotesMobEffects.CLOAKING.get()) && t.isShiftKeyDown()) {
            ci.cancel();
        }
    }
}