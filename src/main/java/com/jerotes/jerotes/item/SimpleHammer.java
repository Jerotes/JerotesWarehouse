package com.jerotes.jerotes.item;

import com.jerotes.jerotes.item.Tool.ItemToolBaseDagger;
import com.jerotes.jerotes.item.Tool.ItemToolBaseHammer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class SimpleHammer extends ItemToolBaseHammer {
	public SimpleHammer() {
		super(new Tier() {
			public int getUses() {
				return 32;
			}

			public float getSpeed() {
				return 2f;
			}

			public float getAttackDamageBonus() {
				return 0;
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
		}, 2.0f, -1.8F, new Properties().durability(32));
	}
}


