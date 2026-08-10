package com.jerotes.jerotes.entity.Shoot.Magic.Target;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseTargetEntity extends MagicAboutEntity {
    private static final EntityDataAccessor<Float> ID_SIZE = SynchedEntityData.defineId(BaseTargetEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TARGET_HEIGHT = SynchedEntityData.defineId(BaseTargetEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_TARGET_UUID = SynchedEntityData.defineId(BaseTargetEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(BaseTargetEntity.class, EntityDataSerializers.INT);

    public BaseTargetEntity(EntityType<? extends MagicAboutEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public BaseTargetEntity(EntityType<? extends MagicAboutEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        super(entityType, d, d2, d3, d4, d5, d6, level);
        this.fixupDimensions();
    }

    public BaseTargetEntity(EntityType<? extends MagicAboutEntity> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        super(entityType, livingEntity, d, d2, d3, level);
        this.fixupDimensions();
    }

    @Override
    public void refreshDimensions() {
        double d = this.getX();
        double d2 = this.getY();
        double d3 = this.getZ();
        super.refreshDimensions();
        this.setPos(d, d2, d3);
    }

    @Nullable
    public Entity getTarget() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getTargetUUID();
            return uuid == null ? null : Main.getEntityByUUID(this.level(), uuid);
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
            this.entityData.set(TARGET_HEIGHT, entity.getBbHeight());
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

    public float getSize() {
        return this.entityData.get(ID_SIZE);
    }

    @VisibleForTesting
    public void setSize(float f) {
        float f2 = Mth.clamp(f, 0, 256);
        this.entityData.set(ID_SIZE, f2);
        this.reapplyPosition();
        this.refreshDimensions();
    }

    public float getTargetHeight() {
        return this.entityData.get(TARGET_HEIGHT);
    }

    @VisibleForTesting
    public void setTargetHeight(float f) {
        this.entityData.set(TARGET_HEIGHT, f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.getTargetUUID() != null) {
            compoundTag.putUUID("Target", this.getTargetUUID());
        }
        compoundTag.putFloat("Size", this.getSize());
        compoundTag.putFloat("TargetHeight", this.getTargetHeight());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.hasUUID("Target")) {
            this.setTargetUUID(compoundTag.getUUID("Target"));
        }
        this.setSize(compoundTag.getFloat("Size"));
        this.setTargetHeight(compoundTag.getFloat("TargetHeight"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(ID_SIZE, 1f);
        this.getEntityData().define(TARGET_HEIGHT, 1f);
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

        if (!this.level().isClientSide) {
            if (this.getTarget() != null && this.getTargetId() == -1) {
                this.setTargetId(this.getTarget().getId());
            }
        }
        if (this.getTarget() != null && !this.getTarget().isAlive()) {
            this.setTarget(null);
        }
        if (this.life <= this.getMaxLife()) {
            if (this.level() instanceof ServerLevel _level && this.getTrailParticle() != null) {
                if (this.tickCount % 20 == 0) {
                    _level.sendParticles(this.getTrailParticle(), this.getX() + ((this.random.nextDouble() - 0.5d)), this.getY() + ((this.random.nextDouble() - 0.5d) * getSize() * 1), this.getZ() + ((this.random.nextDouble() - 0.5d) * getSize() * 1), 0, 0, 0, 0, 0);
                }
            }
            if (this.getTarget() != null && !(this.getTarget() instanceof Player)) {
                this.moveTo(this.getTarget().getX(), this.getTarget().getY(0.5), this.getTarget().getZ());
            }
        }

        if (this.life == this.getUseLife()) {
            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2, 0.0, 0.2));
            list.removeIf(livingEntity -> livingEntity != this.getTarget());
            for (LivingEntity livingEntity : list) {
                if (livingEntity == null) continue;
                Entity entity = this.getOwner();
                if (livingEntity.isAlive() && !livingEntity.isInvulnerable() && livingEntity != entity) {
                    this.useMagicTo(livingEntity);
                }
            }
        }
    }

    public void useMagicTo(LivingEntity livingEntity) {
        JerotesStopSpellEvent event = new JerotesStopSpellEvent(getOwner(), null, this, livingEntity);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
    }

    public abstract int getUseLife();

    @Override
    public int getMaxLife() {
        return 20;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.NULL.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        return itemStack.isEmpty() ? new ItemStack(Items.AIR) : itemStack;
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }

    protected float getLiquidInertia() {
        return 1.0f;
    }

    public boolean showRound() {
        return true;
    }

    public int roundLightI() {
        return 0x64a63a;
    }

    public int roundLightII() {
        return 0xbfe970;
    }
}