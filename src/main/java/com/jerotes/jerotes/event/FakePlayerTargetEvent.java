package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.goal.JerotesFakePlayerTargetGoal;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID)
public class FakePlayerTargetEvent {

    private static final Map<Mob, Boolean> REGISTERED = new WeakHashMap<>();

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }
        if (mob instanceof JerotesPlayerBaseEntity) {
            return;
        }
        if (REGISTERED.putIfAbsent(mob, Boolean.TRUE) != null) {
            return;
        }
        mob.targetSelector.addGoal(0, new JerotesFakePlayerTargetGoal(mob));
    }
}
