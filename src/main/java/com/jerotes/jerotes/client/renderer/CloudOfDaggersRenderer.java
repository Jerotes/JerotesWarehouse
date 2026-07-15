package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.model.Modelcloud_of_daggers;
import com.jerotes.jerotes.entity.Other.OtherSpell.CloudOfDaggersEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class CloudOfDaggersRenderer extends EntityRenderer<CloudOfDaggersEntity> {
   private final Modelcloud_of_daggers<CloudOfDaggersEntity> model;

    public CloudOfDaggersRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new Modelcloud_of_daggers<>(context.bakeLayer(Modelcloud_of_daggers.LAYER_LOCATION));
    }

    public void render(CloudOfDaggersEntity t, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn) {
        float f7 = (float)t.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.scale(Math.min(1, (t.start + partialTicks) / 10), Math.min(1, (t.start + partialTicks) / 10),  Math.min(1, (t.start + partialTicks) / 10));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        this.model.setupAnim(f7 - 20);
        this.model.scale(t, Math.min(1, (t.start - 20 + partialTicks) / 20), f7, Mth.clamp((-(t.life + partialTicks - ((t.spellLevelDamage + 1) * 6 * 20)) / 20f), 0, 1));
        VertexConsumer vb = multiBufferSource.getBuffer(JerotesRenderType.entityTranslucent(getDaggersTextureLocation(t)));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);

        vb = multiBufferSource.getBuffer(JerotesRenderType.flowGlowOneSide(getDaggersTextureLocation(t), 0, 0));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);

        vb = multiBufferSource.getBuffer(JerotesRenderType.flowGlowOneSide(this.getTextureLocation(t), this.xOffset(f7) % 1.0f, 0.0f));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);
        poseStack.popPose();

        super.render(t, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
    }
    protected float xOffset(float f) {
        return f * 0.01F;
    }

    public ResourceLocation getTextureLocation(CloudOfDaggersEntity cloudOfDaggersEntity) {
        return cloudOfDaggersEntity.getTextureLocation();
    }
    public ResourceLocation getDaggersTextureLocation(CloudOfDaggersEntity cloudOfDaggersEntity) {
        return cloudOfDaggersEntity.getDaggersTextureLocation();
    }
}