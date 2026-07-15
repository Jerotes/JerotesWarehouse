package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.BaseTargetEntity;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.UUID;

public class OtherSpellBaseEntity extends Entity implements TraceableEntity, OwnableEntity, MagicAbout {
    private static final EntityDataAccessor<Float> ID_SIZE = SynchedEntityData.defineId(OtherSpellBaseEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_HEIGHT = SynchedEntityData.defineId(OtherSpellBaseEntity.class, EntityDataSerializers.FLOAT);

    public OtherSpellBaseEntity(EntityType<? extends OtherSpellBaseEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public int getBaseSpellLevelDamage() {
        return 1;
    }
    public boolean moveToTarget() {
        return false;
    }
    public int getMaxStart() {
        return 40;
    }
    public int getMaxLife() {
        return 80;
    }
    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double d2 = this.getY();
        double d3 = this.getZ();
        super.refreshDimensions();
        this.setPos(d, d2, d3);
    }
    @Override
    protected AABB makeBoundingBox() {
        return AABB.ofSize(new Vec3(this.getX(), this.getY() + this.getSize() / 2, this.getZ()), this.getSize(), this.getSize(), this.getSize());
    }

    public int spellLevelDamage = getBaseSpellLevelDamage();
    public void setSpellLevelDamage(int n) {
        this.spellLevelDamage = n;
    }
    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
    }
    public int life = 0;
    public int start = 0;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    public void setOwner(@Nullable LivingEntity livingEntity) {
        this.owner = livingEntity;
        this.ownerUUID = livingEntity == null ? null : livingEntity.getUUID();
        this.setBoundingBox(this.makeBoundingBox());
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
    public float getSize() {
        return this.entityData.get(ID_SIZE);
    }
    @VisibleForTesting
    public void setSize(float f) {
        float f2 = Mth.clamp(f, 0, 256);
        this.entityData.set(ID_SIZE, f2);
        this.reapplyPosition();
        this.refreshDimensions();
        this.setBoundingBox(this.makeBoundingBox());
    }
    public float getTargetHeight() {
        return this.entityData.get(TARGET_HEIGHT);
    }
    @VisibleForTesting
    public void setTargetHeight(float f) {
        this.entityData.set(TARGET_HEIGHT, f);
    }
    @Nullable
    private Entity target;
    @Nullable
    private UUID targetUUID;
    @Nullable
    public Entity getTarget() {
        Entity entity;
        if (this.target == null && this.targetUUID != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel)this.level()).getEntity(this.targetUUID)) instanceof Entity) {
            this.target = entity;
        }
        return this.target;
    }
    public void setTarget(@Nullable Entity entity) {
        //法术取消
        //event
        JerotesStopSpellEvent event = new JerotesStopSpellEvent(getOwner(), null, this, entity);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        this.target = entity;
        this.targetUUID = entity == null ? null : entity.getUUID();
        this.setBoundingBox(this.makeBoundingBox());
    }
    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
        compoundTag.putInt("Life", this.life);
        compoundTag.putInt("Start", this.start);
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        if (this.targetUUID != null) {
            compoundTag.putUUID("Target", this.targetUUID);
        }
        compoundTag.putFloat("TargetHeight", this.getTargetHeight());
        compoundTag.putFloat("Size", this.getSize());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
        this.life = compoundTag.getInt("Life");
        this.start = compoundTag.getInt("Start");
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        if (compoundTag.hasUUID("Target")) {
            this.targetUUID = compoundTag.getUUID("Target");
        }
        this.setSize(compoundTag.getFloat("Size"));
        this.setTargetHeight(compoundTag.getFloat("TargetHeight"));
        this.setBoundingBox(this.makeBoundingBox());
    }
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(ID_SIZE, 1f);
        this.getEntityData().define(TARGET_HEIGHT, 1f);
    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (ID_SIZE.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }


    @Override
    public void tick() {
        super.tick();
        ++this.life;
        if (this.moveToTarget()) {
            if (this.getTarget() != null) {
                this.moveTo(this.getTarget().getX(), this.getTarget().getY(0.5), this.getTarget().getZ());
            }
        }
        if (this.level().isClientSide()) {
            this.start = Math.min(getMaxStart(), this.start + 1);
        }
        if (this.life >= getMaxLife()) {
            this.discard();
        }
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
}

