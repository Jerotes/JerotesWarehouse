package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class UnluckAlchemyEffect extends AAAAlchemyEffect {
    public UnluckAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.UNLUCK;
    }

    public String getName() {
        return "jerotes_unluck";
    }
}