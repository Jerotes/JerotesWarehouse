package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.entity.Other.BaseFallingBlock;
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
import org.jetbrains.annotations.NotNull;

public class BaseFallingBlockRenderer extends EntityRenderer<BaseFallingBlock> {
	public BaseFallingBlockRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(BaseFallingBlock entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		matrixStackIn.pushPose();
		if (entityIn.getMode() == BaseFallingBlock.FallingMoveType.OVERALL_MOVE) {
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

	public @NotNull ResourceLocation getTextureLocation(@NotNull BaseFallingBlock baseFallingBlock) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}