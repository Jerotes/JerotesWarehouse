package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class InstantDamageAlchemyEffect extends AAAAlchemyEffect {
    public InstantDamageAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.HARM;
    }
    public int getMobEffectTick() {
        return 1;
    }
    public int getMobEffectTick(int time, int craftLevel) {
        return 1;
    }

    public String getName() {
        return "jerotes_instant_damage";
    }
}