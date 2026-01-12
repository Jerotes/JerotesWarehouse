package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class HealthBoostAlchemyEffect extends AAAAlchemyEffect {
    public HealthBoostAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.HEALTH_BOOST;
    }

    public String getName() {
        return "jerotes_health_boost";
    }
}