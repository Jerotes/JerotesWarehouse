package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
public class JerotesRenderType extends RenderType {
	public JerotesRenderType(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
		super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
	}

	private static final RenderType BEAST_TOUGH_GLINT = create("tough_glint",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			false,
			CompositeState.builder()
					.setShaderState(RENDERTYPE_GLINT_SHADER)
					.setTextureState(new TextureStateShard(new ResourceLocation(JerotesWarehouse.MODID, "textures/misc/tough_glint.png"), true, false))
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(EQUAL_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY)
					.setTexturingState(GLINT_TEXTURING)
					.createCompositeState(false));

	public static RenderType tough_glint() {
		return BEAST_TOUGH_GLINT;
	}

	public static RenderType dagger_glint() {
		return create(
				"dagger_glint",
				DefaultVertexFormat.POSITION_TEX,
				VertexFormat.Mode.QUADS,
				256,
				false,
				false,
				CompositeState.builder()
						.setShaderState(RenderStateShard.POSITION_TEX_SHADER)
						.setTextureState(new TextureStateShard(
								new ResourceLocation(JerotesWarehouse.MODID, "textures/misc/enchanted_glint_dagger.png"),
								false,
								false
						))
						.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
						.setCullState(RenderStateShard.NO_CULL)
						.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
						.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
						.setTexturingState(RenderStateShard.GLINT_TEXTURING)
						.setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
						.createCompositeState(false)
		);
	}
	public static RenderType beam_light() {
		return create(
				"beam_light",
				DefaultVertexFormat.POSITION_COLOR,
				VertexFormat.Mode.TRIANGLES,
				256,
				false,
				false,
				RenderType.CompositeState.builder()
						.setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
						.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
						.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
						.setCullState(RenderStateShard.NO_CULL)
						.createCompositeState(false));
	}

