/*
 * Decompiled with CFR 0.152.
 */
package com.jerotes.jerotes.effect;

import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class BaseMobEffectInstantenous extends MobEffect {
    public BaseMobEffectInstantenous(MobEffectCategory mobEffectCategory, int n) {
        super(mobEffectCategory, n);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    public boolean isDurationEffectTick(int p_19444_, int p_19445_) {
        return p_19444_ >= 1;
    }
}

