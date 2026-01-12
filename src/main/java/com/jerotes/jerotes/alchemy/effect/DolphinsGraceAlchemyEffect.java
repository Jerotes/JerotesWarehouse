package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class DolphinsGraceAlchemyEffect extends AAAAlchemyEffect {
    public DolphinsGraceAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.DOLPHINS_GRACE;
    }

    public String getName() {
        return "jerotes_dolphins_grace";
    }
}