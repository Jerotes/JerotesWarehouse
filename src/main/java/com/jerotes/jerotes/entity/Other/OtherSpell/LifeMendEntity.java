package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public class LifeMendEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {

    public LifeMendEntity(EntityType<? extends LifeMendEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public LifeMendEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.LIFE_MEND.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 1;
    }
    public boolean scaleAboutBoundingBox() {
        return true;
    }
    @Override
    public int getMaxLife() {
        return 2000;
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
        if (getTarget() instanceof LivingEntity livingEntity) {
            if (getTarget().level() instanceof ServerLevel serverLevel) {
                if (this.life == 5) {
                    for (int i = 0; i < Mth.clamp(getSize() * 10, 16, 160); ++i) {
                        serverLevel.sendParticles(JerotesParticleTypes.LIFE_MEND.get(),
                                livingEntity.getRandomX(0.8),
                                livingEntity.getY(0.5f),
                                livingEntity.getRandomZ(0.8), 0,
                                (RandomSource.create().nextFloat() - 0.5f) / 3f,
                                0.2f + RandomSource.create().nextFloat() / 5f,
                                (RandomSource.create().nextFloat() - 0.5f) / 3f, 0.25);
                    }
                    if (livingEntity.getMobType() != MobType.UNDEAD && !EntityFactionFind.isMachine(livingEntity) && !EntityFactionFind.isConstruct(livingEntity)) {
                        livingEntity.heal(
                                Main.rollDice(getSpellLevel(), 8, livingEntity.getRandom()) + getBaseSpellLevelDamage()
                        );
                    }
                }
            }
        }
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    }

    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }
}

