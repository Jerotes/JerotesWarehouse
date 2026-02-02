package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.animation.JerotesHorseAnimation;
import com.jerotes.jerotes.entity.Mob.JerotesHorseEntity;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Modeljerotes_horse_saddle<T extends JerotesHorseEntity> extends Modeljerotes_horse<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "jerotes_horse_saddle"), "main");
	private final ModelPart root;

	public Modeljerotes_horse_saddle(ModelPart root) {
		super(root);
		this.root = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 4.3333F, -4.3333F));

		PartDefinition main_body = body.addOrReplaceChild("main_body", CubeListBuilder.create(), PartPose.offset(0.0F, 3.6667F, 4.3333F));

		PartDefinition saddle = main_body.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(26, 0).addBox(-5.0F, -8.0F, -9.0F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 3.0F, 6.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(0.0F, -2.3333F, -4.6667F));

		PartDefinition mane = neck.addOrReplaceChild("mane", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.01F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -5.625F, -1.63F));

		PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create(), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.offset(0.0F, 4.625F, 1.62F));

		PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.offset(0.0F, 4.625F, 1.62F));

		PartDefinition noseband = head.addOrReplaceChild("noseband", CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition left_rein = head.addOrReplaceChild("left_rein", CubeListBuilder.create().texOffs(32, 2).addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.125F, 5.13F));

		PartDefinition right_rein = head.addOrReplaceChild("right_rein", CubeListBuilder.create().texOffs(32, 2).addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.125F, 5.13F));

		PartDefinition headpiece = head.addOrReplaceChild("headpiece", CubeListBuilder.create().texOffs(1, 1).addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 5.625F, 1.73F));

		PartDefinition left_bit = head.addOrReplaceChild("left_bit", CubeListBuilder.create().texOffs(29, 5).addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition right_bit = head.addOrReplaceChild("right_bit", CubeListBuilder.create().texOffs(29, 5).addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, -1.3333F, 15.3333F));

		PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create(), PartPose.offset(4.0F, 13.0F, -9.0F));

		PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create(), PartPose.offset(-4.0F, 13.0F, -9.0F));

		PartDefinition left_back_leg = partdefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create(), PartPose.offset(4.0F, 13.0F, 8.0F));

		PartDefinition right_back_leg = partdefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create(), PartPose.offset(-4.0F, 13.0F, 8.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}
}