package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.animation.InsectPlagueAnimation;
import com.jerotes.jerotes.entity.Other.OtherSpell.CloudOfDaggersEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.InsectPlagueEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class Modelinsect_plague<T extends InsectPlagueEntity> extends HierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "insect_plague"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart bottom;
	private final ModelPart out;
	private final ModelPart ball_out;
	private final ModelPart ball_in;
	private final ModelPart ball_other;

	public Modelinsect_plague(ModelPart root) {
		this.root = root;
		this.body = root.getChild("body");
		this.bottom = this.body.getChild("bottom");
		this.out = this.body.getChild("out");
		this.ball_out = this.out.getChild("ball_out");
		this.ball_in = this.out.getChild("ball_in");
		this.ball_other = this.out.getChild("ball_other");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition bottom = body.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(-192, 0).addBox(-96.0F, 0.0F, -96.0F, 192.0F, 0.0F, 192.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.1F, 0.0F));

		PartDefinition out = body.addOrReplaceChild("out", CubeListBuilder.create(), PartPose.offset(0.0F, -0.1F, 0.0F));

		PartDefinition ball_out = out.addOrReplaceChild("ball_out", CubeListBuilder.create().texOffs(0, 192).addBox(-16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F, new CubeDeformation(0.0F))
				.texOffs(128, 192).addBox(-16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F, new CubeDeformation(-34.0F)), PartPose.offset(0.0F, -95.9F, 0.0F));

		PartDefinition ball_in = out.addOrReplaceChild("ball_in", CubeListBuilder.create().texOffs(0, 256).addBox(-16.0F, -16.0F, -16.0F, 32.0F, 32.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -95.9F, 0.0F));

		PartDefinition ball_other = out.addOrReplaceChild("ball_other", CubeListBuilder.create().texOffs(-48, 320).addBox(-24.0F, 0.0F, -24.0F, 48.0F, 0.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -95.9F, 0.0F));

		return LayerDefinition.create(meshdefinition, 384, 384);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	public void scale(T entity, float f, float f2) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		KeyframeAnimations.animate(this, InsectPlagueAnimation.START, 1, 1.0F, new Vector3f());
		this.bottom.yScale = f * 0.65f * f2;
		this.bottom.xScale = f2;
		this.bottom.zScale = f2;
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}