package com.jerotes.jerotes.client.particle;

import com.jerotes.jerotes.init.JerotesParticleRenderTypes;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class TargetOtherParticle extends TextureSheetParticle {
	private static final Vector3f ROTATION_VECTOR = new Vector3f(0.5f, 0.5f, 0.5f).normalize();
	private static final Vector3f TRANSFORM_VECTOR = new Vector3f(-1.0f, -1.0f, 0.0f);
	public static DisplayParticleProvider provider(SpriteSet spriteSet) {
		return new DisplayParticleProvider(spriteSet);
	}

	public static class DisplayParticleProvider implements ParticleProvider<SimpleParticleType> {
			private final SpriteSet spriteSet;

		public DisplayParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new TargetOtherParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;

	protected TargetOtherParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(2f, 2f);
		this.quadSize = 2f;
		this.lifetime = 10;
		this.gravity = 0f;
		this.hasPhysics = false;
		this.xd = vx * 1;
		this.yd = vy * 1;
		this.zd = vz * 1;
		this.pickSprite(spriteSet);

	}

	@Override
	public float getQuadSize(float f) {
		return this.quadSize * Mth.clamp(((float)this.age + f) / (float)this.lifetime * 1.5f, 0.0f, 1.0f);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return JerotesParticleRenderTypes.CUSTOM_TRANSLUCENT_SHOCKWAVE;
	}

	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
		this.alpha = 1.0f - Mth.clamp(((float)this.age + f) / (float)this.lifetime, 0.0f, 1.0f);
		this.renderRotatedParticle(vertexConsumer, camera, f, quaternionf -> quaternionf.mul((Quaternionfc)new Quaternionf().rotationYXZ(0.0f, Mth.HALF_PI, 0.0f)));
	}

	private void renderRotatedParticle(VertexConsumer vertexConsumer, Camera camera, float f, Consumer<Quaternionf> consumer) {
		int n;
		Vec3 vec3 = camera.getPosition();
		float f2 = (float)(Mth.lerp((double)f, this.xo, this.x) - vec3.x());
		float f3 = (float)(Mth.lerp((double)f, this.yo, this.y) - vec3.y());
		float f4 = (float)(Mth.lerp((double)f, this.zo, this.z) - vec3.z());
		Quaternionf quaternionf = new Quaternionf().setAngleAxis(0.0f, ROTATION_VECTOR.x(), ROTATION_VECTOR.y(), ROTATION_VECTOR.z());
		consumer.accept(quaternionf);
		Vector3f[] arrvector3f = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
		float f5 = this.getQuadSize(f);
		for (n = 0; n < 4; ++n) {
			Vector3f vector3f = arrvector3f[n];
			vector3f.rotate((Quaternionfc)quaternionf);
			vector3f.mul(f5);
			vector3f.add(f2, f3, f4);
		}
		n = this.getLightColor(f);
		this.makeCornerVertex(vertexConsumer, arrvector3f[0], this.getU1(), this.getV1(), n);
		this.makeCornerVertex(vertexConsumer, arrvector3f[1], this.getU1(), this.getV0(), n);
		this.makeCornerVertex(vertexConsumer, arrvector3f[2], this.getU0(), this.getV0(), n);
		this.makeCornerVertex(vertexConsumer, arrvector3f[3], this.getU0(), this.getV1(), n);
	}

	private void makeCornerVertex(VertexConsumer vertexConsumer, Vector3f vector3f, float f, float f2, int n) {
		vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).uv(f, f2).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(n).endVertex();
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
