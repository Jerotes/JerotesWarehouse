package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ElderGuardian.class)
public abstract class ElderGuardianMixin extends Guardian implements SpellUseEntity {
    protected ElderGuardianMixin(EntityType<? extends Guardian> entityType, Level level) {
        super(entityType, level);
    }

    public int spellLevel = 2;
    @Override
    public int getSpellLevel() {
        return this.spellLevel;
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("SpellLevel", this.spellLevel);
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.spellLevel = compoundTag.getInt("SpellLevel");
    }
}