package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;

public class PruritusAlchemyEffect extends AAAAlchemyEffect {
    public PruritusAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.PRURITUS.get();
    }
    public String getName() {
        return "jerotes_pruritus";
    }
}