package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class FireResistanceAlchemyEffect extends AAAAlchemyEffect {
    public FireResistanceAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.FIRE_RESISTANCE;
    }

    public String getName() {
        return "jerotes_fire_resistance";
    }
}