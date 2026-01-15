package com.jerotes.jerotes.entity.Magic.Target;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.util.AttackFind;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public abstract class BaseTargetEntity extends MagicAboutEntity {
    private static final EntityDataAccessor<Float> ID_SIZE = SynchedEntityData.defineId(BaseTargetEntity.class, EntityDataSerializers.FLOAT);
    
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
        //法术反制
        if (!isHelp() && entity != this.getOwner() && entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get())
                && livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get()).getAmplifier() + 1 >= this.getSpellLevel() && !(this.getOwner() != null && MainConfig.SameFactionAvoidDamage && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity))) {
            if (!livingEntity.level().isClientSide()) {
                livingEntity.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
            }
            livingEntity.swing(InteractionHand.MAIN_HAND);
            SpellFind.Counterspell(livingEntity);
            return;
        }
        this.target = entity;
        this.targetUUID = entity == null ? null : entity.getUUID();
    }
    @VisibleForTesting
    public void setSize(float f) {
        float f2 = Mth.clamp(f, 0, 256);
        this.entityData.set(ID_SIZE, f2);
        this.reapplyPosition();
        this.refreshDimensions();
    }
    public float getSize() {
        return this.entityData.get(ID_SIZE);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.targetUUID != null) {
            compoundTag.putUUID("Target", this.targetUUID);
        }
        compoundTag.putFloat("Size", this.getSize());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.hasUUID("Target")) {
            this.targetUUID = compoundTag.getUUID("Target");
        }
        this.setSize(compoundTag.getFloat("Size"));
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(ID_SIZE, 1f);
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
        //跟随
        //初始粒子
        if (this.life <= this.getUseLife()) {
            if (this.level() instanceof ServerLevel _level && this.getTrailParticle() != null) {
                if (this.tickCount % 20 == 0) {
                    _level.sendParticles(this.getTrailParticle(), this.getX() + ((this.random.nextDouble() - 0.5d)), this.getY() + ((this.random.nextDouble() - 0.5d) * getSize() * 1), this.getZ() + ((this.random.nextDouble() - 0.5d) * getSize() * 1), 0, 0, 0, 0, 0);
                }
            }
            if (this.getTarget() != null && !(this.getTarget() instanceof Player)) {
                this.moveTo(this.getTarget().getX(), this.getTarget().getY(0.5), this.getTarget().getZ());
            }
        }
        //附近
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
        //法术反制
        if (!isHelp() && livingEntity != this.getOwner() && livingEntity.hasEffect(JerotesMobEffects.COUNTERSPELL.get())
                && livingEntity.getEffect(JerotesMobEffects.COUNTERSPELL.get()).getAmplifier() + 1 >= this.getSpellLevel() && !(this.getOwner() != null && MainConfig.SameFactionAvoidDamage && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity))) {
            if (!livingEntity.level().isClientSide()) {
                livingEntity.removeEffect(JerotesMobEffects.COUNTERSPELL.get());
            }
            livingEntity.swing(InteractionHand.MAIN_HAND);
            SpellFind.Counterspell(livingEntity);
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

    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }
}
