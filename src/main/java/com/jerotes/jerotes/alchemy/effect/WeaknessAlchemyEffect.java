package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class WeaknessAlchemyEffect extends AAAAlchemyEffect {
    public WeaknessAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.WEAKNESS;
    }

    public String getName() {
        return "jerotes_weakness";
    }
}