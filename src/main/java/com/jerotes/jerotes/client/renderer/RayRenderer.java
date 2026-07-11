package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.BaseRayEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.lang.Math;

public class RayRenderer<T extends BaseRayEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");
    private final float scale;
    private final boolean fullBright;

    public RayRenderer(EntityRendererProvider.Context context, float f, boolean bl) {
        super(context);
        this.scale = f;
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

        int colorI = entity.beamLightI();
        int colorII = entity.beamLightII();
        int innerR = (colorI >> 16) & 0xFF;
        int innerG = (colorI >> 8) & 0xFF;
        int innerB = colorI & 0xFF;
        int outerR = (colorII >> 16) & 0xFF;
        int outerG = (colorII >> 8) & 0xFF;
        int outerB = colorII & 0xFF;
        float half = entity.getMaxLife() / 2.0f;
        float lifeAbout = Mth.clamp((entity.getLifeRay() - half) / half, 0.0f, 1.0f);
        float alpha = 1.0f;
        int innerAlpha = (int) (255 * alpha);
        int outerAlpha = (int) (180 * alpha);

        float scale = entity.beamScale() * (1- lifeAbout);
        float outerWidth = 0.065f * scale;
        float innerWidth = 0.055f * scale;
        float halfOuter = outerWidth / 2.0f;
        float halfInner = innerWidth / 2.0f;
        VertexConsumer consumer2 = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
        float overlap = outerWidth * 0.35F;

        renderQuadrilateralPipe(consumer2, matrix, normal, len, halfOuter, outerR, outerG, outerB, outerAlpha, packedLight, false);
        renderQuadrilateralPipe(consumer, matrix, normal, len, halfInner, innerR, innerG, innerB, innerAlpha, packedLight, false);

        poseStack.popPose();
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
    @Override
    public ResourceLocation getTextureLocation(BaseRayEntity tex) {
        return TEXTURE_LOCATION;
    }
}