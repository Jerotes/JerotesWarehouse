package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class InstantHealthAlchemyEffect extends AAAAlchemyEffect {
    public InstantHealthAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.HEAL;
    }
    public int getMobEffectTick() {
        return 1;
    }
    public int getMobEffectTick(int time, int craftLevel) {
        return 1;
    }

    public String getName() {
        return "jerotes_instant_health";
    }
}