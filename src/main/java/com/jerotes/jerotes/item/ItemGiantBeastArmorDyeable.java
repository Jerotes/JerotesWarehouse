package com.jerotes.jerotes.item;

import com.jerotes.jerotes.item.Interface.ItemBaseGiantBeastArmor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;

public class ItemGiantBeastArmorDyeable extends ItemGiantBeastArmor implements DyeableLeatherItem, ItemBaseGiantBeastArmor {

	public ItemGiantBeastArmorDyeable(int n, int color, String string, String string2, Properties properties) {
		this(n, color, new ResourceLocation(string, "textures/entity/giant_beast_armor/" + string2 + ".png"), properties);
	}

	public ItemGiantBeastArmorDyeable(int n, int color, ResourceLocation resourceLocation, Properties properties) {
		super(n, color, resourceLocation, properties);
	}
}
