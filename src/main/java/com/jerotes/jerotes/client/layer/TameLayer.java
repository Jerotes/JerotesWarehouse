package com.jerotes.jerotes.client.layer;

import com.jerotes.jerotes.entity.TameMobEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;

public class TameLayer<T extends Mob, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final ResourceLocation textureLocation;
    private final M model;

    public TameLayer(RenderLayerParent<T, M> renderLayerParent, M m, ResourceLocation resourceLocation) {
        super(renderLayerParent);
        this.model = m;
        this.textureLocation = resourceLocation;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int n, T t, float f, float f2, float f3, float f4, float f5, float f6) {
        this.getParentModel().copyPropertiesTo(this.model);
        if (!((t instanceof TameMobEntity tameMob && tameMob.isTame()) ||(t instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) || (t instanceof OwnableEntity ownable && ownable.getOwner() != null))) {
            return;
        }
        this.model.prepareMobModel(t, f, f2, f3);
        this.model.setupAnim(t, f, f2, f4, f5, f6);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(this.textureLocation));
        ((Model)this.model).renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}

