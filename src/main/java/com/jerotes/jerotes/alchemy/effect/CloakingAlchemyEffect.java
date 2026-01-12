package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class CloakingAlchemyEffect extends AAAAlchemyEffect {
    public CloakingAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.CLOAKING.get();
    }

    public String getName() {
        return "jerotes_cloaking";
    }
}