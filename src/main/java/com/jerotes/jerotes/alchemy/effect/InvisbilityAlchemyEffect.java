package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class InvisbilityAlchemyEffect extends AAAAlchemyEffect {
    public InvisbilityAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.INVISIBILITY;
    }

    public String getName() {
        return "jerotes_invisbility";
    }
}