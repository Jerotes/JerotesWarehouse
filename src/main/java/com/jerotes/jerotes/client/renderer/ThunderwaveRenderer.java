package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.OtherSpell.ThunderwaveEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import javax.annotation.Nullable;
import java.awt.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.Math;

public class ThunderwaveRenderer extends EntityRenderer<ThunderwaveEntity> {
    private static final ResourceLocation GLOW_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");

    public ThunderwaveRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    public ResourceLocation getTextureLocation(ThunderwaveEntity entity) {
        return entity.getTextureLocation();
    }
    @Override
    public void render(ThunderwaveEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        // 只在客户端渲染指示器
        if (entity.level().isClientSide) {
            renderRectangleIndicator(entity, partialTicks, poseStack, buffer, packedLight);
        }
    }
    private void renderRectangleIndicator(
            ThunderwaveEntity entity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        // 1. 获取施法者（或实体本身）
        Entity caster = entity.getOwner() != null ? entity.getOwner() : entity;
        if (caster == null) return;

        // 2. 计算水平方向（忽略俯仰）
        Vec3 look = caster.getLookAngle();
        Vec3 forward = new Vec3(look.x, 0, look.z).normalize();
        if (forward.lengthSqr() < 0.001) return;
        Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();

        // 3. 范围参数（与攻击判定一致）
        double halfLen = 4.5;   // 沿 forward 方向
        double halfWid = 2.25;  // 垂直于 forward

        // 4. 起点（施法者脚底位置，可抬高 0.1 避免 Z-fighting）
        Vec3 origin = caster.position().add(0, 0.1, 0);

        // 5. 计算矩形的四个角（近端、远端）
        Vec3 p1 = origin.add(right.scale(-halfWid));                      // 近端左侧
        Vec3 p2 = origin.add(right.scale( halfWid));                      // 近端右侧
        Vec3 p3 = origin.add(forward.scale(halfLen)).add(right.scale( halfWid)); // 远端右侧
        Vec3 p4 = origin.add(forward.scale(halfLen)).add(right.scale(-halfWid)); // 远端左侧

        // 6. 动态透明度（参考你的圆环代码，这里假设 entity.life 存在）
        int life = entity.life;
        int alpha;
        if (life < 5) {
            alpha = (int) (80 * ((life - 0) / 5f));
        } else if (life < 15) {
            alpha = 80;
        } else {
            alpha = (int) (80 * (1 - (life - 15) / 10f));
        }
        alpha = Math.max(0, Math.min(255, alpha));
        int r = 255, g = 255, b = 255; // 白色

        // 7. 渲染填充矩形（半透明）
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(GLOW_LOCATION));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normalMat = pose.normal();

        // 两个三角形构成矩形 (p1-p2-p3, p1-p3-p4)
        // 顶点顺序（顺时针/逆时针均可，但需连续）
        // 三角形1: p1, p2, p3
        // 三角形2: p1, p3, p4
        // 注意：为了贴图正确，uv 可简写为 (0,0)~(1,1)
        float minU = 0, maxU = 1, minV = 0, maxV = 1;

        // 顶点 p1 (近端左)
        consumer.vertex(matrix, (float)p1.x, (float)p1.y, (float)p1.z)
                .color(r, g, b, alpha)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMat, 0, 1, 0)
                .endVertex();
        // p2 (近端右)
        consumer.vertex(matrix, (float)p2.x, (float)p2.y, (float)p2.z)
                .color(r, g, b, alpha)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMat, 0, 1, 0)
                .endVertex();
        // p3 (远端右)
        consumer.vertex(matrix, (float)p3.x, (float)p3.y, (float)p3.z)
                .color(r, g, b, alpha)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMat, 0, 1, 0)
                .endVertex();

        // 三角形2: p1, p3, p4
        consumer.vertex(matrix, (float)p1.x, (float)p1.y, (float)p1.z)
                .color(r, g, b, alpha)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMat, 0, 1, 0)
                .endVertex();
        consumer.vertex(matrix, (float)p3.x, (float)p3.y, (float)p3.z)
                .color(r, g, b, alpha)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMat, 0, 1, 0)
                .endVertex();
        consumer.vertex(matrix, (float)p4.x, (float)p4.y, (float)p4.z)
                .color(r, g, b, alpha)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMat, 0, 1, 0)
                .endVertex();

        // 8. 可选的边框（亮线，提高可见度）
        // 需要 LineRenderType 或重复使用 Glow 类型，但线宽不可控
        // 建议用同样的 VertexConsumer 绘制细边框四边形（窄条）
        // 这里简化为再次绘制四条边（用线段渲染器，略）
    }
}