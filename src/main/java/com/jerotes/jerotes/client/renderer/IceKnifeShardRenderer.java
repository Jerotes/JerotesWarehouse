package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modelice_knife;
import com.jerotes.jerotes.client.model.Modelice_knife_shard;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot.IceKnifeEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicThrow.IceKnifeShardEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class IceKnifeShardRenderer extends EntityRenderer<IceKnifeShardEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/projectiles/ice_knife.png");
	private final Modelice_knife_shard model;

	public IceKnifeShardRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new Modelice_knife_shard<>(context.bakeLayer(Modelice_knife_shard.LAYER_LOCATION));
	}

	@Override
	public void render(IceKnifeShardEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(JerotesRenderType.entityTranslucent(getTextureLocation(entityIn)));
		poseStack.pushPose();

		float yaw = entityIn.getYRot();
		float pitch = entityIn.getXRot();

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
		poseStack.mulPose(Axis.XP.rotationDegrees(pitch + 90.0F));
		poseStack.translate(0.0F, -1.5F, 0.0F);

		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0F);

		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(IceKnifeShardEntity entity) {
		return TEXTURE;
	}
}

