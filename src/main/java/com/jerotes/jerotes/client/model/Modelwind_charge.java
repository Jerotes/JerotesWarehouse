package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;


public class Modelwind_charge<T extends Entity> extends HierarchicalModel<Entity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "wind_charge"), "main");
	private final ModelPart bone;

	public Modelwind_charge(ModelPart modelPart) {
		super(RenderType::entityTranslucent);
		this.bone = modelPart.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();
		PartDefinition partDefinition2 = partDefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition partDefinition3 = partDefinition2.addOrReplaceChild("projectile", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f));
		PartDefinition partDefinition4 = partDefinition3.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(20, 112).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)).texOffs(0, 8).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.0f, 0.0f));
		partDefinition4.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(32, 24).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(-0.6f)), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, -1.5708f, 0.0f, 1.5708f));
		partDefinition4.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(16, 40).addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, new CubeDeformation(-0.3f)), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.5708f));
		partDefinition3.addOrReplaceChild("wind_charge", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, -2.0f, -2.0f, 4.0f, 4.0f, 4.0f, new CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.0f, 0.0f));
		return LayerDefinition.create(meshDefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity windCharge, float f, float f2, float f3, float f4, float f5) {
	}

	@Override
	public ModelPart root() {
		return this.bone;
	}
}