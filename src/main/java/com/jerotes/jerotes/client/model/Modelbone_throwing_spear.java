package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class Modelbone_throwing_spear<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "bone_throwing_spear"), "main");
	public final ModelPart body;

	public Modelbone_throwing_spear(ModelPart root) {
		this.body = root.getChild("body");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -18.0F, -1.0F, 2.0F, 38.0F, 2.0F, new CubeDeformation(-0.25F))
				.texOffs(22, 0).addBox(-1.0F, 19.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F))
				.texOffs(0, 40).addBox(-1.0F, -11.0F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(0, 40).addBox(-1.0F, 0.3F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F))
				.texOffs(0, 40).addBox(-1.0F, 10.7F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F))
				.texOffs(8, 26).addBox(-1.0F, -17.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.1F))
				.texOffs(8, 31).addBox(-1.0F, -17.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(8, 2).addBox(-1.0F, -18.7F, -0.5F, 2.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(8, 10).addBox(-3.0F, -16.7F, -0.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.005F))
				.texOffs(14, 7).addBox(-0.5F, -21.7F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.01F))
				.texOffs(8, 12).addBox(-2.1F, -19.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(-0.11F))
				.texOffs(12, 12).addBox(1.1F, -19.0F, -0.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(-0.11F)), PartPose.offset(0.0F, 16.0F, 0.0F));

		PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(8, 19).addBox(-1.0F, -2.5F, -1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0025F)), PartPose.offsetAndRotation(1.65F, -19.9F, 0.5F, 0.0F, 0.0F, -0.3927F));

		PartDefinition body_r2 = body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(18, 0).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0025F)), PartPose.offsetAndRotation(1.4F, -13.126F, 0.5F, 0.0F, 0.0F, 0.3927F));

		PartDefinition body_r3 = body.addOrReplaceChild("body_r3", CubeListBuilder.create().texOffs(16, 12).addBox(-0.5F, -3.0F, -1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0025F)), PartPose.offsetAndRotation(-1.4F, -13.126F, 0.5F, 0.0F, 0.0F, -0.3927F));

		PartDefinition body_r4 = body.addOrReplaceChild("body_r4", CubeListBuilder.create().texOffs(14, 0).addBox(0.0F, -2.5F, -1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0025F)), PartPose.offsetAndRotation(-1.65F, -19.9F, 0.5F, 0.0F, 0.0F, 0.3927F));

		PartDefinition body_r5 = body.addOrReplaceChild("body_r5", CubeListBuilder.create().texOffs(18, 7).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, -22.2F, 0.5F, 0.0F, 0.0F, -0.7854F));

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(16, 19).addBox(-9.0F, -7.0F, 7.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(8.0F, 24.0F, -8.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}


	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
}
