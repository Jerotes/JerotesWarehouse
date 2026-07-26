package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.entity.Other.OtherSpell.LifeMendEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Random;

public class LifeMendRenderer extends EntityRenderer<LifeMendEntity> {
    public LifeMendRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LifeMendEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn) {
        poseStack.pushPose();
        renderBeam(entity, partialTicks * 2, poseStack, multiBufferSource);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
    }

    private void renderBeam(LifeMendEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        double width = entity.getSize();
        double height = entity.getTargetHeight();
        int life = entity.life;

        float totalAlpha = 1.0f;
        if (life < 5) {
            totalAlpha = life / 5.0f;
        } else if (life > 30) {
            totalAlpha = 1.0f - (life - 30) / 20.0f;
        }
        totalAlpha = Mth.clamp(totalAlpha, 0.0f, 1.0f);
        if (totalAlpha < 0.01f) return;

        VertexConsumer faceConsumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(getTextureLocation(entity)));
        VertexConsumer lineConsumer = buffer.getBuffer(RenderType.LINES);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        Random random = new Random(entity.getId() * 31L + 17L);
        int numSegments = 12;
        int pointsPerSegment = 20;
        float maxHeight = (float) height;
        float baseHalfWidth = 0.04f;

        float riseOffset = (life + partialTick) * 0.025f;
        float baseRotation = (life + partialTick) * 0.20f;

        for (int s = 0; s < numSegments; s++) {
            float segmentFadeOffset = random.nextFloat() * 0.3f;
            float maxRadius = (float) width * 0.5f * (1 + (life + partialTick) * 0.025f);
            float radiusBase = (0.5f + 2.3f * random.nextFloat()) * maxRadius;
            float yBase = (-0.5f + 0.2f * random.nextFloat()) * maxHeight;
            float segHeight = (0.6f + 0.4f * random.nextFloat()) * maxHeight;
            float angleOffset = random.nextFloat() * 2.0f * (float) Math.PI;
            float turns = 1.5f + 2.5f * random.nextFloat();
            float speedFactor = 0.2f + 1.8f * random.nextFloat();

            Vec3[] points = new Vec3[pointsPerSegment + 1];
            for (int i = 0; i <= pointsPerSegment; i++) {
                float progress = (float) i / pointsPerSegment;
                float radius = radiusBase * progress;
                float y = yBase + progress * segHeight + riseOffset;
                float angle = angleOffset + progress * turns * 2.0f * (float) Math.PI + baseRotation * speedFactor;
                float x = radius * Mth.cos(angle);
                float z = radius * Mth.sin(angle);
                points[i] = new Vec3(x, y, z);
            }

            for (int i = 0; i < points.length - 1; i++) {
                Vec3 p0 = points[i];
                Vec3 p1 = points[i + 1];
                double y0 = p0.y, y1 = p1.y;

                if (y0 < 0 && y1 < 0) continue;
                if (y0 > maxHeight && y1 > maxHeight) continue;

                float h0 = (float) Mth.clamp(y0 / maxHeight, 0.0, 1.0);
                float h1 = (float) Mth.clamp(y1 / maxHeight, 0.0, 1.0);

                float edgeFade0 = 1.0f;
                if (y0 < 0.1 * maxHeight) edgeFade0 = (float) (y0 / (0.1 * maxHeight));
                if (y0 > 0.9 * maxHeight) edgeFade0 = (float) ((maxHeight - y0) / (0.1 * maxHeight));
                float edgeFade1 = 1.0f;
                if (y1 < 0.1 * maxHeight) edgeFade1 = (float) (y1 / (0.1 * maxHeight));
                if (y1 > 0.9 * maxHeight) edgeFade1 = (float) ((maxHeight - y1) / (0.1 * maxHeight));

                float baseAlpha0 = totalAlpha * h0 * edgeFade0;
                float baseAlpha1 = totalAlpha * h1 * edgeFade1;
                float fadeFactor0 = 1.0f;
                float fadeFactor1 = 1.0f;
                if (life > 30) {
                    float localFade = (life - 30) / 20.0f;
                    float offsetFade = Math.max(0, localFade - segmentFadeOffset);
                    fadeFactor0 = 1.0f - offsetFade;
                    fadeFactor1 = 1.0f - offsetFade;
                }
                float alpha0 = Mth.clamp(baseAlpha0 * fadeFactor0, 0, 1);
                float alpha1 = Mth.clamp(baseAlpha1 * fadeFactor1, 0, 1);
                if (alpha0 < 0.005f && alpha1 < 0.005f) continue;

                float w0 = baseHalfWidth * (0.2f + 0.8f * h0);
                float w1 = baseHalfWidth * (0.2f + 0.8f * h1);

                Vec3 offset0 = new Vec3(0, w0, 0);
                Vec3 offset1 = new Vec3(0, w1, 0);
                Vec3 p0l = p0.subtract(offset0);
                Vec3 p0r = p0.add(offset0);
                Vec3 p1l = p1.subtract(offset1);
                Vec3 p1r = p1.add(offset1);

                float u0 = 0, u1 = 1;
                float v0 = (float) i / pointsPerSegment;
                float v1 = (float) (i + 1) / pointsPerSegment;

                int a0 = (int) (255 * alpha0);
                int a1 = (int) (255 * alpha1);

                int r0 = (int) Mth.lerp(h0, 201, 133);;
                int g0 = (int) Mth.lerp(h0, 234, 207);
                int b0 = (int) Mth.lerp(h0, 255, 255);
                int r1 = (int) Mth.lerp(h1, 201, 133);
                int g1 = (int) Mth.lerp(h1, 234, 207);
                int b1 = (int) Mth.lerp(h1, 255, 255);

                addVertex(faceConsumer, matrix, normal, p0l, r0, g0, b0, a0, u0, v0);
                addVertex(faceConsumer, matrix, normal, p0r, r0, g0, b0, a0, u1, v0);
                addVertex(faceConsumer, matrix, normal, p1r, r1, g1, b1, a1, u1, v1);
                addVertex(faceConsumer, matrix, normal, p1l, r1, g1, b1, a1, u0, v1);

                lineConsumer.vertex(matrix, (float) p0.x, (float) p0.y, (float) p0.z)
                        .color(r0, g0, b0, a0)
                        .uv(0, 0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(normal, 0, 1, 0)
                        .endVertex();
                lineConsumer.vertex(matrix, (float) p1.x, (float) p1.y, (float) p1.z)
                        .color(r1, g1, b1, a1)
                        .uv(0, 0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(normal, 0, 1, 0)
                        .endVertex();
            }
        }
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                           Vec3 pos, int r, int g, int b, int a,
                           float u, float v) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(LifeMendEntity entity) {
        return entity.getTextureLocation();
    }
}