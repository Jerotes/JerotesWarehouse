package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Shoot.Arrow.AnestheticArrowEntity;
import com.jerotes.jerotes.item.Interface.ItemAnesthetized;
import com.jerotes.jerotes.item.Tool.ItemToolBaseArrow;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AnestheticArrow extends ItemToolBaseArrow implements ItemAnesthetized {
	public AnestheticArrow() {
		super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public AbstractArrow createArrow(Level level, ItemStack itemStack, LivingEntity livingEntity) {
		return new AnestheticArrowEntity(level, livingEntity, itemStack.copyWithCount(1));
	}

	@Override
	public int getAnesthetized() {
		return 180;
	}
	@Override
	public boolean onlyPlayer() {
		return false;
	}
	@Override
	public boolean onlyThrowing() {
		return true;
	}
	@Override
	public float getBaseDamage() {
		return 2.0f;
	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level level, List<Component> list, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, level, list, tooltipFlag);
		list.add(Component.translatable("item.jerotes.anesthetized_shoot", this.getAnesthetized()).withStyle(ChatFormatting.YELLOW));
	}
}
