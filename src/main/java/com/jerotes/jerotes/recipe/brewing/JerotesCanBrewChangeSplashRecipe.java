package com.jerotes.jerotes.recipe.brewing;
import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.Collection;
import java.util.Objects;

public class JerotesCanBrewChangeSplashRecipe implements IBrewingRecipe {
	@Override
	public boolean isInput(ItemStack input) {
		if (input.isEmpty()) return false;
		if (input.is(Items.POTION)) return false;
		CompoundTag tag = input.getTag();
		return tag != null && tag.getBoolean("JerotesCanBrewChange");
	}

	@Override
	public boolean isIngredient(ItemStack ingredient) {
		return ingredient.getItem() == Items.GUNPOWDER;
	}

	@Override
	public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		if (!isInput(input) || !isIngredient(ingredient)) {
			return ItemStack.EMPTY;
		}
		ItemStack output = new ItemStack(Items.SPLASH_POTION, input.getCount());
		CompoundTag inputTag = input.getTag();
		output.setTag(inputTag == null ? null : inputTag.copy());
		return output;
	}
}