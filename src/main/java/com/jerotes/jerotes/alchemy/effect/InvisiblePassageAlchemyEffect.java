package com.jerotes.jerotes.alchemy.effect;

import com.jerotes.jerotes.init.JerotesMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class InvisiblePassageAlchemyEffect extends AAAAlchemyEffect {
    public InvisiblePassageAlchemyEffect(int level, int time) {
        super(level, time);
    }

    public MobEffect getMobEffect() {
        return JerotesMobEffects.INVISIBLE_PASSAGE.get();
    }

    public String getName() {
        return "jerotes_invisible_passage";
    }
}