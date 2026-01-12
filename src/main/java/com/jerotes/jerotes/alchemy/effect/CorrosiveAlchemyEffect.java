package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class CorrosiveAlchemyEffect extends AAAAlchemyEffect {
    public CorrosiveAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.CORROSIVE.get();
    }

    public String getName() {
        return "jerotes_corrosive";
    }
}