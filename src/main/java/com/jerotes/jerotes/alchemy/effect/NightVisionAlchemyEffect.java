package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class NightVisionAlchemyEffect extends AAAAlchemyEffect {
    public NightVisionAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.NIGHT_VISION;
    }

    public String getName() {
        return "jerotes_night_vision";
    }
}