package com.jerotes.jerotes.recipe.brewing;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;

public class JerotesBrewingRecipe extends BrewingRecipe {
	private final Ingredient input;
	private final Ingredient ingredient;
	private final ItemStack output;

	public JerotesBrewingRecipe(Ingredient input, Ingredient ingredient, ItemStack output) {
		super(input, ingredient, output);
		this.input = input;
		this.ingredient = ingredient;
		this.output = output;
	}

	@Override
	public boolean isInput(@Nonnull ItemStack itemStack) {
		ItemStack[] matchingStacks = input.getItems();
		if (matchingStacks.length == 0) {
			return itemStack.isEmpty();
		}
		else {
			for (ItemStack itemStack1 : matchingStacks) {
				if (ItemStack.isSameItem(itemStack, itemStack1) && ItemStack.isSameItemSameTags(itemStack1, itemStack)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public Ingredient getInput() {
		return this.input;
	}

	@Override
	public Ingredient getIngredient() {
		return this.ingredient;
	}

	@Override
	public ItemStack getOutput() {
		return this.output;
	}
}