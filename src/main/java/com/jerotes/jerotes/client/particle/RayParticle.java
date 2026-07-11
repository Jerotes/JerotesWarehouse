package com.jerotes.jerotes.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class RayParticle extends TextureSheetParticle {
	public static RayParticleProvider provider(SpriteSet spriteSet) {
		return new RayParticleProvider(spriteSet);
	}

	public static class RayParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public RayParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed) {
			return new RayParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;
	private float initialScale = 0f;
	private float peakScale = 0.225f;
	private int fadeInTime = 5;
	private int fadeOutTime = 25;

	protected RayParticle(ClientLevel world, double x, double y, double z,
							  double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0f, 0f);
		this.quadSize = initialScale;
		this.lifetime = 40;
		this.gravity = 0f;
		this.hasPhysics = false;
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
		this.pickSprite(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.spriteSet);

		if (this.age < fadeInTime) {
			float fadeInProgress = (float) this.age / (float) fadeInTime;
			this.quadSize = lerp(initialScale, peakScale, easeOutCubic(fadeInProgress));
		} else if (this.age > this.lifetime - fadeOutTime) {
			float fadeOutProgress = (float) (this.age - (this.lifetime - fadeOutTime)) / (float) fadeOutTime;
			this.quadSize = lerp(peakScale, initialScale, easeInCubic(fadeOutProgress));
		} else {
			this.quadSize = peakScale;
		}

		float alpha;
		if (this.age < fadeInTime) {
			float progress = (float) this.age / fadeInTime;
			alpha = easeOutCubic(progress);
		} else if (this.age > this.lifetime - fadeOutTime) {
			float progress = (float) (this.age - (this.lifetime - fadeOutTime)) / fadeOutTime;
			alpha = 1 - easeInCubic(progress);
		} else {
			alpha = 1.0f;
		}
		this.setAlpha(alpha * 0.75f);
	}

	private float lerp(float start, float end, float progress) {
		return start + (end - start) * progress;
	}

	private float easeInCubic(float t) {
		return t * t * t;
	}

	private float easeOutCubic(float t) {
		return 1 - (float) Math.pow(1 - t, 3);
	}

	@Override
	public int getLightColor(float f) {
		return 240;
	}
}