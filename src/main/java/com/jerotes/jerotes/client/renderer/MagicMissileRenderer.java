package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modelblock;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicMissile.BaseMagicMissileEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public class MagicMissileRenderer<T extends BaseMagicMissileEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/projectiles/magic_missile.png");
    private static final ResourceLocation GLOW_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    private final Modelblock model;

    public MagicMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new Modelblock(context.bakeLayer(Modelblock.LAYER_LOCATION));
    }
    @Override
    protected int getBlockLightLevel(T t, BlockPos blockPos) {
        return 15;
    }
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {

        poseStack.pushPose();
        VertexConsumer vb = bufferIn.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(this.getTextureLocation(entityIn)));
        poseStack.scale(-0.25f, -0.25f, 0.25f);
        poseStack.translate(0f, -0.125f * 10, 0f);
        model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 0.5f);
        poseStack.popPose();

        poseStack.pushPose();
        renderTrail(entityIn, partialTicks, poseStack, bufferIn);
        poseStack.popPose();
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
    }

    private void renderTrail(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        List<BaseMagicMissileEntity.TrailPoint> points = entity.getTrailPoints();
        if (points.size() < 2) return;

        poseStack.pushPose();
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSided(GLOW_LOCATION));
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        int currentLife = entity.life;
        int maxLife = 120;

        // 预计算宽度和透明度（保持您原来的逻辑不变）
        int n = points.size();
        float[] widths = new float[n];
        float[] alphas = new float[n];
        for (int i = 0; i < n; i++) {
            BaseMagicMissileEntity.TrailPoint tp = points.get(i);
            int age = currentLife - tp.createdAt;
            float ageFade = 1.0f - Math.min(1.0f, age / 20.0f);
            float overallFade = 1.0f - (float) currentLife / maxLife;
            float alpha = Math.min(0.6f, 0.8f * ageFade * (1 - overallFade * 0.7f));
            alphas[i] = Math.max(0, alpha);
            widths[i] = 0.12f * ageFade;
        }
        Vec3 currentPos = new Vec3(
                Mth.lerp(partialTick, entity.xOld, entity.getX()),
                Mth.lerp(partialTick, entity.yOld, entity.getY()),
                Mth.lerp(partialTick, entity.zOld, entity.getZ())
        ).add(0.0D, 0.0D, 0.0D);
        Vec3 cameraRight = new Vec3(1, 0, 0);
        Vec3 cameraUp = new Vec3(0, 1, 0);

        for (int i = 0; i < n - 1; i++) {
            BaseMagicMissileEntity.TrailPoint tp0 = points.get(i);
            BaseMagicMissileEntity.TrailPoint tp1 = points.get(i+1);
            Vec3 point0 = tp0.getPosition(partialTick);
            Vec3 point1 = tp1.getPosition(partialTick);
            Vec3 local0 = point0.subtract(currentPos);
            Vec3 local1 = point1.subtract(currentPos);
            Vec3 dir = local1.subtract(local0).normalize();

            Vec3 right = cameraUp.cross(dir);
            if (right.lengthSqr() < 1e-4) right = cameraRight.cross(dir);
            right = right.normalize();

            float w0 = widths[i];
            float w1 = widths[i+1];
            float a0 = alphas[i];
            float a1 = alphas[i+1];

            int colorI = entity.beamLightI();
            int innerR = (colorI >> 16) & 0xFF;
            int innerG = (colorI >> 8) & 0xFF;
            int innerB = colorI & 0xFF;

            int r = innerR, g = innerG, b = innerB;

            Vec3 p0l = local0.add(right.scale(-w0));
            Vec3 p0r = local0.add(right.scale(w0));
            Vec3 p1l = local1.add(right.scale(-w1));
            Vec3 p1r = local1.add(right.scale(w1));

            // 直接提交四个顶点（逆时针顺序），形成一个四边形，没有内部对角线
            // 顺序：左下 -> 右下 -> 右上 -> 左上
            addVertex(consumer, matrix, normal, p0l, r, g, b, (int)(a0 * 200), 0, 0);
            addVertex(consumer, matrix, normal, p0r, r, g, b, (int)(a0 * 200), 1, 0);
            addVertex(consumer, matrix, normal, p1r, r, g, b, (int)(a1 * 200), 1, 1);
            addVertex(consumer, matrix, normal, p1l, r, g, b, (int)(a1 * 200), 0, 1);
        }
        poseStack.popPose();
    }
    private void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vec3 pos,
                           int r, int g, int b, int a, float u, float v) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)  // 最大亮度
                .normal(normal, 0, 1, 0)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return t.TextureLocation();
    }
}



