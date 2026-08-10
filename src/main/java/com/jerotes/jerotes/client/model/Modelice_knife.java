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

public class Modelice_knife<T extends Entity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "ice_knife"), "main");
	private final ModelPart sword;
	private final ModelPart blade;
	private final ModelPart rod;

	public Modelice_knife(ModelPart root) {
		this.sword = root.getChild("sword");
		this.blade = this.sword.getChild("blade");
		this.rod = this.sword.getChild("rod");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition sword = partdefinition.addOrReplaceChild("sword", CubeListBuilder.create(), PartPose.offset(-0.75F, 19.5F, 0.5F));

		PartDefinition blade = sword.addOrReplaceChild("blade", CubeListBuilder.create().texOffs(10, 11).addBox(0.0F, -10.0F, -1.5F, 1.0F, 18.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(0, -5).addBox(0.5F, -9.0F, -2.5F, 0.0F, 17.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.25F, -3.5F, 0.0F));

		PartDefinition sword_r1 = blade.addOrReplaceChild("sword_r1", CubeListBuilder.create().texOffs(18, 21).addBox(0.59F, -1.5F, -3.5F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
				.texOffs(18, 11).addBox(0.61F, -1.5F, -3.5F, 0.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, -11.25F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition sword_r2 = blade.addOrReplaceChild("sword_r2", CubeListBuilder.create().texOffs(28, 11).addBox(-0.5F, -1.5F, -1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.5F, -10.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition rod = sword.addOrReplaceChild("rod", CubeListBuilder.create().texOffs(10, 0).addBox(-1.0F, 1.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 17).addBox(-0.5F, 3.5F, -1.5F, 1.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
				.texOffs(30, 0).addBox(-0.5F, 9.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offset(0.25F, 3.5F, 0.0F));

		PartDefinition sword_r3 = rod.addOrReplaceChild("sword_r3", CubeListBuilder.create().texOffs(30, 4).addBox(-0.505F, -1.505F, -0.495F, 1.01F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

		PartDefinition sword_r4 = rod.addOrReplaceChild("sword_r4", CubeListBuilder.create().texOffs(28, 17).addBox(-0.5F, -2.5F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 1.5F, -4.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition sword_r5 = rod.addOrReplaceChild("sword_r5", CubeListBuilder.create().texOffs(28, 17).addBox(-0.5F, -2.5F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 1.5F, 4.0F, -2.9671F, 0.0F, 3.1416F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		sword.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}