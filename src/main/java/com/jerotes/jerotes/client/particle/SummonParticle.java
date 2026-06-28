package com.jerotes.jerotes.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class SummonParticle extends Particle {
	private final SummonParticleOptions cfg;  // 配置对象

	private float innerHeight;
	private float innerHalfWidth;
	private float outerHeight;
	private float outerRadius;
	private int innerAge;
	private int outerAge;

	public SummonParticle(ClientLevel level, double x, double y, double z, SummonParticleOptions options) {
		super(level, x, y, z);
		this.cfg = options;
		this.lifetime = cfg.innerLifespan;  // 使用配置的生命周期
		this.hasPhysics = false;
		this.innerAge = 0;
		this.outerAge = 0;
		this.innerHeight = cfg.innerHeightStart;
		this.innerHalfWidth = cfg.innerHalfWidthStart;
		this.outerHeight = cfg.outerHeightStart;
		this.outerRadius = cfg.outerBaseRadius;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.age >= cfg.innerLifespan) {
			this.remove();
			return;
		}
		innerAge = this.age;
		outerAge = Math.min(this.age, cfg.outerLifespan);

		// --- 内层高度与宽度（完全使用 cfg） ---
		float innerProgress = (float) innerAge / cfg.innerLifespan;
		if (innerProgress <= 0.5F) {
			float t = innerProgress / 0.5F;
			innerHeight = cfg.innerHeightStart + (cfg.innerHeightPeak - cfg.innerHeightStart) * t;
			innerHalfWidth = cfg.innerHalfWidthStart + (cfg.innerHalfWidthPeak - cfg.innerHalfWidthStart) * t;
		} else {
			float t = (innerProgress - 0.5F) / 0.5F;
			innerHeight = cfg.innerHeightPeak + (cfg.innerHeightEnd - cfg.innerHeightPeak) * t;
			innerHalfWidth = cfg.innerHalfWidthPeak + (cfg.innerHalfWidthEnd - cfg.innerHalfWidthPeak) * t;
		}

		// --- 外层高度与半径 ---
		if (outerAge < cfg.outerLifespan) {
			float outerProgress = (float) outerAge / cfg.outerLifespan;
			if (outerProgress <= 0.5F) {
				float t = outerProgress / 0.5F;
				outerHeight = cfg.outerHeightStart + (cfg.outerHeightPeak - cfg.outerHeightStart) * t;
			} else {
				float t = (outerProgress - 0.5F) / 0.5F;
				outerHeight = cfg.outerHeightPeak + (cfg.outerHeightEnd - cfg.outerHeightPeak) * t;
			}
			if (outerProgress <= 0.4F) {
				float t = outerProgress / 0.4F;
				outerRadius = cfg.outerBaseRadius + (cfg.outerPeakRadius - cfg.outerBaseRadius) * t;
			} else {
				float t = (outerProgress - 0.4F) / 0.6F;
				outerRadius = cfg.outerPeakRadius + (cfg.outerBaseRadius - cfg.outerPeakRadius) * t;
			}
		} else {
			outerHeight = 0;
			outerRadius = 0;
		}

		if (innerHeight <= 0.01F) {
			this.remove();
		}
	}

	@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTick) {
		Vec3 camPos = camera.getPosition();
		final float px = (float) (Mth.lerp(partialTick, this.xo, this.x) - camPos.x());
		final float py = (float) (Mth.lerp(partialTick, this.yo, this.y) - camPos.y());
		final float pz = (float) (Mth.lerp(partialTick, this.zo, this.z) - camPos.z());

		if (innerAge < cfg.innerLifespan && innerHeight > 0.01f) {
			float globalAlpha = getGlobalAlpha(innerAge, cfg.innerLifespan, partialTick);
			if (globalAlpha > 0.001f) {
				renderInnerCube(consumer, px, py, pz, innerHeight, innerHalfWidth, globalAlpha);
			}
		}

		if (outerAge < cfg.outerLifespan && outerRadius > 0.05f && outerHeight > 0.01f) {
			float globalAlpha = getGlobalAlpha(outerAge, cfg.outerLifespan, partialTick);
			if (globalAlpha > 0.001f) {
				renderOuterCylinder(consumer, px, py, pz, outerHeight, outerRadius, globalAlpha);
			}
		}
	}

	private float getGlobalAlpha(int age, int lifespan, float partialTick) {
		float progress = (age + partialTick) / lifespan;
		float alpha = 1.0f;
		if (progress < 0.2f) alpha = progress / 0.2f;
		if (progress > 0.7f) alpha = 1.0f - (progress - 0.7f) / 0.3f;
		return Mth.clamp(alpha, 0.0f, 1.0f);
	}

	// ---------- 渲染方法（从 cfg 中取颜色，其余逻辑不变） ----------
	private void renderInnerCube(VertexConsumer consumer, float px, float py, float pz,
								 float height, float halfWidth, float globalAlpha) {
		float yBottom = py;
		float yTop = py + height;
		float xMin = px - halfWidth;
		float xMax = px + halfWidth;
		float zFront = pz + halfWidth;
		float zBack = pz - halfWidth;
		int light = LightTexture.pack(15, 15);

		java.util.function.BiConsumer<Vector3f[], Float> addQuad = (verts, yBase) -> {
			for (Vector3f v : verts) {
				float t = (v.y() - yBottom) / (yTop - yBottom);
				float r = cfg.rBottom + (cfg.rTop - cfg.rBottom) * t;
				float g = cfg.gBottom + (cfg.gTop - cfg.gBottom) * t;
				float b = cfg.bBottom + (cfg.bTop - cfg.bBottom) * t;
				float a = (cfg.alphaBottom + (cfg.alphaTop - cfg.alphaBottom) * t) * globalAlpha;
				consumer.vertex(v.x(), v.y(), v.z()).color(r, g, b, a).uv2(light).endVertex();
			}
		};

		addQuad.accept(new Vector3f[]{new Vector3f(xMin, yBottom, zFront), new Vector3f(xMin, yTop, zFront), new Vector3f(xMax, yTop, zFront), new Vector3f(xMax, yBottom, zFront)}, yBottom);
		addQuad.accept(new Vector3f[]{new Vector3f(xMax, yBottom, zBack), new Vector3f(xMax, yTop, zBack), new Vector3f(xMin, yTop, zBack), new Vector3f(xMin, yBottom, zBack)}, yBottom);
		addQuad.accept(new Vector3f[]{new Vector3f(xMin, yBottom, zBack), new Vector3f(xMin, yTop, zBack), new Vector3f(xMin, yTop, zFront), new Vector3f(xMin, yBottom, zFront)}, yBottom);
		addQuad.accept(new Vector3f[]{new Vector3f(xMax, yBottom, zFront), new Vector3f(xMax, yTop, zFront), new Vector3f(xMax, yTop, zBack), new Vector3f(xMax, yBottom, zBack)}, yBottom);
	}

	private void renderOuterCylinder(VertexConsumer consumer, float px, float py, float pz,
									 float height, float radius, float globalAlpha) {
		int segments = 24;
		double angleStep = 2 * Math.PI / segments;
		float yBottom = py;
		float yTop = py + height;
		int light = LightTexture.pack(15, 15);

		for (int i = 0; i < segments; i++) {
			double angle1 = i * angleStep;
			double angle2 = (i + 1) * angleStep;
			float x1 = px + (float) (radius * Math.cos(angle1));
			float z1 = pz + (float) (radius * Math.sin(angle1));
			float x2 = px + (float) (radius * Math.cos(angle2));
			float z2 = pz + (float) (radius * Math.sin(angle2));

			Vector3f v1 = new Vector3f(x1, yBottom, z1);
			Vector3f v2 = new Vector3f(x2, yBottom, z2);
			Vector3f v3 = new Vector3f(x2, yTop, z2);
			Vector3f v4 = new Vector3f(x1, yTop, z1);

			float t1 = (v1.y() - yBottom) / (yTop - yBottom);
			float t2 = (v2.y() - yBottom) / (yTop - yBottom);
			float t3 = (v3.y() - yBottom) / (yTop - yBottom);
			float t4 = (v4.y() - yBottom) / (yTop - yBottom);

			java.util.function.Function<Float, float[]> getColor = t -> {
				float r = cfg.rBottom + (cfg.rTop - cfg.rBottom) * t;
				float g = cfg.gBottom + (cfg.gTop - cfg.gBottom) * t;
				float b = cfg.bBottom + (cfg.bTop - cfg.bBottom) * t;
				float a = (cfg.alphaBottom + (cfg.alphaTop - cfg.alphaBottom) * t) * globalAlpha;
				return new float[]{r, g, b, a};
			};

			float[] c1 = getColor.apply(t1);
			float[] c2 = getColor.apply(t2);
			float[] c3 = getColor.apply(t3);
			float[] c4 = getColor.apply(t4);

			consumer.vertex(v1.x(), v1.y(), v1.z()).color(c1[0], c1[1], c1[2], c1[3]).uv2(light).endVertex();
			consumer.vertex(v2.x(), v2.y(), v2.z()).color(c2[0], c2[1], c2[2], c2[3]).uv2(light).endVertex();
			consumer.vertex(v3.x(), v3.y(), v3.z()).color(c3[0], c3[1], c3[2], c3[3]).uv2(light).endVertex();
			consumer.vertex(v4.x(), v4.y(), v4.z()).color(c4[0], c4[1], c4[2], c4[3]).uv2(light).endVertex();
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return CUSTOM_RENDER_TYPE;
	}

	private static final ParticleRenderType CUSTOM_RENDER_TYPE = new ParticleRenderType() {
		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.disableCull();
			RenderSystem.depthMask(false);
			builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
		}

		@Override
		public void end(Tesselator tesselator) {
			tesselator.end();
			RenderSystem.depthMask(true);
			RenderSystem.enableCull();
			RenderSystem.disableBlend();
		}

		@Override
		public String toString() {
			return "toge:summon_particle";
		}
	};

	public static class Provider implements ParticleProvider<SummonParticleOptions> {
		@Override
		public Particle createParticle(SummonParticleOptions type, ClientLevel level,
									   double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed) {
			return new SummonParticle(level, x, y, z, type);
		}
		public static Provider create(SpriteSet sprite) {
			return new Provider();
		}
	}
}