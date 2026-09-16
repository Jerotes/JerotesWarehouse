package com.jerotes.jerotes.entity.Shoot.Magic.MagicBeam;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Other.Beam.BaseBeamEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class BaseMagicBeamEntity extends BaseBeamEntity implements MagicAbout {

    public BaseMagicBeamEntity(EntityType<? extends BaseBeamEntity> entityType, Level level) {
        super(entityType, level);
    }
    public BaseMagicBeamEntity(EntityType<? extends BaseBeamEntity> entityType, Level level, int spellLevelDamage) {
        super(entityType, level);
        this.spellLevelDamage = spellLevelDamage;
    }
    public BaseMagicBeamEntity(EntityType<? extends BaseBeamEntity> entityType, Level level, int spellLevelDamage, int spellLevelSubcategory) {
        super(entityType, level);
        this.spellLevelDamage = spellLevelDamage;
        this.spellLevelSubcategory = spellLevelSubcategory;
    }

    public void customHurt(Entity entity) {
        if (this.getOwner() != null && entity instanceof LivingEntity livingEntity && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity, false)) {
            return;
        }
        //法术取消
        //event
        JerotesStopSpellEvent event = new JerotesStopSpellEvent(entity, null, this, getOwner());
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        entity.invulnerableTime = 0;
        customHurtMagic(entity);
    }

    public void customHurtMagic(Entity entity) {
    }

    @Override
    public int getSpellLevel() {
        return spellLevelDamage;
    }
    //伤害
    public int spellLevelDamage = 1;
    public int spellLevelSubcategory = 0;
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
        compoundTag.putInt("SpellLevelSubcategory", this.spellLevelSubcategory);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
        this.spellLevelSubcategory = compoundTag.getInt("SpellLevelSubcategory");
    }
}