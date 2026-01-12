package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.arrow.BaseJavelinEntity;
import com.jerotes.jerotes.entity.arrow.ThrownSimpleJavelinEntity;
import com.jerotes.jerotes.item.tool.ItemToolBaseJavelin;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class SimpleJavelin extends ItemToolBaseJavelin {
	public SimpleJavelin() {
		super(new Properties().stacksTo(1).rarity(Rarity.COMMON).durability(32), 5.0f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownSimpleJavelinEntity(livingEntity.level(), livingEntity, itemStack);
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack2.is(ItemTags.PLANKS) || super.isValidRepairItem(itemStack, itemStack2);
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}
}


