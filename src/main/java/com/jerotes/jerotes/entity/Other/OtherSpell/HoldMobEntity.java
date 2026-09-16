package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public class HoldMobEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {
    public HoldMobEntity(EntityType<? extends HoldMobEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public HoldMobEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.HOLD_MOB.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 2;
    }
    public boolean scaleAboutBoundingBox() {
        return true;
    }
    @Override
    public int getMaxLife() {
        return 60 * 20 + 15;
    }
    public int getMaxStart() {
        return 10;
    }
    public boolean moveToTarget() {
        return this.getLifeTick() >= 10 || !(this.getTarget() instanceof Player);
    }
    @VisibleForTesting
    public void setSize(float f) {
        float f2 = Mth.clamp(f, 0, 128);
        super.setSize(f2);
    }
    @VisibleForTesting
    public void setTargetHeight(float f) {
        float f2 = Mth.clamp(f, 0, 128);
        super.setTargetHeight(f2);
    }

    @Override
    public void tick() {
        super.tick();
        int time = (getMaxLife() - 10) - 5 - (getLifeTick() - 10);

        if (this.getLifeTick() < 10) {
            return;
        }
        if (this.getLifeTick() == 10) {
            boolean bl = false;
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
            list.removeIf(livingEntity -> livingEntity != this.getTarget());
            for (LivingEntity livingEntity : list) {
                if (livingEntity == null) continue;
                Entity entity = this.getOwner();
                if (livingEntity.isAlive() && !livingEntity.isInvulnerable() && livingEntity != entity) {
                    bl = true;
                }
            }
            if (!bl) {
                this.stop();
            }
        }

        if (!this.level().isClientSide()) {
            if (getTarget() instanceof LivingEntity livingEntity && getTarget().isAlive()) {
                livingEntity.addEffect(new MobEffectInstance(JerotesMobEffects.HOLD_MOB.get(), time, 0, false, false), this.getOwner());
                if (this.getLifeTick() % 120 == 10) {
                    if (EntityAndItemFind.savingSpellThrow(livingEntity, 0, true)){
                        if (!livingEntity.level().isClientSide) {
                            livingEntity.removeEffect(JerotesMobEffects.HOLD_MOB.get());
                        }
                        stop();
                    }
                }
            }
            else {
                stop();
            }
        }
        if (this.getLifeTick() >= getMaxLife() - 5) {
            if (getTarget() instanceof LivingEntity livingEntity) {
                livingEntity.removeEffect(JerotesMobEffects.HOLD_MOB.get());
            }
            this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
            this.setTarget(null);
            this.discard();
        }
    }
    public void stop() {
        if (getTarget() instanceof LivingEntity livingEntity) {
            livingEntity.removeEffect(JerotesMobEffects.PHANTASMAL_KILLER.get());
        }
        this.setOwnerUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        this.setTarget(null);
        if (!this.level().isClientSide()) {
            this.setLifeTick(Math.max(this.getLifeTick(), this.getMaxLife() - 15));
        }
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/hold_mob.png");
    }
    public ResourceLocation getOtherTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/hold_mob_other.png");
    }
    public ResourceLocation getRollTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/hold_mob_chain.png");
    }
}

