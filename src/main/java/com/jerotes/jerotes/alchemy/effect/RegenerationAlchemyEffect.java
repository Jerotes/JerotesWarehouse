package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class RegenerationAlchemyEffect extends AAAAlchemyEffect {
    public RegenerationAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.REGENERATION;
    }

    public String getName() {
        return "jerotes_regeneration";
    }
}