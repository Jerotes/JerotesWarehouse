package com.jerotes.jerotes.item.tool;

import com.jerotes.jerotes.item.ItemSpecialEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemToolBaseBow extends BowItem implements ItemSpecialEffect {
	public ItemToolBaseBow(Properties properties) {
		super(properties);
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


