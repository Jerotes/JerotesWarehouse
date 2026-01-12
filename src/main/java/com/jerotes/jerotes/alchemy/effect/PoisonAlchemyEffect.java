package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class PoisonAlchemyEffect extends AAAAlchemyEffect {
    public PoisonAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.POISON;
    }

    public String getName() {
        return "jerotes_poison";
    }
}