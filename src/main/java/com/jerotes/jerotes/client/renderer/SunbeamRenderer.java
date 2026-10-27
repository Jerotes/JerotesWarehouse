package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.Beam.BaseBeamEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicBeam.SunbeamEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import org.joml.Quaternionf;
import org.joml.Vector3f;
public class SunbeamRenderer<T extends SunbeamEntity> extends EntityRenderer<T> {

	private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/magic_missile.png");

	public SunbeamRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	protected float getBeamWidthMultiplier(SunbeamEntity baseBeamEntity) {
		return baseBeamEntity.getBeamScale();
	}

	@Override
	public void render(T t, float f, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
		float targetX = t.getTargetX();
		float targetY = t.getTargetY();
		float targetZ = t.getTargetZ();

		float selfX = (float) t.getX();
		float selfY = (float) t.getY();
		float selfZ = (float) t.getZ();

		if (!t.renderInitialized || t.life < 10) {
			t.selfXProgress = selfX;
			t.selfYProgress = selfY;
			t.selfZProgress = selfZ;

			t.targetXProgress = targetX;
			t.targetYProgress = targetY;
			t.targetZProgress = targetZ;

			t.renderInitialized = true;
		} else {
			t.selfXProgress = selfX;
			t.selfYProgress = selfY;
			t.selfZProgress = selfZ;

			float targetSmooth = 0.35f;

			t.targetXProgress = Mth.lerp(targetSmooth, t.targetXProgress, targetX);
			t.targetYProgress = Mth.lerp(targetSmooth, t.targetYProgress, targetY);
			t.targetZProgress = Mth.lerp(targetSmooth, t.targetZProgress, targetZ);
		}

		if (t.tickCount <= 3) {
			return;
		}

		float startX = Mth.lerp(partialTick, (float) t.xo, t.selfXProgress);
		float startY = Mth.lerp(partialTick, (float) t.yo, t.selfYProgress);
		float startZ = Mth.lerp(partialTick, (float) t.zo, t.selfZProgress);

		float f13 = t.targetXProgress - startX;
		float f14 = t.targetYProgress - startY;
		float f15 = t.targetZProgress - startZ;

		poseStack.pushPose();

		poseStack.translate(0.0f, t.getBbHeight() / 2.0f, 0.0f);
		renderLightLock(t, f13, f14, f15, partialTick, t.tickCount, poseStack, multiBufferSource, packedLight);

		poseStack.popPose();

		super.render(t, f, partialTick, poseStack, multiBufferSource, packedLight);
	}
	@Override
	public ResourceLocation getTextureLocation(T tex) {
		return TEXTURE_LOCATION;
	}

	public ResourceLocation getBeamLocation(BaseBeamEntity baseBeamEntity) {
		return baseBeamEntity.getBeamLocation();
	}

	private static Quaternionf rotationBetween(Vector3f from, Vector3f to) {
		Vector3f f = new Vector3f(from).normalize();
		Vector3f t = new Vector3f(to).normalize();
		float dot = f.dot(t);

		if (dot > 0.999999f) {
			return new Quaternionf();
		}
		if (dot < -0.999999f) {
			Vector3f axis = new Vector3f(1.0f, 0.0f, 0.0f).cross(f);
			if (axis.lengthSquared() < 1.0E-6f) {
				axis = new Vector3f(0.0f, 1.0f, 0.0f).cross(f);
			}
			axis.normalize();
			return new Quaternionf().fromAxisAngleRad(axis.x(), axis.y(), axis.z(), (float) Math.PI);
		}
		return new Quaternionf().rotationTo(f, t);
	}

