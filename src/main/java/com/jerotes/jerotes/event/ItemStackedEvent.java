package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.item.AASpellJavelin;
import com.jerotes.jerotes.item.FlawlessMagicScroll;
import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.network.PacketHandler;
import com.jerotes.jerotes.network.SpellJavelinPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class ItemStackedEvent {

	@SubscribeEvent
	public static void onItemStacked(ItemStackedOnOtherEvent event) {
		ItemStack carried = event.getCarriedItem();
		ItemStack stackedOn = event.getStackedOnItem();
		//这是被右键的
		if (!(carried.getItem() instanceof AASpellJavelin javelin))
			return;
		//这是鼠标拿的
		if (!(stackedOn.getItem() instanceof MagicItem magicItem))
			return;
		if (event.getPlayer().level().isClientSide()) {
			javelin.setSpellJavelin(carried, stackedOn);
			event.getSlot().setChanged();
			PacketHandler.sendToServer(
					new SpellJavelinPacket(event.getSlot().index, stackedOn));
		}
		event.setCanceled(true);
	}
}