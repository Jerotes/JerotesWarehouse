package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.OtherSpell.CloudOfDaggersEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class Modelcloud_of_daggers<T extends CloudOfDaggersEntity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "cloud_of_daggers"), "main");
	private final ModelPart body;
	private final ModelPart out;
	private final ModelPart out_tall;
	private final ModelPart out_short;
	private final ModelPart in;
	private final ModelPart cloud;

	public Modelcloud_of_daggers(ModelPart root) {
		this.body = root.getChild("body");
		this.out = this.body.getChild("out");
		this.out_tall = this.out.getChild("out_tall");
		this.out_short = this.out.getChild("out_short");
		this.in = this.body.getChild("in");
		this.cloud = root.getChild("cloud");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(-80, 0).addBox(-40.0F, -0.1F, -40.0F, 80.0F, 0.0F, 80.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition out = body.addOrReplaceChild("out", CubeListBuilder.create(), PartPose.offset(0.0F, -0.1F, 0.0F));

		PartDefinition out_tall = out.addOrReplaceChild("out_tall", CubeListBuilder.create().texOffs(0, 168).addBox(-39.0F, -16.0F, -39.0F, 22.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(446, 146).addBox(-39.0F, -16.0F, 17.0F, 0.0F, 16.0F, 22.0F, new CubeDeformation(0.0F))
				.texOffs(194, 146).addBox(-39.0F, -16.0F, -39.0F, 0.0F, 16.0F, 22.0F, new CubeDeformation(0.0F))
				.texOffs(360, 168).addBox(-39.0F, -16.0F, 39.0F, 22.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(302, 168).addBox(17.0F, -16.0F, 39.0F, 22.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(230, 146).addBox(39.0F, -16.0F, 17.0F, 0.0F, 16.0F, 22.0F, new CubeDeformation(0.0F))
				.texOffs(158, 146).addBox(39.0F, -16.0F, -39.0F, 0.0F, 16.0F, 22.0F, new CubeDeformation(0.0F))
				.texOffs(86, 168).addBox(17.0F, -16.0F, -39.0F, 22.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition out_short = out.addOrReplaceChild("out_short", CubeListBuilder.create().texOffs(36, 173).addBox(-17.0F, -11.0F, -39.0F, 14.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(468, 159).addBox(-39.0F, -11.0F, 3.0F, 0.0F, 11.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(216, 159).addBox(-39.0F, -11.0F, -17.0F, 0.0F, 11.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(396, 173).addBox(-17.0F, -11.0F, 39.0F, 14.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(302, 173).addBox(3.0F, -11.0F, 39.0F, 14.0F, 11.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(252, 159).addBox(39.0F, -11.0F, 3.0F, 0.0F, 11.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(180, 159).addBox(39.0F, -11.0F, -17.0F, 0.0F, 11.0F, 14.0F, new CubeDeformation(0.0F))
				.texOffs(86, 173).addBox(3.0F, -11.0F, -39.0F, 14.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition in = body.addOrReplaceChild("in", CubeListBuilder.create().texOffs(84, 168).addBox(25.0F, -18.0F, -25.0F, 0.0F, 18.0F, 21.0F, new CubeDeformation(0.0F))
				.texOffs(126, 168).addBox(25.0F, -18.0F, 4.0F, 0.0F, 18.0F, 21.0F, new CubeDeformation(0.0F))
				.texOffs(252, 168).addBox(-25.0F, -18.0F, 4.0F, 0.0F, 18.0F, 21.0F, new CubeDeformation(0.0F))
				.texOffs(294, 168).addBox(-25.0F, -18.0F, -25.0F, 0.0F, 18.0F, 21.0F, new CubeDeformation(0.0F))
				.texOffs(0, 189).addBox(-25.0F, -18.0F, -25.0F, 21.0F, 18.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(42, 189).addBox(4.0F, -18.0F, -25.0F, 21.0F, 18.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(168, 189).addBox(4.0F, -18.0F, 25.0F, 21.0F, 18.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(210, 189).addBox(-25.0F, -18.0F, 25.0F, 21.0F, 18.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.1F, 0.0F));

		PartDefinition cloud = partdefinition.addOrReplaceChild("cloud", CubeListBuilder.create().texOffs(0, 439).addBox(-19.0F, -17.1F, -19.0F, 38.0F, 35.0F, 38.0F, new CubeDeformation(0.0F))
				.texOffs(0, 349).addBox(-30.0F, -30.1F, -30.0F, 60.0F, 30.0F, 60.0F, new CubeDeformation(0.0F))
				.texOffs(0, 250).addBox(-35.0F, -49.1F, -35.0F, 70.0F, 30.0F, 70.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 2.0F, 0.0F));

		PartDefinition body_r1 = cloud.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 250).addBox(-35.0F, -15.0F, -35.0F, 70.0F, 30.0F, 70.0F, new CubeDeformation(-7.0F))
				.texOffs(0, 349).addBox(-30.0F, 4.0F, -30.0F, 60.0F, 30.0F, 60.0F, new CubeDeformation(-6.0F))
				.texOffs(0, 439).addBox(-19.0F, 17.0F, -19.0F, 38.0F, 35.0F, 38.0F, new CubeDeformation(-4.0F)), PartPose.offsetAndRotation(0.0F, -34.1F, 0.0F, 0.0F, 3.1416F, 0.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}


	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	public void setupAnim(float f) {
		if (f < 0)
			return;
		this.cloud.yRot = -f * 0.25f;
	}
	public void scale(T entity, float f, float f2, float f3) {
		this.body.yScale = f * 0.65f * f3;
		this.body.xScale = f3;
		this.body.zScale = f3;
		this.cloud.xScale = f3;
		this.cloud.yScale = Math.max(0, f3 * f);
		this.cloud.zScale = f3;
		if (entity.start < 40)
			return;
		float fs = f2 % 10 - 5;
		if (fs < 0)
			fs *= -1;
		this.in.yScale = 1 + fs/60;
		fs = (f2 + 3) % 10 - 5;
		if (fs < 0)
			fs *= -1;
		this.out.yScale = 1 + fs/80;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		cloud.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}