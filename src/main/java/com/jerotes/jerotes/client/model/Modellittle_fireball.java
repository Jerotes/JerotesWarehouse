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


public class Modellittle_fireball<T extends Entity> extends HierarchicalModel<Entity> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "little_fireball"), "main");
	private final ModelPart root;
	private final ModelPart main;
	private final ModelPart in;
	private final ModelPart out;
	public Modellittle_fireball(ModelPart modelPart) {
		super(RenderType::entityTranslucent);
		this.root = modelPart;
		this.main = root.getChild("main");
		this.in = this.main.getChild("in");
		this.out = this.main.getChild("out");
	}
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create(), PartPose.offset(0.0F, 22.0F, 0.0F));

		PartDefinition in = main.addOrReplaceChild("in", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition out = main.addOrReplaceChild("out", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

		PartDefinition out_r1 = out.addOrReplaceChild("out_r1", CubeListBuilder.create().texOffs(0, 22).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(-0.255F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition out_r2 = out.addOrReplaceChild("out_r2", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 32);
	}
	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	public void setupAnim(float p_103811_, float p_103812_, float p_103813_) {
		this.main.yRot = p_103812_ * ((float)Math.PI / 180F);
		this.main.xRot = p_103813_ * ((float)Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}