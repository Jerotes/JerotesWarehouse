package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Mob.JerotesHorseEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class Modeljerotes_horse_armor<T extends JerotesHorseEntity> extends Modeljerotes_horse<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "jerotes_horse_armor"), "main");
	private final ModelPart root;

	public Modeljerotes_horse_armor(ModelPart root) {
		super(root);
		this.root = root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 4.3333F, -4.3333F));

		PartDefinition main_body = body.addOrReplaceChild("main_body", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -5.0F, -11.0F, 10.0F, 10.0F, 22.0F, new CubeDeformation(0.05F))
				.texOffs(26, 0).addBox(-5.0F, -5.0F, -3.0F, 10.0F, 9.0F, 9.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 3.6667F, 4.3333F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 35).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.3333F, -4.6667F));

		PartDefinition mane = neck.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(56, 36).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 0.0F, -0.01F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, -5.375F, -0.37F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -5.625F, -1.63F));

		PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(19, 16).addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.625F, 1.62F));

		PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(19, 16).addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.625F, 1.62F));

		PartDefinition headpiece = head.addOrReplaceChild("headpiece", CubeListBuilder.create().texOffs(1, 1).addBox(-3.0F, -11.0F, -1.9F, 6.0F, 5.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition noseband = head.addOrReplaceChild("noseband", CubeListBuilder.create().texOffs(19, 0).addBox(-2.0F, -11.0F, -4.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition left_bit = head.addOrReplaceChild("left_bit", CubeListBuilder.create().texOffs(29, 5).addBox(2.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition right_bit = head.addOrReplaceChild("right_bit", CubeListBuilder.create().texOffs(29, 5).addBox(-3.0F, -9.0F, -6.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition left_rein = head.addOrReplaceChild("left_rein", CubeListBuilder.create().texOffs(32, 2).addBox(3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.125F, 5.13F));

		PartDefinition right_rein = head.addOrReplaceChild("right_rein", CubeListBuilder.create().texOffs(32, 2).addBox(-3.1F, -6.0F, -8.0F, 0.0F, 3.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.125F, 5.13F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 36).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -1.3333F, 15.3333F));

		PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, 0.0F, -1.9F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offset(4.0F, 13.0F, -9.0F));

		PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, 0.0F, -1.9F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-4.0F, 13.0F, -9.0F));

		PartDefinition left_back_leg = partdefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, 0.0F, -1.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offset(4.0F, 13.0F, 8.0F));

		PartDefinition right_back_leg = partdefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, 0.0F, -1.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-4.0F, 13.0F, 8.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}
}