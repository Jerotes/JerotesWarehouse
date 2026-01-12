package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class NauseaAlchemyEffect extends AAAAlchemyEffect {
    public NauseaAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.CONFUSION;
    }

    public String getName() {
        return "jerotes_nausea";
    }
}