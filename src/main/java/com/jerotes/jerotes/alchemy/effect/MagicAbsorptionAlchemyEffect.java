package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class MagicAbsorptionAlchemyEffect extends AAAAlchemyEffect {
    public MagicAbsorptionAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.MAGIC_ABSORPTION.get();
    }

    public String getName() {
        return "jerotes_magic_absorption";
    }
}