	public static RenderType flow(ResourceLocation resourceLocation, float f, float f2) {
		return create("flow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new TextureStateShard(resourceLocation, false, false)).setTexturingState(new OffsetTexturingStateShard(f, f2)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(NO_OVERLAY).createCompositeState(false));
	}

	public static RenderType flowGlow(ResourceLocation resourceLocation, float f, float f2) {
		return create("flow_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, CompositeState.builder().setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new TextureStateShard(resourceLocation, false, false)).setTexturingState(new OffsetTexturingStateShard(f, f2)).setTransparencyState(ADDITIVE_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
	}

	public static RenderType flowGlowOneSide(ResourceLocation resourceLocation, float f, float f2) {
		return create("flow_glow_one_side",
				DefaultVertexFormat.NEW_ENTITY,
				VertexFormat.Mode.QUADS,
				1536,
				false,
				true,
				CompositeState.builder()
						.setShaderState(RENDERTYPE_ENERGY_SWIRL_SHADER)
						.setTextureState(new TextureStateShard(resourceLocation, false, false))
						.setTexturingState(new OffsetTexturingStateShard(f, f2))
						.setTransparencyState(ADDITIVE_TRANSPARENCY)
						.setCullState(CullStateShard.CULL)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.createCompositeState(false));
	}

	private static final BiFunction<ResourceLocation, TransparencyStateShard, RenderType> GLOW = Util.memoize((resourceLocation, transparencyStateShard) -> {
		TextureStateShard textureStateShard = new TextureStateShard((ResourceLocation)resourceLocation, false, false);
		return RenderType.create("glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, CompositeState.builder().setShaderState(RENDERTYPE_EYES_SHADER).setTextureState(textureStateShard).setTransparencyState((TransparencyStateShard)transparencyStateShard).setWriteMaskState(COLOR_WRITE).createCompositeState(false));
	});
	public static RenderType glow(ResourceLocation resourceLocation) {
		return GLOW.apply(resourceLocation, ADDITIVE_TRANSPARENCY);
	}

	private static final BiFunction<ResourceLocation, TransparencyStateShard, RenderType> GLOW_DOUBLE_SIDED =
			Util.memoize((resourceLocation, transparencyStateShard) -> {
				TextureStateShard textureStateShard = new TextureStateShard(resourceLocation, false, false);
				return RenderType.create(
						"glow_double_sided",
						DefaultVertexFormat.NEW_ENTITY,
						VertexFormat.Mode.QUADS,
						1536,
						false,
						true,
						CompositeState.builder()
								.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
								.setTextureState(textureStateShard)
								.setTransparencyState(transparencyStateShard)
								.setWriteMaskState(COLOR_DEPTH_WRITE)
								.setCullState(NO_CULL)
								.setDepthTestState(LEQUAL_DEPTH_TEST)
								.setOutputState(MAIN_TARGET)
								.createCompositeState(false)
				);
			});

	public static RenderType glowDoubleSided(ResourceLocation resourceLocation) {
		return GLOW_DOUBLE_SIDED.apply(resourceLocation, ADDITIVE_TRANSPARENCY);
	}
	public static RenderType glowDoubleSidedTranslucent(ResourceLocation resourceLocation) {
		return GLOW_DOUBLE_SIDED.apply(resourceLocation, TRANSLUCENT_TRANSPARENCY);
	}
	public static RenderType glowDoubleSidedNoTransparency(ResourceLocation resourceLocation) {
		return GLOW_DOUBLE_SIDED.apply(resourceLocation, NO_TRANSPARENCY);
	}

	private static final RenderType GLOWING_OUTLINE = create("glowing_outline",
			DefaultVertexFormat.POSITION_COLOR_TEX,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			false,
			CompositeState.builder()
					.setShaderState(RenderStateShard.RENDERTYPE_GLINT_SHADER)
					.setTextureState(new TextureStateShard(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/truesight.png"), true, false))
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(NO_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY)
					.setTexturingState(GLINT_TEXTURING)
					.createCompositeState(false));

	public static RenderType glowing_outline() {
		return GLOWING_OUTLINE;
	}

	private static final RenderType GLOWING_OUTLINE_COMBAT = create("glowing_outline_combat",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			false,
			CompositeState.builder()
					.setShaderState(RenderStateShard.RENDERTYPE_GLINT_SHADER)
					.setTextureState(new TextureStateShard(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/truesight_combat.png"), true, false))
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(NO_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY)
					.setTexturingState(GLINT_TEXTURING)
					.createCompositeState(false));

	public static RenderType glowing_outline_combat() {
		return GLOWING_OUTLINE_COMBAT;
	}

	private static final RenderType GLOWING_OUTLINE_COMBAT_ANGRY = create("glowing_outline_combat_angry",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			false,
			CompositeState.builder()
					.setShaderState(RenderStateShard.RENDERTYPE_GLINT_SHADER)
					.setTextureState(new TextureStateShard(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/truesight_combat_angry.png"), true, false))
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(NO_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY)
					.setTexturingState(GLINT_TEXTURING)
					.createCompositeState(false));
	public static RenderType glowing_outline_combat_angry() {
		return GLOWING_OUTLINE_COMBAT_ANGRY;
	}

	private static final RenderType GLOWING_OUTLINE_COMBAT_LESS = create("glowing_outline_combat_less",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			false,
			CompositeState.builder()
					.setShaderState(RenderStateShard.RENDERTYPE_GLINT_SHADER)
					.setTextureState(new TextureStateShard(new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/truesight_combat_less.png"), true, false))
					.setWriteMaskState(COLOR_WRITE)
					.setCullState(NO_CULL)
					.setDepthTestState(NO_DEPTH_TEST)
					.setTransparencyState(GLINT_TRANSPARENCY)
					.setTexturingState(GLINT_TEXTURING)
					.createCompositeState(false));
	public static RenderType glowing_outline_combat_less() {
		return GLOWING_OUTLINE_COMBAT_LESS;
	}

}

