package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class HasteAlchemyEffect extends AAAAlchemyEffect {
    public HasteAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.DIG_SPEED;
    }

    public String getName() {
        return "jerotes_haste";
    }
}