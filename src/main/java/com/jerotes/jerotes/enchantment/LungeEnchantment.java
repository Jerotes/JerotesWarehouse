package com.jerotes.jerotes.enchantment;

import com.jerotes.jerotes.init.JerotesEnchantments;
import com.jerotes.jerotes.item.ItemToolBaseThrowingSpear;
import com.jerotes.jerotes.item.tool.ItemToolBasePike;
import com.jerotes.jerotes.item.tool.ItemToolBaseSpearBase;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class LungeEnchantment extends Enchantment {
	public LungeEnchantment(EquipmentSlot... slots) {
		super(Rarity.UNCOMMON, JerotesEnchantments.SPEAR_ABOUT, slots);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinCost(int n) {
		return 5 + (n - 1) * 8;
	}

	@Override
	public int getMaxCost(int n) {
		return 25 + (n - 1) * 8;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack itemstack) {
		if (itemstack.getItem() instanceof ItemToolBaseThrowingSpear) {
			return true;
		}
		return itemstack.getItem() instanceof ItemToolBaseSpearBase || itemstack.getItem() instanceof ItemToolBasePike;
	}
}
