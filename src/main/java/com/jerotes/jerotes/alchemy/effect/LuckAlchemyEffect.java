package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class LuckAlchemyEffect extends AAAAlchemyEffect {
    public LuckAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.LUCK;
    }

    public String getName() {
        return "jerotes_luck";
    }
}