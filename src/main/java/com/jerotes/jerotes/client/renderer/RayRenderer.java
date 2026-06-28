package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.BaseRayEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

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
            renderBeam(entity, partialTick, poseStack, buffer, packedLight);
        }
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderBeam(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float startX = entity.getLastX();
        float startY = entity.getLastY();
        float startZ = entity.getLastZ();
        Vec3 currentPos = entity.getPosition(partialTick);
        float endX = (float) currentPos.x;
        float endY = (float) currentPos.y;
        float endZ = (float) currentPos.z;

        float dx = endX - startX;
        float dy = endY - startY;
        float dz = endZ - startZ;
        float distance = Mth.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.01f) return;

        Vec3 startLocal = new Vec3(startX, startY, startZ).subtract(entity.position());
        Vec3 endLocal   = new Vec3(endX, endY, endZ).subtract(entity.position());

        double raiseY = entity.getBbHeight()/3;
        startLocal = startLocal.add(0, raiseY, 0);
        endLocal   = endLocal.add(0, raiseY, 0);

        Vec3 dirLocal = endLocal.subtract(startLocal);
        float len = (float) dirLocal.length();
        if (len < 0.01f) return;

        poseStack.pushPose();
        poseStack.translate(startLocal.x, startLocal.y, startLocal.z);

        float horizontalDistance = Mth.sqrt((float)(dirLocal.x * dirLocal.x + dirLocal.z * dirLocal.z));
        float yaw, pitch;
        if (horizontalDistance < 1e-4f) {
            yaw = 0;
            pitch = (float) (dirLocal.y > 0 ? -Math.PI / 2 : Math.PI / 2);
        } else {
            yaw = (float) Math.atan2(dirLocal.x, dirLocal.z);
            pitch = (float) -Math.atan2(dirLocal.y, horizontalDistance);
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
        float alpha = 1.0f - Mth.clamp((entity.life - half) / half, 0.0f, 1.0f);
        int innerAlpha = (int) (250 * alpha);
        int outerAlpha = (int) (200 * alpha);

        float scale = entity.beamScale();
        float outerWidth = 0.06f * scale;
        float innerWidth = 0.045f * scale;
        float halfOuter = outerWidth / 2.0f;
        float halfInner = innerWidth / 2.0f;
        VertexConsumer consumer2 = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png")));
        renderQuadrilateralPipe(consumer2, matrix, normal, len, halfOuter, outerR, outerG, outerB, outerAlpha, packedLight, false);
        renderQuadrilateralPipe(consumer, matrix, normal, len, halfInner, innerR, innerG, innerB, innerAlpha, packedLight, false);

        poseStack.popPose();
    }
    private void renderQuadrilateralPipe(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal,
                                         float distance, float halfWidth,
                                         int r, int g, int b, int a, int light, boolean bl) {
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