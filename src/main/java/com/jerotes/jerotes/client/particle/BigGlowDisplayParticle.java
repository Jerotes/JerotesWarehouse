package com.jerotes.jerotes.client.particle;

import com.jerotes.jerotes.init.JerotesParticleRenderTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BigGlowDisplayParticle extends TextureSheetParticle {
	private final SpriteSet sprites;
	private float baseScale;
	private float distortionProgress;

	protected BigGlowDisplayParticle(ClientLevel level, double x, double y, double z,
                                     SpriteSet sprites) {
		super(level, x, y, z);
		this.sprites = sprites;
		this.setSpriteFromAge(sprites);

		// 基础参数
		this.rCol = 1.0f;
		this.gCol = 1.0f;
		this.bCol = 1.0f;
		this.alpha = 0.9f;
		this.baseScale = 0.75f;
		this.lifetime = 80;
		this.hasPhysics = false;
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.sprites);

		float progress = (float)this.age / this.lifetime;
		float easeOut = 1 - (float)Math.pow(1 - progress, 4);

		// 平滑放大
		this.quadSize = (baseScale * (1 + easeOut * 2f));

		// 透明度衰减
		this.alpha = 0.9f * (1 - progress);

		// 扭曲进度
		this.distortionProgress = easeOut;
	}

	@Override
	public ParticleRenderType getRenderType() {
		return JerotesParticleRenderTypes.CUSTOM_TRANSLUCENT_SHOCKWAVE;
	}


	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level,
									   double x, double y, double z,
									   double xd, double yd, double zd) {
			return new BigGlowDisplayParticle(level, x, y, z, this.sprites);
		}
	}
}