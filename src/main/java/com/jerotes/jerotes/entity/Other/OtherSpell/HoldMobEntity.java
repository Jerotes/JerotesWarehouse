package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

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
        return 70;
    }
    public int getMaxStart() {
        return 20;
    }
    public boolean moveToTarget() {
        return true;
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
        if (!this.level().isClientSide()) {
            if (getTarget() instanceof LivingEntity livingEntity) {
                if (livingEntity.isAlive() && livingEntity.hasEffect(JerotesMobEffects.HOLD_MOB.get())) {
                    this.life = Math.min(30, this.life);
                    if (this.tickCount % 10 == 0) {
                        this.level().broadcastEntityEvent(this, (byte) 101);
                    }
                } else {
                    this.setTarget(null);
                }
            }
        }
    }
    @Override
    public void handleEntityEvent(byte by) {
        if (by == 101) {
            this.life = Math.min(30, this.life);
        }
        else {
            super.handleEntityEvent(by);
        }
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/hold_mob.png");
    }
    public ResourceLocation getOtherTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/hold_mob_other.png");
    }
    public ResourceLocation getChainTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/other/hold_mob_chain.png");
    }

    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }
}

