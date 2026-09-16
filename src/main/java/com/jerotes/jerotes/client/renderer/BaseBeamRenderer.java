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
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
public class BaseBeamRenderer<T extends BaseBeamEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");

    public BaseBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    protected float getBeamWidthMultiplier(BaseBeamEntity baseBeamEntity) {
        return baseBeamEntity.getBeamScale();
    }

    @Override
    public void render(T t, float f, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        float targetX = t.getTargetX();
        float targetY = t.getTargetY();
        float targetZ = t.getTargetZ();

        float selfX = (float) t.getX();
        float selfY = (float) t.getY();
        float selfZ = (float) t.getZ();

        if (!t.renderInitialized || t.life < 10) {
            t.selfXProgress = selfX;
            t.selfYProgress = selfY;
            t.selfZProgress = selfZ;

            t.targetXProgress = targetX;
            t.targetYProgress = targetY;
            t.targetZProgress = targetZ;

            t.renderInitialized = true;
        } else {
            t.selfXProgress = Mth.lerp(1.0f, t.selfXProgress, selfX);
            t.selfYProgress = Mth.lerp(1.0f, t.selfYProgress, selfY);
            t.selfZProgress = Mth.lerp(1.0f, t.selfZProgress, selfZ);

            float targetSmooth = 0.35f;

            t.targetXProgress = Mth.lerp(targetSmooth, t.targetXProgress, targetX);
            t.targetYProgress = Mth.lerp(targetSmooth, t.targetYProgress, targetY);
            t.targetZProgress = Mth.lerp(targetSmooth, t.targetZProgress, targetZ);
        }

        if (t.tickCount <= 3) {
            return;
        }

        float startX = Mth.lerp(partialTick, (float) t.xo, t.selfXProgress);
        float startY = Mth.lerp(partialTick, (float) t.yo, t.selfYProgress);
        float startZ = Mth.lerp(partialTick, (float) t.zo, t.selfZProgress);

        float f13 = t.targetXProgress - startX;
        float f14 = t.targetYProgress - startY;
        float f15 = t.targetZProgress - startZ;

        poseStack.pushPose();
        renderLightLock(t, f13, f14, f15, partialTick, t.tickCount, poseStack, buffer, light);
        poseStack.popPose();

        super.render(t, f, partialTick, poseStack, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(BaseBeamEntity tex) {
        return TEXTURE_LOCATION;
    }

    public ResourceLocation getBeamLocation(BaseBeamEntity baseBeamEntity) {
        return baseBeamEntity.getBeamLocation();
    }

    private static Quaternionf rotationBetween(Vector3f from, Vector3f to) {
        Vector3f f = new Vector3f(from).normalize();
        Vector3f t = new Vector3f(to).normalize();
        float dot = f.dot(t);

        if (dot > 0.999999f) {
            return new Quaternionf();
        }

        if (dot < -0.999999f) {
            Vector3f axis = new Vector3f(1.0f, 0.0f, 0.0f).cross(f);
            if (axis.lengthSquared() < 1.0E-6f) {
                axis = new Vector3f(0.0f, 1.0f, 0.0f).cross(f);
            }
            axis.normalize();
            return new Quaternionf().fromAxisAngleRad(axis.x(), axis.y(), axis.z(), (float) Math.PI);
        }

        return new Quaternionf().rotationTo(f, t);
    }

    public void renderLightLock(BaseBeamEntity baseBeamEntity, float f, float f2, float f3, float f4, int n, PoseStack poseStack, MultiBufferSource multiBufferSource, int n2) {
        float totalDistance = Mth.sqrt(f * f + f2 * f2 + f3 * f3);

        poseStack.pushPose();
        poseStack.translate(0.0f, 0.0f, 0.0f);

        Vector3f dir = new Vector3f(f, f2, f3);
        if (dir.lengthSquared() > 1.0E-6f) {
            dir.normalize();
            Vector3f zAxis = new Vector3f(0.0f, 0.0f, 1.0f);
            Quaternionf rot = rotationBetween(zAxis, dir);
            poseStack.mulPose(rot);
        }

        final Vector3f FIXED_NORMAL = new Vector3f(0.0f, 0.0f, 1.0f);
        final float widthMul = getBeamWidthMultiplier(baseBeamEntity);
        {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(JerotesRenderType.glowDoubleSidedLightning(getBeamLocation(baseBeamEntity)));
            float uvBottom = 0.0f - ((float) n + f4) * 0.01f;
            float uvTop = totalDistance / 32.0f - ((float) n + f4) * 0.01f;

            float beamWidth = 0.15f * widthMul;
            float halfWidth = beamWidth / 2.0f;

            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, -halfWidth, totalDistance, 0.0f, 0.0f,
                    halfWidth, -halfWidth, totalDistance, 1.0f, 0.0f,
                    halfWidth, halfWidth, totalDistance, 1.0f, 1.0f,
                    -halfWidth, halfWidth, totalDistance, 0.0f, 1.0f,
                    255, 255, 255, 20);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, halfWidth, 0.0f, 0.0f, 1.0f,
                    halfWidth, halfWidth, 0.0f, 1.0f, 1.0f,
                    halfWidth, -halfWidth, 0.0f, 1.0f, 0.0f,
                    -halfWidth, -halfWidth, 0.0f, 0.0f, 0.0f,
                    255, 255, 255, 20);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, halfWidth, 0.0f, 0.0f, uvBottom,
                    halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
                    halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
                    -halfWidth, halfWidth, totalDistance, 0.0f, uvTop,
                    200, 200, 255, 20);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
                    halfWidth, -halfWidth, totalDistance, 1.0f, uvTop,
                    halfWidth, -halfWidth, 0.0f, 1.0f, uvBottom,
                    -halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
                    200, 200, 255, 20);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
                    -halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
                    -halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
                    -halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
                    180, 180, 255, 20);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
                    halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
                    halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
                    halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
                    180, 180, 255, 20);
        }
        {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(JerotesRenderType.glowDoubleSided(getBeamLocation(baseBeamEntity)));
            float uvBottom = 0.0f - ((float) n + f4) * 0.01f;
            float uvTop = totalDistance / 32.0f - ((float) n + f4) * 0.01f;

            float beamWidth = 0.15f * widthMul;
            float halfWidth = beamWidth / 2.0f;

            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, -halfWidth, totalDistance, 0.0f, 0.0f,
                    halfWidth, -halfWidth, totalDistance, 1.0f, 0.0f,
                    halfWidth, halfWidth, totalDistance, 1.0f, 1.0f,
                    -halfWidth, halfWidth, totalDistance, 0.0f, 1.0f,
                    255, 255, 255, 255);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, halfWidth, 0.0f, 0.0f, 1.0f,
                    halfWidth, halfWidth, 0.0f, 1.0f, 1.0f,
                    halfWidth, -halfWidth, 0.0f, 1.0f, 0.0f,
                    -halfWidth, -halfWidth, 0.0f, 0.0f, 0.0f,
                    255, 255, 255, 255);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, halfWidth, 0.0f, 0.0f, uvBottom,
                    halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
                    halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
                    -halfWidth, halfWidth, totalDistance, 0.0f, uvTop,
                    200, 200, 255, 255);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
                    halfWidth, -halfWidth, totalDistance, 1.0f, uvTop,
                    halfWidth, -halfWidth, 0.0f, 1.0f, uvBottom,
                    -halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
                    200, 200, 255, 255);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    -halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
                    -halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
                    -halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
                    -halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
                    180, 180, 255, 255);

            addQuad(vertexConsumer, matrix4f, matrix3f, FIXED_NORMAL, n2,
                    halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
                    halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
                    halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
                    halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
                    180, 180, 255, 255);
        }

        if (totalDistance > 0.05f) {
            ResourceLocation tex = getBeamLocation(baseBeamEntity);
            float time = n + f4;
            int seed = baseBeamEntity.getId();

            renderEnergyRings(tex, time, seed, poseStack, multiBufferSource, n2, totalDistance, widthMul);
            renderPulseStreaks(tex, time, seed, poseStack, multiBufferSource, n2, totalDistance, widthMul);
        }

        poseStack.popPose();
    }

    private void renderEnergyRings(ResourceLocation tex, float time, int seed,
                                   PoseStack poseStack, MultiBufferSource buffer, int light, float totalDistance, float widthMul) {
        int ringCount = Math.max(2, (int) (totalDistance / 1.2f));
        int segmentsPerRing = 10;
        float ringRadius = 0.11f * widthMul;
        float spinSpeed = 0.5f;

        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightning(tex));

        for (int r = 0; r < ringCount; r++) {
            float z = totalDistance * ((r + 0.5f) / ringCount);
            float ringSpin = time * spinSpeed * (r % 2 == 0 ? 1.0f : -1.0f) + seed * 0.3f + r * 0.9f;
            float ringWidth = 0.02f * widthMul;

            int alpha = (int) Mth.clamp(180 * (0.6f + 0.4f * Mth.sin(time * 0.3f + r * 1.3f)), 30f, 200f);

            for (int i = 0; i < segmentsPerRing; i++) {
                float a0 = ringSpin + (float) (i * 2 * Math.PI / segmentsPerRing);
                float a1 = ringSpin + (float) ((i + 1) * 2 * Math.PI / segmentsPerRing);

                float x0 = Mth.cos(a0) * ringRadius;
                float y0 = Mth.sin(a0) * ringRadius;
                float x1 = Mth.cos(a1) * ringRadius;
                float y1 = Mth.sin(a1) * ringRadius;

                float nx = -Mth.sin(a0) * ringWidth;
                float ny = Mth.cos(a0) * ringWidth;

                addVertex(consumer, matrix, normal, new Vec3(x0 - nx, y0 - ny, z - ringWidth), 255, 255, 255, alpha, 0, 0, light);
                addVertex(consumer, matrix, normal, new Vec3(x1 - nx, y1 - ny, z - ringWidth), 255, 255, 255, alpha, 1, 0, light);
                addVertex(consumer, matrix, normal, new Vec3(x1 + nx, y1 + ny, z + ringWidth), 255, 255, 255, alpha, 1, 1, light);
                addVertex(consumer, matrix, normal, new Vec3(x0 + nx, y0 + ny, z + ringWidth), 255, 255, 255, alpha, 0, 1, light);
            }
        }
    }

    private void renderPulseStreaks(ResourceLocation tex, float time, int seed,
                                    PoseStack poseStack, MultiBufferSource buffer, int light, float totalDistance, float widthMul) {
        int streakCount = 4;
        float streakLength = Math.min(0.9f, totalDistance * 0.35f);
        float pulseSpeed = 0.9f;
        float streakHalfWidth = 0.045f * widthMul;

        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightning(tex));

        for (int i = 0; i < streakCount; i++) {
            float phase = hash(seed * 3.1f + i * 7.7f);
            float travel = ((time * pulseSpeed * 0.15f) + phase) % 1.0f;
            float centerZ = travel * totalDistance;

            float angle = (float) (i * Math.PI / 2) + seed * 0.2f;
            float x = Mth.cos(angle) * 0.07f * widthMul;
            float y = Mth.sin(angle) * 0.07f * widthMul;

            float z0 = Mth.clamp(centerZ - streakLength * 0.5f, 0, totalDistance);
            float z1 = Mth.clamp(centerZ + streakLength * 0.5f, 0, totalDistance);
            if (z1 - z0 < 0.02f) continue;

            float fade = Mth.sin((float) Math.PI * travel);
            int alpha = (int) Mth.clamp(220 * fade, 20f, 220f);

            addVertex(consumer, matrix, normal, new Vec3(x - streakHalfWidth, y, z0), 255, 255, 255, alpha, 0, 0, light);
            addVertex(consumer, matrix, normal, new Vec3(x + streakHalfWidth, y, z0), 255, 255, 255, alpha, 1, 0, light);
            addVertex(consumer, matrix, normal, new Vec3(x + streakHalfWidth, y, z1), 255, 255, 255, alpha, 1, 1, light);
            addVertex(consumer, matrix, normal, new Vec3(x - streakHalfWidth, y, z1), 255, 255, 255, alpha, 0, 1, light);
        }
    }

    private static void addQuad(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vector3f fixedNormal, int light,
                                float x0, float y0, float z0, float u0, float v0,
                                float x1, float y1, float z1, float u1, float v1,
                                float x2, float y2, float z2, float u2, float v2,
                                float x3, float y3, float z3, float u3, float v3,
                                int r, int g, int b, int a) {
        emitQuad(consumer, matrix, normal, fixedNormal, light,
                x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3,
                r, g, b, a);
        emitQuad(consumer, matrix, normal, fixedNormal, light,
                x0, y0, z0, u0, v0, x3, y3, z3, u3, v3, x2, y2, z2, u2, v2, x1, y1, z1, u1, v1,
                r, g, b, a);
    }

    private static void emitQuad(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vector3f fixedNormal, int light,
                                 float x0, float y0, float z0, float u0, float v0,
                                 float x1, float y1, float z1, float u1, float v1,
                                 float x2, float y2, float z2, float u2, float v2,
                                 float x3, float y3, float z3, float u3, float v3,
                                 int r, int g, int b, int a) {
        consumer.vertex(matrix, x0, y0, z0).color(r, g, b, a).uv(u0, v0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(normal, fixedNormal.x, fixedNormal.y, fixedNormal.z).endVertex();
        consumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(u1, v1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(normal, fixedNormal.x, fixedNormal.y, fixedNormal.z).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u2, v2)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(normal, fixedNormal.x, fixedNormal.y, fixedNormal.z).endVertex();
        consumer.vertex(matrix, x3, y3, z3).color(r, g, b, a).uv(u3, v3)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(normal, fixedNormal.x, fixedNormal.y, fixedNormal.z).endVertex();
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vec3 pos,
                                  int r, int g, int b, int a, float u, float v, int light) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(normal, 0.0f, 0.0f, 1.0f)
                .endVertex();
    }

    private static float hash(float seed) {
        float x = Mth.sin(seed * 12.9898f) * 43758.5453f;
        return x - Mth.floor(x);
    }
}