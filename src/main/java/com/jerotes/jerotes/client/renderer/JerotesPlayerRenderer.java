package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.layer.HumanSkinEntityBodyLayer;
import com.jerotes.jerotes.client.layer.JerotesPlayerItemInHandLayer;
import com.jerotes.jerotes.client.layer.TruesightLayer;
import com.jerotes.jerotes.client.model.Modelhumanoid_wide_or_slim_for_human;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Mob.HumanEntity;
import com.jerotes.jerotes.entity.Mob.JerotesPlayerEntity;
import com.jerotes.jerotes.util.PlayerSkin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Locale;

public class JerotesPlayerRenderer extends MobRenderer<HumanEntity, Modelhumanoid_wide_or_slim_for_human<HumanEntity>> {
	private static final ResourceLocation NULL_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/null.png");
	public JerotesPlayerRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelhumanoid_wide_or_slim_for_human<>(context.bakeLayer(Modelhumanoid_wide_or_slim_for_human.LAYER_LOCATION)), 0.5f);
		this.addLayer(new CustomHeadLayer(this, context.getModelSet(), 1.0F, 1.0F, 1.0F, context.getItemInHandRenderer()));
		this.addLayer(new ElytraLayer(this, context.getModelSet()));
		this.addLayer(new JerotesPlayerItemInHandLayer(this, context.getItemInHandRenderer()));
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
		this.addLayer(new HumanSkinEntityBodyLayer<>(this, new Modelhumanoid_wide_or_slim_for_human(context.bakeLayer(Modelhumanoid_wide_or_slim_for_human.LAYER_LOCATION)), "human"));
		this.addLayer(new TruesightLayer<>(this));
	}

	@Override
	protected void scale(HumanEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(0.9375f, 0.9375f, 0.9375f);
		super.scale(entity, poseStack, f);
	}

	public void render(HumanEntity humanEntity, float p_117789_, float p_117790_, PoseStack p_117791_, MultiBufferSource p_117792_, int p_117793_) {
		super.render(humanEntity, p_117789_, p_117790_, p_117791_, p_117792_, p_117793_);
	}

	public static String sanitizeString(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}
		String lowerCase = input.toLowerCase(Locale.ROOT);
		String sanitized = lowerCase.replaceAll("[^a-z0-9_./-]", "_");
		sanitized = sanitized.replaceAll(" ", "_");
		return sanitized;
	}

	public static void setModelProperties(Modelhumanoid_wide_or_slim_for_human<?> playermodel, HumanEntity humanEntity) {
		if (humanEntity.isSpectator()) {
			playermodel.setAllVisible(false);
			playermodel.head.visible = true;
			playermodel.hat.visible = true;
		} else {
			playermodel.setAllVisible(true);
//			playermodel.hat.visible = true;
//			playermodel.jacket.visible = true;
//			playermodel.leftPants.visible = true;
//			playermodel.rightPants.visible = true;
//			playermodel.leftSleeve.visible = true;
//			playermodel.rightSleeve.visible = true;
			playermodel.crouching = humanEntity.isCrouching();
			HumanoidModel.ArmPose humanoidmodel$armpose = getArmPose(humanEntity, InteractionHand.MAIN_HAND);
			HumanoidModel.ArmPose humanoidmodel$armpose1 = getArmPose(humanEntity, InteractionHand.OFF_HAND);
			if (humanoidmodel$armpose.isTwoHanded()) {
				humanoidmodel$armpose1 = humanEntity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
			}

			if (humanEntity.getMainArm() == HumanoidArm.RIGHT) {
				playermodel.rightArmPose = humanoidmodel$armpose;
				playermodel.leftArmPose = humanoidmodel$armpose1;
			} else {
				playermodel.rightArmPose = humanoidmodel$armpose1;
				playermodel.leftArmPose = humanoidmodel$armpose;
			}
		}

	}

	protected void setupRotations(HumanEntity p_117802_, PoseStack p_117803_, float p_117804_, float p_117805_, float p_117806_) {
		float f = p_117802_.getSwimAmount(p_117806_);
		float f3;
		float f2;
		if (p_117802_.isFallFlying()) {
			super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
			f3 = (float)p_117802_.getFallFlyingTicks() + p_117806_;
			f2 = Mth.clamp(f3 * f3 / 100.0F, 0.0F, 1.0F);
			if (!p_117802_.isAutoSpinAttack()) {
				p_117803_.mulPose(Axis.XP.rotationDegrees(f2 * (-90.0F - p_117802_.getXRot())));
			}

			Vec3 vec3 = p_117802_.getViewVector(p_117806_);
			Vec3 vec31 = p_117802_.getDeltaMovement();
			double d0 = vec31.horizontalDistanceSqr();
			double d1 = vec3.horizontalDistanceSqr();
			if (d0 > 0.0 && d1 > 0.0) {
				double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
				double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
				p_117803_.mulPose(Axis.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
			}
		} else if (f > 0.0F) {
			super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
			f3 = !p_117802_.isInWater() && !p_117802_.isInFluidType((fluidType, height) -> {
				return p_117802_.canSwimInFluidType(fluidType);
			}) ? -90.0F : -90.0F - p_117802_.getXRot();
			f2 = Mth.lerp(f, 0.0F, f3);
			p_117803_.mulPose(Axis.XP.rotationDegrees(f2));
			if (p_117802_.isVisuallySwimming()) {
				p_117803_.translate(0.0F, -1.0F, 0.3F);
			}
		} else {
			super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
		}

	}

	public Vec3 getRenderOffset(HumanEntity p_117785_, float p_117786_) {
		return p_117785_.isCrouching() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(p_117785_, p_117786_);
	}

	protected void renderNameTag(HumanEntity p_117808_, Component p_117809_, PoseStack p_117810_, MultiBufferSource p_117811_, int p_117812_) {
		double d0 = this.entityRenderDispatcher.distanceToSqr(p_117808_);
		p_117810_.pushPose();
		if (d0 < 100.0) {
			Scoreboard scoreboard = p_117808_.level().getScoreboard();
			Objective objective = scoreboard.getDisplayObjective(2);
			if (objective != null) {
				Score score = scoreboard.getOrCreatePlayerScore(p_117808_.getScoreboardName(), objective);
				super.renderNameTag(p_117808_, Component.literal(Integer.toString(score.getScore())).append(CommonComponents.SPACE).append(objective.getDisplayName()), p_117810_, p_117811_, p_117812_);
				p_117810_.translate(0.0F, 0.25875F, 0.0F);
			}
		}

		super.renderNameTag(p_117808_, p_117809_, p_117810_, p_117811_, p_117812_);
		p_117810_.popPose();
	}

	private static HumanoidModel.ArmPose getArmPose(HumanEntity p_117795_, InteractionHand p_117796_) {
		ItemStack itemstack = p_117795_.getItemInHand(p_117796_);
		if (itemstack.isEmpty()) {
			return HumanoidModel.ArmPose.EMPTY;
		} else {
			if (p_117795_.getUsedItemHand() == p_117796_ && p_117795_.getUseItemRemainingTicks() > 0) {
				UseAnim useanim = itemstack.getUseAnimation();
				if (useanim == UseAnim.BLOCK) {
					return HumanoidModel.ArmPose.BLOCK;
				}

				if (useanim == UseAnim.BOW) {
					return HumanoidModel.ArmPose.BOW_AND_ARROW;
				}

				if (useanim == UseAnim.SPEAR) {
					return HumanoidModel.ArmPose.THROW_SPEAR;
				}

				if (useanim == UseAnim.CROSSBOW && p_117796_ == p_117795_.getUsedItemHand()) {
					return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
				}

				if (useanim == UseAnim.SPYGLASS) {
					return HumanoidModel.ArmPose.SPYGLASS;
				}

				if (useanim == UseAnim.TOOT_HORN) {
					return HumanoidModel.ArmPose.TOOT_HORN;
				}

				if (useanim == UseAnim.BRUSH) {
					return HumanoidModel.ArmPose.BRUSH;
				}
			} else if (!p_117795_.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
				return HumanoidModel.ArmPose.CROSSBOW_HOLD;
			}

			HumanoidModel.ArmPose forgeArmPose = IClientItemExtensions.of(itemstack).getArmPose(p_117795_, p_117796_, itemstack);
			return forgeArmPose != null ? forgeArmPose : HumanoidModel.ArmPose.ITEM;
		}
	}


	@Override
	public ResourceLocation getTextureLocation(HumanEntity entity) {
		if (entity.getUsername() != null && !entity.getUsername().getSkinName().isEmpty()) {
			return PlayerSkin.getPlayerSkin(entity);
		}
		String string = ChatFormatting.stripFormatting(entity.getName().getString());
		String slim = "wide";
		if (entity.IsFemale()) {
			slim = "slim";
		}
		if (entity.getCustomName() != null) {
			if (string != null && !string.isEmpty() && MainConfig.HumanCustomNameWide.contains(string)) {
				return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/custom_skin/" + sanitizeString(string) + ".png");
			}
			if (string != null && !string.isEmpty() && MainConfig.HumanCustomNameSlim.contains(string)) {
				return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/custom_skin/" + sanitizeString(string) + ".png");
			}
		}
		if ("Steve".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/steve.png");
		}
		if ("Alex".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/alex.png");
		}
		if ("Ari".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/ari.png");
		}
		if ("Kai".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/kai.png");
		}
		if ("Noor".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/noor.png");
		}
		if ("Sunny".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/sunny.png");
		}
		if ("Zuri".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/zuri.png");
		}
		if ("Efe".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/efe.png");
		}
		if ("Makena".equals(string)) {
			return new ResourceLocation("minecraft:textures/entity/player/" + slim + "/makena.png");
		}
        return MainConfig.RandomSkinMobHasUnderTexture ? new ResourceLocation(JerotesWarehouse.MODID, "minecraft:textures/entity/player/" + slim + "/steve.png") : NULL_LOCATION;
	}

	protected boolean shouldShowName(HumanEntity entity) {
		return super.shouldShowName(entity) || entity instanceof JerotesPlayerEntity;
	}
}
