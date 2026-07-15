package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.OtherSpell.CureWoundsEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CureWoundsRenderer extends EntityRenderer<CureWoundsEntity> {
    public CureWoundsRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(CureWoundsEntity t, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn) {
        float f7 = (float)t.tickCount + partialTicks;

        poseStack.pushPose();
        renderBeam(t, partialTicks, poseStack, multiBufferSource, LightTexture.FULL_BRIGHT);
        poseStack.popPose();

        super.render(t, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
    }
    private void renderBeam(CureWoundsEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        double width = entity.getSize();
        double height = entity.getTargetHeight();
        int life = entity.life; // 假设实体有 life 字段

        // 淡入淡出 alpha
        float alpha = 1.0f;
        if (life < 5) {
            alpha = life / 5.0f;
        } else if (life > 30) {
            alpha = 1.0f - (life - 30) / 10.0f;
        }
        alpha = Mth.clamp(alpha, 0.0f, 1.0f);
        if (alpha < 0.01f) return;

        // 颜色（可自定义，这里使用白色偏蓝）
        int r = 180, g = 220, b = 255; // 或者从实体获取颜色
        int a = (int)(255 * alpha);

        // 渲染类型（使用实体的纹理）
        ResourceLocation texture = entity.getTextureLocation();
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(texture));

        int segments = 24;         // 每条的分段数，越高越平滑
        int numBands = 6;          // 缠绕条数
        float radius = (float)width * 0.5f;   // 螺旋半径
        float maxY = (float)height;           // 最大高度
        float bandWidth = radius * 0.08f;     // 每条带的宽度（切向厚度）

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        for (int band = 0; band < numBands; band++) {
            float angleOffset = (float)band / numBands * 2.0f * (float)Math.PI;

            Vec3 prevLeft = null, prevRight = null;
            for (int i = 0; i <= segments; i++) {
                float progress = (float)i / segments;
                float y = progress * maxY;

                float angle = angleOffset + progress * 5.0f * (float)Math.PI;
                float cos = Mth.cos(angle);
                float sin = Mth.sin(angle);

                float currentRadius = radius;

                float cx = currentRadius * cos;
                float cz = currentRadius * sin;

                float tx = -sin;
                float tz = cos;
                float len = Mth.sqrt(tx*tx + tz*tz);
                if (len > 0.001f) { tx /= len; tz /= len; }

                float halfBand = bandWidth * 0.5f;
                Vec3 left = new Vec3(cx + tx * halfBand, y, cz + tz * halfBand);
                Vec3 right = new Vec3(cx - tx * halfBand, y, cz - tz * halfBand);

                if (i > 0) {
                    float alphaPrev = 0.7f + 0.3f * (progress - 1.0f/segments);
                    float alphaCur  = 0.7f + 0.3f * progress;
                    int aPrev = (int)(a * alphaPrev);
                    int aCur  = (int)(a * alphaCur);

                    addVertex(consumer, matrix, normal, prevLeft, r, g, b, aPrev, 0, 0);
                    addVertex(consumer, matrix, normal, prevRight, r, g, b, aPrev, 1, 0);
                    addVertex(consumer, matrix, normal, right, r, g, b, aCur, 1, 1);
                    addVertex(consumer, matrix, normal, left, r, g, b, aCur, 0, 1);
                }
                prevLeft = left;
                prevRight = right;
            }
        }
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

    public ResourceLocation getTextureLocation(CureWoundsEntity cureWoundsEntity) {
        return cureWoundsEntity.getTextureLocation();
    }
}