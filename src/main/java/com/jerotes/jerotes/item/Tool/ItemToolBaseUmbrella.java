package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.enchantment.Interface.MeleeEnchantment;
import com.jerotes.jerotes.item.Interface.MeleeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseUmbrella extends Item implements Vanishable, Equipable, MeleeItem {
	public ItemToolBaseUmbrella(Properties properties) {
		super(properties);
	}

	public float getVerticalSpeed() {
		return 1.0f;
	}
	public float getHorizontalSpeed() {
		return 1.0f;
	}
	public float getVerticalSpeedLiquid() {
		return 1.0f;
	}
	public float getHorizontalSpeedLiquid() {
		return 1.0f;
	}

	public boolean isMeleeWeapon() {
		return false;
	}
	@Override
	public int getUseDuration(ItemStack itemStack) {
		return 72000;
	}

	@Override
	public boolean hurtEnemy(ItemStack itemStack, LivingEntity livingEntity2, LivingEntity livingEntity3) {
		itemStack.hurtAndBreak(2, livingEntity3, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		return true;
	}
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment instanceof DamageEnchantment || enchantment instanceof FireAspectEnchantment || enchantment instanceof LootBonusEnchantment lootBonusEnchantment && lootBonusEnchantment.category == EnchantmentCategory.WEAPON || enchantment instanceof KnockbackEnchantment || enchantment instanceof MeleeEnchantment) {
			return this.isMeleeWeapon();
		}
		if (enchantment instanceof SweepingEdgeEnchantment) {
			return true;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity2) {
		if ((double)blockState.getDestroySpeed(level, blockPos) != 0.0) {
			itemStack.hurtAndBreak(2, livingEntity2, livingEntity -> livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		if (itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1) {
			return InteractionResultHolder.fail(itemStack);
		}
		if (EnchantmentHelper.getRiptide(itemStack) > 0 && !player.isInWaterOrRain()) {
			return InteractionResultHolder.fail(itemStack);
		}
		player.startUsingItem(interactionHand);
		itemStack.hurtAndBreak(1, player, (player1) -> {
			player1.broadcastBreakEvent(interactionHand);
		});
		return InteractionResultHolder.consume(itemStack);
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int count) {
		super.onUseTick(level, livingEntity, itemStack, count);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemStack) {
		return UseAnim.SPEAR;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		list.add(Component.translatable("item.jerotes.umbrella", this.getVerticalSpeed(), this.getHorizontalSpeed(), this.getVerticalSpeedLiquid(), this.getHorizontalSpeedLiquid()).withStyle(ChatFormatting.YELLOW));
		super.appendHoverText(itemStack, level, list, tooltipFlag);
	}
}


