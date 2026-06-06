package com.jerotes.jerotes.client.renderer;

import com.google.common.collect.Maps;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.layer.JerotesHorseArmorLayer;
import com.jerotes.jerotes.client.layer.JerotesHorseMarkingLayer;
import com.jerotes.jerotes.client.layer.ReinLayer;
import com.jerotes.jerotes.client.layer.TameLayer;
import com.jerotes.jerotes.client.model.Modelbig_beast;
import com.jerotes.jerotes.client.model.Modeljerotes_horse;
import com.jerotes.jerotes.client.model.Modeljerotes_horse_saddle;
import com.jerotes.jerotes.entity.Mob.BigBeastEntity;
import com.jerotes.jerotes.entity.Mob.JerotesHorseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Variant;

import java.util.Map;

public class BigBeastRenderer extends MobRenderer<BigBeastEntity, Modelbig_beast<BigBeastEntity>> {
	private static final ResourceLocation LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/big_beast/big_beast.png");
	public BigBeastRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelbig_beast<>(context.bakeLayer(Modelbig_beast.LAYER_LOCATION)), 2.75F);
	}

	@Override
	protected void scale(BigBeastEntity entity, PoseStack poseStack, float f) {
		super.scale(entity, poseStack, f);
	}

	public ResourceLocation getTextureLocation(BigBeastEntity p_114872_) {
		return LOCATION;
	}
}

