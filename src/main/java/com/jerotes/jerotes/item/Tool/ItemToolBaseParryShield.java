package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.enchantment.Interface.MeleeEnchantment;
import com.jerotes.jerotes.item.Interface.ItemSpecialInHand;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import com.jerotes.jerotes.util.Main;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ItemToolBaseParryShield extends ItemToolBaseShield implements ItemSpecialInHand, MeleeItem {

	//招架间隔
	//招架持续
	//招架反弹伤害倍数
	//招架反弹伤害最大倍数
	//击退力度
	public final int parryCooldownTicks;
	public final int parryDurationTicks;
	public final float parryDamageMultiplier;
	public final float maxParryDamageMultiplier;
	public final float knockbackStength;

	public ItemToolBaseParryShield(Properties properties, float n, float f, int enchantmentValue, Ingredient repairItem, int parryCooldownTicks,int parryDurationTicks, float parryDamageMultiplier, float maxParryDamageMultiplier, float knockbackStength) {
		super(properties, n, f, enchantmentValue, repairItem);
		this.parryCooldownTicks = parryCooldownTicks;
		this.parryDurationTicks = parryDurationTicks;
		this.parryDamageMultiplier = parryDamageMultiplier;
		this.maxParryDamageMultiplier = maxParryDamageMultiplier;
		this.knockbackStength = knockbackStength;
	}

	public boolean isMeleeWeapon() {
		return false;
	}

	public void makeParrySound(Entity entity) {
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
				SoundEvents.PLAYER_ATTACK_CRIT, entity.getSoundSource(), 1.0f, 1.0f);
	}
	public void makeParryUseSound(Entity entity) {
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
				SoundEvents.ANVIL_HIT, entity.getSoundSource(), 1.0f, 1.0f);
	}

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment instanceof DamageEnchantment || enchantment instanceof FireAspectEnchantment || enchantment instanceof LootBonusEnchantment lootBonusEnchantment && lootBonusEnchantment.category == EnchantmentCategory.WEAPON || enchantment instanceof KnockbackEnchantment || enchantment instanceof MeleeEnchantment) {
			return this.isMeleeWeapon();
		}
		if (enchantment instanceof SweepingEdgeEnchantment) {
			return this instanceof ItemTwoHanded;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new ParryShieldClient());
	}
	public static class ParryShieldClient implements IClientItemExtensions{
		public static final HumanoidModel.ArmPose PARRY_SHIELD_POSE = HumanoidModel.ArmPose.create("JEROTES_PARRY_SHIELD", true, (model, entity, arm) -> {});
		@Override
		public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
			if (entity.getUseItem().getItem() instanceof ItemToolBaseParryShield) {
				return PARRY_SHIELD_POSE;
			}
			return null;
		}

		private void applyItemArmTransform(PoseStack poseStack, HumanoidArm arm, float equipProcess) {
			int i = arm == HumanoidArm.RIGHT ? 1 : -1;
			poseStack.translate((double)((float)i * 0.56F), (double)(-0.52F + equipProcess * -0.6F), (double)-0.72F);
		}

		//第一人称
		@Override
		public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm,
											   ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
			int dir = arm == HumanoidArm.RIGHT ? 1 : -1;
			double d = Main.getJerotesPersistentData(player).getDouble("jerotes_shield_parry_tick");
			if (player.isUsingItem() && player.getUseItem().getItem()instanceof ItemToolBaseParryShield itemToolBaseParryShield && d > 0) {
				this.applyItemArmTransform(poseStack, arm, 0);
				//向前挥动
				firstPersonAttack(((float) d / (float) itemToolBaseParryShield.parryDurationTicks), poseStack, dir, arm);
			}
			return false;
		}
	}

	public static void firstPersonAttack(float f, PoseStack poseStack, int n, HumanoidArm humanoidArm) {
		//后拉
		if (f > 0 && f <= 0.05) {
			float fs = f / 0.05f;
			poseStack.translate(0, 0, 3 * fs);
		}
		//前刺
		else if (f > 0.05 && f <= 0.35) {
			float fs = (f - 0.05f) / 0.3f;
			poseStack.translate(0, 0, 3 - 4 * fs);
		}
		else if (f > 0.35 && f <= 0.75) {
			float fs = (f - 0.35f) / 0.4f;
			poseStack.translate(0, 0, -1 + 0.75 * fs);
		}
		//收回
		else if (f > 0.75) {
			float fs = 1 - (f - 0.75f) / 0.25f;
			poseStack.translate(0, 0, -0.25 * fs);
		}
	}

	@Override
	public void specialInHandLayer(EntityModel<?> entityModel, LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
		if (itemStack.getItem() instanceof ItemToolBaseParryShield itemToolBaseParryShield) {
			double d = Main.getJerotesPersistentData(livingEntity).getDouble("jerotes_shield_parry_tick");
			float f = (float) d / (float) itemToolBaseParryShield.parryDurationTicks;
			//后拉
			if (f > 0 && f <= 0.05) {
				float fs = f / 0.05f;
				poseStack.mulPose(Axis.ZP.rotation((3 * fs)));
			}
			//前刺
			else if (f > 0.05 && f <= 0.35) {
				float fs = (f - 0.05f) / 0.3f;
				poseStack.mulPose(Axis.ZP.rotation((3 - 4 * fs)));
			}
			else if (f > 0.35 && f <= 0.75) {
				float fs = (f - 0.35f) / 0.4f;
				poseStack.mulPose(Axis.ZP.rotation((-1f + 0.75f * fs)));
			}
			//收回
			else if (f > 0.75) {
				float fs = 1 - (f - 0.75f) / 0.25f;
				poseStack.mulPose(Axis.ZP.rotation((-0.25f * fs)));
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		list.add(Component.translatable("item.jerotes.parry_shield", parryCooldownTicks/20f, parryDurationTicks/20f, parryDamageMultiplier, maxParryDamageMultiplier, knockbackStength).withStyle(ChatFormatting.YELLOW));
	}
}


