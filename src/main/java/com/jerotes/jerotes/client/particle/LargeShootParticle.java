package com.jerotes.jerotes.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeShootParticle extends TextureSheetParticle {
	public static ShootParticleProvider provider(SpriteSet spriteSet) {
		return new ShootParticleProvider(spriteSet);
	}

	public static class ShootParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public ShootParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new LargeShootParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;

	protected LargeShootParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.9f, 0.9f);
		this.quadSize *= 6f;
		this.lifetime = 15;
		this.gravity = 0f;
		this.hasPhysics = false;
		this.xd = vx * 2;
		this.yd = vy * 2;
		this.zd = vz * 2;
		this.pickSprite(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getLightColor(float f) {
		return 240;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.spriteSet);
	}
}
