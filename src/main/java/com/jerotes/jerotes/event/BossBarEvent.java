package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, value = Dist.CLIENT)
public class BossBarEvent {
	@SubscribeEvent
	public static void onRenderBossBarFog(CustomizeGuiOverlayEvent.BossEventProgress event) {
		LerpingBossEvent bossEvent = event.getBossEvent();
		if (WorldEvent.FOG_UUID.equals(bossEvent.getId())) {
			event.setCanceled(true);
			event.setIncrement(0);
		}
	}
}