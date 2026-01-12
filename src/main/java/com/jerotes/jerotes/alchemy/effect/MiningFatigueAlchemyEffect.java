package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class MiningFatigueAlchemyEffect extends AAAAlchemyEffect {
    public MiningFatigueAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return MobEffects.DIG_SLOWDOWN;
    }

    public String getName() {
        return "jerotes_mining_fatigue";
    }
}