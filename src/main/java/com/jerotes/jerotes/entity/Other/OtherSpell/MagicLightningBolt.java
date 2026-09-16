package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public abstract class MagicLightningBolt extends LightningBolt implements MagicAbout {
    public MagicLightningBolt(EntityType<? extends MagicLightningBolt> entityType, Level level) {
        super(entityType, level);
    }
    public int spellLevelDamage = 1;
    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
    }
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
    }

}
