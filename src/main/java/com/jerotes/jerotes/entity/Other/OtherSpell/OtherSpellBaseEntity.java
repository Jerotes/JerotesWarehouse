package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OtherSpellBaseEntity extends Entity implements TraceableEntity, OwnableEntity, MagicAbout {
    public static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(OtherSpellBaseEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID_ID = SynchedEntityData.defineId(OtherSpellBaseEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Float> ID_SIZE = SynchedEntityData.defineId(OtherSpellBaseEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_HEIGHT = SynchedEntityData.defineId(OtherSpellBaseEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Optional<UUID>> DATA_TARGET_UUID = SynchedEntityData.defineId(OtherSpellBaseEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(OtherSpellBaseEntity.class, EntityDataSerializers.INT);

    public OtherSpellBaseEntity(EntityType<? extends OtherSpellBaseEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public RandomSource getRandom() {
        return this.random;
    }
    public int getBaseSpellLevelDamage() {
        return 1;
    }
    public boolean moveToTarget() {
        return false;
    }
    public boolean shouldPush() {
        return false;
    }
    public boolean scaleAboutBoundingBox() {
        return false;
    }
    public int getMaxStart() {
        return 40;
    }
    public int getMaxLife() {
        return 80;
    }
    protected void doPush(Entity entity) {
        if (shouldPush()) {
            entity.push(this);
        }
    }
    protected void pushEntities() {
        if (shouldPush()) {
            if (this.level().isClientSide()) {
                this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::doPush);
            } else {
                List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
                if (!list.isEmpty()) {
                    for (Entity entity : list) {
                        this.doPush(entity);
                    }
                }
            }
        }
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
        if (!scaleAboutBoundingBox())
            return super.makeBoundingBox();
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
    public boolean isOwnedBy(LivingEntity livingEntity) {
        return livingEntity == this.getOwner();
    }
    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNER_UUID_ID, Optional.ofNullable(uuid));
    }
    public void setOwner(LivingEntity livingEntity) {
        setOwnerUUID(livingEntity.getUUID());
    }
    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID_ID).orElse((UUID)null);
    }
    public int getOwnerId(){
        return this.entityData.get(DATA_OWNER_ID);
    }
    public void setOwnerId(int id){
        this.entityData.set(DATA_OWNER_ID, id);
    }
    @Override
    public Team getTeam() {
        if (this.getOwner() != null) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
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
    @Override
    public LivingEntity getOwner() {
        if (!this.level().isClientSide){
            UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : Main.getLivingEntityByUUID(this.level(), uuid);
        } else {
            int id = this.getOwnerId();
            return id <= -1 ? null : this.level().getEntity(this.getOwnerId()) instanceof LivingEntity living ? living : null;
        }
    }
    @Nullable
    public Entity getTarget() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getTargetUUID();
            return uuid == null ? null : Main.getLivingEntityByUUID(this.level(), uuid);
        } else {
            int id = this.getTargetId();
            return id <= -1 ? null : this.level().getEntity(id);
        }
    }

    public void setTarget(@Nullable Entity entity) {
        JerotesStopSpellEvent event = new JerotesStopSpellEvent(getOwner(), null, this, entity);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        if (entity != null) {
            this.setTargetUUID(entity.getUUID());
            if (!this.level().isClientSide) {
                this.setTargetId(entity.getId());
            }
        } else {
            this.setTargetUUID(null);
            this.setTargetId(-1);
        }
    }

    @Nullable
    public UUID getTargetUUID() {
        return this.entityData.get(DATA_TARGET_UUID).orElse(null);
    }
    public void setTargetUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_TARGET_UUID, Optional.ofNullable(uuid));
    }
    public int getTargetId() {
        return this.entityData.get(DATA_TARGET_ID);
    }
    public void setTargetId(int id) {
        this.entityData.set(DATA_TARGET_ID, id);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
        compoundTag.putInt("Life", this.life);
        compoundTag.putInt("Start", this.start);
        if (this.getOwnerUUID() != null) {
            compoundTag.putUUID("Owner", this.getOwnerUUID());
        }
        if (this.getTargetUUID() != null) {
            compoundTag.putUUID("Target", this.getTargetUUID());
        }
        compoundTag.putFloat("TargetHeight", this.getTargetHeight());
        compoundTag.putFloat("Size", this.getSize());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
        this.life = compoundTag.getInt("Life");
        this.start = compoundTag.getInt("Start");
        UUID uuid;
        if (compoundTag.hasUUID("Owner")) {
            uuid = compoundTag.getUUID("Owner");
        } else {
            String string = compoundTag.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), string);
        }
        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable throwable) {
            }
        }
        if (compoundTag.hasUUID("Target")) {
            uuid = compoundTag.getUUID("Target");
        } else {
            String string = compoundTag.getString("Target");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), string);
        }
        if (uuid != null) {
            try {
                this.setTargetUUID(uuid);
            } catch (Throwable throwable) {
            }
        }
        this.setSize(compoundTag.getFloat("Size"));
        this.setTargetHeight(compoundTag.getFloat("TargetHeight"));
        this.setBoundingBox(this.makeBoundingBox());
    }
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(ID_SIZE, 1f);
        this.getEntityData().define(TARGET_HEIGHT, 1f);
        this.getEntityData().define(DATA_OWNER_ID, -1);
        this.getEntityData().define(DATA_OWNER_UUID_ID, Optional.empty());
        this.getEntityData().define(DATA_TARGET_UUID, Optional.empty());
        this.getEntityData().define(DATA_TARGET_ID, -1);
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
        if (this.getOwner() != null && this.getOwnerId() == -1) {
            this.setOwnerId(this.getOwner().getId());
        }
        if (getTarget() != null && this.getTargetId() == -1) {
            this.setTargetId(getTarget().getId());
        }
        if (this.shouldPush()) {
            this.pushEntities();
        }
        if (getTarget() != null && !this.getTarget().isAlive()) {
            this.setTarget(null);
            this.setTargetId(-1);
        }
        if (this.moveToTarget()) {
            if (this.getTarget() != null) {
                this.setPos(this.getTarget().getX(), this.getTarget().getY(0), this.getTarget().getZ());
            }
        }
        if (this.level().isClientSide()) {
            this.start = Math.min(getMaxStart(), this.start + 1);
        }
        if (this.life >= getMaxLife()) {
            if (this instanceof ConcentrationSpellEntity concentrationSpellEntity) {
                concentrationSpellEntity.endConcentration();
            } else {
                this.discard();
            }
        }

        if (this.getOwner() != null) {
            if (this instanceof ConcentrationSpellEntity concentrationSpellEntity) {
                if (this.getOwner().level() != this.level()) {
                    concentrationSpellEntity.endConcentration();
                }
            }
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