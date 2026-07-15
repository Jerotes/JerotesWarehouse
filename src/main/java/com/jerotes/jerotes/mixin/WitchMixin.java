package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.FactionEntity;
import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Witch.class)
public abstract class WitchMixin extends Raider implements FactionEntity {
    protected WitchMixin(EntityType<? extends Raider> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public String getFirstFactionTypeName() {
        return "witch";
    }
    @Override
    public List<String> getFactionTypeUntilTame() {
        List<String> list = new ArrayList<>();
        list.add(getFirstFactionTypeName());
        list.add("raider");
        return list;
    }


    @Inject(method = "getDamageAfterMagicAbsorb", at = @At(value = "HEAD"), cancellable = true)
    protected void getDamageAfterMagicAbsorb(DamageSource damageSource, float amount, CallbackInfoReturnable<Float> cir) {
        if (damageSource.getEntity() instanceof SpellUseEntity ||
                damageSource.getDirectEntity() instanceof SpellUseEntity ||
                damageSource.getEntity() instanceof MagicAbout ||
                damageSource.getDirectEntity() instanceof MagicAbout) {
            cir.setReturnValue(super.getDamageAfterMagicAbsorb(damageSource, amount));
        }
    }
}