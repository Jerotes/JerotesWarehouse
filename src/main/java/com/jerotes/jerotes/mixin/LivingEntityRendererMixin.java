package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.client.layer.TruesightLayer;
import com.jerotes.jerotes.entity.Interface.JerotesChangeLivingEntity;
import com.jerotes.jerotes.entity.Interface.SpecialProneEntity;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.EntityAndItemFind;
import com.jerotes.jerotes.util.Main;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.NotNull;
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

    @Shadow
    public abstract M getModel();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectConstructor(EntityRendererProvider.Context context, M model, float shadowRadius, CallbackInfo ci) {
        //真实视觉
        this.layers.add(new TruesightLayer<>(this));
    }


    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    public void shouldShowName(T t, CallbackInfoReturnable<Boolean> cir) {
        if (EntityAndItemFind.isTrueInvisible(t)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }


    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
    public void render(T t, float f, float f2, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int n, CallbackInfo ci) {
        if (t instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity) {
            jerotesChangeLivingEntity.setThisTickRenderTimeJerotes(jerotesChangeLivingEntity.getThisTickRenderTimeJerotes() + 1);
            float attackAnim = jerotesChangeLivingEntity.getProneTickJerotes();
            float fs = jerotesChangeLivingEntity.getLastTickRenderTimeJerotes() + 1;
            float smoothFactor = 1f / fs;
            jerotesChangeLivingEntity.setProneTickProgressJerotes(Mth.lerp(smoothFactor, jerotesChangeLivingEntity.getProneTickProgressJerotes(), attackAnim));
        }
    }

    @Inject(method = "setupRotations", at = @At("TAIL"))
    public void setupRotations(T entity, PoseStack p_115318_, float p_115319_, float p_115320_, float p_115321_, CallbackInfo ci) {
        if (entity instanceof SpecialProneEntity) return;
        float n = (float) (entity instanceof JerotesChangeLivingEntity jerotesChangeLivingEntity ? jerotesChangeLivingEntity.getProneTickProgressJerotes() : 0);
        float f = n/10f;

        if (getModel() instanceof QuadrupedModel<?> && entity.getBbHeight() < entity.getBbWidth() * 1.5f ||
                getModel() instanceof HierarchicalModel<?> && entity instanceof Animal && entity.getBbHeight() < entity.getBbWidth() * 1.5f ||
                entity.getBbHeight() < entity.getBbWidth() * 1.2f) {

            p_115318_.mulPose(Axis.ZP.rotationDegrees(-90.0F * f));
            p_115318_.translate(0, (-entity.getBbHeight() / 2) * f, (entity.getBbWidth() / 4) * f);
        }
        else {
            p_115318_.mulPose(Axis.XP.rotationDegrees(-90.0F * f));
            p_115318_.translate(0, (-entity.getBbHeight() / 2) * f, (entity.getBbWidth() / 4) * f);
        }
    }
}