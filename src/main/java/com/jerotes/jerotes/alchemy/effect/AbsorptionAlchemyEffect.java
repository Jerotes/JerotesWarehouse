package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class AbsorptionAlchemyEffect extends AAAAlchemyEffect {
    public AbsorptionAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.ABSORPTION;
    }

    public String getName() {
        return "jerotes_absorption";
    }
}