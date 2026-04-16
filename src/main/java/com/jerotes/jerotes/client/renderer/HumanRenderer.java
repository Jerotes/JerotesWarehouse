package com.jerotes.jerotes.client.renderer;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.layer.HumanSkinEntityBodyLayer;
import com.jerotes.jerotes.client.layer.TruesightLayer;
import com.jerotes.jerotes.client.model.Modelhumanoid_wide_or_slim;
import com.jerotes.jerotes.client.model.Modelhumanoid_wide_or_slim_for_human;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Mob.HumanEntity;
import com.jerotes.jerotes.entity.Mob.JerotesPlayerEntity;
import com.jerotes.jerotes.util.PlayerSkin;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;

import java.util.Locale;

public class HumanRenderer extends HumanoidMobRenderer<HumanEntity, Modelhumanoid_wide_or_slim<HumanEntity>> {
	private static final ResourceLocation NULL_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/null.png");
	public HumanRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelhumanoid_wide_or_slim<>(context.bakeLayer(Modelhumanoid_wide_or_slim.LAYER_LOCATION)), 0.5f);
		this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
		this.addLayer(new HumanSkinEntityBodyLayer<>(this, new Modelhumanoid_wide_or_slim(context.bakeLayer(Modelhumanoid_wide_or_slim.LAYER_LOCATION)), "human"));
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
