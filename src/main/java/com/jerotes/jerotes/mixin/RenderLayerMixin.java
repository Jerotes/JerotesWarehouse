package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.StrayAbout;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin<T extends Entity, M extends EntityModel<T>> {

    @Shadow
    protected static <T extends LivingEntity> void renderColoredCutoutModel(EntityModel<T> p_117377_, ResourceLocation p_117378_, PoseStack p_117379_, MultiBufferSource p_117380_, int p_117381_, T p_117382_, float p_117383_, float p_117384_, float p_117385_) {
    }

    @Inject(method = "coloredCutoutModelCopyLayerRender", at = @At("HEAD"), cancellable = true)
    private static <T extends LivingEntity> void render(EntityModel<T> p_117360_, EntityModel<T> p_117361_, ResourceLocation p_117362_, PoseStack p_117363_, MultiBufferSource p_117364_, int p_117365_, T p_117366_, float p_117367_,
                                                        float p_117368_, float p_117369_, float p_117370_, float p_117371_, float p_117372_, float p_117373_, float p_117374_,
                                                        float p_117375_, CallbackInfo ci) {
        if (p_117366_ instanceof StrayAbout strayAbout && strayAbout.isJerotesParched()) {
            p_117362_ = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/skeleton/parched_overlay.png");
            if (!p_117366_.isInvisible()) {
                p_117360_.copyPropertiesTo(p_117361_);
                p_117361_.prepareMobModel(p_117366_, p_117367_, p_117368_, p_117372_);
                p_117361_.setupAnim(p_117366_, p_117367_, p_117368_, p_117369_, p_117370_, p_117371_);
                renderColoredCutoutModel(p_117361_, p_117362_, p_117363_, p_117364_, p_117365_, p_117366_, p_117373_, p_117374_, p_117375_);
            }
            ci.cancel();
        }
    }
}