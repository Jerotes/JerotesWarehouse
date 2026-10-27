package com.jerotes.jerotes.client.particle;

import com.jerotes.jerotes.init.JerotesParticleRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;
public class ElementBoltParticle extends TextureSheetParticle {
	private final SpriteSet spriteSet;

	private final double startX;
	private final double startY;
	private final double startZ;

	private final double targetX;
	private final double targetY;
	private final double targetZ;

	private final Vec3 direction;
	private final Vec3 waveAxis;
	private final Vec3 sideAxis;

	private final float widthScale;
	private final float wavePhase;
	private final float waveSeed;

	private static final int FIXED_LIFETIME = 10;

	private static final float TRAIL_LENGTH_FRAC = 0.30f;
	private static final float MAX_HALF_WIDTH = 0.09f;
	private static final float WAVE_AMP = 0.09f;
	private static final float WAVE_FREQ = 1.6f;
	private static final float WAVE_SPEED = 3.0f;

	protected ElementBoltParticle(ClientLevel world,
								  double x,
								  double y,
								  double z,
								  BoltParticleOptions options,
								  SpriteSet spriteSet) {
		super(world, x, y, z);

		this.spriteSet = spriteSet;

		this.startX = x;
		this.startY = y;
		this.startZ = z;

		this.targetX = x + options.getDx();
		this.targetY = y + options.getDy();
		this.targetZ = z + options.getDz();

		Vec3 dir = new Vec3(
				this.targetX - this.startX,
				this.targetY - this.startY,
				this.targetZ - this.startZ
		);

		if (dir.lengthSqr() < 1.0E-6) {
			dir = new Vec3(0, 0, 1);
		}

		this.direction = dir.normalize();

		Vec3 sideRef;

		if (Math.abs(this.direction.y) < 0.9) {
			sideRef = new Vec3(0, 1, 0);
		} else {
			sideRef = new Vec3(1, 0, 0);
		}

		Vec3 wave = this.direction.cross(sideRef);

		if (wave.lengthSqr() < 1.0E-6) {
			wave = new Vec3(1, 0, 0);
		}

		this.waveAxis = wave.normalize();
		this.sideAxis = this.direction.cross(this.waveAxis).normalize();

		RandomSource random = RandomSource.create();

		this.widthScale = 0.6f + random.nextFloat() * 0.6f;
		this.wavePhase = random.nextFloat() * Mth.TWO_PI;
		this.waveSeed = random.nextFloat() * 100.0f;

		this.setSize(0.15f, 0.15f);

		this.quadSize = 1.0f;

		this.lifetime = FIXED_LIFETIME;

		this.gravity = 0.0f;
		this.hasPhysics = false;

		this.alpha = 1.0f;

		this.xd = 0.0;
		this.yd = 0.0;
		this.zd = 0.0;

		this.setColor(
				options.getR(),
				options.getG(),
				options.getB()
		);

		this.pickSprite(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return JerotesParticleRenderTypes.ELEMENT_BOLT;
	}

	@Override
	public boolean shouldCull() {
		return false;
	}

	@Override
	public int getLightColor(float partialTick) {
		return 15728880;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if (this.age++ >= this.lifetime) {
			this.remove();
			return;
		}

		this.setSpriteFromAge(this.spriteSet);

		float progress = Mth.clamp(
				((float) this.age) / this.lifetime,
				0.0f,
				1.0f
		);

		float moveT = easeOutCubic(progress);

		this.x = Mth.lerp(moveT, this.startX, this.targetX);
		this.y = Mth.lerp(moveT, this.startY, this.targetY);
		this.z = Mth.lerp(moveT, this.startZ, this.targetZ);

		this.xd = 0.0;
		this.yd = 0.0;
		this.zd = 0.0;

		this.alpha = 1.0f;
	}

	@Override
	public float getQuadSize(float partialTick) {
		return 1.0f;
	}

	@Override
	public void render(VertexConsumer buffer, Camera camera, float partialTick) {
		float ageF = this.age + partialTick;

		float progress = Mth.clamp(
				ageF / this.lifetime,
				0.0f,
				1.0f
		);

		if (progress <= 0.0f || progress >= 1.0f) {
			return;
		}

		float headProgress = easeOutCubic(progress);

		float tailProgress = Math.max(
				0.0f,
				headProgress - TRAIL_LENGTH_FRAC
		);

		Vec3 start = new Vec3(
				this.startX,
				this.startY,
				this.startZ
		);

		Vec3 end = new Vec3(
				this.targetX,
				this.targetY,
				this.targetZ
		);

		Vec3 cameraPos = camera.getPosition();

		Vec3 head = lerpVec(start, end, headProgress);
		Vec3 tail = lerpVec(start, end, tailProgress);

		head = head.subtract(cameraPos);
		tail = tail.subtract(cameraPos);

		Vec3 dir = end.subtract(start);

		if (dir.lengthSqr() < 1.0E-8) {
			return;
		}

		dir = dir.normalize();

		Vec3 sideRef;

		if (Math.abs(dir.y) < 0.9f) {
			sideRef = new Vec3(0, 1, 0);
		} else {
			sideRef = new Vec3(1, 0, 0);
		}

		Vec3 waveAxis = dir.cross(sideRef);

		if (waveAxis.lengthSqr() < 1.0E-8) {
			return;
		}

		waveAxis = waveAxis.normalize();

		Vec3 sideAxis = dir.cross(waveAxis);

		if (sideAxis.lengthSqr() < 1.0E-8) {
			return;
		}

		sideAxis = sideAxis.normalize();

		float travel = Mth.clamp(
				progress,
				0.0f,
				1.0f
		);

		float alphaIn = Mth.clamp(
				travel * 2.5f,
				0.0f,
				1.0f
		);

		float alphaOut = 1.0f;

		if (progress > 0.55f) {
			alphaOut = 1.0f - Mth.clamp(
					(progress - 0.55f) / 0.45f,
					0.0f,
					1.0f
			);
		}

		float alpha = alphaIn * alphaOut;

		if (alpha <= 0.005f) {
			return;
		}

		float timeSeed = ageF * WAVE_SPEED + this.waveSeed;

		int segments = 4;

		Vec3[] centerLine = new Vec3[segments + 1];
		float[] widths = new float[segments + 1];

		for (int s = 0; s <= segments; s++) {
			float t = (float) s / segments;

			Vec3 base = lerpVec(
					tail,
					head,
					t
			);

			float envelope = Mth.sin(
					t * Mth.PI
			);

			float wave = Mth.sin(
					t * WAVE_FREQ * Mth.TWO_PI
							+ this.wavePhase
							+ timeSeed
			) * WAVE_AMP * envelope;

			float driftSecondary = Mth.cos(
					t * WAVE_FREQ * Mth.PI * 1.3f
							+ this.wavePhase * 1.7f
							+ timeSeed * 0.7f
			) * WAVE_AMP * 0.4f * envelope;

			centerLine[s] = base
					.add(waveAxis.scale(wave))
					.add(sideAxis.scale(driftSecondary));

			widths[s] = MAX_HALF_WIDTH
					* this.widthScale
					* (float) Math.pow(
					Mth.sin(
							Mth.clamp(
									t,
									0.02f,
									0.98f
							) * Mth.PI
					),
					0.7f
			);
		}

		float tailAlpha = alpha * 0.15f;
		float headAlpha = alpha;

		float u0 = this.getU0();
		float u1 = this.getU1();
		float v0 = this.getV0();
		float v1 = this.getV1();

		int light = this.getLightColor(partialTick);

		for (int s = 0; s < segments; s++) {
			float t0 = (float) s / segments;
			float t1 = (float) (s + 1) / segments;

			Vec3 c0 = centerLine[s];
			Vec3 c1 = centerLine[s + 1];

			float w0 = widths[s];
			float w1 = widths[s + 1];

			float a0 = Mth.lerp(
					t0,
					tailAlpha,
					headAlpha
			);

			float a1 = Mth.lerp(
					t1,
					tailAlpha,
					headAlpha
			);

			Vec3 p0a = c0.subtract(
					waveAxis.scale(w0)
			);

			Vec3 p0b = c0.add(
					waveAxis.scale(w0)
			);

			Vec3 p1a = c1.subtract(
					waveAxis.scale(w1)
			);

			Vec3 p1b = c1.add(
					waveAxis.scale(w1)
			);

			addVertex(
					buffer,
					p0a,
					u0,
					Mth.lerp(t0, v0, v1),
					a0,
					light
			);

			addVertex(
					buffer,
					p0b,
					u1,
					Mth.lerp(t0, v0, v1),
					a0,
					light
			);

			addVertex(
					buffer,
					p1b,
					u1,
					Mth.lerp(t1, v0, v1),
					a1,
					light
			);

			addVertex(
					buffer,
					p1a,
					u0,
					Mth.lerp(t1, v0, v1),
					a1,
					light
			);
		}
	}

	private void addVertex(VertexConsumer buffer,
						   Vec3 pos,
						   float u,
						   float v,
						   float alpha,
						   int light) {

		buffer.vertex(
						pos.x,
						pos.y,
						pos.z
				)
				.uv(u, v)
				.color(
						this.rCol,
						this.gCol,
						this.bCol,
						Mth.clamp(alpha, 0.0f, 1.0f)
				)
				.uv2(light)
				.endVertex();
	}

	private static Vec3 lerpVec(
			Vec3 a,
			Vec3 b,
			double t) {

		return new Vec3(
				a.x + (b.x - a.x) * t,
				a.y + (b.y - a.y) * t,
				a.z + (b.z - a.z) * t
		);
	}

	private static float easeOutCubic(float t) {
		float f = t - 1.0f;
		return f * f * f + 1.0f;
	}

	public static Provider provider(SpriteSet spriteSet) {
		return new Provider(spriteSet);
	}

	public static class Provider
			implements ParticleProvider<BoltParticleOptions> {

		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(
				BoltParticleOptions options,
				ClientLevel level,
				double x,
				double y,
				double z,
				double xs,
				double ys,
				double zs) {

			return new ElementBoltParticle(
					level,
					x,
					y,
					z,
					options,
					this.spriteSet
			);
		}
	}

}