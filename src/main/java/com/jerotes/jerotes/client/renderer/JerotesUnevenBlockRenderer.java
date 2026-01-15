package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.entity.Other.BaseFallingBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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

public class JerotesUnevenBlockRenderer extends EntityRenderer<BaseFallingBlock> {
	public JerotesUnevenBlockRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(BaseFallingBlock entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
		matrixStackIn.pushPose();
			BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		matrixStackIn.scale(0.9995f, 0.9995f, 0.9995f);
		matrixStackIn.translate(-0.5f, 0, -0.5f);


		matrixStackIn.translate(0.5f, 0.5f, 0.5f);
		float xRot = entityIn.getXRot() + 90;
		float yRot = entityIn.getYRot() + 90;
		//转向
		matrixStackIn.mulPose(Axis.YP.rotationDegrees(-yRot));
		//里外翻滚
		matrixStackIn.mulPose(Axis.ZP.rotationDegrees(-xRot));
		matrixStackIn.translate(-0.5f, -0.5f, -0.5f);

		dispatcher.renderSingleBlock(entityIn.getBlockState(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
		matrixStackIn.popPose();
	}

	public @NotNull ResourceLocation getTextureLocation(@NotNull BaseFallingBlock baseFallingBlock) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}