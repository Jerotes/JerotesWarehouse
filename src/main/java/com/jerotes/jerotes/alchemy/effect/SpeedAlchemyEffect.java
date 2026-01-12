package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class SpeedAlchemyEffect extends AAAAlchemyEffect {
    public SpeedAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.MOVEMENT_SPEED;
    }

    public String getName() {
        return "jerotes_speed";
    }
}