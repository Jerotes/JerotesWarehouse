package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class ExplosionAlchemyEffect extends AAAAlchemyEffect {
    public ExplosionAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.EXPLOSION.get();
    }
    public int getMobEffectTick() {
        return 1;
    }
    public int getMobEffectTick(int time, int craftLevel) {
        return 1;
    }

    public String getName() {
        return "jerotes_explosion";
    }
}