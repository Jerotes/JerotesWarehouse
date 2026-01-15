/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.entity.Interface.TameMobEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

public class JerotesTameMobNonTameRandomTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final TameMobEntity tamableMob;

    public JerotesTameMobNonTameRandomTargetGoal(TameMobEntity tameMob, Class<T> class_, boolean bl, @Nullable Predicate<LivingEntity> predicate) {
        super((Mob) tameMob, class_, 10, bl, false, predicate);
        this.tamableMob = tameMob;
    }

    @Override
    public boolean canUse() {
        return !this.tamableMob.isTame() && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetConditions.test(this.mob, Objects.requireNonNull(this.target));
    }
}

