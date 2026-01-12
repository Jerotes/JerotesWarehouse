package com.jerotes.jerotes.init;

import com.jerotes.jerotes.client.model.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class JerotesModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(Modelnull.LAYER_LOCATION, Modelnull::createBodyLayer);
		event.registerLayerDefinition(Modeljavelin.LAYER_LOCATION, Modeljavelin::createBodyLayer);
		event.registerLayerDefinition(Modelbone_throwing_spear.LAYER_LOCATION, Modelbone_throwing_spear::createBodyLayer);
		event.registerLayerDefinition(Modelhumanoid.LAYER_LOCATION, Modelhumanoid::createBodyLayer);
		event.registerLayerDefinition(Modelhumanoid_wide_or_slim.LAYER_LOCATION, Modelhumanoid_wide_or_slim::createBodyLayer);
		event.registerLayerDefinition(Modelhumanoid_wide_or_slim_for_human.LAYER_LOCATION, Modelhumanoid_wide_or_slim_for_human::createBodyLayer);
		event.registerLayerDefinition(Modelspecial_action.LAYER_LOCATION, Modelspecial_action::createBodyLayer);
	}
}
