package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class JerotesEvokerFangEntity extends Entity implements TraceableEntity, OwnableEntity, MagicAbout {
    private int warmupDelayTicks;
    private int spellLevelDamage = 3;
    private boolean sentSpikeEvent;
    private int lifeTicks = 22;
    private boolean clientSideAttackStarted;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public JerotesEvokerFangEntity(EntityType<? extends JerotesEvokerFangEntity> entityType, Level level) {
        super(entityType, level);
    }

    public JerotesEvokerFangEntity(Level p_36926_, double p_36927_, double p_36928_, double p_36929_, float p_36930_, int p_36931_, LivingEntity p_36932_) {
        this(JerotesEntityType.JEROTES_EVOKER_FANGS.get(), p_36926_);
        this.warmupDelayTicks = p_36931_;
        this.setOwner(p_36932_);
        this.setYRot(p_36930_ * (180F / (float)Math.PI));
        this.setPos(p_36927_, p_36928_, p_36929_);
    }


    @Override
    protected void defineSynchedData() {
    }

    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUUID = livingEntity == null ? null : livingEntity.getUUID();
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        Entity entity;
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID)) instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
        }
        return this.owner;
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (this.getOwner() != null) {
            LivingEntity livingEntity = this.getOwner();
            if (entity == livingEntity) {
                return true;
            }
            if (livingEntity != null) {
                return livingEntity.isAlliedTo(entity);
            }
        }
        return super.isAlliedTo(entity);
    }
    @Override
    //1.20.4↑//
    //public PlayerTeam getTeam() {
    //1.20.1//
    public Team getTeam() {
        LivingEntity livingEntity;
        if (this.getOwner() != null && (livingEntity = this.getOwner()) != null) {
            return livingEntity.getTeam();
        }
        return super.getTeam();
    }
    public void setSpellLevelDamage(int n) {
        this.spellLevelDamage = n;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.warmupDelayTicks = compoundTag.getInt("Warmup");
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Warmup", this.warmupDelayTicks);
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.clientSideAttackStarted) {
                --this.lifeTicks;
                if (this.lifeTicks == 14) {
                    for(int i = 0; i < 12; ++i) {
                        double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                        double d1 = this.getY() + 0.05D + this.random.nextDouble();
                        double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                        double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                        double d4 = 0.3D + this.random.nextDouble() * 0.3D;
                        double d5 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                        this.level().addParticle(ParticleTypes.CRIT, d0, d1 + 1.0D, d2, d3, d4, d5);
                    }
                }
            }
        } else if (--this.warmupDelayTicks < 0) {
            if (this.warmupDelayTicks == -8) {
                List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
                for (LivingEntity livingEntity : list) {
                    this.dealDamageTo(livingEntity);
                }
            }
            if (!this.sentSpikeEvent) {
                this.level().broadcastEntityEvent(this, (byte)4);
                this.sentSpikeEvent = true;
            }
            if (--this.lifeTicks < 0) {
                this.discard();
            }
        }
    }

    private void dealDamageTo(LivingEntity livingEntity) {
        LivingEntity livingEntity2 = this.getOwner();
        if (!livingEntity.isAlive() || livingEntity.isInvulnerable() || livingEntity == livingEntity2) {
            return;
        }
        if (livingEntity2 == null) {
            boolean bl = livingEntity.hurt(this.damageSources().magic(), 4f + this.spellLevelDamage * 2f);
        }
        else {
            if (AttackFind.FindCanNotAttack(livingEntity2, livingEntity)) {
                return;
            }
            DamageSource damageSource = AttackFind.findDamageType(this, DamageTypes.INDIRECT_MAGIC, this, livingEntity2);
            boolean bl = livingEntity.hurt(damageSource, 4f + this.spellLevelDamage * 2f);
        }
    }

    @Override
    public void handleEntityEvent(byte by) {
        super.handleEntityEvent(by);
        if (by == 4) {
            this.clientSideAttackStarted = true;
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
            }
        }
    }

    public float getAnimationProgress(float f) {
        if (!this.clientSideAttackStarted) {
            return 0.0f;
        }
        int n = this.lifeTicks - 2;
        if (n <= 0) {
            return 1.0f;
        }
        return 1.0f - ((float)n - f) / 20.0f;
    }

    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }
}

