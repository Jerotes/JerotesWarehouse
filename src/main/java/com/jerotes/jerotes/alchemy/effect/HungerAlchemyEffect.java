package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class HungerAlchemyEffect extends AAAAlchemyEffect {
    public HungerAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.HUNGER;
    }

    public String getName() {
        return "jerotes_hunger";
    }
}