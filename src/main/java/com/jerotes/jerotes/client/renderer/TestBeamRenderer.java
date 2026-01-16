package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.TestBeam;
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
import net.minecraft.world.item.Items;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class TestBeamRenderer<T extends TestBeam> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public TestBeamRenderer(EntityRendererProvider.Context context, float f, boolean bl) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = f;
        this.fullBright = bl;
    }

    public TestBeamRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0f, true);
    }

    @Override
    public void render(T t, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {

//        poseStack.pushPose();
//        poseStack.scale(this.scale, this.scale, this.scale);
//        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
//        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
//        this.itemRenderer.renderStatic(Items.DIAMOND.getDefaultInstance(), ItemDisplayContext.GROUND, n, OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, ((Entity) t).level(), ((Entity) t).getId());
//        poseStack.popPose();
//        poseStack.translate(0, 0.125 - t.getBbHeight() / 2, 0);

        poseStack.pushPose();
        float f7 = t.getLightLockX();
        float f8 = t.getLightLockY();
        float f9 = t.getLightLockZ();
        float f10 = (float) (f7 - Mth.lerp((double) f2, t.xo, t.getX()));
        float f11 = (float) (f8 - Mth.lerp((double) f2, t.yo, t.getY()));
        float f12 = (float) (f9 - Mth.lerp((double) f2, t.zo, t.getZ()));
        renderLightLock(f10, f11, f12, f2, t.tickCount, poseStack, multiBufferSource, n);
        poseStack.popPose();

        super.render(t, f, f2, poseStack, multiBufferSource, n);
    }

    @Override
    public ResourceLocation getTextureLocation(TestBeam tex) {
        return TEXTURE_LOCATION;
    }

    private static final ResourceLocation LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/test_beam.png");
    private static final RenderType BEAM = JerotesRenderType.glowDoubleSided(LOCATION);

    public static void renderLightLock(float f, float f2, float f3, float f4, int n, PoseStack poseStack, MultiBufferSource multiBufferSource, int n2) {
        float horizontalDistance = Mth.sqrt(f * f + f3 * f3);
        float totalDistance = Mth.sqrt(f * f + f2 * f2 + f3 * f3);

        poseStack.pushPose();
        poseStack.translate(0.0f, 0.5f, 0.0f);
        poseStack.mulPose(Axis.YP.rotation((float)(-Math.atan2(f3, f)) - 1.5707964f));
        poseStack.mulPose(Axis.XP.rotation((float)(-Math.atan2(horizontalDistance, f2)) - 1.5707964f));

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(BEAM);

        float uvBottom = 0.0f - ((float)n + f4) * 0.01f;
        float uvTop = totalDistance / 32.0f - ((float)n + f4) * 0.01f;

        float beamWidth = 0.15f;
        float halfWidth = beamWidth / 2.0f;

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                .color(255, 255, 255, 255).uv(0.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                .color(255, 255, 255, 255).uv(1.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                .color(255, 255, 255, 255).uv(1.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                .color(255, 255, 255, 255).uv(0.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 0.0f, 1.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                .color(255, 255, 255, 255).uv(0.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                .color(255, 255, 255, 255).uv(1.0f, 1.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                .color(255, 255, 255, 255).uv(1.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                .color(255, 255, 255, 255).uv(0.0f, 0.0f)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 0.0f, -1.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                .color(200, 200, 255, 255).uv(0.0f, uvBottom)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                .color(200, 200, 255, 255).uv(1.0f, uvBottom)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                .color(200, 200, 255, 255).uv(1.0f, uvTop)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                .color(200, 200, 255, 255).uv(0.0f, uvTop)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, 1.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                .color(200, 200, 255, 255).uv(0.0f, uvTop)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                .color(200, 200, 255, 255).uv(1.0f, uvTop)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                .color(200, 200, 255, 255).uv(1.0f, uvBottom)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                .color(200, 200, 255, 255).uv(0.0f, uvBottom)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 0.0f, -1.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, totalDistance)
                .color(180, 180, 255, 255).uv(0.0f, uvTop)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, -halfWidth, 0.0f)
                .color(180, 180, 255, 255).uv(0.0f, uvBottom)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, 0.0f)
                .color(180, 180, 255, 255).uv(1.0f, uvBottom)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, -halfWidth, halfWidth, totalDistance)
                .color(180, 180, 255, 255).uv(1.0f, uvTop)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, -1.0f, 0.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, 0.0f)
                .color(180, 180, 255, 255).uv(0.0f, uvBottom)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, -halfWidth, totalDistance)
                .color(180, 180, 255, 255).uv(0.0f, uvTop)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, totalDistance)
                .color(180, 180, 255, 255).uv(1.0f, uvTop)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();

        vertexConsumer.vertex(matrix4f, halfWidth, halfWidth, 0.0f)
                .color(180, 180, 255, 255).uv(1.0f, uvBottom)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(n2)
                .normal(matrix3f, 1.0f, 0.0f, 0.0f).endVertex();

        poseStack.popPose();
    }
}
