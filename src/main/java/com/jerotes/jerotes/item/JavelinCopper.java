package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Arrow.BaseJavelinEntity;
import com.jerotes.jerotes.entity.Arrow.ThrownJavelinCopperEntity;
import com.jerotes.jerotes.item.Tool.ItemToolBaseJavelin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class JavelinCopper extends ItemToolBaseJavelin {
	public JavelinCopper() {
		super(new Properties().stacksTo(1).rarity(Rarity.COMMON).durability(190), 6f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownJavelinCopperEntity(livingEntity.level(), livingEntity, itemStack);
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack2.is(Items.COPPER_INGOT) || super.isValidRepairItem(itemStack, itemStack2);
	}

	@Override
	public int getEnchantmentValue() {
		return 13;
	}
}


