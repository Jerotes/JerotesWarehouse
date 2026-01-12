package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class BleedingAlchemyEffect extends AAAAlchemyEffect {
    public BleedingAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.BLEEDING.get();
    }

    public String getName() {
        return "jerotes_bleeding";
    }
}