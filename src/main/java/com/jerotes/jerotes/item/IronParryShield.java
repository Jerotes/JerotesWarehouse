package com.jerotes.jerotes.item;

import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.item.Tool.ItemToolBaseParryShield;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;

public class IronParryShield extends ItemToolBaseParryShield {
	public IronParryShield() {
		super(new Item.Properties().stacksTo(1).durability(250 * 3).rarity(Rarity.COMMON),
				3f, 0.8f, 14, Ingredient.of(Items.IRON_INGOT),
				40, 3, 1f, 4, 0.5f);
	}
}


