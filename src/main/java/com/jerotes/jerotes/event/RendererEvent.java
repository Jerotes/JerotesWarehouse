package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RendererEvent {

}
