package com.jerotes.jerotes.init;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class JerotesFuels {
	@SubscribeEvent
	public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
		ItemStack itemstack = event.getItemStack();
		if (itemstack.getItem() == JerotesItems.WOODEN_JAVELIN.get().asItem())
			event.setBurnTime(200);
		if (itemstack.getItem() == JerotesItems.WOODEN_SPEAR.get().asItem())
			event.setBurnTime(200);
	}
}
