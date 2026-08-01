package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.OtherSpell.PowerWordKillEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PowerWordKillRenderer<T extends PowerWordKillEntity> extends EntityRenderer<T> {

    private static final ResourceLocation TEXTURE_LOCATION =
            new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");
    private static final ResourceLocation GLOW_LOCATION =
            new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");

    private static final int KILL_TICK = 10;

    // 整体尺寸放大倍率——九环法术，应该比普通特效大上不止一圈
    private static final float EFFECT_SCALE = 1.6f;
    // 地面特效贴地偏移，只做很小的抬升防止 z-fighting，不再跟 targetHeight 挂钩
    private static final float GROUND_LIFT = 0.03f;

    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public PowerWordKillRenderer(EntityRendererProvider.Context context, float scale, boolean fullBright) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = scale;
        this.fullBright = fullBright;
    }

    public PowerWordKillRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0F, true);
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {

       // renderRound(entity, partialTick, poseStack, buffer);
        renderRitualCircles(entity, partialTick, poseStack, buffer);
        renderGroundGlyphRays(entity, partialTick, poseStack, buffer);
        renderRisingSpikes(entity, partialTick, poseStack, buffer);
        renderDeathMotes(entity, partialTick, poseStack, buffer);
        renderKillBurst(entity, partialTick, poseStack, buffer);

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

//    // ============================================================
//    // 原有：垂直发光柱体（放大尺寸系数）
//    // ============================================================
//    private void renderRound(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
//
//        float growProgress = 0;
//        float fadeAlpha = 0;
//
//        int lifeNeed = entity.life / 3;
//        if (entity.life > 2 && entity.life < lifeNeed) {
//            growProgress = (entity.life + partialTick) / (float) lifeNeed;
//            fadeAlpha = 1.0F;
//        } else if (entity.life > 2) {
//            growProgress = 1.0F - ((entity.life - lifeNeed + partialTick) / (float) (entity.getMaxLife() / 2 - lifeNeed));
//            growProgress = Mth.clamp(growProgress, 0F, 1F);
//            fadeAlpha = Mth.clamp(1.0F, 0F, 1F);
//        }
//
//        float halfWidth = (entity.getSize() * growProgress * 1.1f + 0.15f) * EFFECT_SCALE;
//        float height = (entity.getSize() * growProgress * 1.2F + 0.5f) * EFFECT_SCALE;
//
//        int topColor = entity.roundLightI();
//        int bottomColor = entity.roundLightII();
//
//        int tr = (topColor >> 16) & 255, tg = (topColor >> 8) & 255, tb = topColor & 255;
//        int br = (bottomColor >> 16) & 255, bg = (bottomColor >> 8) & 255, bb = bottomColor & 255;
//
//        int bottomAlpha = (int) (220 * fadeAlpha);
//
//        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(GLOW_LOCATION));
//        PoseStack.Pose pose = poseStack.last();
//        Matrix4f matrix = pose.pose();
//        Matrix3f normal = pose.normal();
//
//        float yBottom = 0.0F; // 贴地，不再跟 targetHeight 挂钩
//        float yTop = height;
//
//        addQuad(consumer, matrix, normal, -halfWidth, yBottom, halfWidth, -halfWidth, yTop, halfWidth, halfWidth, yTop, halfWidth, halfWidth, yBottom, halfWidth, br, bg, bb, tr, tg, tb, bottomAlpha);
//        addQuad(consumer, matrix, normal, halfWidth, yBottom, -halfWidth, halfWidth, yTop, -halfWidth, -halfWidth, yTop, -halfWidth, -halfWidth, yBottom, -halfWidth, br, bg, bb, tr, tg, tb, bottomAlpha);
//        addQuad(consumer, matrix, normal, -halfWidth, yBottom, -halfWidth, -halfWidth, yTop, -halfWidth, -halfWidth, yTop, halfWidth, -halfWidth, yBottom, halfWidth, br, bg, bb, tr, tg, tb, bottomAlpha);
//        addQuad(consumer, matrix, normal, halfWidth, yBottom, halfWidth, halfWidth, yTop, halfWidth, halfWidth, yTop, -halfWidth, halfWidth, yBottom, -halfWidth, br, bg, bb, tr, tg, tb, bottomAlpha);
//    }

    private void addQuad(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                         float x1, float y1, float z1, float x2, float y2, float z2,
                         float x3, float y3, float z3, float x4, float y4, float z4,
                         int br, int bg, int bb, int tr, int tg, int tb, int bottomAlpha) {

        consumer.vertex(matrix, x1, y1, z1).color(br, bg, bb, bottomAlpha).uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(tr, tg, tb, 0).uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(matrix, x3, y3, z3).color(tr, tg, tb, 0).uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();
        consumer.vertex(matrix, x4, y4, z4).color(br, bg, bb, bottomAlpha).uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0, 1, 0).endVertex();
    }

    private void renderRitualCircles(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        float time = entity.tickCount + partialTick;
        float t = Mth.clamp((entity.life + partialTick) / entity.getMaxLife(), 0f, 1f);
        float ringScale = Mth.clamp(t * 4f, 0f, 1f) * (1.0f - Mth.clamp((t - 0.8f) / 0.2f, 0f, 1f) * 0.3f);
        if (ringScale <= 0.01f) return;

        float yBase = GROUND_LIFT;
        float baseRadius = (entity.getSize() * 0.9f + 1.2f) * EFFECT_SCALE;

        int[] colorMain = unpackColor(entity.roundLightI());
        int[] colorEdge = unpackColor(entity.roundLightII());

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();

        int ringCount = 3;
        for (int ring = 0; ring < ringCount; ring++) {
            float ringRadius = baseRadius * (0.5f + ring * 0.28f) * ringScale;
            float spin = time * (0.02f + ring * 0.015f) * (ring % 2 == 0 ? 1 : -1);
            int teeth = 16 + ring * 4;
            int segments = teeth * 2;

            float ringWidth = (0.08f + ring * 0.015f) * EFFECT_SCALE;
            int alpha = (int) (200 * ringScale * (0.7f + 0.3f * Mth.sin(time * 0.1f + ring)));

            for (int i = 0; i < segments; i++) {
                float a0 = (float) (i * 2 * Math.PI / segments) + spin;
                float a1 = (float) ((i + 1) * 2 * Math.PI / segments) + spin;

                float toothMod0 = (i % 2 == 0) ? 1.0f : 0.85f;
                float toothMod1 = ((i + 1) % 2 == 0) ? 1.0f : 0.85f;

                float rOuter0 = ringRadius * toothMod0;
                float rInner0 = (ringRadius - ringWidth) * toothMod0;
                float rOuter1 = ringRadius * toothMod1;
                float rInner1 = (ringRadius - ringWidth) * toothMod1;

                float ox0 = Mth.cos(a0) * rOuter0, oz0 = Mth.sin(a0) * rOuter0;
                float ix0 = Mth.cos(a0) * rInner0, iz0 = Mth.sin(a0) * rInner0;
                float ox1 = Mth.cos(a1) * rOuter1, oz1 = Mth.sin(a1) * rOuter1;
                float ix1 = Mth.cos(a1) * rInner1, iz1 = Mth.sin(a1) * rInner1;

                int r0 = colorMain[0], g0 = colorMain[1], b0 = colorMain[2];
                int r1 = colorEdge[0], g1 = colorEdge[1], b1 = colorEdge[2];

                addVertex(consumer, matrix, ix0, yBase, iz0, r0, g0, b0, alpha);
                addVertex(consumer, matrix, ox0, yBase, oz0, r1, g1, b1, alpha);
                addVertex(consumer, matrix, ox1, yBase, oz1, r1, g1, b1, alpha);
                addVertex(consumer, matrix, ix1, yBase, iz1, r0, g0, b0, alpha);
            }
        }
    }

    // ============================================================
    // 新增 2：地面放射符文刻线（贴地，放大）
    // ============================================================
    private void renderGroundGlyphRays(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        float time = entity.tickCount + partialTick;
        float t = Mth.clamp((entity.life + partialTick) / entity.getMaxLife(), 0f, 1f);
        float scaleT = Mth.clamp(t * 4f, 0f, 1f);
        if (scaleT <= 0.01f) return;

        float yBase = GROUND_LIFT * 0.7f;
        float baseRadius = ((entity.getSize() * 0.9f + 1.2f) * EFFECT_SCALE) * scaleT;

        int[] colorEdge = unpackColor(entity.roundLightII());
        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();

        int rays = 12;
        float lineWidth = 0.03f * EFFECT_SCALE;
        for (int i = 0; i < rays; i++) {
            float angle = (float) (i * 2 * Math.PI / rays);
            float flicker = 0.6f + 0.4f * Mth.sin(time * 0.3f + i * 1.7f);
            int alpha = (int) (140 * scaleT * flicker);

            float dx = Mth.cos(angle), dz = Mth.sin(angle);
            float px = -dz * lineWidth, pz = dx * lineWidth;

            float ox = dx * baseRadius, oz = dz * baseRadius;

            addVertex(consumer, matrix, -px, yBase, -pz, colorEdge[0], colorEdge[1], colorEdge[2], alpha);
            addVertex(consumer, matrix, px, yBase, pz, colorEdge[0], colorEdge[1], colorEdge[2], alpha);
            addVertex(consumer, matrix, ox + px, yBase, oz + pz, colorEdge[0], colorEdge[1], colorEdge[2], 0);
            addVertex(consumer, matrix, ox - px, yBase, oz - pz, colorEdge[0], colorEdge[1], colorEdge[2], 0);
        }
    }

    // ============================================================
    // 新增 3：环绕升起的暗晶尖刺（放大高度/半径）
    // ============================================================
    private void renderRisingSpikes(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        float time = entity.tickCount + partialTick;
        float t = Mth.clamp((entity.life + partialTick) / entity.getMaxLife(), 0f, 1f);
        float riseT = Mth.clamp(t * 3f, 0f, 1f) * (1.0f - Mth.clamp((t - 0.75f) / 0.25f, 0f, 1f));
        if (riseT <= 0.01f) return;

        float baseRadius = (entity.getSize() * 0.75f + 1.0f) * EFFECT_SCALE;
        float yBase = GROUND_LIFT;
        float maxSpikeHeight = (entity.getSize() * 0.6f + 2.2f) * EFFECT_SCALE * riseT;

        int[] colorMain = unpackColor(entity.roundLightI());
        int[] colorEdge = unpackColor(entity.roundLightII());

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();

        int spikeCount = 10;
        for (int i = 0; i < spikeCount; i++) {
            float baseAngle = (float) (i * 2 * Math.PI / spikeCount) + time * 0.01f;
            float twist = time * 0.04f + i * 0.6f;

            float bx = Mth.cos(baseAngle) * baseRadius;
            float bz = Mth.sin(baseAngle) * baseRadius;

            float sway = Mth.sin(time * 0.08f + i) * 0.15f * EFFECT_SCALE;
            float tipAngle = baseAngle + twist * 0.15f;
            float tipRadius = baseRadius * 0.85f;
            float tx = Mth.cos(tipAngle) * tipRadius + sway;
            float tz = Mth.sin(tipAngle) * tipRadius + sway;

            float halfW = 0.08f * EFFECT_SCALE * (1.0f - riseT * 0.2f);
            float yTip = yBase + maxSpikeHeight;

            renderSpikeQuad(consumer, matrix, bx, yBase, bz, tx, yTip, tz, halfW, true,
                    colorMain[0], colorMain[1], colorMain[2], colorEdge[0], colorEdge[1], colorEdge[2]);
            renderSpikeQuad(consumer, matrix, bx, yBase, bz, tx, yTip, tz, halfW, false,
                    colorMain[0], colorMain[1], colorMain[2], colorEdge[0], colorEdge[1], colorEdge[2]);
        }
    }

    private void renderSpikeQuad(VertexConsumer consumer, Matrix4f matrix,
                                 float bx, float by, float bz, float tx, float ty, float tz,
                                 float halfW, boolean alongX,
                                 int r0, int g0, int b0, int r1, int g1, int b1) {
        float ox = alongX ? halfW : 0f;
        float oz = alongX ? 0f : halfW;

        addVertex(consumer, matrix, bx - ox, by, bz - oz, r0, g0, b0, 220);
        addVertex(consumer, matrix, bx + ox, by, bz + oz, r0, g0, b0, 220);
        addVertex(consumer, matrix, tx, ty, tz, r1, g1, b1, 40);
        addVertex(consumer, matrix, tx, ty, tz, r1, g1, b1, 40);
    }

    private void renderDeathMotes(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        float time = entity.tickCount + partialTick;
        float t = Mth.clamp((entity.life + partialTick) / entity.getMaxLife(), 0f, 1f);
        float fade = 1.0f - Mth.clamp((t - 0.85f) / 0.15f, 0f, 1f);
        if (fade <= 0.01f) return;

        float baseRadius = (entity.getSize() * 0.8f + 1.4f) * EFFECT_SCALE;
        float baseHeight = (entity.getSize() * 0.6f + 2.0f) * EFFECT_SCALE;

        int[] colorMain = unpackColor(entity.roundLightI());
        int[] colorEdge = unpackColor(entity.roundLightII());

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();

        int moteCount = 24;
        int seedBase = entity.getId();

        for (int i = 0; i < moteCount; i++) {
            float hseed = i * 63.71f + seedBase * 9.13f;

            float orbitAngle = time * (0.02f + hash(hseed) * 0.04f) + hseed;
            float orbitRadius = baseRadius * (0.5f + hash(hseed + 1f) * 0.8f);
            float height = hash(hseed + 2f) * baseHeight; // 从贴地开始往上飘

            float mx = Mth.cos(orbitAngle) * orbitRadius;
            float mz = Mth.sin(orbitAngle) * orbitRadius;

            float twinkle = 0.5f + 0.5f * Mth.sin(time * (0.5f + hash(hseed + 3f)) + hseed);
            float size = (0.04f + hash(hseed + 4f) * 0.03f) * EFFECT_SCALE;
            int alpha = (int) (200 * twinkle * fade);

            int r = (int) Mth.lerp(twinkle, colorMain[0], colorEdge[0]);
            int g = (int) Mth.lerp(twinkle, colorMain[1], colorEdge[1]);
            int b = (int) Mth.lerp(twinkle, colorMain[2], colorEdge[2]);

            addVertex(consumer, matrix, mx - size, height, mz, r, g, b, alpha);
            addVertex(consumer, matrix, mx, height + size, mz, r, g, b, alpha);
            addVertex(consumer, matrix, mx + size, height, mz, r, g, b, alpha);
            addVertex(consumer, matrix, mx, height - size, mz, r, g, b, alpha);

            addVertex(consumer, matrix, mx, height, mz - size, r, g, b, alpha);
            addVertex(consumer, matrix, mx, height + size, mz, r, g, b, alpha);
            addVertex(consumer, matrix, mx, height, mz + size, r, g, b, alpha);
            addVertex(consumer, matrix, mx, height - size, mz, r, g, b, alpha);
        }
    }

    // ============================================================
    // 新增 5：处决冲击波（放大半径/核心）
    // ============================================================
    private void renderKillBurst(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        float lifeF = entity.life + partialTick;
        float burst = Mth.clamp(1.0f - Math.abs(lifeF - KILL_TICK) / 15f, 0f, 1f);
        if (burst <= 0.01f) return;

        float yBase = GROUND_LIFT * 1.5f;
        float baseRadius = (entity.getSize() * 1.1f + 1.6f) * EFFECT_SCALE;

        float expandT = Mth.clamp((lifeF - (KILL_TICK - 15)) / 20f, 0f, 1f);
        float shockRadius = baseRadius * (0.3f + expandT * 1.8f);
        float ringWidth = 0.25f * EFFECT_SCALE * (1.0f - expandT * 0.5f);

        int[] colorMain = unpackColor(entity.roundLightI());
        int[] colorEdge = unpackColor(entity.roundLightII());

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();

        int alpha = (int) (255 * burst * (1.0f - expandT * 0.6f));
        int segments = 32;
        for (int i = 0; i < segments; i++) {
            float a0 = (float) (i * 2 * Math.PI / segments);
            float a1 = (float) ((i + 1) * 2 * Math.PI / segments);

            float ox0 = Mth.cos(a0) * shockRadius, oz0 = Mth.sin(a0) * shockRadius;
            float ix0 = Mth.cos(a0) * (shockRadius - ringWidth), iz0 = Mth.sin(a0) * (shockRadius - ringWidth);
            float ox1 = Mth.cos(a1) * shockRadius, oz1 = Mth.sin(a1) * shockRadius;
            float ix1 = Mth.cos(a1) * (shockRadius - ringWidth), iz1 = Mth.sin(a1) * (shockRadius - ringWidth);

            addVertex(consumer, matrix, ix0, yBase, iz0, colorEdge[0], colorEdge[1], colorEdge[2], alpha);
            addVertex(consumer, matrix, ox0, yBase, oz0, colorMain[0], colorMain[1], colorMain[2], (int) (alpha * 0.3f));
            addVertex(consumer, matrix, ox1, yBase, oz1, colorMain[0], colorMain[1], colorMain[2], (int) (alpha * 0.3f));
            addVertex(consumer, matrix, ix1, yBase, iz1, colorEdge[0], colorEdge[1], colorEdge[2], alpha);
        }

        float coreSize = (0.5f + expandT * 0.35f) * burst * EFFECT_SCALE;
        int coreAlpha = (int) (230 * burst);
        float cy = yBase + 0.8f * EFFECT_SCALE;

        addVertex(consumer, matrix, -coreSize, cy, 0, 255, 255, 255, coreAlpha);
        addVertex(consumer, matrix, 0, cy + coreSize, 0, 255, 240, 250, coreAlpha);
        addVertex(consumer, matrix, coreSize, cy, 0, 255, 255, 255, coreAlpha);
        addVertex(consumer, matrix, 0, cy - coreSize, 0, 255, 240, 250, coreAlpha);

        addVertex(consumer, matrix, 0, cy, -coreSize, 255, 255, 255, coreAlpha);
        addVertex(consumer, matrix, 0, cy + coreSize, 0, 255, 240, 250, coreAlpha);
        addVertex(consumer, matrix, 0, cy, coreSize, 255, 255, 255, coreAlpha);
        addVertex(consumer, matrix, 0, cy - coreSize, 0, 255, 240, 250, coreAlpha);
    }

    // ============================================================
    private static int[] unpackColor(int packed) {
        return new int[]{(packed >> 16) & 255, (packed >> 8) & 255, packed & 255};
    }

    private void addVertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z,
                           int r, int g, int b, int a) {
        consumer.vertex(matrix, x, y, z).color(r, g, b, a).endVertex();
    }

    private static float hash(float seed) {
        float x = Mth.sin(seed * 12.9898f) * 43758.5453f;
        return x - Mth.floor(x);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE_LOCATION;
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double d, double d2, double d3) {
        if (!entity.shouldRender(d, d2, d3)) {
            return false;
        } else if (entity.noCulling) {
            return true;
        } else {
            AABB aabb = entity.getBoundingBoxForCulling().inflate(128.0D);
            if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
                aabb = new AABB(entity.getX() - 128.0D, entity.getY() - 128.0D, entity.getZ() - 128.0D,
                        entity.getX() + 128.0D, entity.getY() + 128.0D, entity.getZ() + 128.0D);
            }
            return frustum.isVisible(aabb);
        }
    }
}