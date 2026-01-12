package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;

public class AnesthetizedAlchemyEffect extends AAAAlchemyEffect {
    public AnesthetizedAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.ANESTHETIZED.get();
    }

    public String getName() {
        return "jerotes_anesthetized";
    }

    public int getMobEffectTick() {
        return 120 * 20;
    }
    public int getMobEffectLevel(int level, int craftLevel) {
        return 0;
    }
}