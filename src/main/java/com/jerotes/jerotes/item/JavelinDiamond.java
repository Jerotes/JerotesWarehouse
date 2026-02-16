package com.jerotes.jerotes.item;

import com.jerotes.jerotes.entity.Shoot.Arrow.BaseJavelinEntity;
import com.jerotes.jerotes.entity.Shoot.Arrow.ThrownJavelinDiamondEntity;
import com.jerotes.jerotes.item.Tool.ItemToolBaseJavelin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class JavelinDiamond extends ItemToolBaseJavelin {
	public JavelinDiamond() {
		super(new Properties().stacksTo(1).rarity(Rarity.COMMON).durability(1561), 8f, 1.1f);
	}

	@Override
	public BaseJavelinEntity JerotesThrownJavelin(LivingEntity livingEntity, ItemStack itemStack) {
		return new ThrownJavelinDiamondEntity(livingEntity.level(), livingEntity, itemStack);
	}
	public float getThrowingDamage() {
		return 7.0f;
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack2.is(Items.DIAMOND) || super.isValidRepairItem(itemStack, itemStack2);
	}

	@Override
	public int getEnchantmentValue() {
		return 10;
	}
}


