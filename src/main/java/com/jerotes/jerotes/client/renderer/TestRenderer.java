package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modelhumanoid;
import com.jerotes.jerotes.client.model.Modelhumanoid_wide_or_slim;
import com.jerotes.jerotes.client.model.Modelhumanoid_wide_or_slim_for_human;
import com.jerotes.jerotes.entity.Mob.TestEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class TestRenderer extends HumanoidMobRenderer<TestEntity, Modelhumanoid<TestEntity>> {
	private static final ResourceLocation LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/jerotes_old.png");
	private static final ResourceLocation SLIM_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/jerotes_old_slim.png");
	public TestRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelhumanoid_wide_or_slim<>(context.bakeLayer(Modelhumanoid_wide_or_slim.LAYER_LOCATION)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	protected void scale(TestEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(0.9375f * entity.getSize() / 100, 0.9375f * entity.getSize() / 100, 0.9375f * entity.getSize() / 100);
		super.scale(entity, poseStack, f);
	}
	@Override
	public void render(TestEntity entity, float f, float f2, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
		this.shadowRadius = 0.5f * entity.getSize() / 100;
		super.render(entity, f, f2, poseStack, multiBufferSource, n);
	}


	@Override
	public ResourceLocation getTextureLocation(TestEntity entity) {
		if (entity.IsFemale()) {
			return SLIM_LOCATION;
		}
        return LOCATION;
	}

	protected boolean shouldShowName(TestEntity entity) {
		return true;
	}
}