	public void renderLightLock(SunbeamEntity baseBeamEntity, float f, float f2, float f3, float f4, int n,
								PoseStack poseStack, MultiBufferSource multiBufferSource, int n2) {

		float totalDistance = Mth.sqrt(f * f + f2 * f2 + f3 * f3);

		poseStack.pushPose();

		Vector3f dir = new Vector3f(f, f2, f3);
		if (dir.lengthSquared() > 1.0E-6f) {
			dir.normalize();
			Vector3f zAxis = new Vector3f(0.0f, 0.0f, 1.0f);
			Quaternionf rot = rotationBetween(zAxis, dir);
			poseStack.mulPose(rot);
		}

		ResourceLocation tex = baseBeamEntity.getBeamLocation();
		float widthMul = getBeamWidthMultiplier(baseBeamEntity);

		renderBeamCore(tex, f4, n, poseStack, multiBufferSource, n2, totalDistance, widthMul);

		if (totalDistance < 0.05f) {
			poseStack.popPose();
			return;
		}

		float time = n + f4;
		int seed = baseBeamEntity.getId();

		renderOrbitingBands(tex, time, seed, poseStack, multiBufferSource, n2, totalDistance, widthMul);
		renderSparkles(tex, time, seed, poseStack, multiBufferSource, n2, totalDistance, widthMul);
		renderOriginRays(tex, time, seed, poseStack, multiBufferSource, n2, totalDistance, widthMul);
		renderPlayerCube(tex, time, seed, poseStack, multiBufferSource, n2, totalDistance, widthMul);
		renderEndpointOrb(baseBeamEntity, tex, time, seed, poseStack, multiBufferSource, n2, totalDistance, widthMul);
		renderTopFlare(tex, time, seed, poseStack, multiBufferSource, n2, totalDistance);
		renderTopFlareSelf(baseBeamEntity, tex, time, seed, poseStack, multiBufferSource, n2, totalDistance);

		poseStack.popPose();
	}

	private void renderBeamCore(ResourceLocation tex, float f4, int n,
								PoseStack poseStack, MultiBufferSource multiBufferSource, int light, float totalDistance, float widthMul) {

		float uvBottom = 0.0f - ((float) n + f4) * 0.01f;
		float uvTop = totalDistance / 32.0f - ((float) n + f4) * 0.01f;

		float beamWidth = 0.15f * widthMul;
		float halfWidth = beamWidth / 2.0f;

		PoseStack.Pose pose = poseStack.last();
		Matrix4f matrix4f = pose.pose();
		Matrix3f matrix3f = pose.normal();
		Vector3f fixedNormal = new Vector3f(0.0f, 0.0f, 1.0f);

		for (boolean glow : new boolean[]{true, false}) {
			VertexConsumer consumer = glow
					? multiBufferSource.getBuffer(JerotesRenderType.glowDoubleSidedLightningOverlay(tex))
					: multiBufferSource.getBuffer(JerotesRenderType.glowDoubleSided(tex));
			int a = glow ? 20 : 255;

			addQuad(consumer, matrix4f, matrix3f, fixedNormal, light,
					-halfWidth, -halfWidth, totalDistance, 0.0f, 0.0f,
					halfWidth, -halfWidth, totalDistance, 1.0f, 0.0f,
					halfWidth, halfWidth, totalDistance, 1.0f, 1.0f,
					-halfWidth, halfWidth, totalDistance, 0.0f, 1.0f,
					255, 255, 255, a);

			addQuad(consumer, matrix4f, matrix3f, fixedNormal, light,
					-halfWidth, halfWidth, 0.0f, 0.0f, 1.0f,
					halfWidth, halfWidth, 0.0f, 1.0f, 1.0f,
					halfWidth, -halfWidth, 0.0f, 1.0f, 0.0f,
					-halfWidth, -halfWidth, 0.0f, 0.0f, 0.0f,
					255, 255, 255, a);

			addQuad(consumer, matrix4f, matrix3f, fixedNormal, light,
					-halfWidth, halfWidth, 0.0f, 0.0f, uvBottom,
					halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
					halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
					-halfWidth, halfWidth, totalDistance, 0.0f, uvTop,
					200, 200, 255, a);

			addQuad(consumer, matrix4f, matrix3f, fixedNormal, light,
					-halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
					halfWidth, -halfWidth, totalDistance, 1.0f, uvTop,
					halfWidth, -halfWidth, 0.0f, 1.0f, uvBottom,
					-halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
					200, 200, 255, a);

			addQuad(consumer, matrix4f, matrix3f, fixedNormal, light,
					-halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
					-halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
					-halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
					-halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
					180, 180, 255, a);

			addQuad(consumer, matrix4f, matrix3f, fixedNormal, light,
					halfWidth, -halfWidth, 0.0f, 0.0f, uvBottom,
					halfWidth, -halfWidth, totalDistance, 0.0f, uvTop,
					halfWidth, halfWidth, totalDistance, 1.0f, uvTop,
					halfWidth, halfWidth, 0.0f, 1.0f, uvBottom,
					180, 180, 255, a);
		}
	}

