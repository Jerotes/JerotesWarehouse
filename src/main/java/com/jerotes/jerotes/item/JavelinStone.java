package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.arrow.BaseJavelinEntity;
import com.jerotes.jerotes.entity.arrow.ThrownJavelinStoneEntity;
import com.jerotes.jerotes.item.tool.ItemToolBaseJavelin;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class JavelinStone extends ItemToolBaseJavelin {
	public JavelinStone() {
		super(new Properties().stacksTo(1).rarity(Rarity.COMMON).durability(131), 6f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownJavelinStoneEntity(livingEntity.level(), livingEntity, itemStack);
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack2.is(ItemTags.STONE_TOOL_MATERIALS) || super.isValidRepairItem(itemStack, itemStack2);
	}

	@Override
	public int getEnchantmentValue() {
		return 5;
	}
}


