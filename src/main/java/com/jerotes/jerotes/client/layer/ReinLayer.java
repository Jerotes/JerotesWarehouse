package com.jerotes.jerotes.client.layer;

import com.jerotes.jerotes.init.JerotesMobEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReinLayer<T extends Entity & Saddleable, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final ResourceLocation textureLocation;
    private final M model;

    public ReinLayer(RenderLayerParent<T, M> p_117390_, M p_117391_, ResourceLocation p_117392_) {
        super(p_117390_);
        this.model = p_117391_;
        this.textureLocation = p_117392_;
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        if (t.isSaddled() && t.isVehicle()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player == t.getControllingPassenger()) {
                return;
            }
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(t, f, f2, f3);
            this.model.setupAnim(t, f, f2, f4, f5, f6);
            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(this.textureLocation));
            this.model.renderToBuffer(poseStack, vertexconsumer, n, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}

