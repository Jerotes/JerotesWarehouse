package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.Beam.BaseBeamEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BaseBeamRenderer<T extends BaseBeamEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");

    public BaseBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(T t, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
        t.thisTickRenderTime += 1;
        float fs = t.lastTickRenderTime + 1;
        float smoothFactor = 1;
        float selfX = (float) t.getX();
        t.selfXProgress = Mth.lerp(smoothFactor, t.selfXProgress, selfX);
        float selfY = (float) t.getY();
        t.selfYProgress = Mth.lerp(smoothFactor, t.selfYProgress, selfY);
        float selfZ = (float) t.getZ();
        t.selfZProgress = Mth.lerp(smoothFactor, t.selfZProgress, selfZ);
        float smoothFactor2 = 1/fs;
        float ownerX = t.getLightLockX();
        t.ownerXProgress = Mth.lerp(smoothFactor2, t.ownerXProgress, ownerX);
        float ownerY = t.getLightLockY();
        t.ownerYProgress = Mth.lerp(smoothFactor2, t.ownerYProgress, ownerY);
        float ownerZ = t.getLightLockZ();
        t.ownerZProgress = Mth.lerp(smoothFactor2, t.ownerZProgress, ownerZ);

        poseStack.pushPose();
        float f7 = t.ownerXProgress;
        float f8 = t.ownerYProgress;
        float f9 = t.ownerZProgress;

        float f10 = t.selfXProgress;
        float f11 = t.selfYProgress;
        float f12 = t.selfZProgress;
        if (t.tickCount <= 5) {
            poseStack.popPose();
            return;
        }
        float f13 = (float) (f7 - Mth.lerp((double) f2, t.xo, f10));
        float f14 = (float) (f8 - Mth.lerp((double) f2, t.yo, f11));
        float f15 = (float) (f9 - Mth.lerp((double) f2, t.zo, f12));
        renderLightLock(f13, f14, f15, f2, t.tickCount, poseStack, multiBufferSource, n);
        poseStack.popPose();
        super.render(t, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(BaseBeamEntity tex) {
        return TEXTURE_LOCATION;
    }

    public ResourceLocation getBeamLocation() {
        return LOCATION;
    }

    public static final ResourceLocation LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/base_beam.png");
    private static final RenderType BEAM2 = JerotesRenderType.translucent();

    public void renderLightLock(float f, float f2, float f3, float f4, int n, PoseStack poseStack, MultiBufferSource multiBufferSource, int n2) {
        float horizontalDistance = Mth.sqrt(f * f + f3 * f3);
        float totalDistance = Mth.sqrt(f * f + f2 * f2 + f3 * f3);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.65625f, 0.5f);
        float yaw = (float) Math.atan2(f3, f);
        float pitch = (float) Math.atan2(f2, horizontalDistance);
        poseStack.mulPose(Axis.YP.rotation(yaw));
        poseStack.mulPose(Axis.XP.rotation(pitch));

        float uvBottom = 0.0f;
        float uvTop = 1.0f;

        {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(BEAM2);
            float beamWidth = 0.15f;
            float halfWidth = beamWidth / 2.0f;
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();

            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                    .color(255, 255, 255, 20).uv(0.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                    .color(255, 255, 255, 20).uv(1.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                    .color(255, 255, 255, 20).uv(1.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                    .color(255, 255, 255, 20).uv(0.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();

            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                    .color(255, 255, 255, 20).uv(0.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                    .color(255, 255, 255, 20).uv(1.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                    .color(255, 255, 255, 20).uv(1.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                    .color(255, 255, 255, 20).uv(0.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();

            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                    .color(200, 200, 255, 20).uv(0.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                    .color(200, 200, 255, 20).uv(1.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                    .color(200, 200, 255, 20).uv(1.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                    .color(200, 200, 255, 20).uv(0.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();

            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                    .color(200, 200, 255, 20).uv(0.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                    .color(200, 200, 255, 20).uv(1.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                    .color(200, 200, 255, 20).uv(1.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                    .color(200, 200, 255, 20).uv(0.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();

            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                    .color(180, 180, 255, 20).uv(0.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                    .color(180, 180, 255, 20).uv(0.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                    .color(180, 180, 255, 20).uv(1.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                    .color(180, 180, 255, 20).uv(1.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();

            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                    .color(180, 180, 255, 20).uv(0.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                    .color(180, 180, 255, 20).uv(0.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                    .color(180, 180, 255, 20).uv(1.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                    .color(180, 180, 255, 20).uv(1.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();
        }

        {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(JerotesRenderType.glowDoubleSided(getBeamLocation()));
            float beamWidth = 0.15f;
            float halfWidth = beamWidth / 2.0f;
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();

            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                    .color(255, 255, 255, 255).uv(0.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                    .color(255, 255, 255, 255).uv(1.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                    .color(255, 255, 255, 255).uv(1.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                    .color(255, 255, 255, 255).uv(0.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();

            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                    .color(255, 255, 255, 255).uv(0.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                    .color(255, 255, 255, 255).uv(1.0f, 1.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                    .color(255, 255, 255, 255).uv(1.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                    .color(255, 255, 255, 255).uv(0.0f, 0.0f)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();

            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                    .color(200, 200, 255, 255).uv(0.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                    .color(200, 200, 255, 255).uv(1.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                    .color(200, 200, 255, 255).uv(1.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                    .color(200, 200, 255, 255).uv(0.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();

            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                    .color(200, 200, 255, 255).uv(0.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                    .color(200, 200, 255, 255).uv(1.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                    .color(200, 200, 255, 255).uv(1.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                    .color(200, 200, 255, 255).uv(0.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();

            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                    .color(180, 180, 255, 255).uv(0.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                    .color(180, 180, 255, 255).uv(0.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                    .color(180, 180, 255, 255).uv(1.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                    .color(180, 180, 255, 255).uv(1.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();

            // 右侧面
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                    .color(180, 180, 255, 255).uv(0.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                    .color(180, 180, 255, 255).uv(0.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                    .color(180, 180, 255, 255).uv(1.0f, uvTop)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();
            vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                    .color(180, 180, 255, 255).uv(1.0f, uvBottom)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                    .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();
        }

        poseStack.popPose();
    }
}
