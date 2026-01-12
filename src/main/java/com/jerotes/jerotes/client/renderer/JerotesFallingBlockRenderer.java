package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.entity.JerotesFallingBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class JerotesFallingBlockRenderer extends EntityRenderer<JerotesFallingBlock> {
	public JerotesFallingBlockRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(JerotesFallingBlock entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		matrixStackIn.pushPose();
		if (entityIn.getMode() == JerotesFallingBlock.FallingMoveType.OVERALL_MOVE) {
			matrixStackIn.translate(-0.5f, 0, -0.5f);
		} else {
			matrixStackIn.translate(0, 0.5f, 0);
			matrixStackIn.translate(0, Mth.lerp(partialTicks, entityIn.prevAnimY, entityIn.animY), 0);
			matrixStackIn.translate(0, -1, 0);
			matrixStackIn.translate(-0.5f, -0.5f, -0.5f);
		}
		dispatcher.renderSingleBlock(entityIn.getBlockState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
		matrixStackIn.popPose();
	}

	public ResourceLocation getTextureLocation(JerotesFallingBlock p_114632_) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}