package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.renderer.JerotesPlayerRenderer;
import com.jerotes.jerotes.entity.Mob.HumanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public class Modelhumanoid_wide_or_slim_for_human<T extends HumanEntity> extends PlayerModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "humanoid_wide_or_slim_for_human"), "main");
	private boolean slim;

	public final ModelPart left_arm;
	public final ModelPart left_arm_slim;
	public final ModelPart left_arm_wide;
	public final ModelPart right_arm;
	public final ModelPart right_arm_slim;
	public final ModelPart right_arm_wide;
	public final ModelPart left_sleeve;
	public final ModelPart left_sleeve_slim;
	public final ModelPart left_sleeve_wide;
	public final ModelPart right_sleeve;
	public final ModelPart right_sleeve_slim;
	public final ModelPart right_sleeve_wide;

	public Modelhumanoid_wide_or_slim_for_human(ModelPart root) {
		super(root, false);
		this.left_arm = root.getChild("left_arm");
		this.left_arm_slim = left_arm.getChild("left_arm_slim");
		this.left_arm_wide = left_arm.getChild("left_arm_wide");
		this.right_arm = root.getChild("right_arm");
		this.right_arm_slim = right_arm.getChild("right_arm_slim");
		this.right_arm_wide = right_arm.getChild("right_arm_wide");
		this.left_sleeve = root.getChild("left_sleeve");
		this.left_sleeve_slim = left_sleeve.getChild("left_sleeve_slim");
		this.left_sleeve_wide = left_sleeve.getChild("left_sleeve_wide");
		this.right_sleeve = root.getChild("right_sleeve");
		this.right_sleeve_slim = right_sleeve.getChild("right_sleeve_slim");
		this.right_sleeve_wide = right_sleeve.getChild("right_sleeve_wide");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("ear", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("cloak", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition jacket = partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition left_arm_slim = left_arm.addOrReplaceChild("left_arm_slim", CubeListBuilder.create().texOffs(32, 48).addBox(4.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 22.0F, 0.0F));

		PartDefinition left_arm_wide = left_arm.addOrReplaceChild("left_arm_wide", CubeListBuilder.create().texOffs(32, 48).addBox(4.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 22.0F, 0.0F));

		PartDefinition left_sleeve = partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition left_sleeve_slim = left_sleeve.addOrReplaceChild("left_sleeve_slim", CubeListBuilder.create().texOffs(48, 48).addBox(4.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 22.0F, 0.0F));

		PartDefinition left_sleeve_wide = left_sleeve.addOrReplaceChild("left_sleeve_wide", CubeListBuilder.create().texOffs(48, 48).addBox(4.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 22.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition right_arm_slim = right_arm.addOrReplaceChild("right_arm_slim", CubeListBuilder.create().texOffs(40, 16).addBox(-7.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 22.0F, 0.0F));

		PartDefinition right_arm_wide = right_arm.addOrReplaceChild("right_arm_wide", CubeListBuilder.create().texOffs(40, 16).addBox(-8.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 22.0F, 0.0F));

		PartDefinition right_sleeve = partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition right_sleeve_slim = right_sleeve.addOrReplaceChild("right_sleeve_slim", CubeListBuilder.create().texOffs(40, 32).addBox(-7.0F, -24.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 22.0F, 0.0F));

		PartDefinition right_sleeve_wide = right_sleeve.addOrReplaceChild("right_sleeve_wide", CubeListBuilder.create().texOffs(40, 32).addBox(-8.0F, -24.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 22.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition left_pants = partdefinition.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition right_pants = partdefinition.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T humanEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netbipedHeadYaw, float bipedHeadPitch) {
		slim = humanEntity.IsFemale();
		JerotesPlayerRenderer.setModelProperties(this, humanEntity);
		super.setupAnim(humanEntity, limbSwing, limbSwingAmount, ageInTicks, netbipedHeadYaw, bipedHeadPitch);
		left_arm_slim.visible = slim;
		right_arm_slim.visible = slim;
		left_sleeve_slim.visible = slim;
		right_sleeve_slim.visible = slim;

		left_arm_wide.visible = !slim;
		right_arm_wide.visible = !slim;
		left_sleeve_wide.visible = !slim;
		right_sleeve_wide.visible = !slim;

	}


	@Override
	public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
		ModelPart modelPart = this.getArm(humanoidArm);
		if (this.slim) {
			float f = 0.5f * (float)(humanoidArm == HumanoidArm.RIGHT ? 1 : -1);
			modelPart.x += f;
			modelPart.translateAndRotate(poseStack);
			modelPart.x -= f;
		} else {
			modelPart.translateAndRotate(poseStack);
		}
	}
}