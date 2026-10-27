package com.jerotes.jerotes.item;

import com.jerotes.jerotes.item.Tool.ItemToolBaseHammer;
import com.jerotes.jerotes.item.Tool.ItemToolBaseTwoHandedSword;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class SimpleGreatsword extends ItemToolBaseTwoHandedSword {
	public SimpleGreatsword() {
		super(new Tier() {
			public int getUses() {
				return 32;
			}

			public float getSpeed() {
				return 2f;
			}

			public float getAttackDamageBonus() {
				return 8;
			}

			public int getLevel() {
				return 0;
			}

			public int getEnchantmentValue() {
				return 15;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(ItemTags.PLANKS);
			}
		}, -1.0f, -3.2F, new Properties().durability(32));
	}
}


