package com.jerotes.jerotes.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class GustParticle extends TextureSheetParticle {
	private final SpriteSet sprites;

	protected GustParticle(ClientLevel clientLevel, double d, double d2, double d3, SpriteSet spriteSet) {
		super(clientLevel, d, d2, d3);
		this.sprites = spriteSet;
		this.setSpriteFromAge(spriteSet);
		this.lifetime = 12 + this.random.nextInt(4);
		this.quadSize = 1.0f;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getLightColor(float partialTick) {
		return 0xF000F0;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.removed) {
			this.setSpriteFromAge(this.sprites);
		}
	}

	public static class SmallProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public SmallProvider(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			GustParticle gustParticle = new GustParticle(level, x, y, z, this.sprites);
			gustParticle.scale(0.15f);
			return gustParticle;
		}
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level,
									   double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed) {
			GustParticle particle = new GustParticle(level, x, y, z, this.sprites);
			particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
			return particle;
		}
	}
}