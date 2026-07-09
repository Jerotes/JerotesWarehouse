package com.jerotes.jerotes.entity.Other.ShootTargetSpell;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import com.jerotes.jerotes.util.ParticlesUse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FireballEntity extends Entity implements TraceableEntity, OwnableEntity, MagicAbout {
    private static final EntityDataAccessor<Float> START_X = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Y = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Z = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> NOW_X = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> NOW_Y = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> NOW_Z = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LAST_X = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LAST_Y = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LAST_Z = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFE_BOMB = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.INT);
    public FireballEntity(EntityType<? extends FireballEntity> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    public FireballEntity(Level p_36926_, LivingEntity p_36932_) {
        this(JerotesEntityType.FIREBALL.get(), p_36926_);
        this.setOwner(p_36932_);
        this.noCulling = true;
    }

    public int spellLevelDamage = 3;
    public void setSpellLevelDamage(int n) {
        this.spellLevelDamage = n;
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
    public int getLifeBomb() { return this.entityData.get(LIFE_BOMB); }
    public void setLifeBomb(int n) {
        this.entityData.set(LIFE_BOMB, n);
    }
    public void setStartPos(Vec3 vec3) {
        this.entityData.set(START_X, (float) vec3.x);
        this.entityData.set(START_Y, (float) vec3.y);
        this.entityData.set(START_Z, (float) vec3.z);
    }
    public float getStartX() { return this.entityData.get(START_X); }
    public float getStartY() { return this.entityData.get(START_Y); }
    public float getStartZ() { return this.entityData.get(START_Z); }
    public void setNowPos(Vec3 vec3) {
        this.entityData.set(NOW_X, (float) vec3.x);
        this.entityData.set(NOW_Y, (float) vec3.y);
        this.entityData.set(NOW_Z, (float) vec3.z);
    }
    public float getNowX() { return this.entityData.get(NOW_X); }
    public float getNowY() { return this.entityData.get(NOW_Y); }
    public float getNowZ() { return this.entityData.get(NOW_Z); }
    public void setLastPos(Vec3 vec3) {
        this.entityData.set(LAST_X, (float) vec3.x);
        this.entityData.set(LAST_Y, (float) vec3.y);
        this.entityData.set(LAST_Z, (float) vec3.z);
    }
    public float getLastX() { return this.entityData.get(LAST_X); }
    public float getLastY() { return this.entityData.get(LAST_Y); }
    public float getLastZ() { return this.entityData.get(LAST_Z); }
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
    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
        compoundTag.putInt("Life", this.life);
        compoundTag.putInt("LifeBomb", this.getLifeBomb());
        compoundTag.putInt("Start", this.start);
        if (this.ownerUUID != null) {
            compoundTag.putUUID("Owner", this.ownerUUID);
        }
        compoundTag.putDouble("XStart", getStartX());
        compoundTag.putDouble("YStart", getStartY());
        compoundTag.putDouble("ZStart", getStartZ());
        compoundTag.putDouble("XNow", getNowX());
        compoundTag.putDouble("YNow", getNowY());
        compoundTag.putDouble("ZNow", getNowZ());
        compoundTag.putDouble("XLast", getLastX());
        compoundTag.putDouble("YLast", getLastY());
        compoundTag.putDouble("ZLast", getLastZ());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
        this.life = compoundTag.getInt("Life");
        this.setLifeBomb(compoundTag.getInt("LifeBomb"));
        this.start = compoundTag.getInt("Start");
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUUID = compoundTag.getUUID("Owner");
        }
        this.setStartPos(new Vec3(compoundTag.getDouble("XStart"), compoundTag.getDouble("YStart"), compoundTag.getDouble("ZStart")));
        this.setNowPos(new Vec3(compoundTag.getDouble("XNow"), compoundTag.getDouble("YNow"), compoundTag.getDouble("ZNow")));
        this.setLastPos(new Vec3(compoundTag.getDouble("XLast"), compoundTag.getDouble("YLast"), compoundTag.getDouble("ZLast")));
    }
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(START_X, 0.0f);
        this.getEntityData().define(START_Y, 0.0f);
        this.getEntityData().define(START_Z, 0.0f);
        this.getEntityData().define(NOW_X, 0.0f);
        this.getEntityData().define(NOW_Y, 0.0f);
        this.getEntityData().define(NOW_Z, 0.0f);
        this.getEntityData().define(LAST_X, 0.0f);
        this.getEntityData().define(LAST_Y, 0.0f);
        this.getEntityData().define(LAST_Z, 0.0f);
        this.getEntityData().define(LIFE_BOMB, 0);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.life;
        float startLife = (this.life - 5) * 10f;
        Vec3 selfPos = this.getPosition(0f);
        Vec3 startPos = new Vec3(this.getStartX(), getStartY(), getStartZ());
        double distance = selfPos.distanceTo(startPos);
        float startCount = (float) (distance * 10);
        //越高代表越接近目标
        float distanceOfNowPos = (startLife/startCount);
        if (!level().isClientSide) {
            this.setLastPos(new Vec3(this.getNowX(), this.getNowY(), this.getNowZ()));
            this.setNowPos(selfPos.multiply(distanceOfNowPos, distanceOfNowPos, distanceOfNowPos).
                    add(startPos.multiply(1 - distanceOfNowPos, 1 - distanceOfNowPos, 1 - distanceOfNowPos)));
        }
        Vec3 nowPos = new Vec3(this.getNowX(), getNowY(), getNowZ());
        Vec3 lastPos = new Vec3(this.getLastX(), getLastY(), getLastZ());
        if (startLife < 0)
            return;

        if (this.getLifeBomb() <= 0) {
        if (level().isClientSide) {
            Vec3 currentPos = nowPos.add(0.0D, 0.1875D, 0.0D);
            Vec3 prevPos = lastPos.add(0.0D, 0.1875D, 0.0D);
            trailPoints.add(new TrailPoint(prevPos, currentPos, life));
            trailPoints.removeIf(p -> life - p.createdAt > trailMaxLife());
            while (trailPoints.size() > trailMaxPoints()) {
                trailPoints.remove(0);
            }
        }
        }
