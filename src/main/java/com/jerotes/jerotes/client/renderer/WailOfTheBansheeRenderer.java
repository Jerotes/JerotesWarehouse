package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.OtherSpell.WailOfTheBansheeEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class WailOfTheBansheeRenderer extends EntityRenderer<WailOfTheBansheeEntity> {
    private static final ResourceLocation GLOW_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    private static final float RADIUS = 12.0F;
    private static final int BURST_TICK = 24;
    private static final int SOUND_END_TICK = 56;
    private static final float CUM_AT_BURST = 0.492F;

    private static final int SPIRAL_SEGMENTS = 72;
    private static final int RIPPLE_SEGMENTS = 64;

    private static final float[] ENV = {
            0.087F, 0.348F, 0.525F, 0.674F, 0.797F, 0.848F, 0.921F, 0.930F, 0.946F, 0.888F,
            0.930F, 0.902F, 0.947F, 0.922F, 0.921F, 0.912F, 0.893F, 0.927F, 0.929F, 0.954F,
            0.920F, 0.977F, 0.938F, 0.947F, 1.000F, 0.943F, 0.976F, 0.925F, 0.932F, 0.885F,
            0.890F, 0.925F, 0.873F, 0.901F, 0.938F, 0.903F, 0.913F, 0.917F, 0.831F, 0.818F,
            0.797F, 0.711F, 0.753F, 0.719F, 0.707F, 0.679F, 0.672F, 0.628F, 0.557F, 0.471F,
            0.381F, 0.332F, 0.261F, 0.186F, 0.126F, 0.069F, 0.030F, 0.006F, 0.000F, 0.000F
    };

    private static final float[] CUM = {
            0.002F, 0.010F, 0.023F, 0.038F, 0.057F, 0.077F, 0.099F, 0.120F, 0.142F, 0.163F,
            0.185F, 0.206F, 0.229F, 0.250F, 0.272F, 0.293F, 0.314F, 0.336F, 0.358F, 0.380F,
            0.402F, 0.424F, 0.446F, 0.469F, 0.492F, 0.514F, 0.537F, 0.559F, 0.581F, 0.601F,
            0.622F, 0.644F, 0.664F, 0.686F, 0.708F, 0.729F, 0.750F, 0.772F, 0.791F, 0.810F,
            0.829F, 0.846F, 0.863F, 0.880F, 0.897F, 0.913F, 0.929F, 0.943F, 0.956F, 0.967F,
            0.976F, 0.984F, 0.990F, 0.995F, 0.998F, 0.999F, 1.000F, 1.000F, 1.000F, 1.000F
    };

    public WailOfTheBansheeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(WailOfTheBansheeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float life = entity.tickCount + partialTicks;
        if (life > SOUND_END_TICK + 4) {
            return;
        }
        float env = sample(ENV, life);
        VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightning(GLOW_LOCATION));
        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();

        int burstTick = entity.getMaxStart();
        if (life < burstTick) {
            float grow = Mth.clamp(sample(CUM, life) / CUM_AT_BURST, 0.0F, 1.0F);
            float radius = RADIUS * grow;
            float alpha = 0.14F + 0.60F * env;
            this.renderSpiral(pose, consumer, radius, alpha * 0.95F, life * 0.42F, 2.45F, 0.020F);
            this.renderSpiral(pose, consumer, radius * 0.96F, alpha * 0.62F, -life * 0.31F + 1.7F, 3.40F, 0.014F);
            this.renderSpiral(pose, consumer, radius * 0.92F, alpha * 0.42F, life * 0.24F + 3.1F, 4.60F, 0.010F);
            for (int i = 0; i < 4; i++) {
                float t = i / 4.0F;
                float ringRadius = radius * (1.0F - 0.13F * i);
                float y = -radius * 0.55F + radius * 1.05F * (t * 0.7F + grow * 0.6F);
                this.renderRipple(pose, consumer, ringRadius, y, alpha * (0.55F - 0.10F * i), life * 0.9F + i * 1.6F, 0.022F);
            }
        } else {
            float burst = Mth.clamp((life - burstTick) / (SOUND_END_TICK - burstTick), 0.0F, 1.0F);
            float fade = env;
            if (fade > 0.002F) {
                float spin = life * (0.9F + burst * 1.6F);
                this.renderSpiral(pose, consumer, RADIUS * (1.0F + 0.42F * burst), fade * 0.85F, spin, 3.1F, 0.024F);
                this.renderSpiral(pose, consumer, RADIUS * (1.0F + 0.30F * burst), fade * 0.55F, -spin * 0.8F + 2.2F, 4.2F, 0.016F);
                this.renderSpiral(pose, consumer, RADIUS * (1.0F + 0.18F * burst), fade * 0.38F, spin * 0.6F + 4.4F, 5.5F, 0.012F);
                for (int i = 0; i < 6; i++) {
                    float ringRadius = RADIUS * (0.35F + 0.72F * burst + 0.30F * i);
                    this.renderRipple(pose, consumer, ringRadius, 0.05F + i * 0.02F, fade * (0.80F - 0.11F * i), spin * 0.7F + i * 1.1F, Math.max(0.08F, ringRadius * 0.020F));
                }
            }
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static float sample(float[] values, float tick) {
        int i = Mth.floor(tick);
        if (i < 0) {
            return values[0];
        }
        if (i >= values.length - 1) {
            return values[values.length - 1];
        }
        float f = tick - i;
        return Mth.lerp(f, values[i], values[i + 1]);
    }

    private void renderSpiral(PoseStack.Pose pose, VertexConsumer consumer, float radius, float alpha, float phase, float turns, float width) {
        if (radius <= 0.15F || alpha <= 0.002F) {
            return;
        }
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        float bandHalf = Math.max(0.05F, radius * width);
        for (int i = 0; i < SPIRAL_SEGMENTS; i++) {
            float t0 = i / (float) SPIRAL_SEGMENTS;
            float t1 = (i + 1) / (float) SPIRAL_SEGMENTS;
            float th0 = (float) Math.PI * (0.08F + 0.84F * t0);
            float th1 = (float) Math.PI * (0.08F + 0.84F * t1);
            float ph0 = turns * (float) (Math.PI * 2.0) * t0 + phase;
            float ph1 = turns * (float) (Math.PI * 2.0) * t1 + phase;

            float r0 = organicRadius(radius, th0, ph0, phase);
            float r1 = organicRadius(radius, th1, ph1, phase);
            float dth0 = bandHalf / Math.max(0.5F, r0);
            float dth1 = bandHalf / Math.max(0.5F, r1);

            float a0x = sphereX(r0, th0, ph0);
            float a0y = sphereY(r0, th0);
            float a0z = sphereZ(r0, th0, ph0);
            float b0x = sphereX(r0, th0 + dth0, ph0);
            float b0y = sphereY(r0, th0 + dth0);
            float b0z = sphereZ(r0, th0 + dth0, ph0);
            float a1x = sphereX(r1, th1, ph1);
            float a1y = sphereY(r1, th1);
            float a1z = sphereZ(r1, th1, ph1);
            float b1x = sphereX(r1, th1 + dth1, ph1);
            float b1y = sphereY(r1, th1 + dth1);
            float b1z = sphereZ(r1, th1 + dth1, ph1);

            float pulse = 0.55F + 0.45F * Mth.sin(phase * 1.1F + t0 * 9.0F);
            float a = alpha * pulse;
            consumer.vertex(matrix, a0x, a0y, a0z).color(0.88F, 0.88F, 0.82F, a).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix, b0x, b0y, b0z).color(0.44F, 0.38F, 0.25F, a * 0.35F).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix, b1x, b1y, b1z).color(0.44F, 0.38F, 0.25F, a * 0.35F).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix, a1x, a1y, a1z).color(0.88F, 0.88F, 0.82F, a).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        }
    }

    private void renderRipple(PoseStack.Pose pose, VertexConsumer consumer, float radius, float y, float alpha, float phase, float half) {
        if (radius <= 0.1F || alpha <= 0.002F) {
            return;
        }
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();
        for (int i = 0; i < RIPPLE_SEGMENTS; i++) {
            float a0 = (float) (Math.PI * 2.0 * i / RIPPLE_SEGMENTS);
            float a1 = (float) (Math.PI * 2.0 * (i + 1) / RIPPLE_SEGMENTS);
            float wave0 = 1.0F + 0.055F * Mth.sin(a0 * 5.0F + phase) + 0.030F * Mth.sin(a0 * 9.0F - phase * 1.4F);
            float wave1 = 1.0F + 0.055F * Mth.sin(a1 * 5.0F + phase) + 0.030F * Mth.sin(a1 * 9.0F - phase * 1.4F);
            float r0 = radius * wave0;
            float r1 = radius * wave1;
            float x0 = Mth.cos(a0);
            float z0 = Mth.sin(a0);
            float x1 = Mth.cos(a1);
            float z1 = Mth.sin(a1);
            float band = 0.55F + 0.45F * Math.abs(Mth.sin(a0 * 4.0F + phase * 0.8F));
            float a = alpha * band;
            consumer.vertex(matrix, x0 * (r0 - half), y, z0 * (r0 - half)).color(0.75F, 0.69F, 0.63F, a).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix, x1 * (r1 - half), y, z1 * (r1 - half)).color(0.75F, 0.69F, 0.63F, a).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix, x1 * (r1 + half), y, z1 * (r1 + half)).color(0.44F, 0.38F, 0.25F, a * 0.55F).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix, x0 * (r0 + half), y, z0 * (r0 + half)).color(0.44F, 0.38F, 0.25F, a * 0.55F).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
        }
    }

    private static float organicRadius(float radius, float theta, float phi, float phase) {
        float n = Mth.sin(theta * 5.0F + phase * 0.7F) * Mth.sin(phi * 3.0F - phase * 0.5F);
        float m = Mth.sin(theta * 9.0F - phase * 1.3F) * Mth.sin(phi * 7.0F + phase * 0.9F);
        return radius * (1.0F + 0.075F * n + 0.035F * m);
    }

    private static float sphereX(float r, float theta, float phi) {
        return r * Mth.sin(theta) * Mth.cos(phi);
    }

    private static float sphereY(float r, float theta) {
        return r * Mth.cos(theta);
    }

    private static float sphereZ(float r, float theta, float phi) {
        return r * Mth.sin(theta) * Mth.sin(phi);
    }


    @Override
    public ResourceLocation getTextureLocation(WailOfTheBansheeEntity entity) {
        return GLOW_LOCATION;
    }
}
