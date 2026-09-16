package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;

public class ColorMagicWindEntity extends OtherSpellBaseEntity implements TraceableEntity, OwnableEntity, MagicAbout {
    private static final EntityDataAccessor<Integer> DATA_COLOR_I = SynchedEntityData.defineId(ColorMagicWindEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR_II = SynchedEntityData.defineId(ColorMagicWindEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR_III = SynchedEntityData.defineId(ColorMagicWindEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR_IV = SynchedEntityData.defineId(ColorMagicWindEntity.class, EntityDataSerializers.INT);

    public ColorMagicWindEntity(EntityType<? extends ColorMagicWindEntity> entityType, Level level) {
        super(entityType, level);
        this.fixupDimensions();
    }

    public ColorMagicWindEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.COLOR_MAGIC_WIND.get(), p_36926_);
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
        return 120;
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
    public void setColorI(int color) { this.getEntityData().set(DATA_COLOR_I, color); }
    public int getColorI() { return this.getEntityData().get(DATA_COLOR_I); }
    public void setColorII(int color) {
        this.getEntityData().set(DATA_COLOR_II, color);
    }
    public int getColorII() {
        return this.getEntityData().get(DATA_COLOR_II);
    }

    public void setColorIII(int color) {
        this.getEntityData().set(DATA_COLOR_III, color);
    }
    public int getColorIII() {
        return this.getEntityData().get(DATA_COLOR_III);
    }

    public void setColorIV(int color) {
        this.getEntityData().set(DATA_COLOR_IV, color);
    }
    public int getColorIV() {
        return this.getEntityData().get(DATA_COLOR_IV);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ColorI", this.getColorI());
        compound.putInt("ColorII", this.getColorII());
        compound.putInt("ColorIII", this.getColorIII());
        compound.putInt("ColorIV", this.getColorIV());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ColorI")) this.setColorI(compound.getInt("ColorI"));
        if (compound.contains("ColorII")) this.setColorII(compound.getInt("ColorII"));
        if (compound.contains("ColorIII")) this.setColorIII(compound.getInt("ColorIII"));
        if (compound.contains("ColorIV")) this.setColorIV(compound.getInt("ColorIV"));
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_COLOR_I, 0xFFFF7F7F);
        this.getEntityData().define(DATA_COLOR_II, 0xFF7FBFFF);
        this.getEntityData().define(DATA_COLOR_III, 0xFFFF7F7F);
        this.getEntityData().define(DATA_COLOR_IV, 0xFF7FBFFF);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/beam/ray.png");
    }
}

