package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class DarknessAlchemyEffect extends AAAAlchemyEffect {
    public DarknessAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.DARKNESS;
    }

    public String getName() {
        return "jerotes_darkness";
    }
}