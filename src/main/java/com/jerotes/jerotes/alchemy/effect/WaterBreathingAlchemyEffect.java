package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class WaterBreathingAlchemyEffect extends AAAAlchemyEffect {
    public WaterBreathingAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.WATER_BREATHING;
    }

    public String getName() {
        return "jerotes_water_breathing";
    }
}