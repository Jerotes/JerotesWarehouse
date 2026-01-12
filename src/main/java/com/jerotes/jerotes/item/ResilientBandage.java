package com.jerotes.jerotes.item;

import com.jerotes.jerotes.item.tool.*;
import net.minecraft.world.item.Rarity;

public class ResilientBandage extends ItemToolBaseBandage {
	public ResilientBandage() {
		super(new Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public float getHealth() {
		return 8.0f;
	}
}
