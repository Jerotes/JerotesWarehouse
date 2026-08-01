package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PowerWordKillEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {

    public PowerWordKillEntity(EntityType<? extends PowerWordKillEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
        this.noCulling = true;
    }

    public PowerWordKillEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.POWER_WORD_KILL.get(), p_36926_);
        this.setOwnerUUID(p_36932_.getUUID());
        this.fixupDimensions();
        this.noCulling = true;
    }

    public int getBaseSpellLevelDamage() {
        return 9;
    }
    public boolean scaleAboutBoundingBox() {
        return true;
    }
    @Override
    public int getMaxLife() {
        return 60;
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
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.getLifeTick() <= 60) {
                for (int i = 0; i < 4; i++) {
                    double angle = (Math.PI * 2 * (getLifeTick() + i * 10)) / 60;
                    double radius = this.getSize() + 0.5f;
                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;
                    serverLevel.sendParticles(JerotesParticleTypes.POWER_WORD.get(), this.getX() + offsetX, this.getY(0.05f), this.getZ() + offsetZ, 0, 0, 0.12, 0, 1);
                    serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX() + offsetX * 1.25f, this.getY(0.05f), this.getZ() + offsetZ * 1.25f, 0, 0, 0.15, 0, 1);
                }
                for (int i = 0; i < 8; i++) {
                    double angle = (Math.PI * 2 * (getLifeTick() + i * 5)) / 60;
                    double radius = this.getSize() + 2;
                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;
                    serverLevel.sendParticles(JerotesParticleTypes.POWER_WORD.get(), this.getX() + offsetX, this.getY(0.05f), this.getZ() + offsetZ, 0, 0, 0.12, 0, 1);
                    serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX() + offsetX * 1.25f, this.getY(0.05f), this.getZ() + offsetZ * 1.25f, 0, 0, 0.15, 0, 1);
                }
            }
        }
        if (getTarget() instanceof LivingEntity livingEntity) {
            if (getTarget().level() instanceof ServerLevel) {
                if (this.getLifeTick() == 10) {
                    DamageSource damageSource = AttackFind.findDamageType(livingEntity, JerotesDamageTypes.PSYCHIC, this, this.getOwner());
                    if (livingEntity.getHealth() <= 100 && !(livingEntity instanceof Player player && player.isCreative())) {
                        for (int i = 0; i < 40; ++i) {
                            livingEntity.invulnerableTime = 0;
                            livingEntity.hurt(damageSource, Float.MAX_VALUE);
                            livingEntity.setDeltaMovement(0,0,0);
                        }
                        if (livingEntity.isAlive()) {
                            livingEntity.invulnerableTime = 0;
                            livingEntity.kill();
                            livingEntity.setDeltaMovement(0,0,0);
                            livingEntity.invulnerableTime = 0;
                            livingEntity.hurt(this.damageSources().genericKill(), Float.MAX_VALUE);
                            livingEntity.setDeltaMovement(0,0,0);
                            livingEntity.invulnerableTime = 0;
                            livingEntity.hurt(this.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
                            livingEntity.setDeltaMovement(0,0,0);
                        }
                        if (livingEntity.getHealth() > 0) {
                            livingEntity.setHealth(0);
                        }
                        livingEntity.setDeltaMovement(0,0,0);
                    }
                    else {
                        livingEntity.hurt(damageSource,
                               Main.rollDice(12 + 2 * (getSpellLevel() - 9), 12, livingEntity.getRandom())
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
    public int roundLightI() {
        return 0x820019;
    }
    public int roundLightII() {
        return 0xffe1ff;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double d) {
        double d2 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d2)) {
            d2 = 4.0;
        }
        d2 *= 128.0;
        return d < d2 * d2;
    }
}

