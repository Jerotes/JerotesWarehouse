package com.jerotes.jerotes.entity.Shoot.Magic.Ray;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

public abstract class BaseRayEntity extends MagicAboutEntity {
    public static final EntityDataAccessor<Boolean> USEFUL = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.BOOLEAN);

    public BaseRayEntity(EntityType<? extends MagicAboutEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseRayEntity(EntityType<? extends MagicAboutEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        super(entityType, d, d2, d3, d4, d5, d6, level);
    }

    public BaseRayEntity(EntityType<? extends MagicAboutEntity> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        super(entityType, livingEntity, d, d2, d3, level);
    }

    public double summonTod = 1;
    public double summonTod2 = 1;
    public double summonTod3 = 1;
    public double xSpeedRay = 0;
    public double ySpeedRay = 0;
    public double zSpeedRay = 0;
    public boolean isUseful() {
        return this.getEntityData().get(USEFUL);
    }
    public void setUseful(boolean bl) {
        this.getEntityData().set(USEFUL, bl);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("SpellLevelMainEffectTime", this.spellLevelMainEffectTime);
        compoundTag.putInt("SpellLevelMainEffectLevel", this.spellLevelMainEffectLevel);
        compoundTag.putInt("Life", life);
        compoundTag.putDouble("SummonTod", summonTod);
        compoundTag.putDouble("SummonTod2", summonTod2);
        compoundTag.putDouble("SummonTod3", summonTod3);
        compoundTag.putDouble("XSpeedRay", xSpeedRay);
        compoundTag.putDouble("YSpeedRay", ySpeedRay);
        compoundTag.putDouble("ZSpeedRay", zSpeedRay);
        compoundTag.putBoolean("IsUseful", this.isUseful());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.spellLevelMainEffectTime = compoundTag.getInt("SpellLevelMainEffectTime");
        this.spellLevelMainEffectLevel = compoundTag.getInt("SpellLevelMainEffectLevel");
        this.life = compoundTag.getInt("Life");
        this.summonTod = compoundTag.getDouble("SummonTod");
        this.summonTod2 = compoundTag.getDouble("SummonTod2");
        this.summonTod3 = compoundTag.getDouble("SummonTod3");
        this.xSpeedRay = compoundTag.getDouble("XSpeedRay");
        this.ySpeedRay = compoundTag.getDouble("YSpeedRay");
        this.zSpeedRay = compoundTag.getDouble("ZSpeedRay");
        this.setUseful(compoundTag.getBoolean("IsUseful"));
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(USEFUL, true);
    }
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (USEFUL.equals(entityDataAccessor)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public void tick() {
        if (this.life >= this.getMaxLife()) {
            this.setUseful(false);
        }
        super.tick();
        if (this.getOwner() != null && this.isUseful()) {
            BaseRayEntity ray = this.getRay();
            ray.setOwner(this.getOwner());
            ray.setPos(this.getX(), this.getY(), this.getZ());
            ray.setRot(this.getYRot(), this.getXRot());
            ray.setDeltaMovement(this.getDeltaMovement());
            ray.setUseful(true);
            ray.life = this.life;
            ray.setLastHurt(this.getLastHurt());
            List<UUID> listTag = this.getHurtUUIDs();
            for (UUID uuid : listTag) {
                ray.addHurtUUID(uuid);
            }
            this.level().addFreshEntity(ray);

            this.setUseful(false);
            xSpeedRay = this.getDeltaMovement().x;
            ySpeedRay = this.getDeltaMovement().y;
            zSpeedRay = this.getDeltaMovement().z;
            if (life > 5) {
                if (this.getTrailParticle() != null) {
                    for (int i = -1; i < 5; ++i) {
                        this.level().addParticle(this.getTrailParticle(), this.getX() + this.xSpeedRay * i * 0.2, this.getY() + this.ySpeedRay * i * 0.2+ this.getBbHeight() / 2, this.getZ() + this.zSpeedRay * i * 0.2, 0.0d, 0.0d, 0.0d);
                    }
                }
            }
        } else {
            this.setDeltaMovement(0d, 0d, 0d);
            if (this.getTrailParticle() != null) {
                for (int i = -1; i < 5; ++i) {
                    this.level().addParticle(this.getTrailParticle(), this.getX() + this.xSpeedRay * i * 0.2, this.getY() + this.ySpeedRay * i * 0.2 + this.getBbHeight() / 2, this.getZ() + this.zSpeedRay * i * 0.2, 0.0d, 0.0d, 0.0d);
                }
            }
        }
        if (this.getTrailParticle() != null) {
            if (life > 1) {
                this.level().addParticle(this.getTrailParticle(), this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(), 0.0d, 0.0d, 0.0d);
            }
        }
    }

    public abstract BaseRayEntity getRay();

    @Override
    public int getMaxLife() {
        return 40;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return JerotesParticleTypes.NULL.get();
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemStack = this.getItemRaw();
        if (this.isUseful()) {
            return itemStack.isEmpty() ? new ItemStack(Items.AIR) : itemStack;
        }
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
