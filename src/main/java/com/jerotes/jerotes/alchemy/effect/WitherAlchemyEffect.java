package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class WitherAlchemyEffect extends AAAAlchemyEffect {
    public WitherAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.WITHER;
    }

    public String getName() {
        return "jerotes_wither";
    }
}