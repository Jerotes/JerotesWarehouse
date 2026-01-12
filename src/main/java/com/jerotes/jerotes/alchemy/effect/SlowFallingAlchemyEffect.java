package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class SlowFallingAlchemyEffect extends AAAAlchemyEffect {
    public SlowFallingAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.SLOW_FALLING;
    }

    public String getName() {
        return "jerotes_slow_falling";
    }
}