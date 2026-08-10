package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.model.Modelcloud_of_daggers;
import com.jerotes.jerotes.client.model.Modelmagic_bonfire;
import com.jerotes.jerotes.entity.Other.OtherSpell.CloudOfDaggersEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.MagicBonfireEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MagicBonfireRenderer extends EntityRenderer<MagicBonfireEntity> {
   private final Modelmagic_bonfire<MagicBonfireEntity> model;

    public MagicBonfireRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new Modelmagic_bonfire<>(context.bakeLayer(Modelmagic_bonfire.LAYER_LOCATION));
    }

    public void render(MagicBonfireEntity t, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn) {
        float f7 = (float)t.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.scale(Math.min(1, (t.start + partialTicks) / 10), Math.min(1, (t.start + partialTicks) / 10),  Math.min(1, (t.start + partialTicks) / 10));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        this.model.setupAnim(f7 - 20);
        this.model.scale(t,
                //开始
                Mth.clamp((t.life + partialTicks) / 20,0 , 1),
                //火焰
                Mth.clamp((t.life + partialTicks - 10) / 20 ,0 , 1),
                //烧炼
                1 - Mth.clamp((t.getCookCooldown() - partialTicks) / 120,0 , 1),
                //攻击
                1 + Mth.clamp((t.getAttackTick() - partialTicks) / 50,0 , 0.2f),
                //自身计时
                f7,
                //消失
                Mth.clamp((t.getMaxLife() - (t.life + partialTicks)) / 10f,0 , 1));
        VertexConsumer vb = multiBufferSource.getBuffer(JerotesRenderType.entityTranslucent(getOtherTextureLocation(t)));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);

        vb = multiBufferSource.getBuffer(JerotesRenderType.flow(getTextureLocation(t), 0, 0));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);
        poseStack.popPose();

        super.render(t, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
    }
    protected float xOffset(float f) {
        return f * 0.01F;
    }

    public ResourceLocation getTextureLocation(MagicBonfireEntity magicBonfireEntity) {
        return magicBonfireEntity.getTextureLocation();
    }
    public ResourceLocation getOtherTextureLocation(MagicBonfireEntity magicBonfireEntity) {
        return magicBonfireEntity.getOtherTextureLocation();
    }
}