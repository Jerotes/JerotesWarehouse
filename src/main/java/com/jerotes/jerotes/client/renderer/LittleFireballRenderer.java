package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.client.model.Modellittle_fireball;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LittleFireballRenderer extends EntityRenderer<MagicAboutEntity> {
	private final Modellittle_fireball model;

	public LittleFireballRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new Modellittle_fireball(context.bakeLayer(Modellittle_fireball.LAYER_LOCATION));
	}

	protected int getBlockLightLevel(MagicAboutEntity magicAboutEntity, BlockPos blockPos) {
		return magicAboutEntity.isNormalLittleFireball() ? 15 : super.getBlockLightLevel(magicAboutEntity, blockPos);
	}

	@Override
	public void render(MagicAboutEntity magicAboutEntity, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
		poseStack.pushPose();
		float f3 = (float)magicAboutEntity.tickCount + f2;
		VertexConsumer vertexConsumer = multiBufferSource.getBuffer(JerotesRenderType.flow(getTextureLocation(magicAboutEntity), this.xOffset(f3) % 1.0f, 0.0f));

		poseStack.scale(-1.0F, -1.0F, 1.0F);
		poseStack.translate(0f, -1.5 - 1/8f, 0f);
		float fs = Mth.rotLerp(f2, magicAboutEntity.yRotO, magicAboutEntity.getYRot());
		float fs1 = Mth.lerp(f2, magicAboutEntity.xRotO, magicAboutEntity.getXRot());


		this.model.setupAnim(0.0F, fs, fs1);
		this.model.renderToBuffer(poseStack, vertexConsumer, n, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 0.5f);
		poseStack.popPose();
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

