package com.jerotes.jerotes.client.tooltip;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.item.FlawlessMagicScroll;
import com.jerotes.jerotes.spell.MagicSpell;
import com.jerotes.jerotes.spell.SpellListByString;
import com.jerotes.jerotes.spell.SpellRegistry;
import com.mojang.datafixers.util.Either;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvent {
    @SubscribeEvent
    public static void register(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(
                SpellTooltipComponent.class,
                SpellClientTooltipComponent::new
        );
    }
}