package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.BaseRayEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
public class LightningBoltRenderer<T extends BaseRayEntity> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");
    private final float scale;
    private final boolean fullBright;

    public LightningBoltRenderer(EntityRendererProvider.Context context, float f, boolean bl) {
        super(context);
        this.scale = f;
        this.fullBright = bl;
    }

    public LightningBoltRenderer(EntityRendererProvider.Context context) {
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
        Vec3 renderPos = entity.getPosition(partialTick);
        Vec3 startWorld = new Vec3(entity.getLastX(), entity.getLastY(), entity.getLastZ());
        Vec3 endWorld = renderPos;
        double raiseY = entity.getBbHeight() / 3.0;
        Vec3 startLocal = startWorld.subtract(renderPos).add(0.0, raiseY, 0.0);
        Vec3 endLocal = new Vec3(0.0, raiseY, 0.0);
        Vec3 dirLocal = endLocal.subtract(startLocal);
        float totalLen = (float) dirLocal.length();

        if (totalLen < 0.001F) {
            return;
        }

        int timeSeed = entity.getLifeRay() / 2;
        RandomSource random = entity.level().getRandom();
        int uuidHash = entity.getUUID().hashCode();
        float half = entity.getMaxLife() / 2.0f;
        float rawProgress = (entity.getLifeRay() - half) / half;
        float fadeFactor = 1.0f - Mth.clamp(rawProgress, 0.0f, 1.0f);
        float currentAmplitude = 0.8f * fadeFactor;
        int numStrands = 4 + random.nextInt(2);
        int segments = 10 + random.nextInt(4);
        float strandWidth = 0.012f;

        int colorI = entity.beamLightI();
        int colorII = entity.beamLightII();
        int baseInnerR = (colorI >> 16) & 0xFF;
        int baseInnerG = (colorI >> 8) & 0xFF;
        int baseInnerB = colorI & 0xFF;
        int baseOuterR = (colorII >> 16) & 0xFF;
        int baseOuterG = (colorII >> 8) & 0xFF;
        int baseOuterB = colorII & 0xFF;

        int lineAlpha = (int) (200 * fadeFactor);
        ResourceLocation tex = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(tex));

        List<Vec3> startAnchors = new ArrayList<>();
        List<Vec3> endAnchors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            double angle = i * 2 * Math.PI / 5 + timeSeed * 0.02 + uuidHash * 0.001;
            double radius = 0.2 + 0.08 * Math.sin(timeSeed * 0.03 + i * 1.7 + uuidHash * 0.0005);
            startAnchors.add(startLocal.add(
                    radius * Math.cos(angle),
                    radius * 0.3 * Math.sin(angle * 1.3 + uuidHash * 0.001),
                    radius * Math.sin(angle)
            ));
            endAnchors.add(endLocal.add(
                    radius * Math.cos(angle + 1.2 + uuidHash * 0.0005),
                    radius * 0.3 * Math.sin(angle * 1.3 + 0.8 + uuidHash * 0.001),
                    radius * Math.sin(angle + 1.2 + uuidHash * 0.0005)
            ));
        }

        for (int strand = 0; strand < numStrands; strand++) {
            Vec3 startPoint = startAnchors.get(strand % 5);
            Vec3 endPoint = endAnchors.get(strand % 5);
            long seed = (long) uuidHash + (long) timeSeed * 137L + (long) segments * 31L + (long) strand * 7L;
            RandomSource strandRandom = RandomSource.create(seed);

            List<Vec3> points = new ArrayList<>();
            points.add(startPoint);

            for (int i = 1; i < segments; i++) {
                float t = (float) i / segments;
                Vec3 basePos = startPoint.lerp(endPoint, t);
                float envelope = (float) Math.sin(Math.PI * t);

                double noiseX = (strandRandom.nextDouble() - 0.5) * 2.0 * currentAmplitude * envelope;
                double noiseY = (strandRandom.nextDouble() - 0.5) * 1.8 * currentAmplitude * envelope;
                double noiseZ = (strandRandom.nextDouble() - 0.5) * 2.0 * currentAmplitude * envelope;

                double timeOffset = timeSeed * 0.02;
                double wave1 = Math.sin(t * 18.0 + strand * 1.7 + timeOffset + uuidHash * 0.0003) * 0.35 * currentAmplitude * envelope;
                double wave2 = Math.cos(t * 15.0 + strand * 2.3 + timeOffset * 1.2 + uuidHash * 0.0004) * 0.35 * currentAmplitude * envelope;

                double offsetX = noiseX + wave1;
                double offsetY = noiseY + wave2 * 0.6;
                double offsetZ = noiseZ + Math.sin(t * 20.0 + strand * 3.1 + timeOffset * 0.8 + uuidHash * 0.0002) * 0.3 * currentAmplitude * envelope;

                points.add(basePos.add(offsetX, offsetY, offsetZ));
            }
            points.add(endPoint);

            for (int segIdx = 0; segIdx < points.size() - 1; segIdx++) {
                Vec3 p1 = points.get(segIdx);
                Vec3 p2 = points.get(segIdx + 1);
                Vec3 segDir = p2.subtract(p1);
                float segLen = (float) segDir.length();
                if (segLen < 0.001F) continue;

                float hDist = Mth.sqrt((float)(segDir.x * segDir.x + segDir.z * segDir.z));
                float yaw, pitch;
                if (hDist < 1.0E-4F) {
                    yaw = 0.0F;
                    pitch = segDir.y > 0.0 ? -(float)Math.PI / 2F : (float)Math.PI / 2F;
                } else {
                    yaw = (float)Math.atan2(segDir.x, segDir.z);
                    pitch = (float)-Math.atan2(segDir.y, hDist);
                }

                poseStack.pushPose();
                poseStack.translate(p1.x, p1.y, p1.z);
                poseStack.mulPose(Axis.YP.rotation(yaw));
                poseStack.mulPose(Axis.XP.rotation(pitch));

                PoseStack.Pose pose = poseStack.last();
                Matrix4f matrix = pose.pose();
                Matrix3f normal = pose.normal();

                long colorSeed = (long) strand * 137L + (long) segIdx * 331L + (long) timeSeed * 7L;
                RandomSource colorRandom = RandomSource.create(colorSeed);
                int shiftR = colorRandom.nextInt(41) - 20;
                int shiftG = colorRandom.nextInt(41) - 20;
                int shiftB = colorRandom.nextInt(41) - 20;

                int r = Math.max(0, Math.min(255, baseInnerR + shiftR));
                int g = Math.max(0, Math.min(255, baseInnerG + shiftG));
                int b = Math.max(0, Math.min(255, baseInnerB + shiftB));
                int rOut = Math.max(0, Math.min(255, baseOuterR + shiftR / 2));
                int gOut = Math.max(0, Math.min(255, baseOuterG + shiftG / 2));
                int bOut = Math.max(0, Math.min(255, baseOuterB + shiftB / 2));

                float brightFactor = 0.7f + 0.3f * (strand % 3) / 2.0f;
                r = (int)(r * brightFactor);
                g = (int)(g * brightFactor);
                b = (int)(b * brightFactor);
                rOut = (int)(rOut * brightFactor);
                gOut = (int)(gOut * brightFactor);
                bOut = (int)(bOut * brightFactor);

                float halfWidth = strandWidth / 2f;
                float widthFade = 0.5f + 0.5f * fadeFactor;
                float currentHalfWidth = halfWidth * widthFade;

                renderQuadrilateralPipe(
                        consumer, matrix, normal, segLen, currentHalfWidth,
                        r, g, b, lineAlpha, packedLight, false);
                renderQuadrilateralPipe(
                        consumer, matrix, normal, segLen, currentHalfWidth * 1.8f,
                        rOut, gOut, bOut, (int)(lineAlpha * 0.25f), packedLight, false);

                poseStack.popPose();
            }
        }
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