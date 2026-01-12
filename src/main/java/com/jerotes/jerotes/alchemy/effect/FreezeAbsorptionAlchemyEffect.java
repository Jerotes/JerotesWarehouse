package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class FreezeAbsorptionAlchemyEffect extends AAAAlchemyEffect {
    public FreezeAbsorptionAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.FREEZE_ABSORPTION.get();
    }

    public String getName() {
        return "jerotes_freeze_absorption";
    }
}