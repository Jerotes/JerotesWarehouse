package com.jerotes.jerotes.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
public abstract class ElementParticle extends TextureSheetParticle {

	protected final SpriteSet spriteSet;

	protected final int fadeInTime = 2;
	protected final int fadeOutTime;

	protected ElementParticle(ClientLevel world, double x, double y, double z,
							  double vx, double vy, double vz,
							  SpriteSet spriteSet,
							  int minLifetime, int maxLifetime, int fadeOutTime) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.2f, 0.2f);
		this.quadSize *= 1f;

		int range = Math.max(1, maxLifetime - minLifetime + 1);
		this.lifetime = minLifetime + this.random.nextInt(range);

		this.fadeOutTime = Math.min(fadeOutTime, Math.max(1, this.lifetime - this.fadeInTime));

		this.gravity = 0f;
		this.alpha = 0f;
		this.hasPhysics = true;
		this.xd = vx * 2;
		this.yd = vy * 2;
		this.zd = vz * 2;
		this.pickSprite(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getLightColor(float f) {
		return 240;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.spriteSet);
		this.setAlpha(getFadeProgress(0f));
	}

	@Override
	public float getQuadSize(float partialTick) {
		return this.quadSize * getFadeProgress(partialTick);
	}

	protected float getFadeProgress(float partialTick) {
		float age = this.age + partialTick;

		if (age < this.fadeInTime) {
			float progress = age / this.fadeInTime;
			return easeOutCubic(progress);
		} else if (age > this.lifetime - this.fadeOutTime) {
			float progress = (age - (this.lifetime - this.fadeOutTime)) / this.fadeOutTime;
			return Mth.clamp(1f - easeInCubic(progress), 0f, 1f);
		} else {
			return 1f;
		}
	}

	protected float easeInCubic(float t) {
		return t * t * t;
	}

	protected float easeOutCubic(float t) {
		return 1f - (float) Math.pow(1f - t, 3);
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}
		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level,
									   double x, double y, double z,
									   double vx, double vy, double vz) {
			return new ElementFlameParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
		}
	}

	public static class ElementFlameParticle extends ElementParticle {

		protected ElementFlameParticle(ClientLevel world, double x, double y, double z,
									   double vx, double vy, double vz, SpriteSet spriteSet) {
			super(world, x, y, z, vx, vy, vz, spriteSet, 20, 40, 15);
		}
		public static SymbolParticle.DisplayParticleProvider provider(SpriteSet spriteSet) {
			return new SymbolParticle.DisplayParticleProvider(spriteSet);
		}
		public static class Provider implements ParticleProvider<SimpleParticleType> {
			private final SpriteSet spriteSet;

			public Provider(SpriteSet spriteSet) {
				this.spriteSet = spriteSet;
			}
			@Override
			public Particle createParticle(SimpleParticleType type, ClientLevel level,
										   double x, double y, double z,
										   double vx, double vy, double vz) {
				return new ElementFlameParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
			}
		}
	}

	public static class ElementFrostParticle extends ElementParticle {

		protected ElementFrostParticle(ClientLevel world, double x, double y, double z,
									   double vx, double vy, double vz, SpriteSet spriteSet) {
			super(world, x, y, z, vx, vy, vz, spriteSet, 30, 50, 15);
			this.gravity = 0.02f;
			this.friction = 0.98f;
		}
		public static SymbolParticle.DisplayParticleProvider provider(SpriteSet spriteSet) {
			return new SymbolParticle.DisplayParticleProvider(spriteSet);
		}

		public static class Provider implements ParticleProvider<SimpleParticleType> {
			private final SpriteSet spriteSet;

			public Provider(SpriteSet spriteSet) {
				this.spriteSet = spriteSet;
			}

			@Override
			public Particle createParticle(SimpleParticleType type, ClientLevel level,
										   double x, double y, double z,
										   double vx, double vy, double vz) {
				return new ElementFrostParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
			}
		}
	}
	public static class ElementShockParticle extends ElementParticle {
		private static final float FLICKER_MIN = 0.5f;
		private static final float FLICKER_MAX = 1.0f;

		protected ElementShockParticle(ClientLevel world, double x, double y, double z,
									   double vx, double vy, double vz, SpriteSet spriteSet) {
			super(world, x, y, z, vx, vy, vz, spriteSet, 15, 30, 10);
		}
		@Override
		public void tick() {
			super.tick();
			float flicker = FLICKER_MIN + this.random.nextFloat() * (FLICKER_MAX - FLICKER_MIN);
			this.setAlpha(this.alpha * flicker);
		}
		public static SymbolParticle.DisplayParticleProvider provider(SpriteSet spriteSet) {
			return new SymbolParticle.DisplayParticleProvider(spriteSet);
		}

		public static class Provider implements ParticleProvider<SimpleParticleType> {
			private final SpriteSet spriteSet;

			public Provider(SpriteSet spriteSet) {
				this.spriteSet = spriteSet;
			}

			@Override
			public Particle createParticle(SimpleParticleType type, ClientLevel level,
										   double x, double y, double z,
										   double vx, double vy, double vz) {
				return new ElementShockParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
			}
		}
	}
}
