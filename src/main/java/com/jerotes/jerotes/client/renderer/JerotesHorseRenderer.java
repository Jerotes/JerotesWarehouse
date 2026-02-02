package com.jerotes.jerotes.client.renderer;

import com.google.common.collect.Maps;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.layer.JerotesHorseArmorLayer;
import com.jerotes.jerotes.client.layer.JerotesHorseMarkingLayer;
import com.jerotes.jerotes.client.layer.ReinLayer;
import com.jerotes.jerotes.client.layer.TameLayer;
import com.jerotes.jerotes.client.model.Modeljerotes_horse;
import com.jerotes.jerotes.client.model.Modeljerotes_horse_saddle;
import com.jerotes.jerotes.entity.Mob.JerotesHorseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Variant;

import java.util.Map;

public class JerotesHorseRenderer extends MobRenderer<JerotesHorseEntity, Modeljerotes_horse<JerotesHorseEntity>> {
	private static final Map<Variant, ResourceLocation> LOCATION_BY_VARIANT = Util.make(Maps.newEnumMap(Variant.class), (p_114874_) -> {
		p_114874_.put(Variant.WHITE, new ResourceLocation("textures/entity/horse/horse_white.png"));
		p_114874_.put(Variant.CREAMY, new ResourceLocation("textures/entity/horse/horse_creamy.png"));
		p_114874_.put(Variant.CHESTNUT, new ResourceLocation("textures/entity/horse/horse_chestnut.png"));
		p_114874_.put(Variant.BROWN, new ResourceLocation("textures/entity/horse/horse_brown.png"));
		p_114874_.put(Variant.BLACK, new ResourceLocation("textures/entity/horse/horse_black.png"));
		p_114874_.put(Variant.GRAY, new ResourceLocation("textures/entity/horse/horse_gray.png"));
		p_114874_.put(Variant.DARK_BROWN, new ResourceLocation("textures/entity/horse/horse_darkbrown.png"));
	});
	private static final ResourceLocation TAME_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/horse/jerotes_horse_tame.png");
	private static final ResourceLocation SADDLE_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/horse/jerotes_horse_saddle.png");
	private static final ResourceLocation REIN_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/horse/jerotes_horse_rein.png");
	public JerotesHorseRenderer(EntityRendererProvider.Context context) {
		super(context, new Modeljerotes_horse(context.bakeLayer(Modeljerotes_horse.LAYER_LOCATION)), 0.75F);
		this.addLayer(new JerotesHorseMarkingLayer(this));
		this.addLayer(new TameLayer(this, new Modeljerotes_horse(context.bakeLayer(Modeljerotes_horse.LAYER_LOCATION)), TAME_LOCATION));
		this.addLayer(new SaddleLayer(this, new Modeljerotes_horse_saddle(context.bakeLayer(Modeljerotes_horse_saddle.LAYER_LOCATION)), SADDLE_LOCATION));
		this.addLayer(new ReinLayer(this, new Modeljerotes_horse_saddle(context.bakeLayer(Modeljerotes_horse_saddle.LAYER_LOCATION)), REIN_LOCATION));
		this.addLayer(new JerotesHorseArmorLayer(this, context.getModelSet()));
	}

	@Override
	protected void scale(JerotesHorseEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(1.1F, 1.1F, 1.1F);
		super.scale(entity, poseStack, f);
	}

	public ResourceLocation getTextureLocation(JerotesHorseEntity p_114872_) {
		return LOCATION_BY_VARIANT.get(p_114872_.getVariant());
	}
}

