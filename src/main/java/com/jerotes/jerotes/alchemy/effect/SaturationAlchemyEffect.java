package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class SaturationAlchemyEffect extends AAAAlchemyEffect {
    public SaturationAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.SATURATION;
    }
    public int getMobEffectTick() {
        return 10;
    }

    public String getName() {
        return "jerotes_saturation";
    }
}