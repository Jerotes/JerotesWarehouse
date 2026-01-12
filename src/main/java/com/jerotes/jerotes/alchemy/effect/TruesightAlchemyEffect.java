package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;

public class TruesightAlchemyEffect extends AAAAlchemyEffect {
    public TruesightAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.TRUESIGHT.get();
    }

    public String getName() {
        return "jerotes_truesight";
    }
}