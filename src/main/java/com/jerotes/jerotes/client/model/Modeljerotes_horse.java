package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.animation.JerotesHorseAnimation;
import com.jerotes.jerotes.entity.Mob.JerotesHorseEntity;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class Modeljerotes_horse<T extends JerotesHorseEntity> extends AgeableHierarchicalModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "jerotes_horse"), "main");
	private final ModelPart root;
	private final ModelPart body;
	private final ModelPart neck;
	private final ModelPart head;


	public Modeljerotes_horse(ModelPart root) {
		super(0.5f, 24.0f);
		this.root = root;
		this.body = root.getChild("body");
		this.neck = this.body.getChild("neck");
		this.head = this.neck.getChild("head");

	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 4.3333F, -4.3333F));

		PartDefinition main_body = body.addOrReplaceChild("main_body", CubeListBuilder.create().texOffs(0, 32).addBox(-5.0F, -5.0F, -11.0F, 10.0F, 10.0F, 22.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0F, 3.6667F, 4.3333F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 35).addBox(-2.05F, -6.0F, -2.0F, 4.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.3333F, -4.6667F));

		PartDefinition mane = neck.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(56, 36).addBox(-1.0F, -11.0F, 5.01F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, -0.01F));

		PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 13).addBox(-3.0F, -5.375F, -0.37F, 6.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.625F, -1.63F));

		PartDefinition mouth = head.addOrReplaceChild("mouth", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, -11.0F, -7.0F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.625F, 1.63F));

		PartDefinition left_ear = head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(19, 16).addBox(0.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.625F, 1.62F));

		PartDefinition right_ear = head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(19, 16).addBox(-2.55F, -13.0F, 4.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.625F, 1.62F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(42, 36).addBox(-1.5F, 0.0F, 0.0F, 3.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.3333F, 15.3333F));

		PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, 0.0F, -1.9F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 13.0F, -9.0F));

		PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, 0.0F, -1.9F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 13.0F, -9.0F));

		PartDefinition left_back_leg = partdefinition.addOrReplaceChild("left_back_leg", CubeListBuilder.create().texOffs(48, 21).mirror().addBox(-3.0F, 0.0F, -1.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(4.0F, 13.0F, 8.0F));

		PartDefinition right_back_leg = partdefinition.addOrReplaceChild("right_back_leg", CubeListBuilder.create().texOffs(48, 21).addBox(-1.0F, 0.0F, -1.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 13.0F, 8.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
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
		if (t.isSaddled() && t.isVehicle() || t.getPanicTick() > 0) {
			this.animateWalk(JerotesHorseAnimation.RUN, f, f2, 2.0f, 2.0f);
		}
		else {
			float speed = Math.max(0, t.getPanicTick() - 15)/5f;
			this.animateWalk(JerotesHorseAnimation.RUN, f, f2 * speed, 2.0f, 2.0f);
			this.animateWalk(JerotesHorseAnimation.WALK, f, f2 * (1-speed), 2.0f, 2.0f);
		}
		this.animate(t.idleAnimationState, JerotesHorseAnimation.IDLE, f3);
		this.animate(t.angry1AnimationState, JerotesHorseAnimation.ANGRY1, f3);
		this.animate(t.angry2AnimationState, JerotesHorseAnimation.ANGRY2, f3);
		this.animate(t.jumpAnimationState, JerotesHorseAnimation.JUMP, f3);
		this.animate(t.longJumpAnimationState, JerotesHorseAnimation.LONGJUMP, f3);
		this.animate(t.sitAnimationState, JerotesHorseAnimation.SIT, f3);
		this.animate(t.toSitAnimationState, JerotesHorseAnimation.TOSIT, f3);
		this.animate(t.stopSitAnimationState, JerotesHorseAnimation.STOPSIT, f3);
		this.animate(t.eatAnimationState, JerotesHorseAnimation.EAT, f3);
		this.animate(t.eatGrassAnimationState, JerotesHorseAnimation.EATGRASS, f3);
	}

	private void applyHeadRotation(T t, float f, float f2, float f3) {
		f = Mth.clamp(f, -10.0f, 10.0f);
		f2 = Mth.clamp(f2, -20.0f, 20.0f);
		this.neck.yRot = f * ((float) Math.PI / 180F);
		this.neck.xRot = f2 * ((float) Math.PI / 180F);
	}

	@Override
	public ModelPart root() {
		return this.root;
	}
}