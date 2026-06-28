package com.jerotes.jerotes.recipe.brewing;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.Objects;

public class JerotesCanBrewChangeLingeringRecipe implements IBrewingRecipe {

	@Override
	public boolean isInput(ItemStack input) {
		if (input.isEmpty()) return false;
		if (input.getItem() != Items.SPLASH_POTION) return false;
		CompoundTag tag = input.getTag();
		return tag != null && tag.getBoolean("JerotesCanBrewChange");
	}

	@Override
	public boolean isIngredient(ItemStack ingredient) {
		return ingredient.getItem() == Items.DRAGON_BREATH;
	}

	@Override
	public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		if (!isInput(input) || !isIngredient(ingredient)) {
			return ItemStack.EMPTY;
		}
		ItemStack output = new ItemStack(Items.LINGERING_POTION, input.getCount());
		PotionUtils.setPotion(output, PotionUtils.getPotion(input));
		PotionUtils.setCustomEffects(output, PotionUtils.getCustomEffects(input));
		CompoundTag inputTag = input.getTag();
		if (inputTag != null) {
			CompoundTag outputTag = output.getOrCreateTag();
			for (String key : inputTag.getAllKeys()) {
				if (!key.equals("Potion") && !key.equals("CustomPotionEffects")) {
					outputTag.put(key, Objects.requireNonNull(inputTag.get(key)).copy());
				}
			}
		}
		return output;
	}
}