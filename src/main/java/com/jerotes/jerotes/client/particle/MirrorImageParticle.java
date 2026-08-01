package com.jerotes.jerotes.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

@OnlyIn(Dist.CLIENT)
public class MirrorImageParticle extends TextureSheetParticle {

	public static ShootParticleProvider provider(SpriteSet spriteSet) {
		return new ShootParticleProvider(spriteSet);
	}

	public static class ShootParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public ShootParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level,
									   double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed) {
			return new MirrorImageParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;

	protected MirrorImageParticle(ClientLevel level, double x, double y, double z,
								  double vx, double vy, double vz, SpriteSet spriteSet) {
		super(level, x, y, z);
		this.spriteSet = spriteSet;

		this.friction = 0.7F;
		this.gravity = 0.5F;
		this.hasPhysics = false;

		this.xd *= 0.1F;
		this.yd *= 0.1F;
		this.zd *= 0.1F;
		this.xd += vx * 0.4D;
		this.yd += vy * 0.4D;
		this.zd += vz * 0.4D;

		this.rCol = 1.0F;
		this.gCol = 1.0F;
		this.bCol = 1.0F;

		this.quadSize *= 0.75F;
		this.lifetime = Math.max((int) (6.0D / (Math.random() * 0.8D + 0.6D)), 1);

		this.alpha = 1.0F;
		this.pickSprite(spriteSet);
		this.tick();
	}

	@Override
	public float getQuadSize(float partialTick) {
		float progress = ((float) this.age + partialTick) / (float) this.lifetime;
		float scale = Mth.clamp(progress * 32.0F, 0.0F, 1.0F);
		return this.quadSize * scale;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.spriteSet);

		Player player = Minecraft.getInstance().player;
		if (player != null) {
			double distSq = player.position().add(0,player.getEyeHeight(),0).distanceToSqr(this.x, this.y, this.z);
			double dist = Math.sqrt(distSq);
			float targetAlpha;
			if (dist < 1.0F) {
				targetAlpha = 0.0F;
			} else if (dist < 2.0F) {
				targetAlpha = (float) ((dist - 1.0F) / 1.0F);
			} else {
				targetAlpha = 1.0F;
			}
			this.alpha = targetAlpha;
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getLightColor(float partialTick) {
		return 240;
	}
}