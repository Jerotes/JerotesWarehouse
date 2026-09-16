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
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BallParticle extends Particle {

	private final BallParticleOptions options;
	private float currentRadius;
	private float currentAlpha;
	private int currentColor; // 当前颜色 (ARGB)

	public BallParticle(ClientLevel level, double x, double y, double z, BallParticleOptions options) {
		super(level, x, y, z);
		this.options = options;
		this.lifetime = options.lifespan;
		this.hasPhysics = false;
		this.currentRadius = options.startRadius;
		this.currentAlpha = options.alphaStart;
		this.currentColor = options.startColor;
	}

	@Override
	public void tick() {
		super.tick();
		if (age >= lifetime) {
			remove();
			return;
		}

		float progress = (float) age / lifetime;

		// 半径曲线：先膨胀后收缩（对称）
		if (progress <= 0.5f) {
			float t = progress / 0.5f;
			currentRadius = options.startRadius + (options.peakRadius - options.startRadius) * t;
		} else {
			float t = (progress - 0.5f) / 0.5f;
			currentRadius = options.peakRadius + (options.endRadius - options.peakRadius) * t;
		}

		// 透明度：从 alphaStart 到 alphaPeak（在 progress=0.5 时达到峰值），再到 alphaEnd
		// 这里使用分段线性插值，与 renderBomb 的 fadeAlpha 思路类似
		float alpha;
		if (progress <= 0.5f) {
			float t = progress / 0.5f;
			alpha = options.alphaStart + (options.alphaPeak - options.alphaStart) * t;
		} else {
			float t = (progress - 0.5f) / 0.5f;
			alpha = options.alphaPeak + (options.alphaEnd - options.alphaPeak) * t;
		}
		currentAlpha = Mth.clamp(alpha, 0.0f, 1.0f);

		// 颜色插值：线性从 startColor 到 endColor
		int rStart = (options.startColor >> 16) & 0xFF;
		int gStart = (options.startColor >> 8) & 0xFF;
		int bStart = options.startColor & 0xFF;
		int rEnd = (options.endColor >> 16) & 0xFF;
		int gEnd = (options.endColor >> 8) & 0xFF;
		int bEnd = options.endColor & 0xFF;

		float r = rStart + (rEnd - rStart) * progress;
		float g = gStart + (gEnd - gStart) * progress;
		float b = bStart + (bEnd - bStart) * progress;
		int rInt = Mth.clamp((int) r, 0, 255);
		int gInt = Mth.clamp((int) g, 0, 255);
		int bInt = Mth.clamp((int) b, 0, 255);
		// 保留 alpha 通道（渲染时单独使用 alpha，颜色中可忽略 alpha）
		currentColor = (rInt << 16) | (gInt << 8) | bInt;
	}

	@Override
	public void render(VertexConsumer consumer, Camera camera, float partialTick) {
		float radius = currentRadius;
		if (radius <= 0.01f || currentAlpha <= 0.001f) return;

		// 粒子位置（相对于相机）
		Vec3 camPos = camera.getPosition();
		float px = (float) (Mth.lerp(partialTick, xo, x) - camPos.x());
		float py = (float) (Mth.lerp(partialTick, yo, y) - camPos.y());
		float pz = (float) (Mth.lerp(partialTick, zo, z) - camPos.z());

		int segments = 24;
		int stacks = 16;
		int light = LightTexture.pack(15, 15);

		// 提取当前颜色的 RGB
		int cr = (currentColor >> 16) & 0xFF;
		int cg = (currentColor >> 8) & 0xFF;
		int cb = currentColor & 0xFF;
		int alpha = (int) (currentAlpha * 255);

		// 球体网格顶点生成（与 renderBomb 类似，但坐标需要平移到粒子位置）
		for (int i = 0; i < stacks; i++) {
			float phi1 = (float) i / stacks * (float) Math.PI;
			float phi2 = (float) (i + 1) / stacks * (float) Math.PI;
			for (int j = 0; j < segments; j++) {
				float theta1 = (float) j / segments * 2.0f * (float) Math.PI;
				float theta2 = (float) (j + 1) / segments * 2.0f * (float) Math.PI;

				// 计算局部坐标（球心在原点）
				float x1 = radius * (float) Math.sin(phi1) * (float) Math.cos(theta1);
				float y1 = radius * (float) Math.cos(phi1);
				float z1 = radius * (float) Math.sin(phi1) * (float) Math.sin(theta1);

				float x2 = radius * (float) Math.sin(phi1) * (float) Math.cos(theta2);
				float y2 = radius * (float) Math.cos(phi1);
				float z2 = radius * (float) Math.sin(phi1) * (float) Math.sin(theta2);

				float x3 = radius * (float) Math.sin(phi2) * (float) Math.cos(theta2);
				float y3 = radius * (float) Math.cos(phi2);
				float z3 = radius * (float) Math.sin(phi2) * (float) Math.sin(theta2);

				float x4 = radius * (float) Math.sin(phi2) * (float) Math.cos(theta1);
				float y4 = radius * (float) Math.cos(phi2);
				float z4 = radius * (float) Math.sin(phi2) * (float) Math.sin(theta1);

				// 平移至粒子世界位置（相对于相机）
				x1 += px; y1 += py; z1 += pz;
				x2 += px; y2 += py; z2 += pz;
				x3 += px; y3 += py; z3 += pz;
				x4 += px; y4 += py; z4 += pz;

				// 法线（归一化的位置向量，因为球心在原点）
				Vec3 n1 = new Vec3(x1 - px, y1 - py, z1 - pz).normalize();
				Vec3 n2 = new Vec3(x2 - px, y2 - py, z2 - pz).normalize();
				Vec3 n3 = new Vec3(x3 - px, y3 - py, z3 - pz).normalize();
				Vec3 n4 = new Vec3(x4 - px, y4 - py, z4 - pz).normalize();

				// 添加四边形（使用 position-color 模式，不需要纹理坐标）
				consumer.vertex(x1, y1, z1).color(cr, cg, cb, alpha).uv2(light).endVertex();
				consumer.vertex(x2, y2, z2).color(cr, cg, cb, alpha).uv2(light).endVertex();
				consumer.vertex(x3, y3, z3).color(cr, cg, cb, alpha).uv2(light).endVertex();
				consumer.vertex(x4, y4, z4).color(cr, cg, cb, alpha).uv2(light).endVertex();
			}
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
			RenderSystem.disableCull();   // 双面渲染使球体内部可见，更漂亮
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
			return "toge:ball_particle";
		}
	};

	public static class Provider implements ParticleProvider<BallParticleOptions> {
		@Override
		public Particle createParticle(BallParticleOptions type, ClientLevel level,
									   double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed) {
			return new BallParticle(level, x, y, z, type);
		}

		public static Provider create(SpriteSet sprite) {
			return new Provider();
		}
	}
}