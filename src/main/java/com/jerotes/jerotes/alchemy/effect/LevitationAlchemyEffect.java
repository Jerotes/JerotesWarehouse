package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class LevitationAlchemyEffect extends AAAAlchemyEffect {
    public LevitationAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.LEVITATION;
    }

    public String getName() {
        return "jerotes_levitation";
    }
}