//
//        if (this.level() instanceof ServerLevel serverLevel) {
//            double d = getNowX();
//            double d2 = getNowY();
//            double d3 = getNowZ();
//            serverLevel.sendParticles(ParticleTypes.FLAME, d, d2, d3, 0, 0, 0, 0, 0);
//        }

        if (startLife >= startCount) {
            if (!level().isClientSide) {
                this.setLifeBomb(this.getLifeBomb() + 1);
            }
        }
        if (life % 5 == 0 && !this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 0.3f, 2.0f);
        }
        //初次爆炸
        if (this.getLifeBomb() == 1) {
            this.specialExplode();
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 10; i++) {
                    //火焰球
                    ParticlesUse.sendBallParticles(this, ParticleTypes.FLAME, true, 6.0f, 0.8f);
                    //闪光
                    for (int j = 0; j < 36; j++) {
                        float angle = (float) (j / 36.0 * 2 * Math.PI);
                        float radius = i/2f + random.nextFloat() * 2.0f;
                        double x = this.getRandomX((this.random.nextDouble() - 0.5f) * 3) + radius * Math.cos(angle);
                        double z = this.getRandomZ((this.random.nextDouble() - 0.5f) * 3) + radius * Math.sin(angle);
                        serverLevel.sendParticles(ParticleTypes.FLASH, x, this.getY() + 0.5, z, 0, 0, 0, 0, 0);
                    }
                    Main.spawnUnevenBlockByPos(serverLevel, BlockPos.containing(this.position()), 4);
                }
            }
        }
        if (this.getLifeBomb() == 8) {
            if (this.level() instanceof ServerLevel serverLevel) {
                //火花
                for (int j = 0; j < 128; j++) {
                    float angle = (float) (j / 128.0 * 2 * Math.PI);
                    float radius = 4;
                    double x = this.getX() + radius * Math.cos(angle);
                    double z = this.getZ() + radius * Math.sin(angle);
                    serverLevel.sendParticles(ParticleTypes.LAVA, x, this.getY() + 2.5, z, 0, 0, 0, 0, 0f);
                }
                Main.spawnUnevenBlockByPos(serverLevel, BlockPos.containing(this.position()), 3);
            }
        }
        if (this.getLifeBomb() == 10) {
            if (this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 10; i++) {
                    //烟
                    for (int j = 0; j < 36; j++) {
                        float angle = (float) (j / 36.0 * 2 * Math.PI);
                        float radius = Math.max(4, i/2f + random.nextFloat() * 2.0f);
                        double x = this.getRandomX((this.random.nextDouble() - 0.5f) * 3) + radius * Math.cos(angle);
                        double z = this.getRandomZ((this.random.nextDouble() - 0.5f) * 3) + radius * Math.sin(angle);
                        serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, this.getY() + 0.5, z, 0, 0, i * 0.2f * random.nextFloat(), 0, 0.3f);
                    }
                    Main.spawnUnevenBlockByPos(serverLevel, BlockPos.containing(this.position()), 3);
                }
            }
        }
        if (this.getLifeBomb() == 60) {
            this.discard();
        }
    }

    public void specialExplode() {
        this.level().broadcastEntityEvent(this, (byte)3);
        float distance = 6;
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, this.getSoundSource(), 15.0f, (float) (0.8f + Math.random() * 0.4f));
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 15.0f, (float) (0.8f + Math.random() * 0.4f));
        List<Entity> lists = this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(distance + 2, distance + 2, distance + 2));
        for (Entity hurt : lists) {
            if (hurt.distanceTo(this) > distance) continue;
            float strength = (24 - this.distanceTo(hurt)) / 3f;
            if (this.getOwner() != null && AttackFind.FindCanNotAttack(this.getOwner(), hurt)) continue;

            float visibility = getSeenPercent(hurt, this);
            float damageMultiplier = Math.max(0.0f, visibility);
            if (damageMultiplier <= 0.01f) continue;

            //生物造成伤害与击退
            if (hurt instanceof LivingEntity livingHurt) {
                DamageSource damageSources = AttackFind.findDamageType(this, JerotesDamageTypes.FIRE_MAGIC, this, this.getOwner());
                boolean bl = hurt.hurt(damageSources, (8 + (getSpellLevel() - 3)) * Main.randomReach(this.random, 1, 6));
                if (bl) {
                    hurt.setSecondsOnFire(6 + getSpellLevel() * 3);
                }

                //推动
                double knock;
                if (livingHurt.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
                    knock = livingHurt.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                } else {
                    knock = 0.0;
                }
                double d2 = Math.max(0.2, 1.2 - knock);
                livingHurt.setDeltaMovement(livingHurt.getDeltaMovement().add(-(this.getX() - livingHurt.getX()) * d2 / 3, -(this.getY() - livingHurt.getY()) * d2 / 4, -(this.getZ() - livingHurt.getZ()) * d2 / 3));

                if (Main.mobSizeSmall(hurt) || Main.mobSizeMedium(hurt) || Main.mobSizeLarge(hurt)) {
                    livingHurt.setDeltaMovement(livingHurt.getDeltaMovement().add(0, (this.getY() < livingHurt.getY() ? -1 : 1) * strength / 24, 0));
                }
            }
            if (hurt instanceof ItemEntity itemEntity && itemEntity.getItem().getRarity() == Rarity.COMMON) {
                itemEntity.setDeltaMovement(0, strength / 6, 0);
            }
            if (!(hurt instanceof ItemEntity)){
                hurt.setDeltaMovement(0, strength / 5, 0);
            }
            if (!(hurt instanceof LivingEntity livingHurt)) continue;
            if (!livingHurt.level().isClientSide) {
                livingHurt.addEffect(new MobEffectInstance(JerotesMobEffects.QUAKE.get(), 120, 0, false, false), this);
            }
        }
        this.setDeltaMovement(0,0,0);
        this.level().explode(this, null, null, this.getX(), this.getY(), this.getZ(), Math.min(spellLevelDamage/2, 3), true, Level.ExplosionInteraction.NONE);
    }
    private float getSeenPercent(Entity target, Entity source) {
        Vec3 start = target.getEyePosition(1.0f);
        Vec3 end = source.position();
        int hits = 0;
        int samples = 5;
        for (int i = 0; i < samples; i++) {
            Vec3 randomEnd = end.add(
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.5
            );
            BlockHitResult result = source.level().clip(
                    new ClipContext(start, randomEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target)
            );
            if (result.getType() == HitResult.Type.MISS) hits++;
        }
        return (float) hits / samples;
    }

    public int beamLightI() {
        return 0xe6903b;
    }
    public int beamLightII() {
        return 0x580900;
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
        return 12;
    }
    public int trailMaxPoints() {
        return 12;
    }
    public List<TrailPoint> getTrailPoints() {
        return trailPoints;
    }

    @Override
    public int getSpellLevel() {
        return this.spellLevelDamage;
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

