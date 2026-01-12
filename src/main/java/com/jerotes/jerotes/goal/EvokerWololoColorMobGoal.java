package com.jerotes.jerotes.goal;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.JerotesEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameRules;

import java.util.List;

public class EvokerWololoColorMobGoal extends EvokerWololoBaseGoal {
    public LivingEntity target;

    public EvokerWololoColorMobGoal(Evoker livingEntity) {
        super(livingEntity);
    }

    @Override
    public boolean canUse() {
        if (this.livingEntity.getTarget() != null) {
            return false;
        }
        if (this.livingEntity.isCastingSpell()) {
            return false;
        }
        if (this.livingEntity.tickCount < this.nextAttackTickCount) {
            return false;
        }
        if (!this.livingEntity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return false;
        }
        List<LivingEntity> list = this.livingEntity.level().getEntitiesOfClass(LivingEntity.class, this.livingEntity.getBoundingBox().inflate(16.0, 4.0, 16.0));
        list.removeIf(livingEntity1 -> !isBlue(livingEntity1)
              || !(livingEntity1 instanceof JerotesEntity) && !MainConfig.AffectsNonThisModEntities
        );
        if (list.isEmpty()) {
            return false;
        }
        this.target = list.get(this.livingEntity.getRandom().nextInt(list.size()));
        return true;
    }

    public static boolean isBlue(LivingEntity livingEntity) {
        CompoundTag compoundTag = new CompoundTag();
        livingEntity.addAdditionalSaveData(compoundTag);
        if (compoundTag.get("Color") != null && compoundTag.contains("Color", Tag.TAG_BYTE)) {
            return DyeColor.byId(compoundTag.getByte("Color")) == DyeColor.BLUE;
        }
        return false;
    }
    public static void setBlue(LivingEntity livingEntity) {
        CompoundTag compoundTag = new CompoundTag();
        livingEntity.addAdditionalSaveData(compoundTag);
        if (compoundTag.get("Color") != null && compoundTag.contains("Color", Tag.TAG_BYTE)) {
            compoundTag.putByte("Color", (byte)DyeColor.RED.getId());
        }
        livingEntity.readAdditionalSaveData(compoundTag);
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.attackWarmupDelay > 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.target = null;
    }

    @Override
    protected void performSpellCasting() {
        LivingEntity target1 = this.target;
        if (target1 != null && target1.isAlive() && isBlue(target1)) {
            setBlue(target1);
        }
    }

    @Override
    protected int getCastWarmupTime() {
        return 40;
    }

    @Override
    protected int getCastingTime() {
        return 60;
    }

    @Override
    protected int getCastingInterval() {
        return 140;
    }

    @Override
    protected SoundEvent getSpellPrepareSound() {
        return SoundEvents.EVOKER_PREPARE_WOLOLO;
    }
}