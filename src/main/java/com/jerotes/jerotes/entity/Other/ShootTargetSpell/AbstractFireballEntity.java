package com.jerotes.jerotes.entity.Other.ShootTargetSpell;

import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
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

public abstract class AbstractFireballEntity extends Entity implements TraceableEntity, OwnableEntity, MagicAbout {
    private static final EntityDataAccessor<Float> START_X = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Y = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> START_Z = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> NOW_X = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> NOW_Y = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> NOW_Z = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LAST_X = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LAST_Y = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LAST_Z = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFE_BOMB = SynchedEntityData.defineId(AbstractFireballEntity.class, EntityDataSerializers.INT);

    public int spellLevelDamage;
    public int life = 0;
    public int start = 0;
    protected int subcategory = 0;

    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    protected final List<TrailPoint> trailPoints = new ArrayList<>();

    public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    protected AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, Level level, LivingEntity owner) {
        this(entityType, level);
        this.setOwner(owner);
    }

    public void setSpellLevelDamage(int n) {
        this.spellLevelDamage = n;
    }

    public void setSubcategory(int subcategory) {
        this.subcategory = subcategory;
    }

    public int getSubcategory() {
        return this.subcategory;
    }

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
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel) this.level()).getEntity(this.ownerUUID)) instanceof LivingEntity) {
            this.owner = (LivingEntity) entity;
        }
        return this.owner;
    }

    public int getLifeBomb() {
        return this.entityData.get(LIFE_BOMB);
    }

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
        compoundTag.putInt("Subcategory", this.subcategory);
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
        this.subcategory = compoundTag.getInt("Subcategory");
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

        if (!level().isClientSide) {
            this.setLastPos(new Vec3(this.getNowX(), this.getNowY(), this.getNowZ()));
            float totalTicks = (float) Math.max(1.0, distance);
            float progress = Mth.clamp((this.life - 5) / totalTicks, 0.0f, 1.0f);
            Vec3 newPos = computeNowPosition(startPos, selfPos, progress);
            this.setNowPos(newPos);
        }

        if (startLife < 0)
            return;

        Vec3 nowPos = new Vec3(this.getNowX(), getNowY(), getNowZ());
        Vec3 lastPos = new Vec3(this.getLastX(), getLastY(), getLastZ());

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
        } else {
            if (level().isClientSide) {
                trailPoints.clear();
            }
        }

        if (startLife >= startCount) {
            if (!level().isClientSide) {
                this.setLifeBomb(this.getLifeBomb() + 1);
            }
        }

        if (life == 5 && !this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 1.0f, 2.0f);
            this.level().playSound(null, this.getStartX(), this.getStartY(), this.getStartZ(),
                    SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.NEUTRAL, 1.0f, 2.0f);
        }

        if (this.getLifeBomb() == 1) {
            this.performExplosion();
        }

        int lifeBomb = this.getLifeBomb();
        if (lifeBomb >= lifeBombOnsetTick() && lifeBomb < lifeBombDuration()) {
            this.onLifeBomb(lifeBomb);
        }

        if (this.getLifeBomb() == lifeBombDuration()) {
            this.discard();
        }
    }

    protected int lifeBombOnsetTick() {
        return 8;
    }
    protected int lifeBombDuration() {
        return 60;
    }

    protected Vec3 computeNowPosition(Vec3 startPos, Vec3 selfPos, float progress) {
        double distance = startPos.distanceTo(selfPos);
        float startLife = (this.life - 5) * 10f;
        float startCount = (float) (distance * 10);
        float t = Mth.clamp(startLife / startCount, 0.0f, 1.0f);
        return selfPos.multiply(t, t, t).add(startPos.multiply(1 - t, 1 - t, 1 - t));
    }

    protected void performExplosion() {

    }
    protected void onLifeBomb(int tick) {
    }

    protected int trailMaxLife() {
        return 12;
    }
    protected int trailMaxPoints() {
        return 12;
    }

    protected float getSeenPercent(Entity target, Entity source) {
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
}