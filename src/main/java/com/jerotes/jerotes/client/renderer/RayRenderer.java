package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.BaseRayEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class RayRenderer<T extends BaseRayEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");
    private final boolean fullBright;

    public RayRenderer(EntityRendererProvider.Context context, float f, boolean bl) {
        super(context);
        this.fullBright = bl;
    }

    public RayRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0f, true);
    }

    @Override
    protected int getBlockLightLevel(T t, BlockPos blockPos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(t, blockPos);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.showBeam() && !entity.isFirst()) {
            poseStack.pushPose();
            renderBeam(entity, partialTick, poseStack, buffer, LightTexture.FULL_BRIGHT);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderBall(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, float beamLength) {
        if (!entity.isHitUseful() || entity.isUseful()) {
            return;
        }
        float lifeBomb = entity.getLifeRayLast()+partialTick;
        lifeBomb = Mth.clamp(lifeBomb, 1.0f, 15.0f);

        float start = 0.0f;
        float growEnd = 4.0f;
        float plateauEnd = 9.0f;
        float fadeEnd = 13.0f;

        float progress = 0;
        float fade = 0;
        if (lifeBomb <= start) {
            progress = 0;
        } else if (lifeBomb <= growEnd) {
            progress = (lifeBomb - start) / (growEnd - start);
        } else if (lifeBomb <= plateauEnd) {
            progress = 1;
        } else {
            progress = 1 - (lifeBomb - plateauEnd) / (fadeEnd - plateauEnd);
            fade = (lifeBomb - plateauEnd) / (fadeEnd - plateauEnd);
        }
        progress = Mth.clamp(progress, 0, 1);
        fade = Mth.clamp(fade, 0, 1);

        float growProgress = progress;
        float fadeAlpha = 1 - fade;

        int color;
        if (fade <= 0) {
            color = entity.beamLightII();
        } else {
            int innerR = (entity.beamLightI() >> 16) & 0xFF;
            int innerG = (entity.beamLightI() >> 8) & 0xFF;
            int innerB = entity.beamLightI() & 0xFF;
            float r = innerR / 255F - fade * 0.35f;
            float g = innerG / 255F - fade * 0.35f;
            float b = innerB / 255F - fade;
            r = Math.max(0, Math.min(1, r));
            g = Math.max(0, Math.min(1, g));
            b = Math.max(0, Math.min(1, b));
            color = ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
        }

        if (fadeAlpha < 0.01f) return;

        float maxRadius = 6.0f;
        float radius = maxRadius * growProgress;

        float coverage = 0.3f;
        float maxPhi = (float)Math.acos(1.0 - coverage);
        float centerOffset = radius * (float)Math.cos(maxPhi);

        poseStack.pushPose();
        poseStack.translate(0, 0, beamLength + centerOffset + 2.5f);
        poseStack.mulPose(Axis.XP.rotation(-(float)Math.PI / 2));

        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningAdd(
                new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMat = pose.normal();

        int cr = (color >> 16) & 0xFF;
        int cg = (color >> 8) & 0xFF;
        int cb = color & 0xFF;
        int baseAlpha = (int)(255 * fadeAlpha);

        int segments = 24;
        int stacks = 16;

        float time = entity.getLifeRay() + partialTick;
        float pulseAmplitude = 0.2f;
        float pulseSpeed = 0.5f;

        for (int i = 0; i < stacks; i++) {
            float phi1 = (float)i / stacks * maxPhi;
            float phi2 = (float)(i + 1) / stacks * maxPhi;
            for (int j = 0; j < segments; j++) {
                float theta1 = (float)j / segments * 2.0f * (float)Math.PI;
                float theta2 = (float)(j + 1) / segments * 2.0f * (float)Math.PI;

                float x1 = radius * (float)Math.sin(phi1) * (float)Math.cos(theta1);
                float y1 = radius * (float)Math.cos(phi1);
                float z1 = radius * (float)Math.sin(phi1) * (float)Math.sin(theta1);

                float x2 = radius * (float)Math.sin(phi1) * (float)Math.cos(theta2);
                float y2 = radius * (float)Math.cos(phi1);
                float z2 = radius * (float)Math.sin(phi1) * (float)Math.sin(theta2);

                float x3 = radius * (float)Math.sin(phi2) * (float)Math.cos(theta2);
                float y3 = radius * (float)Math.cos(phi2);
                float z3 = radius * (float)Math.sin(phi2) * (float)Math.sin(theta2);

                float x4 = radius * (float)Math.sin(phi2) * (float)Math.cos(theta1);
                float y4 = radius * (float)Math.cos(phi2);
                float z4 = radius * (float)Math.sin(phi2) * (float)Math.sin(theta1);

                Vec3 n1 = new Vec3(x1, y1, z1).normalize();
                Vec3 n2 = new Vec3(x2, y2, z2).normalize();
                Vec3 n3 = new Vec3(x3, y3, z3).normalize();
                Vec3 n4 = new Vec3(x4, y4, z4).normalize();

                float pulse = pulseAmplitude * (float)Math.sin(time * pulseSpeed);
                float baseFactor = 0.8f;
                float alphaFactor1 = 1.0f - (phi1 / maxPhi) * (baseFactor + pulse);
                float alphaFactor2 = 1.0f - (phi2 / maxPhi) * (baseFactor + pulse);
                alphaFactor1 = Mth.clamp(alphaFactor1, 0.0f, 1.0f);
                alphaFactor2 = Mth.clamp(alphaFactor2, 0.0f, 1.0f);

                int a1 = (int)(baseAlpha * alphaFactor1);
                int a2 = (int)(baseAlpha * alphaFactor2);

                addVertexBall(consumer, matrix, normalMat, x1, y1, z1, cr, cg, cb, a1, 0, 0, n1);
                addVertexBall(consumer, matrix, normalMat, x2, y2, z2, cr, cg, cb, a1, 1, 0, n2);
                addVertexBall(consumer, matrix, normalMat, x3, y3, z3, cr, cg, cb, a2, 1, 1, n3);
                addVertexBall(consumer, matrix, normalMat, x4, y4, z4, cr, cg, cb, a2, 0, 1, n4);
            }
        }

        poseStack.popPose();
    }
    private void addVertexBall(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMat,
                           float x, float y, float z, int r, int g, int b, int a,
                           float u, float v, Vec3 normal) {
        consumer.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normalMat, (float)normal.x, (float)normal.y, (float)normal.z)
                .endVertex();
    }

    private void renderBeam(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        Vec3 renderPos = entity.getPosition(partialTick);

        Vec3 startWorld = new Vec3(entity.getLastX(), entity.getLastY(), entity.getLastZ());
        Vec3 endWorld = renderPos;

        double raiseY = entity.getBbHeight() / 3.0;

        Vec3 startLocal = startWorld.subtract(renderPos).add(0.0, raiseY, 0.0);
        Vec3 endLocal = new Vec3(0.0, raiseY, 0.0);

        Vec3 dirLocal = endLocal.subtract(startLocal);
        float len = (float)dirLocal.length();
        if (len < 0.001F) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(startLocal.x, startLocal.y, startLocal.z);

        float horizontalDistance = Mth.sqrt((float)(dirLocal.x * dirLocal.x + dirLocal.z * dirLocal.z));

        float yaw;
        float pitch;

        if (horizontalDistance < 1.0E-4F) {
            yaw = 0.0F;
            pitch = dirLocal.y > 0.0 ? -(float)Math.PI / 2F : (float)Math.PI / 2F;
        } else {
            yaw = (float)Math.atan2(dirLocal.x, dirLocal.z);
            pitch = (float)-Math.atan2(dirLocal.y, horizontalDistance);
        }

        poseStack.mulPose(Axis.YP.rotation(yaw));
        poseStack.mulPose(Axis.XP.rotation(pitch));

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        int innerR = (entity.beamLightI() >> 16) & 0xFF;
        int innerG = (entity.beamLightI() >> 8) & 0xFF;
        int innerB = entity.beamLightI() & 0xFF;
        int outerR = (entity.beamLightII() >> 16) & 0xFF;
        int outerG = (entity.beamLightII() >> 8) & 0xFF;
        int outerB = entity.beamLightII() & 0xFF;
        float half = entity.getMaxLife() / 2.0f;
        float lifeRay = entity.getLifeRay() + partialTick;
        float lifeAbout = Mth.clamp((lifeRay - half) / half, 0.0f, 1.0f);
        float alpha = 1 - Math.max(0, 3 - lifeRay)/3f;
        int innerAlpha = (int) (255 * alpha);
        int outerAlpha = (int) (180 * alpha);

        float baseScale;
        if (lifeAbout <= 5f) {
            float p = lifeAbout / 5f;
            float smooth = p * p * (3f - 2f * p);
            baseScale = 1f + 1.5f * (1f - smooth);
        } else if (lifeAbout <= 15f) {
            float p = (lifeAbout - 5f) / 10f;
            float smooth = p * p * (3f - 2f * p);
            baseScale = 1f + 0.125f * smooth;
        } else if (lifeAbout <= 25f) {
            baseScale = 1.0f;
        } else {
            baseScale = 1.0f;
        }
        float scale = entity.beamScale() * (1- lifeAbout) * baseScale;

        float outerWidth = 0.065f * scale;
        float innerWidth = 0.055f * scale;
        float halfOuter = outerWidth / 2.0f;
        float halfInner = innerWidth / 2.0f;

        VertexConsumer consumer3 = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningAdd(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
        renderQuadrilateralPipeAlpha(consumer3, matrix, normal, len, halfOuter * 2f, outerR, outerG, outerB, (int) (outerAlpha * 0.45f), packedLight, false);
        VertexConsumer consumer2 = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightning(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
        renderQuadrilateralPipe(consumer2, matrix, normal, len, halfOuter, outerR, outerG, outerB, outerAlpha, packedLight, false);
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightning(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
        renderQuadrilateralPipe(consumer, matrix, normal, len, halfInner, innerR, innerG, innerB, innerAlpha, packedLight, false);
        renderSpiralWrap(entity, partialTick, poseStack, buffer, packedLight, len, halfOuter);
        if (entity.isHitUseful() && !entity.isUseful()) {
            renderBall(entity, partialTick, poseStack, buffer, len);
        }
        poseStack.popPose();
    }
    private void renderSpiralWrap(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float length, float outerRadius) {
        if (length < 0.001f || outerRadius < 0.001f) return;

        float life = entity.getLifeRay();
        float maxLife = entity.getMaxLife();
        float progressLife = Mth.clamp(life / maxLife, 0f, 1f);

        int segments = 18;
        float step = 1.0f / segments;
        float rayCountBase = entity.getRayCount();
        float drillSpeed = 0.2f;
        float maxTwist = 2.0f * (float)Math.PI;

        float wrapRadius = outerRadius + outerRadius * (Math.min(16, 1 + rayCountBase * rayCountBase * 0.15f));

        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        Vec3[] leftPoints = new Vec3[segments + 1];
        Vec3[] rightPoints = new Vec3[segments + 1];
        float[] alphas = new float[segments + 1];

        for (int i = 0; i <= segments; i++) {
            float progress = (float) i / segments;
            float z = progress * length;

            float offset = (rayCountBase + i) * step;
            float angle = life * drillSpeed + progress * maxTwist + offset * maxTwist;
            float cos = Mth.cos(angle);
            float sin = Mth.sin(angle);

            float currentRadius = wrapRadius;

            float alpha = 0.8f * (1f - progressLife * 0.6f) * (1f - Math.abs(progress - 0.5f) * 0.5f);
            alpha = Math.max(0f, Math.min(1f, alpha));

            Vec3 offsetVec = new Vec3(cos * currentRadius, sin * currentRadius, 0);
            Vec3 point = new Vec3(0, 0, z).add(offsetVec);

            float bandWidth = 0.025f * (1f - progressLife * 0.5f);
            Vec3 tangent = new Vec3(-sin, cos, 0).normalize();
            leftPoints[i] = point.add(tangent.scale(-bandWidth));
            rightPoints[i] = point.add(tangent.scale(bandWidth));
            alphas[i] = alpha;
        }

        int colorI = entity.beamLightI();
        int r = (colorI >> 16) & 0xFF;
        int g = (colorI >> 8) & 0xFF;
        int b = colorI & 0xFF;

        for (int i = 0; i < segments; i++) {
            Vec3 p0l = leftPoints[i];
            Vec3 p0r = rightPoints[i];
            Vec3 p1l = leftPoints[i+1];
            Vec3 p1r = rightPoints[i+1];

            int a0 = (int)(alphas[i] * 250);
            int a1 = (int)(alphas[i+1] * 250);

            VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightning(
                    new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
            addVertex(consumer, matrix, normal, p0l, r, g, b, a0, 0, 0);
            addVertex(consumer, matrix, normal, p0r, r, g, b, a0, 1, 0);
            addVertex(consumer, matrix, normal, p1r, r, g, b, a1, 1, 1);
            addVertex(consumer, matrix, normal, p1l, r, g, b, a1, 0, 1);
        }
    }
    private void renderQuadrilateralPipeAlpha(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float distance, float halfWidth, int r, int g, int b, int a, int light, boolean bl) {
        float alpha = a / 255f;
        r = (int)(r * alpha);
        g = (int)(g * alpha);
        b = (int)(b * alpha);
        if (bl) {
            consumer.vertex(matrix, -halfWidth, -halfWidth, distance).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(matrix, halfWidth, -halfWidth, distance).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(matrix, halfWidth, halfWidth, distance).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(matrix, -halfWidth, halfWidth, distance).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();

            consumer.vertex(matrix, -halfWidth, halfWidth, 0).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(matrix, halfWidth, halfWidth, 0).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(matrix, halfWidth, -halfWidth, 0).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(matrix, -halfWidth, -halfWidth, 0).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
        }
        consumer.vertex(matrix, -halfWidth,  halfWidth, 0).color(r,g,b,a).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,1,0).endVertex();
        consumer.vertex(matrix,  halfWidth,  halfWidth, 0).color(r,g,b,a).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,1,0).endVertex();
        consumer.vertex(matrix,  halfWidth,  halfWidth, distance).color(r,g,b,a).uv(1,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,1,0).endVertex();
        consumer.vertex(matrix, -halfWidth,  halfWidth, distance).color(r,g,b,a).uv(0,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,1,0).endVertex();

        consumer.vertex(matrix, -halfWidth, -halfWidth, distance).color(r,g,b,a).uv(0,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,-1,0).endVertex();
        consumer.vertex(matrix,  halfWidth, -halfWidth, distance).color(r,g,b,a).uv(1,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,-1,0).endVertex();
        consumer.vertex(matrix,  halfWidth, -halfWidth, 0).color(r,g,b,a).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,-1,0).endVertex();
        consumer.vertex(matrix, -halfWidth, -halfWidth, 0).color(r,g,b,a).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,-1,0).endVertex();

        consumer.vertex(matrix, -halfWidth, -halfWidth, distance).color(r,g,b,a).uv(0,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1,0,0).endVertex();
        consumer.vertex(matrix, -halfWidth, -halfWidth, 0).color(r,g,b,a).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1,0,0).endVertex();
        consumer.vertex(matrix, -halfWidth,  halfWidth, 0).color(r,g,b,a).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1,0,0).endVertex();
        consumer.vertex(matrix, -halfWidth,  halfWidth, distance).color(r,g,b,a).uv(1,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1,0,0).endVertex();

        consumer.vertex(matrix,  halfWidth, -halfWidth, 0).color(r,g,b,a).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1,0,0).endVertex();
        consumer.vertex(matrix,  halfWidth, -halfWidth, distance).color(r,g,b,a).uv(0,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1,0,0).endVertex();
        consumer.vertex(matrix,  halfWidth,  halfWidth, distance).color(r,g,b,a).uv(1,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1,0,0).endVertex();
        consumer.vertex(matrix,  halfWidth,  halfWidth, 0).color(r,g,b,a).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1,0,0).endVertex();
    }
    private void renderQuadrilateralPipe(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float distance, float halfWidth, int r, int g, int b, int a, int light, boolean bl) {
        if (bl) {
            consumer.vertex(matrix, -halfWidth, -halfWidth, distance).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(matrix, halfWidth, -halfWidth, distance).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(matrix, halfWidth, halfWidth, distance).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();
            consumer.vertex(matrix, -halfWidth, halfWidth, distance).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, 1).endVertex();

            consumer.vertex(matrix, -halfWidth, halfWidth, 0).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(matrix, halfWidth, halfWidth, 0).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(matrix, halfWidth, -halfWidth, 0).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
            consumer.vertex(matrix, -halfWidth, -halfWidth, 0).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0, 0, -1).endVertex();
        }
        consumer.vertex(matrix, -halfWidth,  halfWidth, 0).color(r,g,b,a).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,1,0).endVertex();
        consumer.vertex(matrix,  halfWidth,  halfWidth, 0).color(r,g,b,a).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,1,0).endVertex();
        consumer.vertex(matrix,  halfWidth,  halfWidth, distance).color(r,g,b,a).uv(1,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,1,0).endVertex();
        consumer.vertex(matrix, -halfWidth,  halfWidth, distance).color(r,g,b,a).uv(0,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,1,0).endVertex();

        consumer.vertex(matrix, -halfWidth, -halfWidth, distance).color(r,g,b,a).uv(0,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,-1,0).endVertex();
        consumer.vertex(matrix,  halfWidth, -halfWidth, distance).color(r,g,b,a).uv(1,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,-1,0).endVertex();
        consumer.vertex(matrix,  halfWidth, -halfWidth, 0).color(r,g,b,a).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,-1,0).endVertex();
        consumer.vertex(matrix, -halfWidth, -halfWidth, 0).color(r,g,b,a).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0,-1,0).endVertex();

        consumer.vertex(matrix, -halfWidth, -halfWidth, distance).color(r,g,b,a).uv(0,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1,0,0).endVertex();
        consumer.vertex(matrix, -halfWidth, -halfWidth, 0).color(r,g,b,a).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1,0,0).endVertex();
        consumer.vertex(matrix, -halfWidth,  halfWidth, 0).color(r,g,b,a).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1,0,0).endVertex();
        consumer.vertex(matrix, -halfWidth,  halfWidth, distance).color(r,g,b,a).uv(1,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, -1,0,0).endVertex();

        consumer.vertex(matrix,  halfWidth, -halfWidth, 0).color(r,g,b,a).uv(0,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1,0,0).endVertex();
        consumer.vertex(matrix,  halfWidth, -halfWidth, distance).color(r,g,b,a).uv(0,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1,0,0).endVertex();
        consumer.vertex(matrix,  halfWidth,  halfWidth, distance).color(r,g,b,a).uv(1,1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1,0,0).endVertex();
        consumer.vertex(matrix,  halfWidth,  halfWidth, 0).color(r,g,b,a).uv(1,0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 1,0,0).endVertex();
    }
    private void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vec3 pos, int r, int g, int b, int a, float u, float v) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }
    @Override
    public ResourceLocation getTextureLocation(BaseRayEntity tex) {
        return TEXTURE_LOCATION;
    }
}