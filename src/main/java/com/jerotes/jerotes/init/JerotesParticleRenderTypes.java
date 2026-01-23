package com.jerotes.jerotes.init;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JerotesParticleRenderTypes {
	public static final ParticleRenderType CUSTOM_TRANSLUCENT_SHOCKWAVE = new ParticleRenderType() {
		@Override
		public void begin(BufferBuilder builder, TextureManager manager) {
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(
					GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE
			);
			RenderSystem.depthMask(false);
			builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		}

		public void end(Tesselator tesselator) {
			tesselator.end();
			RenderSystem.depthMask(true);
			RenderSystem.defaultBlendFunc();
		}
	};
}