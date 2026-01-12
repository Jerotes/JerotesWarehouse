package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class SlownessAlchemyEffect extends AAAAlchemyEffect {
    public SlownessAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.MOVEMENT_SLOWDOWN;
    }

    public String getName() {
        return "jerotes_slowness";
    }
}