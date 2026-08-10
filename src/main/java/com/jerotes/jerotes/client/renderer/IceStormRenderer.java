package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modelblock;
import com.jerotes.jerotes.entity.Other.OtherSpell.IceStormEntity;
import com.jerotes.jerotes.entity.Other.ShootTargetSpell.FireballEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public class IceStormRenderer extends EntityRenderer<IceStormEntity> {
	private static final ResourceLocation RAY_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID,
			"textures/entity/projectiles/frost_bolt.png");
	private final Modelblock model;

	public IceStormRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new Modelblock(context.bakeLayer(Modelblock.LAYER_LOCATION));
	}

	@Override
	public void render(IceStormEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		if (entityIn.life < 5) return;

		float partialLife = entityIn.life + partialTicks;

		poseStack.pushPose();

		if (entityIn.life >= 5 && entityIn.life <= 25) {
			renderRangeIndicator(entityIn, partialTicks, poseStack, bufferIn);
		}
		if (partialLife >= IceStormEntity.WARNING_TICK) {
			if (partialLife <= IceStormEntity.WARNING_TICK + 20) {
				renderFallingHail(entityIn, partialLife, poseStack, bufferIn);
			}
		}

		poseStack.popPose();

		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	private void renderRangeIndicator(IceStormEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
		float radius = 6.0f;
		float innerRadius = 5.5f;
		int segments = 32;
		float y = 0.0f;

		VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedTranslucent(RAY_LOCATION));
		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normalMat = pose.normal();

		int alpha = (int) (80 * (entity.life >= 15 ? (1 - (entity.life - 15)/10f) : (entity.life - 5)/10f));
		int r = 255, g = 255, b = 255;

		for (int i = 0; i < segments; i++) {
			float angle1 = (float) i / segments * 2.0f * (float) Math.PI;
			float angle2 = (float) (i + 1) / segments * 2.0f * (float) Math.PI;

			float x1o = radius * (float) Math.cos(angle1);
			float z1o = radius * (float) Math.sin(angle1);
			float x2o = radius * (float) Math.cos(angle2);
			float z2o = radius * (float) Math.sin(angle2);

			float x1i = innerRadius * (float) Math.cos(angle1);
			float z1i = innerRadius * (float) Math.sin(angle1);
			float x2i = innerRadius * (float) Math.cos(angle2);
			float z2i = innerRadius * (float) Math.sin(angle2);

			consumer.vertex(matrix, x1o, y, z1o).color(r, g, b, alpha).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMat, 0, 1, 0).endVertex();
			consumer.vertex(matrix, x2o, y, z2o).color(r, g, b, alpha).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMat, 0, 1, 0).endVertex();
			consumer.vertex(matrix, x2i, y, z2i).color(r, g, b, alpha).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMat, 0, 1, 0).endVertex();
			consumer.vertex(matrix, x1i, y, z1i).color(r, g, b, alpha).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMat, 0, 1, 0).endVertex();
		}
	}

	private void renderFallingHail(IceStormEntity entity, float partialLife, PoseStack poseStack, MultiBufferSource buffer) {
		int hailCount = 40;
		float radius = IceStormEntity.RADIUS - 0.4f;
		float fallSpeed = 1.4f;
		float trailLength = 1.6f;
		float hailWidth = 0.08f;

		float stormLife = partialLife - IceStormEntity.WARNING_TICK;
		float fade = 1.0f;

		if (stormLife > 15f) {
			fade = 1.0f - (stormLife - 15f) / 5f;
			fade = Mth.clamp(fade, 0f, 1f);
		}

		int seed = entity.getId();
		float localLife = partialLife - IceStormEntity.WARNING_TICK;
		float topY = IceStormEntity.HEIGHT_UP;

		//先统一渲染所有RAY_LOCATION拖尾，避免和冰块纹理交叉提交
		VertexConsumer consumer = buffer.getBuffer(
				JerotesRenderType.glowDoubleSidedLightning(RAY_LOCATION)
		);

		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix = pose.pose();
		Matrix3f normalMat = pose.normal();

		for (int i = 0; i < hailCount; i++) {
			float s = i * 53.173f + seed * 3.7f;

			float radiusOffset = hash(s) * radius;
			float angle = hash(s + 1f) * Mth.TWO_PI;

			float x = Mth.cos(angle) * radiusOffset;
			float z = Mth.sin(angle) * radiusOffset;

			float cycle = 14f + hash(s + 2f) * 6f;
			float phase = hash(s + 3f) * cycle;
			float localTime = (localLife * fallSpeed + phase) % cycle;

			float y = topY - localTime;

			if (y < -1f || y > topY) {
				continue;
			}

			float driftX = (hash(s + 4f) - 0.5f) * 0.06f;
			float driftZ = (hash(s + 5f) - 0.5f) * 0.06f;

			float yTail = y + trailLength;

			int alpha = (int)Mth.clamp(
					220f * (1f - localTime / cycle) * fade,
					0f,
					220f
			);

			int tailAlpha = (int)(15f * fade);

			consumer.vertex(matrix, x - hailWidth, y - 0.12f, z)
					.color(255,255,255,alpha)
					.uv(0,0)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(15728880)
					.normal(normalMat,0,0,1)
					.endVertex();
			consumer.vertex(matrix, x + hailWidth, y - 0.12f, z)
					.color(255,255,255,alpha)
					.uv(1,0)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(15728880)
					.normal(normalMat,0,0,1)
					.endVertex();
			consumer.vertex(matrix, x + hailWidth + driftX, yTail, z + driftZ)
					.color(255,255,255,tailAlpha)
					.uv(1,1)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(15728880)
					.normal(normalMat,0,0,1)
					.endVertex();
			consumer.vertex(matrix, x - hailWidth + driftX, yTail, z + driftZ)
					.color(255,255,255,tailAlpha)
					.uv(0,1)
					.overlayCoords(OverlayTexture.NO_OVERLAY)
					.uv2(15728880)
					.normal(normalMat,0,0,1)
					.endVertex();
		}
		VertexConsumer modelConsumer = buffer.getBuffer(
				JerotesRenderType.glowDoubleSidedTranslucent(TEXTURE_LOCATION)
		);

		for (int i = 0; i < hailCount; i++) {
			float s = i * 53.173f + seed * 3.7f;
			float radiusOffset = hash(s) * radius;
			float angle = hash(s + 1f) * Mth.TWO_PI;
			float x = Mth.cos(angle) * radiusOffset;
			float z = Mth.sin(angle) * radiusOffset;
			float cycle = 14f + hash(s + 2f) * 6f;
			float phase = hash(s + 3f) * cycle;
			float localTime = (localLife * fallSpeed + phase) % cycle;
			float y = topY - localTime;
			if (y < -1f || y > topY) {
				continue;
			}
			poseStack.pushPose();
			poseStack.translate(x, y + 0.10f, z);

			poseStack.mulPose(
					Axis.XP.rotationDegrees(hash(s + 10f) * 360f)
			);
			poseStack.mulPose(
					Axis.YP.rotationDegrees(hash(s + 11f) * 360f)
			);
			poseStack.mulPose(
					Axis.ZP.rotationDegrees(hash(s + 12f) * 360f)
			);
			float scale = 0.12f + hash(s + 13f) * 0.06f;
			poseStack.scale(
					-scale,
					-scale,
					scale
			);
			poseStack.translate(
					0f,
					-0.125f * 10f,
					0f
			);
			model.renderToBuffer(
					poseStack,
					modelConsumer,
					15728880,
					OverlayTexture.NO_OVERLAY,
					1f,
					1f,
					1f,
					fade
			);
			poseStack.popPose();
		}
	}
	private static float hash(float seed) {
		int i = Float.floatToIntBits(seed);
		i ^= (i >>> 16);
		i *= 0x7feb352d;
		i ^= (i >>> 15);
		i *= 0x846ca68b;
		i ^= (i >>> 16);
		float mixed = (i & 0x00FFFFFF) * (1.0f / 0x01000000);
		float x = Mth.sin(mixed * 6.2831853f + seed * 0.001f) * 43758.5453f;
		return x - Mth.floor(x);
	}

	@Override
	public ResourceLocation getTextureLocation(IceStormEntity iceStormEntity) {
		return RAY_LOCATION;
	}

	@Override
	public boolean shouldRender(IceStormEntity iceStormEntity, Frustum frustum, double d, double d2, double d3) {
		if (!iceStormEntity.shouldRender(d, d2, d3)) {
			return false;
		} else if (iceStormEntity.noCulling) {
			return true;
		} else {
			AABB aabb = iceStormEntity.getBoundingBoxForCulling().inflate(128.0D);
			if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
				aabb = new AABB(iceStormEntity.getX() - 128.0D, iceStormEntity.getY() - 128.0D, iceStormEntity.getZ() - 128.0D,
						iceStormEntity.getX() + 128.0D, iceStormEntity.getY() + 128.0D, iceStormEntity.getZ() + 128.0D);
			}
			return frustum.isVisible(aabb);
		}
	}
}