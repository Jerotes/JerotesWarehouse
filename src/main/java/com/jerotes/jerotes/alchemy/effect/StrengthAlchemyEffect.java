package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class StrengthAlchemyEffect extends AAAAlchemyEffect {
    public StrengthAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.DAMAGE_BOOST;
    }

    public String getName() {
        return "jerotes_strength";
    }
}