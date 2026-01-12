package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class DeadlyPoisonAlchemyEffect extends AAAAlchemyEffect {
    public DeadlyPoisonAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.DEADLY_POISON.get();
    }

    public String getName() {
        return "jerotes_deadly_poison";
    }
}