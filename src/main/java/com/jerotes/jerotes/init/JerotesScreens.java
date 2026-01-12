package com.jerotes.jerotes.init;

import com.jerotes.jerotes.client.gui.MobInventoryGUIScreen;
import com.jerotes.jerotes.client.gui.SuchInventoryScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JerotesScreens {
    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(JerotesMenus.MOB_INVENTORY_GUI.get(), MobInventoryGUIScreen::new);
            MenuScreens.register(JerotesMenus.SUCH_INVENTORY.get(), SuchInventoryScreen::new);
        });
    }
}
