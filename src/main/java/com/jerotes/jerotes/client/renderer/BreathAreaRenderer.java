package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.entity.Other.BreathAreaSpell.BaseBreathAreaEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.culling.Frustum;

import javax.annotation.Nonnull;
public class BreathAreaRenderer extends EntityRenderer<BaseBreathAreaEntity> {

	private static final float FORWARD_OFFSET = 0.4f;
	private static final float RING_STRIP_HALF_THICK = 0.045f;
	private static final float RING_STRIP_ARC_DEG = 4.5f;
	private static final float RING_SPIN_SPEED = 14f;
	private static final float FLICKER_FREQ = 1.4f;

	private static final int CONE_SEGMENTS = 4;
	private static final float CONE_WAVE_AMP = 0.09f;
	private static final float CONE_WAVE_FREQ = 1.6f;
	private static final float CONE_WAVE_SPEED = 3.0f;
	private static final float CONE_MAX_HALF_WIDTH = 0.09f;
	private static final float CONE_FLIGHT_FRAC = 1.0f;
	private static final float CONE_DRIFT_EXTRA = 0.0f;
	private static final float CONE_TRAIL_LEN_FRAC = 0.30f;
	private static final float CONE_IGNITE_SPREAD = 0.35f;

	public BreathAreaRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(BaseBreathAreaEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		Entity ownerRaw = entityIn.getOwner();
		Entity caster = ownerRaw != null ? ownerRaw : entityIn;

		Vec3 casterPos = new Vec3(
				Mth.lerp(partialTicks, caster.xOld, caster.getX()),
				Mth.lerp(partialTicks, caster.yOld, caster.getY()),
				Mth.lerp(partialTicks, caster.zOld, caster.getZ())
		);

		Vec3 forward = caster.getViewVector(partialTicks);
		if (forward.lengthSqr() < 1.0E-6) forward = new Vec3(0, 0, 1);
		forward = forward.normalize();

		Vec3 worldUp = new Vec3(0, 1, 0);
		Vec3 right = forward.cross(worldUp).normalize();
		if (right.lengthSqr() < 1.0E-6) right = new Vec3(1, 0, 0);
		Vec3 up = right.cross(forward).normalize();

		Vec3 origin = casterPos
				.add(0, caster.getBbHeight() * 0.7, 0)
				.add(forward.scale(FORWARD_OFFSET));

		Vec3 entityRenderPos = new Vec3(
				Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX()),
				Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY()),
				Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ())
		);

		float life = entityIn.getLifeTick() + partialTicks;
		int circleStart = entityIn.getCircleStart();
		int circleStop = entityIn.getCircleStop();
		int stripeStart = entityIn.getBallStopStripeStart();
		int stripeStop = entityIn.getStripeStop();
		int maxLife = entityIn.getMaxLife();

		float globalFade = 1.0f;
		if (life > stripeStop) {
			globalFade = 1.0f - Mth.clamp((life - stripeStop) / Math.max(1, (maxLife - stripeStop)), 0f, 1f);
		}

		if (globalFade > 0.001f) {
			VertexConsumer consumer = bufferIn.getBuffer(JerotesRenderType.glowDoubleSided(this.getTextureLocation(entityIn)));
			Matrix4f matrix = poseStack.last().pose();
			Matrix3f normalMat = poseStack.last().normal();

			if (life <= circleStart) {
				float radiusMul = entityIn.computeRingRadiusMul(life);
				float t = Mth.clamp(life / Math.max(1, circleStart), 0f, 1f);
				float alphaMul = Mth.clamp(t * 1.5f, 0f, 1f);
				renderRings(entityIn, consumer, matrix, normalMat, entityRenderPos, origin, right, up, forward, life, radiusMul, alphaMul, globalFade);
			} else if (life <= circleStop) {
				float radiusMul = entityIn.computeRingRadiusMul(life);
				renderRings(entityIn, consumer, matrix, normalMat, entityRenderPos, origin, right, up, forward, life, radiusMul, 1.0f, globalFade);
			} else if (life <= stripeStart) {
				float pulse = 0.85f + 0.15f * Mth.sin(life * 0.9f);
				renderRings(entityIn, consumer, matrix, normalMat, entityRenderPos, origin, right, up, forward, life, entityIn.computeRingRadiusMul(life), pulse, globalFade);
			}
			// else {
			//     float t = Mth.clamp((life - stripeStart) / Math.max(1, (stripeStop - stripeStart)), 0f, 1f);
			//     renderCone(entityIn, consumer, matrix, normalMat, entityRenderPos, origin, forward, right, up, t, life, globalFade);
			// }
		}

		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	private void renderRings(BaseBreathAreaEntity entityIn, VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMat,
							 Vec3 entityRenderPos, Vec3 origin, Vec3 right, Vec3 up, Vec3 forward,
							 float life, float radiusMul, float alphaMul, float globalFade) {
		int stripCount = entityIn.getStripCount();
		float innerRadius = entityIn.getInnerCircleRadius();
		float outerRadius = entityIn.getOuterCircleRadius();
		int colorI = entityIn.beamLightI();
		int colorII = entityIn.beamLightII();
		int colorIII = entityIn.beamLightIII();
		int id = entityIn.getId();
		Vec3 thickDir = forward.scale(RING_STRIP_HALF_THICK);

		for (int i = 0; i < stripCount; i++) {
			boolean innerRing = (i & 1) == 0;
			float baseRadius = innerRing ? innerRadius : outerRadius;
			float radius = baseRadius * radiusMul;
			if (radius <= 0.001f) continue;

			float baseAngle = 360f * i / stripCount;
			float jitterStatic = (hash01(id, i, 1) - 0.5f) * 6f;
			float spinDir = innerRing ? 1f : -1f;
			float spinMul = 0.85f + hash01(id, i, 2) * 0.3f;
			float angleDeg = baseAngle + jitterStatic + spinDir * spinMul * RING_SPIN_SPEED * life;

			float a0 = (float) Math.toRadians(angleDeg);
			float a1 = (float) Math.toRadians(angleDeg + RING_STRIP_ARC_DEG);

			Vec3 p0 = origin.add(right.scale(Mth.cos(a0) * radius)).add(up.scale(Mth.sin(a0) * radius));
			Vec3 p1 = origin.add(right.scale(Mth.cos(a1) * radius)).add(up.scale(Mth.sin(a1) * radius));

			float widthPulse = 0.8f + 0.2f * Mth.sin(life * 1.1f + hash01(id, i, 8) * 6.283f);
			Vec3 thick = thickDir.scale(widthPulse);

			Vec3 in0 = p0.subtract(thick);
			Vec3 out0 = p0.add(thick);
			Vec3 in1 = p1.subtract(thick);
			Vec3 out1 = p1.add(thick);

			float phase = hash01(id, i, 7) * 6.283f;
			float flick = 0.8f + 0.2f * Mth.sin(life * FLICKER_FREQ + phase);
			int alpha = (int) (Mth.clamp(alphaMul * flick, 0f, 1f) * globalFade * 200);

			float colorT = ((angleDeg % 360f) + 360f) % 360f / 360f;
			int r = lerpColorChannel(colorI, colorII, colorIII, colorT, 16);
			int g = lerpColorChannel(colorI, colorII, colorIII, colorT, 8);
			int b = lerpColorChannel(colorI, colorII, colorIII, colorT, 0);

			addVertex(consumer, matrix, normalMat, in0.subtract(entityRenderPos), r, g, b, alpha, 0, 0);
			addVertex(consumer, matrix, normalMat, out0.subtract(entityRenderPos), r, g, b, alpha, 1, 0);
			addVertex(consumer, matrix, normalMat, out1.subtract(entityRenderPos), r, g, b, alpha, 1, 1);
			addVertex(consumer, matrix, normalMat, in1.subtract(entityRenderPos), r, g, b, alpha, 0, 1);
		}
	}

	private void renderCone(BaseBreathAreaEntity entityIn, VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMat,
							Vec3 entityRenderPos, Vec3 origin, Vec3 forward, Vec3 right, Vec3 up,
							float coneT, float life, float globalFade) {
		int stripCount = entityIn.getStripCount();
		float range = entityIn.getMaxReach();
		float halfAngleRad = (float) Math.toRadians(entityIn.getSpellAngle() * 0.5f);
		float baseRadius = range * (float) Math.tan(halfAngleRad);
		int colorI = entityIn.beamLightI();
		int colorII = entityIn.beamLightII();
		int colorIII = entityIn.beamLightIII();
		int id = entityIn.getId();

		for (int i = 0; i < stripCount; i++) {
			float ignite = hash01(id, i, 3) * CONE_IGNITE_SPREAD;
			float local = Mth.clamp((coneT - ignite) / Math.max(0.0001f, (1.0f - ignite)), 0f, 1f);
			if (local <= 0f) continue;

			float edgeBias = (float) Math.pow(hash01(id, i, 4), 0.4f);
			float rotAngle = hash01(id, i, 5) * 360f;
			float lenVar = 0.75f + hash01(id, i, 6) * 0.5f;

			float ra = (float) Math.toRadians(rotAngle);
			Vec3 radialDir = right.scale(Mth.cos(ra)).add(up.scale(Mth.sin(ra)));
			Vec3 endPoint = origin.add(forward.scale(range * lenVar)).add(radialDir.scale(baseRadius * edgeBias * lenVar));

			float travel = Mth.clamp(local / CONE_FLIGHT_FRAC, 0f, 1f);
			float posFrac = easeOutCubic(travel);
			if (local > CONE_FLIGHT_FRAC) {
				float driftT = (local - CONE_FLIGHT_FRAC) / (1f - CONE_FLIGHT_FRAC);
				posFrac += driftT * CONE_DRIFT_EXTRA;
			}

			float headFrac = Math.max(0.001f, posFrac);
			float tailFrac = Math.max(0f, headFrac - CONE_TRAIL_LEN_FRAC);
			float headFracClamped = Mth.clamp(headFrac, 0f, 1f);
			float tailFracClamped = Mth.clamp(tailFrac, 0f, 1f);

			Vec3 head = lerpVec(origin, endPoint, headFrac);
			Vec3 tail = lerpVec(origin, endPoint, tailFrac);

			Vec3 dir = endPoint.subtract(origin).normalize();
			Vec3 sideRef = (Math.abs(dir.y) < 0.9) ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
			Vec3 waveAxis = dir.cross(sideRef).normalize();
			Vec3 sideAxis = dir.cross(waveAxis).normalize();

			float alphaIn = Mth.clamp(travel * 2.5f, 0f, 1f);
			float alphaOut = 1.0f - Mth.clamp((local - 0.55f) / 0.45f, 0f, 1f);
			float alpha = alphaIn * alphaOut * globalFade;
			if (alpha <= 0.005f) continue;

			float widthScale = 0.6f + hash01(id, i, 9) * 0.6f;
			float wavePhase = hash01(id, i, 10) * 6.283f;
			float timeSeed = life * CONE_WAVE_SPEED;

			int rHead = lerpColorChannel(colorI, colorII, colorIII, headFracClamped, 16);
			int gHead = lerpColorChannel(colorI, colorII, colorIII, headFracClamped, 8);
			int bHead = lerpColorChannel(colorI, colorII, colorIII, headFracClamped, 0);
			int rTail = lerpColorChannel(colorI, colorII, colorIII, tailFracClamped, 16);
			int gTail = lerpColorChannel(colorI, colorII, colorIII, tailFracClamped, 8);
			int bTail = lerpColorChannel(colorI, colorII, colorIII, tailFracClamped, 0);

			drawWavyRibbon(consumer, matrix, normalMat, entityRenderPos, tail, head, waveAxis, sideAxis,
					CONE_MAX_HALF_WIDTH * widthScale, CONE_WAVE_AMP, CONE_WAVE_FREQ, wavePhase, timeSeed,
					alpha, alpha * 0.15f, CONE_SEGMENTS,
					rTail, gTail, bTail, rHead, gHead, bHead);
		}
	}

	private void drawWavyRibbon(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMat, Vec3 entityRenderPos,
								Vec3 start, Vec3 end, Vec3 waveAxis, Vec3 sideAxis,
								float maxHalfWidth, float waveAmp, float waveFreq, float wavePhase, float timeSeed,
								float alphaHead, float alphaTail, int segments,
								int rTail, int gTail, int bTail, int rHead, int gHead, int bHead) {
		Vec3 dir = end.subtract(start);
		if (dir.lengthSqr() < 1.0E-8) return;

		Vec3[] centerLine = new Vec3[segments + 1];
		float[] widths = new float[segments + 1];
		for (int s = 0; s <= segments; s++) {
			float t = (float) s / segments;
			Vec3 base = start.add(dir.scale(t));
			float envelope = Mth.sin(t * (float) Math.PI);
			float wave = Mth.sin((float) (t * waveFreq * Math.PI * 2 + wavePhase + timeSeed)) * waveAmp * envelope;
			float driftSecondary = Mth.cos((float) (t * waveFreq * Math.PI * 1.3f + wavePhase * 1.7f + timeSeed * 0.7f)) * waveAmp * 0.4f * envelope;
			centerLine[s] = base.add(waveAxis.scale(wave)).add(sideAxis.scale(driftSecondary));
			widths[s] = maxHalfWidth * (float) Math.pow(Mth.sin(Mth.clamp(t, 0.02f, 0.98f) * (float) Math.PI), 0.7f);
		}

		for (int s = 0; s < segments; s++) {
			Vec3 c0 = centerLine[s];
			Vec3 c1 = centerLine[s + 1];

			float t0 = (float) s / segments;
			float t1 = (float) (s + 1) / segments;
			float w0 = widths[s];
			float w1 = widths[s + 1];
			float a0 = Mth.lerp(t0, alphaTail, alphaHead);
			float a1 = Mth.lerp(t1, alphaTail, alphaHead);
			int r0 = (int) Mth.lerp(t0, rTail, rHead);
			int g0 = (int) Mth.lerp(t0, gTail, gHead);
			int b0 = (int) Mth.lerp(t0, bTail, bHead);
			int r1 = (int) Mth.lerp(t1, rTail, rHead);
			int g1 = (int) Mth.lerp(t1, gTail, gHead);
			int b1 = (int) Mth.lerp(t1, bTail, bHead);

			Vec3 p0a = c0.subtract(waveAxis.scale(w0));
			Vec3 p0b = c0.add(waveAxis.scale(w0));
			Vec3 p1a = c1.subtract(waveAxis.scale(w1));
			Vec3 p1b = c1.add(waveAxis.scale(w1));

			addVertex(consumer, matrix, normalMat, p0a.subtract(entityRenderPos), r0, g0, b0, (int) (a0 * 200), 0, t0);
			addVertex(consumer, matrix, normalMat, p0b.subtract(entityRenderPos), r0, g0, b0, (int) (a0 * 200), 1, t0);
			addVertex(consumer, matrix, normalMat, p1b.subtract(entityRenderPos), r1, g1, b1, (int) (a1 * 200), 1, t1);
			addVertex(consumer, matrix, normalMat, p1a.subtract(entityRenderPos), r1, g1, b1, (int) (a1 * 200), 0, t1);
		}
	}

	private static Vec3 lerpVec(Vec3 a, Vec3 b, double t) {
		return new Vec3(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t);
	}

	private static float easeOutCubic(float t) {
		float f = t - 1f;
		return f * f * f + 1f;
	}

	private static float hash01(int id, int i, int salt) {
		int h = id * 374761393 + i * 668265263 + salt * -1640531527;
		h = (h ^ (h >>> 15)) * -2048144789;
		h = (h ^ (h >>> 13)) * -1028477387;
		h = h ^ (h >>> 16);
		return (h & 0xFFFFFF) / (float) 0xFFFFFF;
	}

	private int lerpColorChannel(int c1, int c2, int c3, float t, int shift) {
		int ch1 = (c1 >> shift) & 0xFF;
		int ch2 = (c2 >> shift) & 0xFF;
		int ch3 = (c3 >> shift) & 0xFF;
		if (t < 0.5f) return (int) Mth.lerp(t / 0.5f, ch1, ch2);
		return (int) Mth.lerp((t - 0.5f) / 0.5f, ch2, ch3);
	}

	private void addVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, Vec3 pos, int r, int g, int b, int a, float u, float v) {
		consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
				.color(r, g, b, a)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(15728880)
				.normal(normal, 0, 1, 0)
				.endVertex();
	}

	@Override
	@Nonnull
	public ResourceLocation getTextureLocation(BaseBreathAreaEntity e) {
		return e.getTextureLocation();
	}

	@Override
	public boolean shouldRender(BaseBreathAreaEntity e, Frustum frustum, double d, double d2, double d3) {
		if (!e.shouldRender(d, d2, d3)) return false;
		if (e.noCulling) return true;
		AABB aabb = e.getBoundingBoxForCulling().inflate(128.0D);
		if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
			aabb = new AABB(e.getX() - 128.0D, e.getY() - 128.0D, e.getZ() - 128.0D,
					e.getX() + 128.0D, e.getY() + 128.0D, e.getZ() + 128.0D);
		}
		return frustum.isVisible(aabb);
	}
}