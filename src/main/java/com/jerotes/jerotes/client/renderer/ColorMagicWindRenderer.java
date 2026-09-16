package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.entity.Other.OtherSpell.ColorMagicWindEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.CureWoundsEntity;
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

public class ColorMagicWindRenderer extends EntityRenderer<ColorMagicWindEntity> {
    public ColorMagicWindRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ColorMagicWindEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn) {
        poseStack.pushPose();
        renderBeam(entity, partialTicks * 2, poseStack, multiBufferSource);
        renderBeamLine(entity, partialTicks * 2, poseStack, multiBufferSource);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
    }

    private void renderBeamLine(ColorMagicWindEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        double width = entity.getSize();
        double height = entity.getTargetHeight();
        int life = entity.life * 2;

        int colorIII = entity.getColorIII();
        int colorIV = entity.getColorIV();
        int rIII = (colorIII >> 16) & 0xFF;
        int gIII = (colorIII >> 8) & 0xFF;
        int bIII = colorIII & 0xFF;
        int rIV = (colorIV >> 16) & 0xFF;
        int gIV = (colorIV >> 8) & 0xFF;
        int bIV = colorIV & 0xFF;

        VertexConsumer consumer = buffer.getBuffer(RenderType.LINES);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        // 以 width=1, height=2 为基准，直接加法调整
        float widthOffset = (float) (width - 1.0);
        float heightOffset = (float) (height - 2.0);

        int numSegments = 6 + (int) (widthOffset * 2 + heightOffset * 1.5);
        numSegments = Mth.clamp(numSegments, 6, 24);
        int pointsPerSegment = 20 + (int) (widthOffset * 2 + heightOffset * 2);
        pointsPerSegment = Mth.clamp(pointsPerSegment, 20, 50);
        float maxHeight = (float) height;

        float riseOffset = (life + partialTick) * 0.025f;
        float baseRotation = (life + partialTick) * 0.20f;

        // 基础半径 = width/2，不再乘以额外系数
        float baseMaxRadius = (float) width * 0.5f;

        for (int s = 0; s < numSegments; s++) {
            Random random = new Random(entity.getId() * 31L + 17L + s * 101L);

            float disappearTime = 30f + (float)(numSegments - 1 - s) * 20f / numSegments;
            if (life >= disappearTime) continue;

            float alphaFactor;
            if (life < 20) alphaFactor = 1.0f;
            else alphaFactor = 1.0f - (life - 20) / (disappearTime - 20);
            alphaFactor = Mth.clamp(alphaFactor, 0.0f, 1.0f);
            if (alphaFactor < 0.01f) continue;

            // 半径：基准半径 + 随时间的扩散
            float radius = baseMaxRadius * (1.0f + (life + partialTick) * 0.025f);
            // 随机缩放：0.3 ~ 2.0（去掉原来的 0.5 + 2.3 * random）
            float randomScale = 0.3f + 1.7f * random.nextFloat();
            float finalRadius = radius * randomScale;

            // Y位置：直接使用高度范围，不再乘以额外系数
            float yBase = (-0.2f + 0.4f * random.nextFloat()) * maxHeight;
            float segHeight = (0.3f + 0.7f * random.nextFloat()) * maxHeight;
            float angleOffset = random.nextFloat() * 2.0f * (float) Math.PI;
            float turns = 1.5f + 2.5f * random.nextFloat();
            float speedFactor = 0.2f + 1.8f * random.nextFloat();

            Vec3[] points = new Vec3[pointsPerSegment + 1];
            for (int i = 0; i <= pointsPerSegment; i++) {
                float progress = (float) i / pointsPerSegment;
                float r = finalRadius * progress;
                float y = yBase + progress * segHeight + riseOffset;
                float angle = angleOffset + progress * turns * 2.0f * (float) Math.PI + baseRotation * speedFactor;
                float x = r * Mth.cos(angle);
                float z = r * Mth.sin(angle);
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

                float alpha0 = alphaFactor * h0 * edgeFade0;
                float alpha1 = alphaFactor * h1 * edgeFade1;
                if (alpha0 < 0.005f && alpha1 < 0.005f) continue;

                int a0 = (int) (255 * alpha0);
                int a1 = (int) (255 * alpha1);

                consumer.vertex(matrix, (float) p0.x, (float) p0.y, (float) p0.z)
                        .color(rIII, gIII, bIII, a0)
                        .uv(0, 0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(normal, 0, 1, 0)
                        .endVertex();
                consumer.vertex(matrix, (float) p1.x, (float) p1.y, (float) p1.z)
                        .color(rIV, gIV, bIV, a1)
                        .uv(0, 0)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(15728880)
                        .normal(normal, 0, 1, 0)
                        .endVertex();
            }
        }
    }

    private void renderBeam(ColorMagicWindEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        double width = entity.getSize();
        double height = entity.getTargetHeight();
        int life = entity.life * 2;

        int colorI = entity.getColorI();
        int colorII = entity.getColorII();
        int rI = (colorI >> 16) & 0xFF;
        int gI = (colorI >> 8) & 0xFF;
        int bI = colorI & 0xFF;
        int rII = (colorII >> 16) & 0xFF;
        int gII = (colorII >> 8) & 0xFF;
        int bII = colorII & 0xFF;

        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(getTextureLocation(entity)));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        // 以 width=1, height=2 为基准，直接加法调整
        float widthOffset = (float) (width - 1.0);
        float heightOffset = (float) (height - 2.0);

        int numSegments = 6 + (int) (widthOffset * 2 + heightOffset * 1.5);
        numSegments = Mth.clamp(numSegments, 6, 24);
        int pointsPerSegment = 20 + (int) (widthOffset * 2 + heightOffset * 2);
        pointsPerSegment = Mth.clamp(pointsPerSegment, 20, 50);
        float maxHeight = (float) height;

        // 宽度：基准 0.04 + 宽度变化 * 0.02（不再用 1.0 + width * 0.08）
        float baseHalfWidth = 0.04f + (float) width * 0.02f;

        float riseOffset = (life + partialTick) * 0.025f;
        float baseRotation = (life + partialTick) * 0.20f;
        float baseMaxRadius = (float) width * 0.5f;

        for (int s = 0; s < numSegments; s++) {
            Random random = new Random(entity.getId() * 31L + 19L + s * 103L);

            float disappearTime = 30f + (float)(numSegments - 1 - s) * 20f / numSegments;
            if (life >= disappearTime) continue;

            float alphaFactor;
            if (life < 20) alphaFactor = 1.0f;
            else alphaFactor = 1.0f - (life - 20) / (disappearTime - 20);
            alphaFactor = Mth.clamp(alphaFactor, 0.0f, 1.0f);
            if (alphaFactor < 0.01f) continue;

            float radius = baseMaxRadius * (1.0f + (life + partialTick) * 0.025f);
            float randomScale = 0.3f + 1.7f * random.nextFloat();
            float finalRadius = radius * randomScale;

            float yBase = (-0.2f + 0.4f * random.nextFloat()) * maxHeight;
            float segHeight = (0.3f + 0.7f * random.nextFloat()) * maxHeight;
            float angleOffset = random.nextFloat() * 2.0f * (float) Math.PI;
            float turns = 1.5f + 2.5f * random.nextFloat();
            float speedFactor = 0.2f + 1.8f * random.nextFloat();

            Vec3[] points = new Vec3[pointsPerSegment + 1];
            for (int i = 0; i <= pointsPerSegment; i++) {
                float progress = (float) i / pointsPerSegment;
                float r = finalRadius * progress;
                float y = yBase + progress * segHeight + riseOffset;
                float angle = angleOffset + progress * turns * 2.0f * (float) Math.PI + baseRotation * speedFactor;
                float x = r * Mth.cos(angle);
                float z = r * Mth.sin(angle);
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

                float alpha0 = alphaFactor * h0 * edgeFade0;
                float alpha1 = alphaFactor * h1 * edgeFade1;
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

                addVertex(consumer, matrix, normal, p0l, rI, gI, bI, a0, u0, v0);
                addVertex(consumer, matrix, normal, p0r, rII, gII, bII, a0, u1, v0);
                addVertex(consumer, matrix, normal, p1r, rII, gII, bII, a1, u1, v1);
                addVertex(consumer, matrix, normal, p1l, rI, gI, bI, a0, u0, v1);
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
    public ResourceLocation getTextureLocation(ColorMagicWindEntity entity) {
        return entity.getTextureLocation();
    }
}