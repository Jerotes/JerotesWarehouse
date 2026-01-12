package com.jerotes.jerotes.enchantment;

import com.jerotes.jerotes.item.ItemToolBaseThrowingSpear;
import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import com.jerotes.jerotes.item.tool.ItemToolBaseThrowingSpearOfJavelin;
import com.jerotes.jerotes.item.tool.ItemToolBaseThrowingSpearOfSpear;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.FireAspectEnchantment;


public class BaneOfHumanoidsEnchantment extends Enchantment {
	public BaneOfHumanoidsEnchantment(EquipmentSlot... slots) {
		super(Rarity.COMMON, EnchantmentCategory.WEAPON, slots);
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	protected boolean checkCompatibility(Enchantment ench) {
		return this != ench && !(ench instanceof DamageEnchantment);
	}

	@Override
	public boolean canEnchant(ItemStack itemStack) {
		if (itemStack.getItem() instanceof AxeItem) {
			return true;
		}
		if (itemStack.getItem() instanceof ItemToolBaseSpearBase) {
			return true;
		}
		if (itemStack.getItem() instanceof ItemToolBaseThrowingSpear) {
			return true;
		}
		return super.canEnchant(itemStack);
	}

	@Override
	public boolean isTreasureOnly() {
		return true;
	}

	@Override
	public boolean isTradeable() {
		return false;
	}
}
