package com.jerotes.jerotes.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
@OnlyIn(Dist.CLIENT)
public class FeatherParticle extends TextureSheetParticle {
	public static ShootParticleProvider provider(SpriteSet spriteSet) {
		return new ShootParticleProvider(spriteSet);
	}

	public static class ShootParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public ShootParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new FeatherParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;
	private final int fadeInTime = 5;
	private final int fadeOutTime = 45;

	protected FeatherParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.2f, 0.2f);
		this.quadSize *= 1f;
		this.lifetime = 50;
		this.gravity = 0f;
		this.alpha = 0f;
		this.hasPhysics = true;
		this.xd = vx * 2;
		this.yd = vy * 2;
		this.zd = vz * 2;
		this.setSprite(spriteSet.get(this.random));
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		float alpha;
		if (this.age < fadeInTime) {
			float progress = (float) this.age / fadeInTime;
			alpha = easeOutCubic(progress);
		} else if (this.age > this.lifetime - fadeOutTime) {
			float progress = (float) (this.age - (this.lifetime - fadeOutTime)) / fadeOutTime;
			alpha = 1f - easeInCubic(progress);
		} else {
			alpha = 1f;
		}
		this.setAlpha(alpha);
	}

	private float easeInCubic(float t) {
		return t * t * t;
	}

	private float easeOutCubic(float t) {
		return 1f - (float) Math.pow(1f - t, 3);
	}
}