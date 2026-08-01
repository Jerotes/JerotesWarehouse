package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.OtherSpell.CloudOfDaggersEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.HoldMobEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.OtherSpellBaseEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class Modelhold_mob<T extends OtherSpellBaseEntity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "hold_mob"), "main");
	private final ModelPart body;
	private final ModelPart out;
	private final ModelPart in;

	public Modelhold_mob(ModelPart root) {
		this.body = root.getChild("body");
		this.out = this.body.getChild("out");
		this.in = this.body.getChild("in");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(-48, 0).addBox(-24.0F, -0.1F, -24.0F, 48.0F, 0.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition out = body.addOrReplaceChild("out", CubeListBuilder.create().texOffs(84, 113).addBox(-24.0F, -15.0F, 24.0F, 13.0F, 15.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(102, 100).addBox(24.0F, -15.0F, -24.0F, 0.0F, 15.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(0, 100).addBox(24.0F, -15.0F, 11.0F, 0.0F, 15.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(78, 100).addBox(-24.0F, -15.0F, 11.0F, 0.0F, 15.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(48, 100).addBox(-24.0F, -15.0F, -24.0F, 0.0F, 15.0F, 13.0F, new CubeDeformation(0.0F))
				.texOffs(52, 113).addBox(-24.0F, -15.0F, -24.0F, 13.0F, 15.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(26, 113).addBox(11.0F, -15.0F, -24.0F, 13.0F, 15.0F, 0.0F, new CubeDeformation(0.0F))
				.texOffs(26, 113).addBox(11.0F, -15.0F, 24.0F, 13.0F, 15.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.1F, 0.0F));

		PartDefinition in = body.addOrReplaceChild("in", CubeListBuilder.create().texOffs(0, 48).addBox(-16.0F, -10.5F, -16.0F, 32.0F, 21.0F, 32.0F, new CubeDeformation(-3.0F)), PartPose.offset(0.0F, -7.6F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	public void setupAnim(float f) {
		if (f < 0)
			return;
		this.in.yRot = -f * 0.0775f;
	}
	public void scale(T entity, float f, float f2, float f3) {
		this.body.yScale = f * f3;
		this.body.xScale = f * f3;
		this.body.zScale = f * f3;
		this.in.yScale = f;
		float fs = f2 % 10 - 5;
		if (fs < 0)
			fs *= -1;
		this.in.yScale = 1 + fs/100;
		fs = (f2 + 3) % 10 - 5;
		if (fs < 0)
			fs *= -1;
		this.out.yScale =  0.15f + fs/80;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}