	private void renderOrbitingBands(ResourceLocation tex, float time, int seed,
									 PoseStack poseStack, MultiBufferSource buffer, int light, float totalDistance, float widthMul) {

		int strandCount = 2;
		int segments = 24;
		float drillSpeed = 0.15f;
		float maxTwist = (float) (2.0 * Math.PI);
		float wrapRadius = 0.22f * widthMul;
		float bandHalfWidth = 0.045f * widthMul;

		Matrix4f matrix = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningOverlay(tex));

		for (int s = 0; s < strandCount; s++) {
			float strandPhase = s * (float) Math.PI;
			boolean blueBand = (s == 0);

			Vec3[] leftPoints = new Vec3[segments + 1];
			Vec3[] rightPoints = new Vec3[segments + 1];
			int[] rr = new int[segments + 1];
			int[] gg = new int[segments + 1];
			int[] bb = new int[segments + 1];
			int[] aa = new int[segments + 1];

			for (int i = 0; i <= segments; i++) {
				float progress = (float) i / segments;
				float z = progress * totalDistance;

				float angle = time * drillSpeed + progress * maxTwist + strandPhase + seed * 0.1f;
				float wobble = 0.08f * Mth.sin(time * 0.3f + progress * 6f + s);
				float currentRadius = wrapRadius * (1.0f + wobble);

				float cos = Mth.cos(angle);
				float sin = Mth.sin(angle);

				Vec3 point = new Vec3(cos * currentRadius, sin * currentRadius, z);

				Vec3 tangent = new Vec3(-sin, cos, 0).normalize();
				leftPoints[i] = point.add(tangent.scale(-bandHalfWidth));
				rightPoints[i] = point.add(tangent.scale(bandHalfWidth));

				if (blueBand) {
					rr[i] = (int) Mth.lerp(progress, 190, 225);
					gg[i] = (int) Mth.lerp(progress, 215, 240);
					bb[i] = 255;
				} else {
					rr[i] = 255;
					gg[i] = (int) Mth.lerp(progress, 205, 175);
					bb[i] = (int) Mth.lerp(progress, 140, 90);
				}

				float alpha = 0.9f * (0.5f + 0.5f * Mth.sin(progress * (float) Math.PI));
				aa[i] = (int) Mth.clamp(alpha * 255f, 30f, 230f);
			}

			for (int i = 0; i < segments; i++) {
				float u0 = (float) i / segments;
				float u1 = (float) (i + 1) / segments;

				addDoubleSidedQuad(consumer, matrix, normal, light,
						leftPoints[i], rr[i], gg[i], bb[i], aa[i], u0, 0,
						rightPoints[i], rr[i], gg[i], bb[i], aa[i], u0, 1,
						rightPoints[i + 1], rr[i + 1], gg[i + 1], bb[i + 1], aa[i + 1], u1, 1,
						leftPoints[i + 1], rr[i + 1], gg[i + 1], bb[i + 1], aa[i + 1], u1, 0);
			}
		}
	}

	private void renderSparkles(ResourceLocation tex, float time, int seed,
								PoseStack poseStack, MultiBufferSource buffer, int light, float totalDistance, float widthMul) {

		int sparkCount = 18;
		float cycleLength = 32.0f;

		Matrix4f matrix = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningOverlay(tex));

		for (int i = 0; i < sparkCount; i++) {
			float s = i * 63.117f + seed * 9.31f;

			float cycleOffset = hash(s) * cycleLength;
			float localTime = (time + cycleOffset) % cycleLength;
			float riseProgress = localTime / cycleLength;

			float height = riseProgress * totalDistance;

			float angle = hash(s + 1.0f) * (float) (2 * Math.PI) + time * (0.1f + hash(s + 2f) * 0.08f);
			float radius = (0.06f + hash(s + 3f) * 0.10f) * widthMul;

			float x = Mth.cos(angle) * radius;
			float y = Mth.sin(angle) * radius;

			float size = (0.03f + hash(s + 4f) * 0.02f) * widthMul;

			float fadeIn = Mth.clamp(riseProgress * 8f, 0f, 1f);
			float fadeOut = Mth.clamp((1f - riseProgress) * 4f, 0f, 1f);
			int alpha = (int) (200 * fadeIn * fadeOut);
			if (alpha <= 2) continue;

			int r = 255;
			int g = (int) Mth.lerp(hash(s + 6f), 235, 250);
			int b = (int) Mth.lerp(hash(s + 6f), 165, 210);

			Vec3 center = new Vec3(x, y, height);
			float sparkAngle = hash(s + 5f) * (float) Math.PI;
			Vec3 right = new Vec3(Mth.cos(sparkAngle), Mth.sin(sparkAngle), 0).scale(size);
			Vec3 up = new Vec3(0, 0, size);

			Vec3 v0 = center.subtract(right).subtract(up);
			Vec3 v1 = center.add(right).subtract(up);
			Vec3 v2 = center.add(right).add(up);
			Vec3 v3 = center.subtract(right).add(up);

			addVertex(consumer, matrix, normal, v0, r, g, b, alpha, 0, 0, light);
			addVertex(consumer, matrix, normal, v1, r, g, b, alpha, 1, 0, light);
			addVertex(consumer, matrix, normal, v2, r, g, b, alpha, 1, 1, light);
			addVertex(consumer, matrix, normal, v3, r, g, b, alpha, 0, 1, light);
		}
	}

	private void renderOriginRays(ResourceLocation tex, float time, int seed,
								  PoseStack poseStack, MultiBufferSource buffer, int light, float totalDistance, float widthMul) {

		int rayCount = 10;
		float spin = time * 0.04f;

		Matrix4f matrix = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningOverlay(tex));

		float maxRayLength = Math.min(3.5f, totalDistance * 0.6f) * widthMul;
		Vec3 origin = new Vec3(0, 0, 0);

		for (int i = 0; i < rayCount; i++) {
			float angle = (float) (i * 2 * Math.PI / rayCount) + spin + hash(seed + i) * 0.5f;

			float flick = 0.5f + 0.5f * Mth.sin(time * 0.25f + i * 2.1f + seed);
			float rayLength = maxRayLength * (0.55f + 0.45f * flick);
			float rayHalfWidth = 0.02f * widthMul;

			Vec3 outDir = new Vec3(Mth.cos(angle), Mth.sin(angle), -0.15f).normalize();
			Vec3 tip = origin.add(outDir.scale(rayLength));
			Vec3 perp = new Vec3(-Mth.sin(angle), Mth.cos(angle), 0).scale(rayHalfWidth);

			Vec3 v0 = origin.subtract(perp);
			Vec3 v1 = origin.add(perp);
			Vec3 v2 = tip;

			int alphaBase = (int) (60 * (0.6f + 0.4f * flick));

			addVertex(consumer, matrix, normal, v0, 255, 205, 155, alphaBase, 0, 0, light);
			addVertex(consumer, matrix, normal, v1, 255, 205, 155, alphaBase, 1, 0, light);
			addVertex(consumer, matrix, normal, v2, 255, 225, 195, 0, 0.5f, 1, light);
			addVertex(consumer, matrix, normal, v2, 255, 225, 195, 0, 0.5f, 1, light);
		}
	}

	private void renderEndpointOrb(SunbeamEntity baseBeamEntity, ResourceLocation tex, float time, int seed,
								   PoseStack poseStack, MultiBufferSource buffer, int light,
								   float totalDistance, float widthMul) {

		Matrix4f matrix = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningOverlay(tex));

		float pulse = 1.0f + 0.08f * Mth.sin(time * 0.4f + seed);
		float coreSize = 0.22f * widthMul * pulse * getBeamWidthMultiplier(baseBeamEntity) / 1.5f;
		int coreAlpha = 220;

		Vec3 c = new Vec3(0, 0, totalDistance - 0.06f);
		Vec3 rgt = new Vec3(coreSize, 0, 0);
		Vec3 upv = new Vec3(0, coreSize, 0);

		addVertex(consumer, matrix, normal, c.subtract(rgt).subtract(upv), 255, 255, 255, coreAlpha, 0, 0, light);
		addVertex(consumer, matrix, normal, c.add(rgt).subtract(upv), 255, 255, 255, coreAlpha, 1, 0, light);
		addVertex(consumer, matrix, normal, c.add(rgt).add(upv), 255, 255, 255, coreAlpha, 1, 1, light);
		addVertex(consumer, matrix, normal, c.subtract(rgt).add(upv), 255, 255, 255, coreAlpha, 0, 1, light);

		float haloSize = coreSize * 1.9f;
		Vec3 rgt2 = new Vec3(haloSize, 0, 0);
		Vec3 upv2 = new Vec3(0, haloSize, 0);
		int haloAlpha = 130;
		addVertex(consumer, matrix, normal, c.subtract(rgt2).subtract(upv2), 210, 225, 255, haloAlpha, 0, 0, light);
		addVertex(consumer, matrix, normal, c.add(rgt2).subtract(upv2), 210, 225, 255, haloAlpha, 1, 0, light);
		addVertex(consumer, matrix, normal, c.add(rgt2).add(upv2), 210, 225, 255, haloAlpha, 1, 1, light);
		addVertex(consumer, matrix, normal, c.subtract(rgt2).add(upv2), 210, 225, 255, haloAlpha, 0, 1, light);
	}

	private void renderTopFlare(ResourceLocation tex, float time, int seed,
								PoseStack poseStack, MultiBufferSource buffer, int light, float totalDistance) {

		int petals = 7;
		float spin = time * 0.09f;

		Matrix4f matrix = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningOverlay(tex));

		for (int i = 0; i < petals; i++) {
			float angle = (float) (i * 2 * Math.PI / petals) + spin;

			float flick = 0.15f * Mth.sin(time * 0.5f + i * 1.7f + seed);
			float petalLength = 0.28f + flick;
			float petalWidth = 0.05f;

			Vec3 base = new Vec3(0, 0, 0);
			Vec3 dirOut = new Vec3(Mth.cos(angle), Mth.sin(angle), 0.35f).normalize();
			Vec3 tip = base.add(dirOut.scale(petalLength));

			Vec3 perp = new Vec3(-Mth.sin(angle), Mth.cos(angle), 0).scale(petalWidth);

			Vec3 v0 = base.subtract(perp);
			Vec3 v1 = base.add(perp);
			Vec3 v2 = tip;

			int alphaBase = 200;
			int alphaTip = 30;

			addVertex(consumer, matrix, normal, v0, 255, 140, 40, alphaBase, 0, 0, light);
			addVertex(consumer, matrix, normal, v1, 255, 140, 40, alphaBase, 1, 0, light);
			addVertex(consumer, matrix, normal, v2, 255, 220, 160, alphaTip, 0.5f, 1, light);
			addVertex(consumer, matrix, normal, v2, 255, 220, 160, alphaTip, 0.5f, 1, light);
		}

		float coreSize = 0.1f;
		int coreAlpha = 180;
		Vec3 c = new Vec3(0, 0, 0);
		Vec3 rgt = new Vec3(coreSize, 0, 0);
		Vec3 upv = new Vec3(0, coreSize, 0);
		addVertex(consumer, matrix, normal, c.subtract(rgt).subtract(upv), 255, 255, 220, coreAlpha, 0, 0, light);
		addVertex(consumer, matrix, normal, c.add(rgt).subtract(upv), 255, 255, 220, coreAlpha, 1, 0, light);
		addVertex(consumer, matrix, normal, c.add(rgt).add(upv), 255, 240, 180, (int) (coreAlpha * 0.5f), 1, 1, light);
		addVertex(consumer, matrix, normal, c.subtract(rgt).add(upv), 255, 240, 180, (int) (coreAlpha * 0.5f), 0, 1, light);
	}

	private void renderTopFlareSelf(SunbeamEntity sunbeamEntity, ResourceLocation tex, float time, int seed,
									PoseStack poseStack, MultiBufferSource buffer, int light, float totalDistance) {
		int petals = 7;
		float spin = time * 0.09f;
		float flareScale = 2.0f * getBeamWidthMultiplier(sunbeamEntity);

		Matrix4f matrix = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningOverlay(tex));

		float selfZ = totalDistance - 0.28f;

		for (int i = 0; i < petals; i++) {
			float angle = (float) (i * 2 * Math.PI / petals) + spin;
			float flick = 0.4f * Mth.sin(time * 0.5f + i * 1.7f + seed);
			float petalLength = (1.2f + flick) * flareScale;
			float petalWidth = 0.16f * flareScale;

			Vec3 base = new Vec3(0, 0, selfZ);
			Vec3 dirOut = new Vec3(Mth.cos(angle), Mth.sin(angle), -0.35f).normalize();
			Vec3 tip = base.add(dirOut.scale(petalLength));
			Vec3 perp = new Vec3(-Mth.sin(angle), Mth.cos(angle), 0).scale(petalWidth);

			Vec3 v0 = base.subtract(perp);
			Vec3 v1 = base.add(perp);
			Vec3 v2 = tip;

			int alphaBase = 220;
			int alphaTip = 40;

			addVertex(consumer, matrix, normal, v0, 255, 140, 40, alphaBase, 0, 0, light);
			addVertex(consumer, matrix, normal, v1, 255, 140, 40, alphaBase, 1, 0, light);
			addVertex(consumer, matrix, normal, v2, 255, 220, 160, alphaTip, 0.5f, 1, light);
			addVertex(consumer, matrix, normal, v2, 255, 220, 160, alphaTip, 0.5f, 1, light);
		}

		float coreSize = 0.3f * flareScale;
		int coreAlpha = 220;
		Vec3 c = new Vec3(0, 0, selfZ);
		Vec3 rgt = new Vec3(coreSize, 0, 0);
		Vec3 upv = new Vec3(0, coreSize, 0);

		addVertex(consumer, matrix, normal, c.subtract(rgt).subtract(upv), 255, 255, 220, coreAlpha, 0, 0, light);
		addVertex(consumer, matrix, normal, c.add(rgt).subtract(upv), 255, 255, 220, coreAlpha, 1, 0, light);
		addVertex(consumer, matrix, normal, c.add(rgt).add(upv), 255, 240, 180, (int) (coreAlpha * 0.5f), 1, 1, light);
		addVertex(consumer, matrix, normal, c.subtract(rgt).add(upv), 255, 240, 180, (int) (coreAlpha * 0.5f), 0, 1, light);
	}

	private void renderPlayerCube(ResourceLocation tex, float time, int seed,
								  PoseStack poseStack, MultiBufferSource buffer, int light, float totalDistance, float widthMul) {

		Matrix4f matrix = poseStack.last().pose();
		Matrix3f normal = poseStack.last().normal();
		VertexConsumer consumer = buffer.getBuffer(JerotesRenderType.glowDoubleSidedLightningOverlay(tex));

		float size = 0.13f * widthMul;
		float angleY = time * 0.6f + seed * 0.3f;
		float angleX = time * 0.4f + seed * 0.7f;

		Quaternionf spin = new Quaternionf().rotateY(angleY).rotateX(angleX);
		float cubeZ = 0.3f;
		Vec3 center = new Vec3(0, 0, cubeZ);

		Vector3f[] corners = new Vector3f[8];
		for (int b = 0; b < 8; b++) {
			float xs = ((b & 1) == 0) ? -size : size;
			float ys = ((b & 2) == 0) ? -size : size;
			float zs = ((b & 4) == 0) ? -size : size;
			Vector3f v = new Vector3f(xs, ys, zs);
			spin.transform(v);
			corners[b] = v;
		}

		int r = 235, g = 240, b2 = 255, a = 170;

		renderCubeFace(consumer, matrix, normal, light, center, corners, 4, 6, 7, 5, r, g, b2, a);
		renderCubeFace(consumer, matrix, normal, light, center, corners, 1, 3, 2, 0, r, g, b2, a);
		renderCubeFace(consumer, matrix, normal, light, center, corners, 2, 3, 7, 6, r, g, b2, a);
		renderCubeFace(consumer, matrix, normal, light, center, corners, 0, 4, 5, 1, r, g, b2, a);
		renderCubeFace(consumer, matrix, normal, light, center, corners, 1, 5, 7, 3, 255, 255, 255, a);
		renderCubeFace(consumer, matrix, normal, light, center, corners, 0, 2, 6, 4, 255, 255, 255, a);
	}

	private static void renderCubeFace(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, int light,
									   Vec3 center, Vector3f[] corners, int i0, int i1, int i2, int i3,
									   int r, int g, int b, int a) {
		addVertex(consumer, matrix, normal, center.add(new Vec3(corners[i0])), r, g, b, a, 0, 0, light);
		addVertex(consumer, matrix, normal, center.add(new Vec3(corners[i1])), r, g, b, a, 1, 0, light);
		addVertex(consumer, matrix, normal, center.add(new Vec3(corners[i2])), r, g, b, a, 1, 1, light);
		addVertex(consumer, matrix, normal, center.add(new Vec3(corners[i3])), r, g, b, a, 0, 1, light);
	}

	private static void addQuad(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vector3f fixedNormal, int light,
								float x0, float y0, float z0, float u0, float v0,
								float x1, float y1, float z1, float u1, float v1,
								float x2, float y2, float z2, float u2, float v2,
								float x3, float y3, float z3, float u3, float v3,
								int r, int g, int b, int a) {
		emitQuad(consumer, matrix, normal, fixedNormal, light,
				x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3,
				r, g, b, a);
		emitQuad(consumer, matrix, normal, fixedNormal, light,
				x0, y0, z0, u0, v0, x3, y3, z3, u3, v3, x2, y2, z2, u2, v2, x1, y1, z1, u1, v1,
				r, g, b, a);
	}

	private static void emitQuad(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vector3f fixedNormal, int light,
								 float x0, float y0, float z0, float u0, float v0,
								 float x1, float y1, float z1, float u1, float v1,
								 float x2, float y2, float z2, float u2, float v2,
								 float x3, float y3, float z3, float u3, float v3,
								 int r, int g, int b, int a) {
		consumer.vertex(matrix, x0, y0, z0).color(r, g, b, a).uv(u0, v0)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
				.normal(normal, fixedNormal.x, fixedNormal.y, fixedNormal.z).endVertex();
		consumer.vertex(matrix, x1, y1, z1).color(r, g, b, a).uv(u1, v1)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
				.normal(normal, fixedNormal.x, fixedNormal.y, fixedNormal.z).endVertex();
		consumer.vertex(matrix, x2, y2, z2).color(r, g, b, a).uv(u2, v2)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
				.normal(normal, fixedNormal.x, fixedNormal.y, fixedNormal.z).endVertex();
		consumer.vertex(matrix, x3, y3, z3).color(r, g, b, a).uv(u3, v3)
				.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
				.normal(normal, fixedNormal.x, fixedNormal.y, fixedNormal.z).endVertex();
	}

	private static void addDoubleSidedQuad(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, int light,
										   Vec3 p0, int r0, int g0, int b0, int a0, float u0, float v0,
										   Vec3 p1, int r1, int g1, int b1, int a1, float u1, float v1,
										   Vec3 p2, int r2, int g2, int b2, int a2, float u2, float v2,
										   Vec3 p3, int r3, int g3, int b3, int a3, float u3, float v3) {
		addVertex(consumer, matrix, normal, p0, r0, g0, b0, a0, u0, v0, light);
		addVertex(consumer, matrix, normal, p1, r1, g1, b1, a1, u1, v1, light);
		addVertex(consumer, matrix, normal, p2, r2, g2, b2, a2, u2, v2, light);
		addVertex(consumer, matrix, normal, p3, r3, g3, b3, a3, u3, v3, light);

		addVertex(consumer, matrix, normal, p0, r0, g0, b0, a0, u0, v0, light);
		addVertex(consumer, matrix, normal, p3, r3, g3, b3, a3, u3, v3, light);
		addVertex(consumer, matrix, normal, p2, r2, g2, b2, a2, u2, v2, light);
		addVertex(consumer, matrix, normal, p1, r1, g1, b1, a1, u1, v1, light);
	}

	private static void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vec3 pos,
								  int r, int g, int b, int a, float u, float v, int light) {
		consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
				.color(r, g, b, a)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(light)
				.normal(normal, 0.0f, 0.0f, 1.0f)
				.endVertex();
	}

	private static float hash(float seed) {
		float x = Mth.sin(seed * 12.9898f) * 43758.5453f;
		return x - Mth.floor(x);
	}

	@Override
	public boolean shouldRender(T entity, Frustum frustum, double d, double d2, double d3) {
		if (!entity.shouldRender(d, d2, d3)) {
			return false;
		} else if (entity.noCulling) {
			return true;
		} else {
			AABB aabb = entity.getBoundingBoxForCulling().inflate(128.0D);
			if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
				aabb = new AABB(entity.getX() - 128.0D, entity.getY() - 128.0D, entity.getZ() - 128.0D,
						entity.getX() + 128.0D, entity.getY() + 128.0D, entity.getZ() + 128.0D);
			}
			return frustum.isVisible(aabb);
		}
	}
}