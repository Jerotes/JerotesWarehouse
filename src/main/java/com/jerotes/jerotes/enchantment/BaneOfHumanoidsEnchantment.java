package com.jerotes.jerotes.enchantment;

import com.jerotes.jerotes.enchantment.Interface.MeleeEnchantment;
import com.jerotes.jerotes.item.Interface.ItemToolBaseThrowingSpear;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;


public class BaneOfHumanoidsEnchantment extends Enchantment implements MeleeEnchantment {
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
