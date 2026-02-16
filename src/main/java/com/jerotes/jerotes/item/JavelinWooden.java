package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Shoot.Arrow.BaseJavelinEntity;
import com.jerotes.jerotes.entity.Shoot.Arrow.ThrownJavelinWoodenEntity;
import com.jerotes.jerotes.item.Tool.ItemToolBaseJavelin;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class JavelinWooden extends ItemToolBaseJavelin {
	public JavelinWooden() {
		super(new Properties().stacksTo(1).rarity(Rarity.COMMON).durability(59), 5f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownJavelinWoodenEntity(livingEntity.level(), livingEntity, itemStack);
	}
	public float getThrowingDamage() {
		return 4.0f;
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


