package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class WorldEvent {
    public static final UUID FOG_UUID = UUID.fromString("11a0fd41-978a-4b4a-95f8-543c231a1ad0");
     private final static JerotesBossEvent fogEvent = new JerotesBossEvent(Component.translatable("effect.jerotes.fog"), FOG_UUID,BossEvent.BossBarColor.WHITE, true, true, true);
    @SubscribeEvent
    public static void PlayerZsieinFog(LivingEvent.LivingTickEvent event) {
        LivingEntity livingEntity = event.getEntity();
        //玩家血条
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.hasEffect(JerotesMobEffects.FOG.get())) {
                if (!fogEvent.getPlayers().contains(serverPlayer)) {
                    fogEvent.addPlayer(serverPlayer);
                }
            } else {
                if (fogEvent.getPlayers().contains(serverPlayer)) {
                    fogEvent.removePlayer(serverPlayer);
                }
            }
        }
    }
}
