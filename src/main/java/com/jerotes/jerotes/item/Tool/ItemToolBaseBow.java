package com.jerotes.jerotes.item.Tool;

import com.jerotes.jerotes.item.Interface.ItemSpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseBow extends BowItem implements ItemSpecialEffect {
	public ItemToolBaseBow(Properties properties) {
		super(properties);
	}

	@Override
	public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int n) {
		if (livingEntity instanceof Player player) {
			boolean flag = player.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemStack) > 0;
			ItemStack itemStack2 = player.getProjectile(itemStack);
			int i = this.getUseDuration(itemStack) - n;
			i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(itemStack, level, player, i, !itemStack2.isEmpty() || flag);
			if (i < 0) return;
			if (!itemStack2.isEmpty() || flag) {
				if (itemStack2.isEmpty() && !player.getAbilities().instabuild) {
					itemStack2 = new ItemStack(Items.ARROW);
				}
				float f = getPowerForTime(i);
				if (itemStack.getItem() instanceof ItemToolBaseBow itemToolBaseBow) {
					f = itemToolBaseBow.getPowerForTimeJerotes(i);
				}
				if (!((double)f < 0.1D)) {
					boolean flag1 = player.getAbilities().instabuild || (itemStack2.getItem() instanceof ArrowItem && ((ArrowItem)itemStack2.getItem()).isInfinite(itemStack2, itemStack, player));
					if (!level.isClientSide()) {
						ArrowItem arrowitem = (ArrowItem)(itemStack2.getItem() instanceof ArrowItem ? itemStack2.getItem() : Items.ARROW);
						AbstractArrow abstractarrow = arrowitem.createArrow(level, itemStack2, player);
						abstractarrow = customArrow(abstractarrow);
						abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, getArrowSpeed(f), getArrowInaccuracy());
						if (f == 1.0F) {
							abstractarrow.setCritArrow(true);
						}
						int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
						if (j > 0) {
							abstractarrow.setBaseDamage(abstractarrow.getBaseDamage() + (double)j * 0.5D + 0.5D);
						}
						int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemStack);
						if (k > 0) {
							abstractarrow.setKnockback(k);
						}
						if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemStack) > 0) {
							abstractarrow.setSecondsOnFire(100);
						}
						itemStack.hurtAndBreak(1, player, (player2) -> {
							player2.broadcastBreakEvent(player.getUsedItemHand());
						});
						if (flag1 || player.getAbilities().instabuild && (itemStack2.getItem() instanceof ArrowItem)) {
							abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
						}
						level.addFreshEntity(abstractarrow);
					}
					level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
					if (!flag1 && !player.getAbilities().instabuild) {
						itemStack2.shrink(1);
						if (itemStack2.isEmpty()) {
							player.getInventory().removeItem(itemStack2);
						}
					}
					player.awardStat(Stats.ITEM_USED.get(this));
				}
			}
		}
	}

	public float getPowerForTimeJerotes(int n) {
		float f = (float)n / 20.0f;
		f = (f * f + f * 2.0f) / 3.0f;
		if (f > 1.0f) {
			f = 1.0f;
		}
		return f;
	}
	public float getArrowSpeed(float f) {
		return f * 3.0F;
	}
	public float getArrowInaccuracy() {
		return 1.0F;
	}

	//发射特殊非箭弹药
	public boolean specialShootBullet(LivingEntity livingEntity, LivingEntity livingEntityTarget, float f, int n, int n2) {
		return false;
	}
	//选择特殊箭矢
	public boolean useBaseShootArrow() {
		return false;
	}
	public AbstractArrow customBaseShootArrow(LivingEntity livingEntity, ItemStack itemStack) {
		return null;
	}
	public float customBaseShootArrowChance() {
		return 1.0f;
	}
	public float customBaseShootArrowChance(LivingEntity livingEntity) {
		return customBaseShootArrowChance();
	}
	@Override
	public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
		list.add(Component.translatable("item.jerotes.bow").withStyle(ChatFormatting.YELLOW));
		super.appendHoverText(itemStack, level, list, tooltipFlag);
	}
}


