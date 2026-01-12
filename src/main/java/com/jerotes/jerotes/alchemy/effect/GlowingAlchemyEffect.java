package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class GlowingAlchemyEffect extends AAAAlchemyEffect {
    public GlowingAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.GLOWING;
    }

    public String getName() {
        return "jerotes_glowing";
    }
}