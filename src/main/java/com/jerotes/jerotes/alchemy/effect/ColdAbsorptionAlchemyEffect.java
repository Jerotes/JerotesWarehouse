package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;

public class ColdAbsorptionAlchemyEffect extends AAAAlchemyEffect {
    public ColdAbsorptionAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.COLD_ABSORPTION.get();
    }

    public String getName() {
        return "jerotes_cold_absorption";
    }
}