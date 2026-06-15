package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.BaseTargetEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class TargetRenderer<T extends BaseTargetEntity> extends EntityRenderer<T> {

    private static final ResourceLocation TEXTURE_LOCATION =
            new ResourceLocation(JerotesWarehouse.MODID,
                    "textures/item/magic_missile.png");
    private static final ResourceLocation GLOW_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");

    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullBright;

    public TargetRenderer(EntityRendererProvider.Context context,
                          float scale,
                          boolean fullBright) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.scale = scale;
        this.fullBright = fullBright;
    }

    public TargetRenderer(EntityRendererProvider.Context context) {
        this(context, 1.0F, true);
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return this.fullBright ? 15 : super.getBlockLightLevel(entity, pos);
    }

    @Override
    public void render(T entity,
                       float entityYaw,
                       float partialTick,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight) {

        if (entity.showRound()) {
            renderRound(entity, partialTick, poseStack, buffer);
        }

        poseStack.pushPose();

        poseStack.scale(this.scale, this.scale, this.scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        this.itemRenderer.renderStatic(
                entity.getItem(),
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private void renderRound(T entity,
                             float partialTick,
                             PoseStack poseStack,
                             MultiBufferSource buffer) {

        float growProgress = 0;
        float fadeAlpha = 0;

        int lifeNeed = entity.getUseLife()/2;
        if (entity.life > 2 && entity.life < lifeNeed) {
            growProgress = (entity.life + partialTick) / (float) lifeNeed;
            fadeAlpha = 1.0F;
        }
        else if (entity.life > 2) {
            growProgress = 1.0F - ((entity.life - lifeNeed + partialTick) / (float) (entity.getMaxLife() - lifeNeed));
            growProgress = Mth.clamp(growProgress, 0F, 1F);
            fadeAlpha = 1.0F;
            fadeAlpha = Mth.clamp(fadeAlpha, 0F, 1F);
        }

        float halfWidth = entity.getSize() * growProgress * 2;
        float height = entity.getSize() * growProgress * 3.0F;

        int topColor = entity.roundLightI();
        int bottomColor = entity.roundLightII();

        int tr = (topColor >> 16) & 255;
        int tg = (topColor >> 8) & 255;
        int tb = topColor & 255;

        int br = (bottomColor >> 16) & 255;
        int bg = (bottomColor >> 8) & 255;
        int bb = bottomColor & 255;

        int bottomAlpha = (int) (220 * fadeAlpha);

        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(GLOW_LOCATION));

        PoseStack.Pose pose = poseStack.last();

        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        float yBottom = 0.0F;
        float yTop = height;

        addQuad(consumer, matrix, normal, -halfWidth, yBottom, halfWidth, -halfWidth, yTop, halfWidth, halfWidth, yTop, halfWidth, halfWidth, yBottom, halfWidth, br, bg, bb, tr, tg, tb, bottomAlpha);
        addQuad(consumer, matrix, normal, halfWidth, yBottom, -halfWidth, halfWidth, yTop, -halfWidth, -halfWidth, yTop, -halfWidth, -halfWidth, yBottom, -halfWidth, br, bg, bb, tr, tg, tb, bottomAlpha);
        addQuad(consumer, matrix, normal, -halfWidth, yBottom, -halfWidth, -halfWidth, yTop, -halfWidth, -halfWidth, yTop, halfWidth, -halfWidth, yBottom, halfWidth, br, bg, bb, tr, tg, tb, bottomAlpha);
        addQuad(consumer, matrix, normal, halfWidth, yBottom, halfWidth, halfWidth, yTop, halfWidth, halfWidth, yTop, -halfWidth, halfWidth, yBottom, -halfWidth, br, bg, bb, tr, tg, tb, bottomAlpha);
    }

    private void addQuad(VertexConsumer consumer,
                         Matrix4f matrix,
                         Matrix3f normal,

                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         float x3, float y3, float z3,
                         float x4, float y4, float z4,

                         int br, int bg, int bb,
                         int tr, int tg, int tb,
                         int bottomAlpha) {

        consumer.vertex(matrix, x1, y1, z1)
                .color(br, bg, bb, bottomAlpha)
                .uv(0, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0, 1, 0)
                .endVertex();

        consumer.vertex(matrix, x2, y2, z2)
                .color(tr, tg, tb, 0)
                .uv(0, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0, 1, 0)
                .endVertex();

        consumer.vertex(matrix, x3, y3, z3)
                .color(tr, tg, tb, 0)
                .uv(1, 1)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0, 1, 0)
                .endVertex();

        consumer.vertex(matrix, x4, y4, z4)
                .color(br, bg, bb, bottomAlpha)
                .uv(1, 0)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE_LOCATION;
    }
}