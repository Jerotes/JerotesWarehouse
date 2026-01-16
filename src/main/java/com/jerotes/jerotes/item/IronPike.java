package com.jerotes.jerotes.item;

import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;

public class IronPike extends ItemToolBasePike {
	public IronPike() {
		super(Tiers.IRON, new Properties().stacksTo(1).rarity(Rarity.COMMON), 10f - 2f, (1.0f / 1.43f) - 4f, 1.43f, 0.2f,
				0.9f, 1.1f, 4f,
				JerotesSoundEvents.SPEAR_ATTACK,
				JerotesSoundEvents.SPEAR_HIT,
				JerotesSoundEvents.SPEAR_WOOD_HIT,
				3.5f, 7.5f, 3.5f, 9.5f, 0.25f, 0.85f);
	}
}


