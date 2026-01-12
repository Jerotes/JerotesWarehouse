package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.init.JerotesItems;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ColorEvent {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        BlockColors blockColors = event.getBlockColors();

        //皮革兽铠
        event.register((itemStack, n) -> n > 0 ? -1 :
                ((DyeableLeatherItem)((Object)itemStack.getItem())).getColor(itemStack), JerotesItems.LEATHER_WAR_BEAST_ARMOR.get(), JerotesItems.LEATHER_GIANT_BEAST_ARMOR.get());

    }
}
