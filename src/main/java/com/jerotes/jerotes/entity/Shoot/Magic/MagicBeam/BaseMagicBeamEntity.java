package com.jerotes.jerotes.entity.Shoot.Magic.MagicBeam;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Other.Beam.BaseBeamEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BaseMagicBeamEntity extends BaseBeamEntity implements MagicAbout {

    public BaseMagicBeamEntity(EntityType<? extends BaseBeamEntity> entityType, Level level) {
        super(entityType, level);
    }
    public BaseMagicBeamEntity(EntityType<? extends BaseBeamEntity> entityType, Level level, int spellLevelDamage) {
        super(entityType, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    protected void customHurt(Entity entity) {
        if (this.getOwner() != null && entity instanceof LivingEntity livingEntity && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity)) {
            return;
        }
        //法术反制
        if (!isHelp() && entity != this.getOwner() && entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get())
                && livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get()).getAmplifier() + 1 >= this.getSpellLevel() && !(this.getOwner() != null && MainConfig.SameFactionAvoidDamage && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity))) {
            if (!livingEntity.level().isClientSide()) {
                livingEntity.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
            }
            livingEntity.swing(InteractionHand.MAIN_HAND);
            SpellFind.Counterspell(livingEntity);
            return;
        }
        customHurtMagic(entity);
    }

    protected void customHurtMagic(Entity entity) {
    }
    
    @Override
    public int getSpellLevel() {
        return spellLevelDamage;
    }
    //伤害
    public int spellLevelDamage = 1;
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
    }
}