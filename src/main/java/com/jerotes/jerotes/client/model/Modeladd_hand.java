package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.SkinEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class Modeladd_hand<T extends LivingEntity> extends Modelhumanoid<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "add_hand"), "main");
	private boolean slim;

	private final ModelPart left_arm;
	private final ModelPart left_arm_wide;
	private final ModelPart right_arm;
	private final ModelPart right_arm_wide;
	private final ModelPart left_sleeve;
	private final ModelPart left_sleeve_wide;
	private final ModelPart right_sleeve;
	private final ModelPart right_sleeve_wide;

	public Modeladd_hand(ModelPart root) {
		super(root);
		this.left_arm = root.getChild("left_arm");
		this.left_arm_wide = left_arm.getChild("left_arm_wide");
		this.right_arm = root.getChild("right_arm");
		this.right_arm_wide = right_arm.getChild("right_arm_wide");
		this.left_sleeve = root.getChild("left_sleeve");
		this.left_sleeve_wide = left_sleeve.getChild("left_sleeve_wide");
		this.right_sleeve = root.getChild("right_sleeve");
		this.right_sleeve_wide = right_sleeve.getChild("right_sleeve_wide");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("ear", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("cloak", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("left_pants", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("right_pants", CubeListBuilder.create(), PartPose.ZERO);

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

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T t, float limbSwing, float limbSwingAmount, float ageInTicks, float netbipedHeadYaw, float bipedHeadPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		boolean bl = t instanceof SkinEntity skinEntity && skinEntity.IsFemale();
		slim = bl;
		super.setupAnim(t, limbSwing, limbSwingAmount, ageInTicks, netbipedHeadYaw, bipedHeadPitch);
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