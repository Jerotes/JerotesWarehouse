package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.model.Modelwind_charge;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class WindChargeRenderer extends EntityRenderer<MagicAboutEntity> {
	private final Modelwind_charge model;

	public WindChargeRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new Modelwind_charge(context.bakeLayer(Modelwind_charge.LAYER_LOCATION));
	}

	@Override
	public void render(MagicAboutEntity magicAboutEntity, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
		float f3 = (float)magicAboutEntity.tickCount + f2;
		VertexConsumer vertexConsumer = multiBufferSource.getBuffer(JerotesRenderType.flow(getTextureLocation(magicAboutEntity), this.xOffset(f3) % 1.0f, 0.0f));
		this.model.setupAnim(magicAboutEntity, 0.0f, 0.0f, f3, 0.0f, 0.0f);
		this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 0.5f);
		super.render(magicAboutEntity, f, f2, poseStack, multiBufferSource, n);
	}

	protected float xOffset(float f) {
		return f * 0.03f;
	}

	@Override
	public ResourceLocation getTextureLocation(MagicAboutEntity magicAboutEntity) {
		return magicAboutEntity.getAsSpecialRendererTextureLocation();
	}
}

