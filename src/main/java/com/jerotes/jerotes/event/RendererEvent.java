package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.layer.TruesightLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RendererEvent {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
        for (EntityRenderer<?> renderer : manager.renderers.values()) {
            if (renderer instanceof LivingEntityRenderer livingRenderer) {
                addLayerToRenderer(livingRenderer);
            }
        }
        LivingEntityRenderer<Player, PlayerModel<Player>> renderer = event.getSkin("default");
        if (renderer != null) {
            addLayerToRenderer(renderer);
        }
        LivingEntityRenderer<Player, PlayerModel<Player>> renderer2 = event.getSkin("slim");
        if (renderer2 != null) {
            addLayerToRenderer(renderer2);
        }
    }
    private static <T extends LivingEntity, M extends EntityModel<T>>
    void addLayerToRenderer(LivingEntityRenderer<T, M> renderer) {
        renderer.addLayer(new TruesightLayer<>(renderer));
    }
}