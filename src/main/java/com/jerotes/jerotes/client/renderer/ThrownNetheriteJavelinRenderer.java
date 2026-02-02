package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modeljavelin;
import com.jerotes.jerotes.entity.Shoot.Arrow.ThrownJavelinNetheriteEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ThrownNetheriteJavelinRenderer extends EntityRenderer<ThrownJavelinNetheriteEntity> {
	private static final ResourceLocation texture = new ResourceLocation(JerotesWarehouse.MODID, "textures/item/netherite_javelin_model.png");
	private final Modeljavelin model;

	public ThrownNetheriteJavelinRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new Modeljavelin(context.bakeLayer(Modeljavelin.LAYER_LOCATION));
	}

	@Override
	public void render(ThrownJavelinNetheriteEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = ItemRenderer.getFoilBufferDirect(bufferIn, this.model.renderType(this.getTextureLocation(entityIn)), false, entityIn.isFoil());
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(ThrownJavelinNetheriteEntity entity) {
		return texture;
	}
}
