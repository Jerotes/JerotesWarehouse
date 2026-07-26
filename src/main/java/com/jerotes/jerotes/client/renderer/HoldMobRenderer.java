package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.model.Modelhold_mob;
import com.jerotes.jerotes.entity.Other.OtherSpell.HoldMobEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class HoldMobRenderer extends EntityRenderer<HoldMobEntity> {
   private final Modelhold_mob<HoldMobEntity> model;

    public HoldMobRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new Modelhold_mob<>(context.bakeLayer(Modelhold_mob.LAYER_LOCATION));
    }

    public void render(HoldMobEntity t, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn) {
        float f7 = (float)t.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.scale(t.getSize(), t.getSize(), t.getSize());
        poseStack.scale(Math.min(1, (t.start + partialTicks) / 10), Math.min(1, (t.start + partialTicks) / 10),  Math.min(1, (t.start + partialTicks) / 10));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        this.model.setupAnim(f7);
        this.model.scale(t,
                //开始
                Mth.clamp((t.start + partialTicks) / 20,0 , 1),
                //自身计时
                f7,
                //消失
                1);
        VertexConsumer vb = multiBufferSource.getBuffer(JerotesRenderType.entityTranslucent(getOtherTextureLocation(t)));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);

        vb = multiBufferSource.getBuffer(JerotesRenderType.flowGlowOneSide(getOtherTextureLocation(t), 0, 0));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);

        vb = multiBufferSource.getBuffer(JerotesRenderType.flowGlowOneSide(this.getTextureLocation(t), this.xOffset(f7) % 1.0f, 0.0f));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);

        vb = multiBufferSource.getBuffer(JerotesRenderType.flowGlow(this.getChainTextureLocation(t), this.xOffsetChain(f7) % 1.0f, 0.0f));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);
        poseStack.popPose();

        super.render(t, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
    }
    protected float xOffset(float f) {
        return f * -0.01F;
    }
    protected float xOffsetChain(float f) {
        return f * -0.0025F;
    }

    public ResourceLocation getTextureLocation(HoldMobEntity holdMobEntity) {
        return holdMobEntity.getTextureLocation();
    }
    public ResourceLocation getOtherTextureLocation(HoldMobEntity holdMobEntity) {
        return holdMobEntity.getOtherTextureLocation();
    }
    public ResourceLocation getChainTextureLocation(HoldMobEntity holdMobEntity) {
        return holdMobEntity.getChainTextureLocation();
    }
}