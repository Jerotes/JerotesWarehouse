package com.jerotes.jerotes.client.model;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.animation.SpearAnimations;
import com.jerotes.jerotes.compat.tacz.TaczAnimate;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class Modelspecial_action<T extends LivingEntity> extends PlayerModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(JerotesWarehouse.MODID, "special_action"), "main");
	private final ModelPart root;
	public final ModelPart head;
	public final ModelPart hat;
	public final ModelPart body;
	public final ModelPart jacket;
	public final ModelPart left_arm;
	public final ModelPart left_sleeve;
	public final ModelPart right_arm;
	public final ModelPart right_sleeve;
	public final ModelPart left_leg;
	public final ModelPart left_pants;
	public final ModelPart right_leg;
	public final ModelPart right_pants;

	public Modelspecial_action(ModelPart root) {
		super(root, false);
		this.root = root;
		this.head = root.getChild("head");
		this.hat = root.getChild("hat");
		this.body = root.getChild("body");
		this.jacket = root.getChild("jacket");
		this.left_arm = root.getChild("left_arm");
		this.left_sleeve = root.getChild("left_sleeve");
		this.right_arm = root.getChild("right_arm");
		this.right_sleeve = root.getChild("right_sleeve");
		this.left_leg = root.getChild("left_leg");
		this.left_pants = root.getChild("left_pants");
		this.right_leg = root.getChild("right_leg");
		this.right_pants = root.getChild("right_pants");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		partdefinition.addOrReplaceChild("ear", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("cloak", CubeListBuilder.create(), PartPose.ZERO);

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition jacket = partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition left_sleeve = partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition right_sleeve = partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition left_pants = partdefinition.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		PartDefinition right_pants = partdefinition.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T t, float f, float f2, float f3, float f4, float f5) {
		super.setupAnim(t, f, f2, f3, f4, f5);
	}

	public ModelPart root() {
		return this.root;
	}

	private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

	protected void animate(AnimationState state, AnimationDefinition definition, float tickCount) {
		this.animate(state, definition, tickCount, 1.0F);
	}

	protected void animate(AnimationState state, AnimationDefinition definition, float tickCount, float speed) {
		state.updateTime(tickCount, speed);
		state.ifStarted((animState) -> Modelspecial_action.animate(this, definition, animState.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE));
	}

	protected void animateWalk(AnimationDefinition definition, float swing, float swingAmount, float speed, float scale) {
		long accumulatedTime = (long)(swing * 50.0F * speed);
		float interpolationScale = Math.min(swingAmount * scale, 1.0F);
		Modelspecial_action.animate(this, definition, accumulatedTime, interpolationScale, ANIMATION_VECTOR_CACHE);
	}

	public Optional<ModelPart> getAnyDescendantWithName(String name) {
		return name.equals("root") ? Optional.of(root()) : root().getAllParts().filter((part) -> part.hasChild(name)).findFirst().map((part) -> part.getChild(name));
	}
	public static void animate(Modelspecial_action model, AnimationDefinition definition, long accumulatedTime, float scale, Vector3f cache) {
		float f = getElapsedSeconds(definition, accumulatedTime);

		for (Map.Entry<String, List<AnimationChannel>> entry : definition.boneAnimations().entrySet()) {
			Optional<ModelPart> optional = model.getAnyDescendantWithName(entry.getKey());
			List<AnimationChannel> list = entry.getValue();
			optional.ifPresent((modelPart) -> {
				list.forEach((animationChannel) -> {
					Keyframe[] akeyframe = animationChannel.keyframes();
					int i = Math.max(0, Mth.binarySearch(0, akeyframe.length, (i1) -> f <= akeyframe[i1].timestamp()) - 1);
					int j = Math.min(akeyframe.length - 1, i + 1);
					Keyframe keyframe = akeyframe[i];
					Keyframe keyframe1 = akeyframe[j];
					float f1 = f - keyframe.timestamp();
					float f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
					keyframe1.interpolation().apply(cache, f2, akeyframe, i, j, scale);
					animationChannel.target().apply(modelPart, cache);
				});
			});
		}
	}

	private static float getElapsedSeconds(AnimationDefinition definition, long accumulatedTime) {
		float f = (float)accumulatedTime / 1000.0F;
		return definition.looping() ? f % definition.lengthInSeconds() : f;
	}


	public static void specialAnim(LivingEntity living, Modelspecial_action modelspecialAction, float limbSwing, float limbSwingAmount, float ageInTicks, float netbipedHeadYaw, float bipedHeadPitch) {
		specialAnim(living, modelspecialAction, modelspecialAction.head, modelspecialAction.right_arm, modelspecialAction.left_arm, modelspecialAction.body, limbSwing, limbSwingAmount, ageInTicks, netbipedHeadYaw, bipedHeadPitch);
	}

	public boolean shouldTaczIdle() {
		return true;
	}

	public static void specialAnim(LivingEntity living, Modelspecial_action model, ModelPart head, ModelPart rightArm, ModelPart leftArm, ModelPart body, float limbSwing, float limbSwingAmount, float ageInTicks, float netbipedHeadYaw, float bipedHeadPitch) {
		ItemStack itemStack = living.getMainHandItem();
		ItemStack itemStacks = living.getOffhandItem();
		boolean bl3 = living.getFallFlyingTicks() > 4;
		boolean bl4 = living.isVisuallySwimming();
		ModelPart mainHand;
		ModelPart offHand;
		if (!(living instanceof Mob mob && mob.isLeftHanded())) {
			mainHand = rightArm;
			offHand = leftArm;
		} else {
			mainHand = leftArm;
			offHand = rightArm;
		}

		//tacz
		if (living.getMainHandItem().getItem().getDescriptionId().contains("modern_kinetic_gun") && model.shouldTaczIdle()) {
			if (mainHand == rightArm) {
				rightArm.yRot = -0.1f + head.yRot;
				leftArm.yRot = 0.1f + head.yRot + 0.4f;
			} else {
				rightArm.yRot = -0.1f + head.yRot - 0.4f;
				leftArm.yRot = 0.1f + head.yRot;
			}
			rightArm.xRot = -1.5707964f + head.xRot;
			leftArm.xRot = -1.5707964f + head.xRot;
			TaczAnimate.setRotationAnglesHead(living, rightArm, leftArm, body, head, limbSwingAmount);
		}
		//矛
		{
			if (Modelspecial_action.poseRightArm(model, living)) {
				SpearAnimations.thirdPersonHandUse(rightArm, head, true, living.getUseItem(), model, living);
			}
			if (Modelspecial_action.poseLeftArm(model, living)) {
				SpearAnimations.thirdPersonHandUse(leftArm, head, false, living.getUseItem(), model, living);
			}

			if (living.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear()) {
				if (!(model.attackTime <= 0.0F)) {
					HumanoidArm humanoidarm = getAttackArm(living);
					ModelPart modelpart = model.getArm(humanoidarm);
					float f = model.attackTime;
					model.body.yRot = Mth.sin(Mth.sqrt(f) * 6.2831855F) * 0.2F;
					ModelPart modelPart;
					if (humanoidarm == HumanoidArm.LEFT) {
						modelPart = model.body;
						modelPart.yRot *= -1.0F;
					}

					rightArm.z = Mth.sin(body.yRot) * 5.0F;
					rightArm.x = -Mth.cos(body.yRot) * 5.0F;
					leftArm.z = -Mth.sin(body.yRot) * 5.0F;
					leftArm.x = Mth.cos(body.yRot) * 5.0F;
					modelPart = rightArm;
					modelPart.yRot += body.yRot;
					modelPart = leftArm;
					modelPart.yRot += body.yRot;
					modelPart = leftArm;
					modelPart.xRot += body.yRot;
					SpearAnimations.thirdPersonAttackHand(model, model, living);
				}
			}

		}
		//长枪
		{
			if (living.getMainHandItem().getItem() instanceof ItemToolBasePike) {
				if (living.getMainArm() == HumanoidArm.RIGHT) {
					//副手
					ItemToolBasePike.animate(rightArm, leftArm, living, false);
					//主手
					SpearAnimations.thirdPersonHandUse(rightArm, head, true, living.getUseItem(), model, living);
					if (living.isUsingItem() && living.getUsedItemHand() == InteractionHand.MAIN_HAND) {
						float f = 1 - Math.min(25, living.getTicksUsingItem()) / 25f;
						if (f > 0 && f <= 0.40) {
							float fs = f / 0.40f;
							rightArm.yRot += fs * 0.35f;
						} else if (f > 0.40 && f <= 0.5) {
							float fs = (f - 0.40f) / 0.10f;
							rightArm.yRot += 0.35f + fs * -0.55f;
						} else if (f > 0.5) {
							float fs = 1 - (f - 0.5f) / 0.5f;
							rightArm.yRot += fs * -0.2f;
						}
					}
				} else {
					//副手
					ItemToolBasePike.animate(rightArm, leftArm, living, true);
					//主手
					SpearAnimations.thirdPersonHandUse(leftArm, head, false, living.getUseItem(), model, living);
					if (living.isUsingItem() && living.getUsedItemHand() == InteractionHand.MAIN_HAND) {
						float f = 1 - Math.min(25, living.getTicksUsingItem()) / 25f;
						if (f > 0 && f <= 0.40) {
							float fs = f / 0.40f;
							leftArm.yRot -= fs * 0.35f;
						} else if (f > 0.40 && f <= 0.5) {
							float fs = (f - 0.40f) / 0.10f;
							leftArm.yRot -= (0.35f + fs * -0.55f);
						} else if (f > 0.5) {
							float fs = 1 - (f - 0.5f) / 0.5f;
							leftArm.yRot -= fs * -0.2f;
						}
					}
				}
				if (living.getMainHandItem().getItem() instanceof ItemToolBasePike) {
					if (!(model.attackTime <= 0.0F)) {
						HumanoidArm humanoidarm = getAttackArm(living);
						ModelPart modelpart = model.getArm(humanoidarm);
						float f = model.attackTime;
						model.body.yRot = Mth.sin(Mth.sqrt(f) * 6.2831855F) * 0.2F;
						ModelPart modelPart;
						if (humanoidarm == HumanoidArm.LEFT) {
							modelPart = model.body;
							modelPart.yRot *= -1.0F;
						}

						rightArm.z = Mth.sin(body.yRot) * 5.0F;
						rightArm.x = -Mth.cos(body.yRot) * 5.0F;
						leftArm.z = -Mth.sin(body.yRot) * 5.0F;
						leftArm.x = Mth.cos(body.yRot) * 5.0F;
						modelPart = rightArm;
						modelPart.yRot += body.yRot;
						modelPart = leftArm;
						modelPart.yRot += body.yRot;
						modelPart = leftArm;
						modelPart.xRot += body.yRot;
						ItemToolBasePike.animateAttack(model, model, living);
					}
				}
			}
		}
	}

	public static HumanoidArm getAttackArm(LivingEntity p_102857_) {
		HumanoidArm humanoidarm = p_102857_.getMainArm();
		return p_102857_.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
	}

	public static boolean poseRightArm(HumanoidModel<?> humanoidModel, LivingEntity entity) {
		boolean spear = (humanoidModel.rightArmPose == ItemToolBaseSpearBase.SpearClient.SPEAR_POSE);
		if (!(entity instanceof Player)) {
			//使用 作为副手优先持有 仅此手持有
			//右主手
			if (entity.getMainArm() == HumanoidArm.RIGHT) {
				//右手使用
				if (entity.getUseItem().getItem() instanceof ItemToolBaseSpearBase &&
						entity.getUsedItemHand() == InteractionHand.MAIN_HAND) {
					spear = true;
				}
				//左手作为副手优先持有 不可以
				else if (entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase && entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) {
					spear = false;
				}
				//右手作为主手仅此手持有
				else if (!(entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) && entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase) {
					spear = true;
				}
			}
			//左主手
			else {
				//右手使用
				if (entity.getUseItem().getItem() instanceof ItemToolBaseSpearBase &&
						entity.getUsedItemHand() != InteractionHand.MAIN_HAND) {
					spear = true;
				}
				//右手作为副手优先持有
				else if (entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase && entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) {
					spear = true;
				}
				//右手作为副手仅此手持有
				else if (!(entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase) && entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) {
					spear = true;
				}
			}
		}
		return spear;
	}

	public static boolean poseLeftArm(HumanoidModel<?> humanoidModel, LivingEntity entity) {
		boolean spear = (humanoidModel.leftArmPose == ItemToolBaseSpearBase.SpearClient.SPEAR_POSE);
		if (!(entity instanceof Player)) {
			//使用 作为副手优先持有 仅此手持有
			//左主手
			if (entity.getMainArm() == HumanoidArm.LEFT) {
				//左手使用
				if (entity.getUseItem().getItem() instanceof ItemToolBaseSpearBase &&
						entity.getUsedItemHand() == InteractionHand.MAIN_HAND) {
					spear = true;
				}
				//右手作为副手优先持有 不可以
				else if (entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase && entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) {
					spear = false;
				}
				//左手作为主手仅此手持有
				else if (!(entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) && entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase) {
					spear = true;
				}
			}
			//右主手
			else {
				//左手使用
				if (entity.getUseItem().getItem() instanceof ItemToolBaseSpearBase &&
						entity.getUsedItemHand() != InteractionHand.MAIN_HAND) {
					spear = true;
				}
				//左手作为副手优先持有
				else if (entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase && entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) {
					spear = true;
				}
				//左手作为副手仅此手持有
				else if (!(entity.getMainHandItem().getItem() instanceof ItemToolBaseSpearBase) && entity.getOffhandItem().getItem() instanceof ItemToolBaseSpearBase) {
					spear = true;
				}
			}
		}
		return spear;
	}
}