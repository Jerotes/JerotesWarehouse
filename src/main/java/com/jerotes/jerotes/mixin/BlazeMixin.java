package com.jerotes.jerotes.mixin;

import com.jerotes.jerotes.entity.Interface.ContractableEntity;
import com.jerotes.jerotes.entity.Interface.SpellUseEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Blaze.JerotesBlazeEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Blaze.class)
public abstract class BlazeMixin extends Monster implements ContractableEntity, SpellUseEntity {
    protected BlazeMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public int spellLevel = 1;
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

    public boolean isCanContract() {
        return this.getType() == EntityType.BLAZE;
    }
    public int contractShouldLevel() {
        return contractShouldBaseLevel();
    }
    public int contractShouldBaseLevel() {
        return 5;
    }
    public boolean contractUse(LivingEntity livingEntity) {
        JerotesBlazeEntity jerotesBlaze = this.convertTo(JerotesEntityType.JEROTES_BLAZE.get(), true);
        ForgeEventFactory.onLivingConvert(this, jerotesBlaze);
        if (jerotesBlaze != null) {
            jerotesBlaze.tame(livingEntity);
            jerotesBlaze.setNotPersistent(false);
            jerotesBlaze.setLifeTick(0);
        }
        return true;
    }
}