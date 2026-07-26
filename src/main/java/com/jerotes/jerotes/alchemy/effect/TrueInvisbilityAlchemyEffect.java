package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;

public class TrueInvisbilityAlchemyEffect extends AAAAlchemyEffect {
    public TrueInvisbilityAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.INVISIBLE_PASSAGE.get();
    }

    public String getName() {
        return "jerotes_true_invisbility";
    }
}