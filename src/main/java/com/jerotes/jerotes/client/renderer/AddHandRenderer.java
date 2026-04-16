package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.model.Modeladd_hand;
import com.jerotes.jerotes.entity.Mob.AddHandEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class AddHandRenderer extends HumanoidMobRenderer<AddHandEntity, Modeladd_hand<AddHandEntity>> {
	private static final ResourceLocation LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/null.png");
	public AddHandRenderer(EntityRendererProvider.Context context) {
		super(context, new Modeladd_hand<>(context.bakeLayer(Modeladd_hand.LAYER_LOCATION)), 0f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
	}

	@Override
	protected void scale(AddHandEntity entity, PoseStack poseStack, float f) {
		poseStack.scale((float) entity.getSize() / 100, (float) entity.getSize() / 100, (float) entity.getSize() / 100);
		super.scale(entity, poseStack, f);
	}

	@Override
	public ResourceLocation getTextureLocation(AddHandEntity entity) {
        return LOCATION;
	}

	protected boolean shouldShowName(AddHandEntity entity) {
		return false;
	}
}
