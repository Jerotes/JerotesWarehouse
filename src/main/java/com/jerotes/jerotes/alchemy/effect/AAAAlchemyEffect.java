package com.jerotes.jerotes.alchemy.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffect;

public abstract class AAAAlchemyEffect {
    public int level;
    public int time;
    public AAAAlchemyEffect(int level, int time) {
        this.level = level;
        this.time = time;
    }

    public MobEffect getMobEffect() {
        return null;
    }
    public int getLevel() {
        return level;
    }
    public int getTime() {
        return time;
    }
    public int getMobEffectLevel() {
        return 0;
    }
    public int getMobEffectTick() {
        return 30 * 20;
    }
    public int getMobEffectLevel(int level, int craftLevel) {
        return Math.max(0, getMobEffectLevel() + (level - 1) + (int)(craftLevel/2));
    }
    public int getMobEffectTick(int time, int craftLevel) {
        return getMobEffectTick() * time * Math.max(1, craftLevel);
    }
    public String getName() {
        return "jerotes_alchemy";
    }
    public Component getNames() {
        return Component.translatable("alchemy." + this.getName());
    }
    public Component getTooltip() {
        return getNames().copy()
                .append(" ")
                .append(Component.translatable("message.jerotes.effect_level", getMobEffectLevel(getLevel(), 0) + 1))
                .append(" ")
                .append(Component.translatable("message.jerotes.effect_second", Component.literal(StringUtil.formatTickDuration(getMobEffectTick(getTime(), 0)))))
                ;
    }
}