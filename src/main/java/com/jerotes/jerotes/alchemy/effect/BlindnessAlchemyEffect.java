package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class BlindnessAlchemyEffect extends AAAAlchemyEffect {
    public BlindnessAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.BLINDNESS;
    }

    public String getName() {
        return "jerotes_blindness";
    }
}