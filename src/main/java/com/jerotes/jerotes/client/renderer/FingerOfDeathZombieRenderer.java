package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modeljerotes_zombie;
import com.jerotes.jerotes.entity.MagicSummoned.Zombie.FingerOfDeathZombieEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Zombie.JerotesZombieEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FingerOfDeathZombieRenderer extends HumanoidMobRenderer<FingerOfDeathZombieEntity, Modeljerotes_zombie<FingerOfDeathZombieEntity>> {
	private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/zombie/finger_of_death_zombie.png");

	public FingerOfDeathZombieRenderer(EntityRendererProvider.Context context) {
		super(context, new Modeljerotes_zombie<>(context.bakeLayer(Modeljerotes_zombie.LAYER_LOCATION)), 0.5F);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(context.bakeLayer(ModelLayers.ZOMBIE_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(ModelLayers.ZOMBIE_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	protected void scale(FingerOfDeathZombieEntity entity, PoseStack poseStack, float f) {
		poseStack.scale((entity.getScaleHeight() / 1.95f), (entity.getScaleHeight() / 1.95f), (entity.getScaleHeight() / 1.95f));
		super.scale(entity, poseStack, f);
	}
	@Override
	public void render(FingerOfDeathZombieEntity entity, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
		this.shadowRadius = (entity.getScaleHeight() / 1.95f * 0.5f);
		super.render(entity, f, f2, poseStack, multiBufferSource, n);
	}
	public ResourceLocation getTextureLocation(FingerOfDeathZombieEntity jerotesZombieEntity) {
		return ZOMBIE_LOCATION;
	}

	protected boolean isShaking(FingerOfDeathZombieEntity jerotesZombieEntity) {
		return super.isShaking(jerotesZombieEntity) || jerotesZombieEntity.isUnderWaterConverting();
	}
}