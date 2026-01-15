package com.jerotes.jerotes.entity.Magic.MagicMissile;

import com.jerotes.jerotes.entity.Magic.MagicAboutEntity;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public abstract class BaseMagicMissileEntity extends MagicAboutEntity {
    public BaseMagicMissileEntity(EntityType<? extends MagicAboutEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseMagicMissileEntity(EntityType<? extends MagicAboutEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        super(entityType, d, d2, d3, d4, d5, d6, level);
    }

    public BaseMagicMissileEntity(EntityType<? extends MagicAboutEntity> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        super(entityType, livingEntity, d, d2, d3, level);
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
        this.target = entity;
        this.targetUUID = entity == null ? null : entity.getUUID();
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.targetUUID != null) {
            compoundTag.putUUID("Target", this.targetUUID);
        }
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.hasUUID("Target")) {
            this.targetUUID = compoundTag.getUUID("Target");
        }
    }

    @Override
    public void tick() {
        super.tick();
        //行动
        if (this.getTarget() != null && this.getOwner() instanceof LivingEntity livingEntity && !livingEntity.hasEffect(MobEffects.BLINDNESS) && (!(this.getTarget() instanceof Player) || this.life < this.getMaxLife()/4)) {
            Entity targets = this.getTarget();
            float speed;
            Vec3 vec3 = this.getDeltaMovement();
            if (this.isInWater()) {
                speed = this.getLiquidInertia();
            } else {
                speed = this.getInertia();
            }
            this.lookAt(targets, 360.0f, 360.0f);
            float f = this.getYRot();
            float f2 = this.getXRot();
            float f3 = -Mth.sin(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
            float f4 = -Mth.sin(f2 * 0.017453292f);
            float f5 = Mth.cos(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
            float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
            float f7 = 0.3f;
            this.setDeltaMovement(vec3.multiply(0.5f, 0.5f, 0.5f).add(f3 *= f7 / f6 * 2, f4 *= f7 / f6 * 2, f5 *= f7 / f6 * 2).scale(speed));
        }
    }

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
