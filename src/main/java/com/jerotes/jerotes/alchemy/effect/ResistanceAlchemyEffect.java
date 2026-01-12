package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class ResistanceAlchemyEffect extends AAAAlchemyEffect {
    public ResistanceAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.DAMAGE_RESISTANCE;
    }

    public String getName() {
        return "jerotes_resistance";
    }
}