package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modelice_knife;
import com.jerotes.jerotes.client.model.Modeljavelin;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot.IceKnifeEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicThrow.WitchBoltEntity;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class IceKnifeRenderer extends EntityRenderer<IceKnifeEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/projectiles/ice_knife.png");
	private final Modelice_knife model;

	public IceKnifeRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new Modelice_knife(context.bakeLayer(Modelice_knife.LAYER_LOCATION));
	}

	@Override
	public void render(IceKnifeEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(JerotesRenderType.entityTranslucent(getTextureLocation(entityIn)));
		poseStack.pushPose();
		float yaw = entityIn.getYRot();
		float pitch = entityIn.getXRot();

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0.0F, -0.25F, 0.0F);
		poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
		poseStack.mulPose(Axis.XP.rotationDegrees(-pitch + 90.0F));
		poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
		poseStack.translate(0.0F, -0.5F, 0.0F);

		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1.0f);

		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(IceKnifeEntity entity) {
		return TEXTURE;
	}
}

