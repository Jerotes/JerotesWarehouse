package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.animation.BigBeastAnimation;
import com.jerotes.jerotes.client.animation.JerotesHorseAnimation;
import com.jerotes.jerotes.entity.Mob.BigBeastEntity;
import com.jerotes.jerotes.entity.Mob.JerotesHorseEntity;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Modelbig_beast<T extends BigBeastEntity> extends AgeableHierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "big_beast"), "main");
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart neck;
	private final ModelPart right_hind_leg;
	private final ModelPart left_hind_leg;
	private final ModelPart right_front_leg;
	private final ModelPart left_front_leg;


	public Modelbig_beast(ModelPart root) {
		super(0.5f, 24.0f);
		this.root = root;
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.neck = this.body.getChild("neck");
		this.right_hind_leg = root.getChild("right_hind_leg");
		this.left_hind_leg = root.getChild("left_hind_leg");
		this.right_front_leg = root.getChild("right_front_leg");
		this.left_front_leg = root.getChild("left_front_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 120).addBox(-16.5F, -15.5F, -26.5F, 33.0F, 41.0F, 31.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -29.0F, -29.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-19.975F, -21.975F, -28.025F, 40.0F, 56.0F, 64.0F, new CubeDeformation(0.05F)), PartPose.offset(0.025F, -27.975F, -1.025F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(), PartPose.offset(-0.025F, -6.025F, -25.975F));

		PartDefinition right_hind_leg = partdefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(128, 174).addBox(-12.0F, -2.0F, -10.0F, 16.0F, 58.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(-16.0F, -32.0F, 26.0F));

		PartDefinition left_hind_leg = partdefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 192).addBox(-4.0F, -2.0F, -10.0F, 16.0F, 58.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(16.0F, -32.0F, 26.0F));

		PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(204, 174).addBox(-8.0F, -2.0F, -11.0F, 16.0F, 58.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(-20.0F, -32.0F, -19.0F));

		PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(208, 0).addBox(-8.0F, -2.0F, -11.0F, 16.0F, 58.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offset(20.0F, -32.0F, -19.0F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		if (t.isBaby()) {
			this.neck.xScale = 1.15f;
			this.neck.yScale = 1.15f;
			this.neck.zScale = 1.15f;
		}
		else {
			this.neck.xScale = 1f;
			this.neck.yScale = 1f;
			this.neck.zScale = 1f;
		}
		this.applyHeadRotation(t, f4, f5, f3);
		if (t.isAggressive()) {
			this.animateWalk(BigBeastAnimation.RUN, f, f2, 1.5f, 1.5f);
		}
		else {
			this.animateWalk(BigBeastAnimation.WALK, f, f2, 2.0f, 2.0f);
		}
		this.animate(t.attackAnimationState, BigBeastAnimation.ATTACK1, f3);
	}

	private void applyHeadRotation(T t, float f, float f2, float f3) {
		f = Mth.clamp(f, -30.0f, 30.0f);
		f2 = Mth.clamp(f2, -30.0f, 30.0f);
		this.neck.yRot = f * ((float) Math.PI / 180F);
		this.neck.xRot = f2 * ((float) Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}