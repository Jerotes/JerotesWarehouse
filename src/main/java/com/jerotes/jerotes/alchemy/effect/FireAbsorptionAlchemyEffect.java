package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;

public class FireAbsorptionAlchemyEffect extends AAAAlchemyEffect {
    public FireAbsorptionAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.FIRE_ABSORPTION.get();
    }

    public String getName() {
        return "jerotes_fire_absorption";
    }
}