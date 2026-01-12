package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;

public class LightningAbsorptionAlchemyEffect extends AAAAlchemyEffect {
    public LightningAbsorptionAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.LIGHTNING_ABSORPTION.get();
    }

    public String getName() {
        return "jerotes_lightning_absorption";
    }
}