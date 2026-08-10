package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Other.OtherSpell.CloudOfDaggersEntity;
import com.jerotes.jerotes.entity.Other.OtherSpell.MagicBonfireEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class Modelmagic_bonfire<T extends MagicBonfireEntity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "magic_bonfire"), "main");
	private final ModelPart main;
	private final ModelPart fire;
	private final ModelPart body;
	private final ModelPart bottom;

	public Modelmagic_bonfire(ModelPart root) {
		this.main = root.getChild("main");
		this.fire = this.main.getChild("fire");
		this.body = this.main.getChild("body");
		this.bottom = this.main.getChild("bottom");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.05F, 0.0F));

		PartDefinition fire = main.addOrReplaceChild("fire", CubeListBuilder.create(), PartPose.offset(0.0F, -1.05F, 0.0F));

		PartDefinition fire_r1 = fire.addOrReplaceChild("fire_r1", CubeListBuilder.create().texOffs(0, -16).addBox(0.0F, -8.0F, -8.0F, 0.0F, 16.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 0).addBox(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

		PartDefinition body = main.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, 1.9F, -8.0F, 6.0F, 1.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 33).addBox(-7.0F, -1.1F, -8.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
				.texOffs(0, 73).addBox(-8.0F, -4.1F, -7.0F, 16.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 81).addBox(-8.0F, -4.1F, 3.0F, 16.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 53).addBox(3.0F, -1.1F, -8.0F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.95F, 0.0F));

		PartDefinition bottom = main.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 80).addBox(-24.0F, 0.0F, -24.0F, 48.0F, 0.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.15F, 0.0F));

		return LayerDefinition.create(meshdefinition, 144, 128);
	}

	@Override
	public void setupAnim(MagicBonfireEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}
	public void setupAnim(float f) {
	}
	public void scale(T entity, float f, float ff, float fff, float ffff, float f2, float f3) {
		this.main.yScale = f * f3;
		this.main.xScale = f * f3;
		this.main.zScale = f * f3;

		this.fire.yScale = f3 * ff * fff * ffff;
		float fs = f2 % 10 - 5;
		if (fs < 0)
			fs *= -1;
		this.fire.xScale = 1.3f + fs/60f;
		fs = (f2 + 3) % 10 - 5;
		if (fs < 0)
			fs *= -1;
		this.fire.zScale = 1.3f + fs/60f;
	}


	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}