package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.model.Modelcloud_of_daggers;
import com.jerotes.jerotes.client.model.Modelinsect_plague;
import com.jerotes.jerotes.entity.Other.OtherSpell.CloudOfDaggersEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.InsectPlagueEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class InsectPlagueRenderer extends EntityRenderer<InsectPlagueEntity> {
    private final Modelinsect_plague<InsectPlagueEntity> model;

    private static final float CUBE_HALF = 6.0f;
    private static final float CENTER_Y = 6.0f;

    private static final int VOID_END = 12;
    private static final int BURST_START = 10;
    private static final int SETTLE_END = 30;

    public InsectPlagueRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new Modelinsect_plague<>(context.bakeLayer(Modelinsect_plague.LAYER_LOCATION));
    }

    @Override
    public void render(InsectPlagueEntity t, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int packedLightIn) {
        float f7 = (float) t.tickCount + partialTicks;

        poseStack.pushPose();
        Matrix4f effectMatrix = poseStack.last().pose();

        renderVoidCore(t, partialTicks, effectMatrix, multiBufferSource);
        renderShockwave(t, partialTicks, effectMatrix, multiBufferSource);
        renderBurstSwirl(t, partialTicks, effectMatrix, multiBufferSource);
        renderFragments(t, partialTicks, effectMatrix, multiBufferSource);
        renderCenterGlow(t, partialTicks, effectMatrix, multiBufferSource);

        poseStack.popPose();

        poseStack.pushPose();
        poseStack.scale(Math.min(1, (t.start + partialTicks) / 10), Math.min(1, (t.start + partialTicks) / 10), Math.min(1, (t.start + partialTicks) / 10));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);
        this.model.scale(t,
                Math.min(1, (t.start - 20 + partialTicks) / 20),
                Mth.clamp((t.getMaxLife() - (t.life + partialTicks)) / 10f,0 , 1));
        VertexConsumer vb = multiBufferSource.getBuffer(JerotesRenderType.flowGlowOneSide(this.getTextureLocation(t), this.xOffset(f7) % 1.0f, 0.0f));
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);
        poseStack.popPose();

        super.render(t, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
    }

    private void renderVoidCore(InsectPlagueEntity t, float partialTick, Matrix4f matrix, MultiBufferSource buffer) {
        float life = t.life + partialTick;
        if (life > VOID_END + 8) return;

        float growT = Mth.clamp(life / VOID_END, 0f, 1f);
        float collapseT = Mth.clamp((life - VOID_END) / 6f, 0f, 1f);
        float radius = 2.4f * growT * (1f - collapseT);

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());

        if (radius > 0.02f) {
            int coreAlpha = (int) (230 * (1f - collapseT * 0.3f));
            renderSphere(consumer, matrix, 0, CENTER_Y, 0, radius, 12, 12, 16, coreAlpha, 8, 12);
        }

        float rimRadius = radius * 1.2f + 0.08f;
        float ringWidth = 0.12f + 0.05f * growT;
        if (rimRadius > 0.05f) {
            float time = t.tickCount + partialTick;
            int rimAlpha = (int) (220 * growT * (1f - collapseT * 0.6f));

            renderRingPlane(consumer, matrix, 0, CENTER_Y, 0, 0, rimRadius, rimRadius - ringWidth,
                    100, 255, 140, rimAlpha, 28, time * 0.06f);
            renderRingPlane(consumer, matrix, 0, CENTER_Y, 0, 1, rimRadius * 0.85f, rimRadius * 0.85f - ringWidth,
                    120, 255, 160, (int) (rimAlpha * 0.7f), 24, -time * 0.045f);
            renderRingPlane(consumer, matrix, 0, CENTER_Y, 0, 2, rimRadius * 0.7f, rimRadius * 0.7f - ringWidth,
                    140, 255, 180, (int) (rimAlpha * 0.5f), 20, time * 0.08f);
        }
    }

    private void renderShockwave(InsectPlagueEntity t, float partialTick, Matrix4f matrix, MultiBufferSource buffer) {
        float life = t.life + partialTick;
        if (life < BURST_START) return;

        float progress = (life - BURST_START) / 16f;
        if (progress > 1.2f) return;

        float clamped = Mth.clamp(progress, 0f, 1f);
        float radius = Mth.lerp(clamped, 0.4f, CUBE_HALF + 2.5f);
        float width = 0.5f * (1f - clamped * 0.6f);
        int alpha = (int) (255 * (1f - clamped));
        if (alpha <= 2) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        renderRingPlane(consumer, matrix, 0, CENTER_Y, 0, 0, radius, Math.max(0.05f, radius - width),
                255, 255, 255, alpha, 40, 0);
        renderRingPlane(consumer, matrix, 0, CENTER_Y, 0, 0, radius * 0.92f, Math.max(0.05f, radius * 0.92f - width * 1.3f),
                179, 233, 142, (int) (alpha * 0.6f), 40, 0);
    }

    private void renderBurstSwirl(InsectPlagueEntity t, float partialTick, Matrix4f matrix, MultiBufferSource buffer) {
        float life = t.life + partialTick;
        if (life < BURST_START) return;

        float progress = Mth.clamp((life - BURST_START) / (float) (SETTLE_END - BURST_START), 0f, 1f);
        float fade = 1f - Mth.clamp((life - SETTLE_END) / 10f, 0f, 1f);
        if (fade <= 0.01f) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        int strandCount = 5;
        int segments = 22;
        float maxRadius = CUBE_HALF * (0.3f + progress * 0.9f);
        float time = t.tickCount + partialTick;

        for (int s = 0; s < strandCount; s++) {
            float strandPhase = (float) (s * 2 * Math.PI / strandCount);

            float prevX = 0, prevY = CENTER_Y, prevZ = 0;
            for (int i = 1; i <= segments; i++) {
                float t2 = (float) i / segments;
                float radius = maxRadius * t2;
                float twist = time * 0.15f + t2 * 10f + strandPhase;

                float x = Mth.cos(twist) * radius;
                float z = Mth.sin(twist) * radius;
                float y = CENTER_Y + Mth.sin(t2 * (float) Math.PI) * 1.6f * (0.5f + 0.5f * Mth.sin(strandPhase + time * 0.05f));

                int alpha = (int) (230 * fade * (1f - t2 * 0.5f));
                renderThinRibbon(consumer, matrix, prevX, prevY, prevZ, x, y, z, 0.06f, 255, 255, 255, alpha);

                prevX = x;
                prevY = y;
                prevZ = z;
            }
        }
    }

    private void renderFragments(InsectPlagueEntity t, float partialTick, Matrix4f matrix, MultiBufferSource buffer) {
        renderBurstFragments(t, partialTick, matrix, buffer);
        renderAmbientFragments(t, partialTick, matrix, buffer);
    }

    private void renderBurstFragments(InsectPlagueEntity t, float partialTick, Matrix4f matrix, MultiBufferSource buffer) {
        float life = t.life + partialTick;
        if (life < BURST_START - 2) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        int fragCount = 26;
        int seedBase = t.getId();

        for (int i = 0; i < fragCount; i++) {
            float hseed = i * 53.7f + seedBase * 11.3f;

            float spawnDelay = hash(hseed) * 4f;
            float localTime = life - (BURST_START + spawnDelay);
            if (localTime < 0) continue;

            float fragLifetime = 30f + hash(hseed + 1f) * 24f;
            if (localTime > fragLifetime) continue;

            float dirYaw = hash(hseed + 2f) * (float) (2 * Math.PI);
            float dirPitch = (hash(hseed + 3f) - 0.5f) * (float) Math.PI;

            float dirX = Mth.cos(dirPitch) * Mth.cos(dirYaw);
            float dirY = Mth.sin(dirPitch);
            float dirZ = Mth.cos(dirPitch) * Mth.sin(dirYaw);

            float speed = 0.32f + hash(hseed + 4f) * 0.28f;
            float progressT = Mth.clamp(localTime / fragLifetime, 0f, 1f);
            float travel = speed * localTime * (1f - progressT * 0.45f);

            float px = dirX * travel;
            float py = dirY * travel - 0.015f * localTime * localTime * 0.02f;
            float pz = dirZ * travel;

            float size = 0.14f + hash(hseed + 5f) * 0.1f;
            float fadeIn = Mth.clamp(localTime * 4f, 0f, 1f);
            float fadeOut = 1f - Mth.clamp((localTime - fragLifetime * 0.7f) / (fragLifetime * 0.3f), 0f, 1f);
            int alpha = (int) (255 * fadeIn * fadeOut);
            if (alpha <= 2) continue;

            float spin = localTime * (0.3f + hash(hseed + 6f) * 0.4f);

            renderFragmentQuad(consumer, matrix, px, CENTER_Y + py, pz, size, spin, 179, 233, 142, alpha);
        }
    }

    private static final float SPAWN_INTERVAL = 30f;
    private static final int SLOTS_PER_BUCKET = 12;
    private static final float SPAWN_CHANCE = 0.55f;

    private void renderAmbientFragments(InsectPlagueEntity t, float partialTick, Matrix4f matrix, MultiBufferSource buffer) {
        float life = t.life + partialTick;
        if (life < SETTLE_END) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        int seedBase = t.getId();

        int currentBucket = (int) (life / SPAWN_INTERVAL);
        for (int bucketOffset = -1; bucketOffset <= 0; bucketOffset++) {
            int bucket = currentBucket + bucketOffset;
            if (bucket < 0) continue;

            float bucketStartTime = bucket * SPAWN_INTERVAL;

            for (int slot = 0; slot < SLOTS_PER_BUCKET; slot++) {
                float hseed = bucket * 977.13f + slot * 61.7f + seedBase * 8.9f;

                if (hash(hseed) > SPAWN_CHANCE) continue;

                float spawnDelay = hash(hseed + 1f) * SPAWN_INTERVAL * 0.8f;
                float localTime = life - (bucketStartTime + spawnDelay);
                if (localTime < 0) continue;

                float fragLifetime = 22f + hash(hseed + 2f) * 20f;
                if (localTime > fragLifetime) continue;

                float dirYaw = hash(hseed + 3f) * (float) (2 * Math.PI);
                float dirPitch = (hash(hseed + 4f) - 0.5f) * (float) Math.PI;

                float dirX = Mth.cos(dirPitch) * Mth.cos(dirYaw);
                float dirY = Mth.sin(dirPitch);
                float dirZ = Mth.cos(dirPitch) * Mth.sin(dirYaw);

                float speed = 0.12f + hash(hseed + 5f) * 0.16f;
                float progressT = Mth.clamp(localTime / fragLifetime, 0f, 1f);
                float travel = speed * localTime * (1f - progressT * 0.3f);

                float px = dirX * travel;
                float py = dirY * travel * 0.6f - 0.008f * localTime;
                float pz = dirZ * travel;

                float size = 0.1f + hash(hseed + 6f) * 0.08f;
                float fadeIn = Mth.clamp(localTime * 3f, 0f, 1f);
                float fadeOut = 1f - Mth.clamp((localTime - fragLifetime * 0.6f) / (fragLifetime * 0.4f), 0f, 1f);
                int alpha = (int) (200 * fadeIn * fadeOut);
                if (alpha <= 2) continue;

                float spin = localTime * (0.2f + hash(hseed + 7f) * 0.3f);

                renderFragmentQuad(consumer, matrix, px, CENTER_Y + py, pz, size, spin, 179, 233, 142, alpha);
            }
        }
    }

    private void renderCenterGlow(InsectPlagueEntity t, float partialTick, Matrix4f matrix, MultiBufferSource buffer) {
        float life = t.life + partialTick;
        float time = t.tickCount + partialTick;

        float burstT = Mth.clamp((life - BURST_START) / (float) (SETTLE_END - BURST_START), 0f, 1f);
        float introT = Mth.clamp(life / VOID_END, 0f, 1f);

        float ambient = 0.35f;
        float pulse = 0.08f * Mth.sin(time * 0.15f);

        float intensity;
        if (life < BURST_START) {
            intensity = 0.4f * introT;
        } else if (life < SETTLE_END) {
            intensity = Mth.lerp(burstT, 1.0f, ambient);
        } else {
            intensity = ambient + pulse;
        }
        intensity = Mth.clamp(intensity, 0f, 1f);
        if (intensity <= 0.01f) return;

        float radius = 2.6f + 0.9f * Mth.sin(time * 0.1f);

        float colorT = Mth.clamp(burstT, 0f, 1f) * (1f - Mth.clamp((life - SETTLE_END) / 12f, 0f, 1f));
        int r = (int) Mth.lerp(colorT, 179, 255);
        int g = (int) Mth.lerp(colorT, 233, 255);
        int b = (int) Mth.lerp(colorT, 142, 255);

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());

        renderHazeDisc(consumer, matrix, 0, CENTER_Y, 0, 0, radius, intensity, r, g, b);
        renderHazeDisc(consumer, matrix, 0, CENTER_Y, 0, 1, radius * 0.9f, intensity * 0.8f, r, g, b);
        renderHazeDisc(consumer, matrix, 0, CENTER_Y, 0, 2, radius * 0.9f, intensity * 0.8f, r, g, b);
    }

    private void renderHazeDisc(VertexConsumer consumer, Matrix4f matrix, float cx, float cy, float cz,
                                int plane, float maxRadius, float intensity, int r, int g, int b) {
        int layers = 6;
        int segments = 24;
        int peakAlpha = (int) (90 * intensity);

        for (int layer = layers; layer >= 1; layer--) {
            float outerT = (float) layer / layers;
            float innerT = (float) (layer - 1) / layers;

            float outerR = maxRadius * outerT;
            float innerR = maxRadius * innerT;

            float falloff = 1.0f - outerT * outerT;
            int alpha = (int) (peakAlpha * falloff);
            if (alpha <= 1) continue;

            for (int i = 0; i < segments; i++) {
                float a0 = (float) (i * 2 * Math.PI / segments);
                float a1 = (float) ((i + 1) * 2 * Math.PI / segments);

                float[] o0 = planePoint(plane, a0, outerR);
                float[] i0 = planePoint(plane, a0, innerR);
                float[] o1 = planePoint(plane, a1, outerR);
                float[] i1 = planePoint(plane, a1, innerR);

                int alphaInner = alpha;
                int alphaOuter = (int) (alpha * 0.3f);

                addVertex(consumer, matrix, cx + i0[0], cy + i0[1], cz + i0[2], r, g, b, alphaInner);
                addVertex(consumer, matrix, cx + o0[0], cy + o0[1], cz + o0[2], r, g, b, alphaOuter);
                addVertex(consumer, matrix, cx + o1[0], cy + o1[1], cz + o1[2], r, g, b, alphaOuter);
                addVertex(consumer, matrix, cx + i1[0], cy + i1[1], cz + i1[2], r, g, b, alphaInner);

                addVertex(consumer, matrix, cx + i1[0], cy + i1[1], cz + i1[2], r, g, b, alphaInner);
                addVertex(consumer, matrix, cx + o1[0], cy + o1[1], cz + o1[2], r, g, b, alphaOuter);
                addVertex(consumer, matrix, cx + o0[0], cy + o0[1], cz + o0[2], r, g, b, alphaOuter);
                addVertex(consumer, matrix, cx + i0[0], cy + i0[1], cz + i0[2], r, g, b, alphaInner);
            }
        }
    }

    private void renderSphere(VertexConsumer consumer, Matrix4f matrix, float cx, float cy, float cz,
                              float radius, int r, int g, int b, int a, int rings, int segments) {
        for (int i = 0; i < rings; i++) {
            float lat0 = (float) (Math.PI * (-0.5 + (double) i / rings));
            float lat1 = (float) (Math.PI * (-0.5 + (double) (i + 1) / rings));
            float y0 = Mth.sin(lat0) * radius, rr0 = Mth.cos(lat0) * radius;
            float y1 = Mth.sin(lat1) * radius, rr1 = Mth.cos(lat1) * radius;

            for (int j = 0; j < segments; j++) {
                float lon0 = (float) (2 * Math.PI * j / segments);
                float lon1 = (float) (2 * Math.PI * (j + 1) / segments);

                float x00 = Mth.cos(lon0) * rr0, z00 = Mth.sin(lon0) * rr0;
                float x01 = Mth.cos(lon1) * rr0, z01 = Mth.sin(lon1) * rr0;
                float x10 = Mth.cos(lon0) * rr1, z10 = Mth.sin(lon0) * rr1;
                float x11 = Mth.cos(lon1) * rr1, z11 = Mth.sin(lon1) * rr1;

                addVertex(consumer, matrix, cx + x00, cy + y0, cz + z00, r, g, b, a);
                addVertex(consumer, matrix, cx + x01, cy + y0, cz + z01, r, g, b, a);
                addVertex(consumer, matrix, cx + x11, cy + y1, cz + z11, r, g, b, a);
                addVertex(consumer, matrix, cx + x10, cy + y1, cz + z10, r, g, b, a);
            }
        }
    }

    private void renderRingPlane(VertexConsumer consumer, Matrix4f matrix, float cx, float cy, float cz,
                                 int plane, float outerR, float innerR,
                                 int r, int g, int b, int a, int segments, float spin) {
        for (int i = 0; i < segments; i++) {
            float a0 = (float) (i * 2 * Math.PI / segments) + spin;
            float a1 = (float) ((i + 1) * 2 * Math.PI / segments) + spin;

            float[] o0 = planePoint(plane, a0, outerR);
            float[] i0 = planePoint(plane, a0, innerR);
            float[] o1 = planePoint(plane, a1, outerR);
            float[] i1 = planePoint(plane, a1, innerR);

            addVertex(consumer, matrix, cx + i0[0], cy + i0[1], cz + i0[2], r, g, b, a);
            addVertex(consumer, matrix, cx + o0[0], cy + o0[1], cz + o0[2], r, g, b, a);
            addVertex(consumer, matrix, cx + o1[0], cy + o1[1], cz + o1[2], r, g, b, a);
            addVertex(consumer, matrix, cx + i1[0], cy + i1[1], cz + i1[2], r, g, b, a);

            addVertex(consumer, matrix, cx + i1[0], cy + i1[1], cz + i1[2], r, g, b, a);
            addVertex(consumer, matrix, cx + o1[0], cy + o1[1], cz + o1[2], r, g, b, a);
            addVertex(consumer, matrix, cx + o0[0], cy + o0[1], cz + o0[2], r, g, b, a);
            addVertex(consumer, matrix, cx + i0[0], cy + i0[1], cz + i0[2], r, g, b, a);
        }
    }

    private float[] planePoint(int plane, float angle, float radius) {
        float c = Mth.cos(angle) * radius;
        float s = Mth.sin(angle) * radius;
        return switch (plane) {
            case 0 -> new float[]{c, 0, s};
            case 1 -> new float[]{c, s, 0};
            default -> new float[]{0, c, s};
        };
    }

    private void renderThinRibbon(VertexConsumer consumer, Matrix4f matrix,
                                  float x0, float y0, float z0, float x1, float y1, float z1,
                                  float halfW, int r, int g, int b, int a) {
        addVertex(consumer, matrix, x0 - halfW, y0, z0, r, g, b, a);
        addVertex(consumer, matrix, x0 + halfW, y0, z0, r, g, b, a);
        addVertex(consumer, matrix, x1 + halfW, y1, z1, r, g, b, a);
        addVertex(consumer, matrix, x1 - halfW, y1, z1, r, g, b, a);

        addVertex(consumer, matrix, x0, y0, z0 - halfW, r, g, b, a);
        addVertex(consumer, matrix, x0, y0, z0 + halfW, r, g, b, a);
        addVertex(consumer, matrix, x1, y1, z1 + halfW, r, g, b, a);
        addVertex(consumer, matrix, x1, y1, z1 - halfW, r, g, b, a);
    }

    private void renderFragmentQuad(VertexConsumer consumer, Matrix4f matrix, float cx, float cy, float cz,
                                    float size, float spin, int r, int g, int b, int a) {
        float c = Mth.cos(spin), s = Mth.sin(spin);

        float x0 = -size * c, z0 = -size * s;
        float x1 = size * c, z1 = size * s;
        addVertex(consumer, matrix, cx + x0, cy - size, cz + z0, r, g, b, a);
        addVertex(consumer, matrix, cx + x0, cy + size, cz + z0, r, g, b, a);
        addVertex(consumer, matrix, cx + x1, cy + size, cz + z1, r, g, b, a);
        addVertex(consumer, matrix, cx + x1, cy - size, cz + z1, r, g, b, a);

        float x2 = -size * s, z2 = size * c;
        float x3 = size * s, z3 = -size * c;
        addVertex(consumer, matrix, cx + x2, cy - size, cz + z2, r, g, b, a);
        addVertex(consumer, matrix, cx + x2, cy + size, cz + z2, r, g, b, a);
        addVertex(consumer, matrix, cx + x3, cy + size, cz + z3, r, g, b, a);
        addVertex(consumer, matrix, cx + x3, cy - size, cz + z3, r, g, b, a);
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z,
                           int r, int g, int b, int a) {
        consumer.vertex(matrix, x, y, z).color(r, g, b, a).endVertex();
    }

    private static float hash(float seed) {
        float x = Mth.sin(seed * 12.9898f) * 43758.5453f;
        return x - Mth.floor(x);
    }

    protected float xOffset(float f) {
        return 0;
    }

    public ResourceLocation getTextureLocation(InsectPlagueEntity insectPlagueEntity) {
        return insectPlagueEntity.getTextureLocation();
    }
}