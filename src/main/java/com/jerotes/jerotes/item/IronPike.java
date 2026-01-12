package com.jerotes.jerotes.item;

import com.jerotes.jerotes.init.JerotesSounds;
import com.jerotes.jerotes.item.tool.ItemToolBasePike;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;

public class IronPike extends ItemToolBasePike {
	public IronPike() {
		super(Tiers.IRON, new Properties().stacksTo(1).rarity(Rarity.COMMON), 10f - 2f, (1.0f / 1.43f) - 4f, 1.43f, 0.2f,
				0.9f, 1.1f, 4f,
				JerotesSounds.SPEAR_ATTACK,
				JerotesSounds.SPEAR_HIT,
				JerotesSounds.SPEAR_WOOD_HIT,
				3.5f, 7.5f, 3.5f, 9.5f, 0.25f, 0.85f);
	}
}


