package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesIronGoelmEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesSnowGoelmEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Modeljerotes_snow_golem<T extends JerotesSnowGoelmEntity> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "jerotes_snow_golem"), "main");
	private static final String UPPER_BODY = "upper_body";
	private final ModelPart root;
	private final ModelPart upperBody;
	private final ModelPart head;
	private final ModelPart leftArm;
	private final ModelPart rightArm;

	public Modeljerotes_snow_golem(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.leftArm = root.getChild("left_arm");
		this.rightArm = root.getChild("right_arm");
		this.upperBody = root.getChild("upper_body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		float f = 4.0F;
		CubeDeformation cubedeformation = new CubeDeformation(-0.5F);
		partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubedeformation), PartPose.offset(0.0F, 4.0F, 0.0F));
		CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, cubedeformation);
		partdefinition.addOrReplaceChild("left_arm", cubelistbuilder, PartPose.offsetAndRotation(5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 1.0F));
		partdefinition.addOrReplaceChild("right_arm", cubelistbuilder, PartPose.offsetAndRotation(-5.0F, 6.0F, -1.0F, 0.0F, (float)Math.PI, -1.0F));
		partdefinition.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubedeformation), PartPose.offset(0.0F, 13.0F, 0.0F));
		partdefinition.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(0, 36).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, cubedeformation), PartPose.offset(0.0F, 24.0F, 0.0F));
		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	public void setupAnim(T p_103845_, float p_103846_, float p_103847_, float p_103848_, float p_103849_, float p_103850_) {
		this.head.yRot = p_103849_ * ((float)Math.PI / 180F);
		this.head.xRot = p_103850_ * ((float)Math.PI / 180F);
		this.upperBody.yRot = p_103849_ * ((float)Math.PI / 180F) * 0.25F;
		float f = Mth.sin(this.upperBody.yRot);
		float f1 = Mth.cos(this.upperBody.yRot);
		this.leftArm.yRot = this.upperBody.yRot;
		this.rightArm.yRot = this.upperBody.yRot + (float)Math.PI;
		this.leftArm.x = f1 * 5.0F;
		this.leftArm.z = -f * 5.0F;
		this.rightArm.x = -f1 * 5.0F;
		this.rightArm.z = f * 5.0F;
	}

	public ModelPart root() {
		return this.root;
	}

	public ModelPart getHead() {
		return this.head;
	}
}