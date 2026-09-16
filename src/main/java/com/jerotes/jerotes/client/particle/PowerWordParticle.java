package com.jerotes.jerotes.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;


@OnlyIn(Dist.CLIENT)
public class PowerWordParticle extends TextureSheetParticle {

	public static ShootParticleProvider provider(SpriteSet spriteSet) {
		return new ShootParticleProvider(spriteSet);
	}

	public static class ShootParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public ShootParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new PowerWordParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;
	private final int fadeInTime = 5;
	private final int fadeOutTime = 15;

	private static final float START_R = 0.51f, START_G = 0.0f, START_B = 0.1f;
	private static final float END_R = 1.0f, END_G = 0.88f, END_B = 1.0f;

	private float rollSpeed;
	private float baseQuadSize;

	protected PowerWordParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.2f, 0.2f);
		this.baseQuadSize = this.quadSize;
		this.lifetime = 55;
		this.gravity = 0f;
		this.alpha = 0f;
		this.hasPhysics = true;
		this.xd = vx * 2;
		this.yd = vy * 2;
		this.zd = vz * 2;

		this.rollSpeed = (this.random.nextBoolean() ? 1f : -1f) * (0.3f + this.random.nextFloat() * 0.4f);
		this.roll = this.random.nextFloat() * (float) Math.PI * 2f;
		this.oRoll = this.roll;

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

		this.oRoll = this.roll;
		this.roll += this.rollSpeed;

		float lifeProgress = Mth.clamp((float) this.age / this.lifetime, 0f, 1f);

		float colorT = Mth.clamp(lifeProgress * 1.3f, 0f, 1f);
		float r = Mth.lerp(colorT, START_R, END_R);
		float g = Mth.lerp(colorT, START_G, END_G);
		float b = Mth.lerp(colorT, START_B, END_B);
		this.setColor(r, g, b);

		float scale;
		if (this.age < fadeInTime) {
			float t = (float) this.age / fadeInTime;
			float overshoot = 1.7f;
			float tm1 = t - 1f;
			scale = 1f + tm1 * tm1 * ((overshoot + 1f) * tm1 + overshoot);
			scale = Mth.clamp(scale, 0f, 1.35f);
		} else if (this.age > this.lifetime - fadeOutTime) {
			float t = (float) (this.age - (this.lifetime - fadeOutTime)) / fadeOutTime;
			scale = 1f - easeInCubic(t);
		} else {
			scale = 1f + 0.06f * Mth.sin(this.age * 0.5f);
		}
		this.quadSize = this.baseQuadSize * Math.max(scale, 0.001f);

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

	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		Vec3 camPos = renderInfo.getPosition();
		float px = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
		float py = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
		float pz = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

		Quaternionf camRot = new Quaternionf(renderInfo.rotation());
		float rollLerp = Mth.lerp(partialTicks, this.oRoll, this.roll);
		camRot.mul(Axis.ZP.rotation(rollLerp));

		double speed = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
		float stretch = 1.0f + Mth.clamp((float) speed * 6.0f, 0f, 2.2f);
		float widthScale = 1.0f / (1.0f + Mth.clamp((float) speed * 1.5f, 0f, 0.5f));

		float size = this.getQuadSize(partialTicks);

		Vector3f[] corners = new Vector3f[]{
				new Vector3f(-stretch, -widthScale, 0.0f),
				new Vector3f(-stretch, widthScale, 0.0f),
				new Vector3f(stretch, widthScale, 0.0f),
				new Vector3f(stretch, -widthScale, 0.0f)
		};

		for (Vector3f corner : corners) {
			corner.rotate(camRot);
			corner.mul(size);
			corner.add(px, py, pz);
		}

		float u0 = this.getU0(), u1 = this.getU1();
		float v0 = this.getV0(), v1 = this.getV1();
		int light = this.getLightColor(partialTicks);

		int tailAlpha = (int) (this.alpha * 255 * 0.15f);
		int headAlpha = (int) (this.alpha * 255f);

		buffer.vertex((double) corners[0].x(), (double) corners[0].y(), (double) corners[0].z())
				.uv(u1, v1).color(this.rCol, this.gCol, this.bCol, tailAlpha / 255f).uv2(light).endVertex();
		buffer.vertex((double) corners[1].x(), (double) corners[1].y(), (double) corners[1].z())
				.uv(u1, v0).color(this.rCol, this.gCol, this.bCol, tailAlpha / 255f).uv2(light).endVertex();
		buffer.vertex((double) corners[2].x(), (double) corners[2].y(), (double) corners[2].z())
				.uv(u0, v0).color(this.rCol, this.gCol, this.bCol, headAlpha / 255f).uv2(light).endVertex();
		buffer.vertex((double) corners[3].x(), (double) corners[3].y(), (double) corners[3].z())
				.uv(u0, v1).color(this.rCol, this.gCol, this.bCol, headAlpha / 255f).uv2(light).endVertex();
	}

	private float easeInCubic(float t) {
		return t * t * t;
	}

	private float easeOutCubic(float t) {
		return 1f - (float) Math.pow(1f - t, 3);
	}
}