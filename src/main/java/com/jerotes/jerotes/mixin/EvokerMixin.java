package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.EvokerAbout;
import com.jerotes.jerotes.goal.EvokerWololoColorMobGoal;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Evoker.class)
public abstract class EvokerMixin extends SpellcasterIllager implements EvokerAbout {
    @Shadow protected abstract @NotNull SoundEvent getCastingSoundEvent();

    protected EvokerMixin(EntityType<? extends SpellcasterIllager> entityType, Level level) {
        super(entityType, level);
    }
    @Inject(method = "registerGoals", at = @At("TAIL"))
    protected void registerGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(6, new EvokerWololoColorMobGoal((Evoker) (Object)this));
    }

    @Override
    public void jerotes1_20_4$setEvokerAboutSpellCastingTickCount(int n) {
        this.spellCastingTickCount = n;
    }
    @Override
    public SoundEvent jerotes1_20_4$getEvokerAboutCastingSoundEvent() {
        return getCastingSoundEvent();
    }
    @Override
    public void jerotes1_20_4$spellEvokerAboutSpell() {
        this.setIsCastingSpell(IllagerSpell.WOLOLO);
    }
}