package com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicMissile.BaseMagicMissileEntity;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BaseMagicBoltEntity extends MagicAboutEntity {
    public BaseMagicBoltEntity(EntityType<? extends MagicAboutEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseMagicBoltEntity(EntityType<? extends MagicAboutEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        super(entityType, d, d2, d3, d4, d5, d6, level);
    }

    public BaseMagicBoltEntity(EntityType<? extends MagicAboutEntity> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        super(entityType, livingEntity, d, d2, d3, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            Vec3 currentPos = this.position().add(0.0D, 0.1875D, 0.0D);
            Vec3 prevPos = new Vec3(
                    this.xOld,
                    this.yOld,
                    this.zOld
            ).add(0.0D, 0.1875D, 0.0D);
            trailPoints.add(new TrailPoint(prevPos, currentPos, life));
            trailPoints.removeIf(p -> life - p.createdAt > trailMaxLife());
            while (trailPoints.size() > trailMaxPoints()) {
                trailPoints.remove(0);
            }
        }
        if (this.showParticle()) {
            Vec3 vec3 = this.getDeltaMovement();
            double d = this.getX() + vec3.x;
            double d2 = this.getY() + vec3.y;
            double d3 = this.getZ() + vec3.z;
            ParticleOptions particleOptions = this.getTrailParticle();
            if (particleOptions != null) {
                this.level().addParticle(particleOptions, d, d2 + this.getBbHeight() / 2, d3, 0.0, 0.0, 0.0);
            }
        }
    }

    public List<TrailPoint> getTrailPoints() {
        return trailPoints;
    }
    public boolean showParticle() {
        return false;
    }
    protected boolean shouldTail() {
        return false;
    }
    public boolean showBeam() {
        return true;
    }
    public int beamLightI() {
        return 0xbe1d8f;
    }
    public int beamLightII() {
        return 0xbe1d8f;
    }
    public int beamLightIII() {
        return 0x20464b;
    }
    public static class TrailPoint {
        public final Vec3 prevPosition;
        public final Vec3 position;
        public final int createdAt;
        public TrailPoint(Vec3 prevPosition, Vec3 position, int tick) {
            this.prevPosition = prevPosition;
            this.position = position;
            this.createdAt = tick;
        }
        public Vec3 getPosition(float partialTick) {
            return prevPosition.lerp(position, partialTick);
        }
    }
    private final List<TrailPoint> trailPoints = new ArrayList<>();
    public int trailMaxLife() {
        return 6;
    }
    public int trailMaxPoints() {
        return 6;
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
    public ResourceLocation TextureLocation() {
        return new ResourceLocation(JerotesWarehouse.MODID, "textures/entity/projectiles/magic_missile.png");
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
