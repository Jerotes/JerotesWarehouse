package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class JumpBoostAlchemyEffect extends AAAAlchemyEffect {
    public JumpBoostAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.JUMP;
    }

    public String getName() {
        return "jerotes_jump_boost";
    }
}