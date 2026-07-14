package com.jerotes.jerotes.entity.Shoot.Magic.Ray;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutEntity;
import com.jerotes.jerotes.forge.JerotesStopSpellEvent;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.spell.SpellFind;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import com.jerotes.jerotes.util.ParticlesUse;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class BaseRayEntity extends MagicAboutEntity {
    public static final EntityDataAccessor<Boolean> USEFUL = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HIT_USEFUL = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> FIRST = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> LAST_X = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LAST_Y = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> LAST_Z = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> LIFE_RAY = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LIFE_RAY_LAST = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RAY_COUNT = SynchedEntityData.defineId(BaseRayEntity.class, EntityDataSerializers.INT);

    public BaseRayEntity(EntityType<? extends MagicAboutEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseRayEntity(EntityType<? extends MagicAboutEntity> entityType, double d, double d2, double d3, double d4, double d5, double d6, Level level) {
        super(entityType, d, d2, d3, d4, d5, d6, level);
    }

    public BaseRayEntity(EntityType<? extends MagicAboutEntity> entityType, LivingEntity livingEntity, double d, double d2, double d3, Level level) {
        super(entityType, livingEntity, d, d2, d3, level);
    }

    @Override
    public void setDeltaMovement(Vec3 vec3){
    }
    protected boolean shouldBubble() {
        return false;
    }
    protected boolean shouldTail() {
        return false;
    }
    @Override
    public void shoot(double p_37266_, double p_37267_, double p_37268_, float p_37269_, float p_37270_) {
        Vec3 vec3 = (new Vec3(p_37266_, p_37267_, p_37268_)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double)p_37270_), this.random.triangle(0.0D, 0.0172275D * (double)p_37270_), this.random.triangle(0.0D, 0.0172275D * (double)p_37270_)).scale((double)p_37269_);
        this.setRayDeltaMovement(vec3);
        Vec3 startPos = this.getPosition(0);
        this.setRayStartPos(startPos);
        if (!this.level().isClientSide()) {
            this.setUseful(true);
            this.setFirst(true);
            this.setRayLastPos(startPos);
        }
        double d0 = vec3.horizontalDistance();
        this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        if (showHitParticle()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                Vec3 center = this.position().add(0, this.getBbHeight() * 0.4F, 0);
                Vec3 lastPos = center.add(getRayDeltaMovement());
                Vec3 direction = center.subtract(lastPos);
                ParticlesUse.spawnDirectionalHemisphereParticles(serverLevel, center, direction, false, 0.5f, this.getTrailParticle(), 0.005f, 0.015f, 20);
            }
        }
    }
    @Override
    public void shootFromRotation(Entity entity, float p_37253_, float p_37254_, float p_37255_, float p_37256_, float p_37257_) {
        float f = -Mth.sin(p_37254_ * ((float)Math.PI / 180F)) * Mth.cos(p_37253_ * ((float)Math.PI / 180F));
        float f1 = -Mth.sin((p_37253_ + p_37255_) * ((float)Math.PI / 180F));
        float f2 = Mth.cos(p_37254_ * ((float)Math.PI / 180F)) * Mth.cos(p_37253_ * ((float)Math.PI / 180F));
        this.shoot((double)f, (double)f1, (double)f2, p_37256_, p_37257_);
    }
    public Vec3 getRayDeltaMovement() {
        return new Vec3(xSpeedRay/2, ySpeedRay/2, zSpeedRay/2);
    }
    public void setRayDeltaMovement(Vec3 vec3){
        xSpeedRay = vec3.x * 2;
        ySpeedRay = vec3.y * 2;
        zSpeedRay = vec3.z * 2;
    }
    public void setRayLastPos(Vec3 vec3) {
        this.entityData.set(LAST_X, (float) vec3.x);
        this.entityData.set(LAST_Y, (float) vec3.y);
        this.entityData.set(LAST_Z, (float) vec3.z);
    }
    public void setRayStartPos(Vec3 vec3) {
        this.xStartRay = vec3.x;
        this.yStartRay = vec3.y;
        this.zStartRay = vec3.z;
    }
    public float getLastX() { return this.entityData.get(LAST_X); }
    public float getLastY() { return this.entityData.get(LAST_Y); }
    public float getLastZ() { return this.entityData.get(LAST_Z); }
    public void setLifeRay(int n) {
        this.entityData.set(LIFE_RAY, n);
    }
    public int getLifeRay() { return this.entityData.get(LIFE_RAY); }
    public void setLifeRayLast(int n) {
        this.entityData.set(LIFE_RAY_LAST, n);
    }
    public int getLifeRayLast() { return this.entityData.get(LIFE_RAY_LAST); }
    public void setRayCount(int n) {
        this.entityData.set(RAY_COUNT, n);
    }
    public int getRayCount() { return this.entityData.get(RAY_COUNT); }
    public void setRayDeltaMovement(double p_20335_, double p_20336_, double p_20337_) {
        this.setRayDeltaMovement(new Vec3(p_20335_, p_20336_, p_20337_));
    }


    public double summonTod = 1;
    public double summonTod2 = 1;
    public double summonTod3 = 1;
    public double xSpeedRay = 0;
    public double ySpeedRay = 0;
    public double zSpeedRay = 0;
    public double xStartRay = 0;
    public double yStartRay = 0;
    public double zStartRay = 0;
    public boolean isUseful() {
        return this.getEntityData().get(USEFUL);
    }
    public void setUseful(boolean bl) {
        this.getEntityData().set(USEFUL, bl);
        if (showHitParticle()) {
            if (this.isHitUseful() && this.getTrailParticle() != null && !bl) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    Vec3 center = this.position().add(0,this.getBbHeight()*0.4F,0);
                    Vec3 lastPos = new Vec3(this.getLastX(), this.getLastY(), this.getLastZ()).add(0,this.getBbHeight()*0.4F,0);
                    Vec3 direction = center.subtract(lastPos);
                    ParticlesUse.spawnDirectionalHemisphereParticles(serverLevel, center, direction, false,0.35f, this.getTrailParticle(), 0.005f, 0.25f, 120);
                    ParticlesUse.spawnDirectionalHemisphereParticles(serverLevel, center, direction, false,0.35f, this.getTrailParticle(), 0.005f, 0.15f, 100);
                    ParticlesUse.spawnDirectionalHemisphereParticles(serverLevel, center, direction, false,0.35f, this.getTrailParticle(), 0.005f, 0.05f, 80);
                }
            }
//            if (this.getTrailParticle() != null && bl) {
//                if (this.level() instanceof ServerLevel serverLevel) {
//                    Vec3 center = this.position().add(0,this.getBbHeight()*0.4F,0);
//                    Vec3 lastPos = new Vec3(this.getLastX(), this.getLastY(), this.getLastZ()).add(0,this.getBbHeight()*0.4F,0);
//                    Vec3 direction = center.subtract(lastPos);
//                    ParticlesUse.spawnDirectionalHemisphereParticles(serverLevel, center, direction, false,0.5f, this.getTrailParticle(), 0.25f, 0.015f, 24);
//                }
//            }
        }
    }
    public boolean isHitUseful() {
        return this.getEntityData().get(HIT_USEFUL);
    }
    public void setHitUseful(boolean bl) {
        this.getEntityData().set(HIT_USEFUL, bl);
    }
    public boolean isFirst() {
        return this.getEntityData().get(FIRST);
    }
    public void setFirst(boolean bl) {
        this.getEntityData().set(FIRST, bl);
    }
    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("SpellLevelMainEffectTime", this.spellLevelMainEffectTime);
        compoundTag.putInt("SpellLevelMainEffectLevel", this.spellLevelMainEffectLevel);
        compoundTag.putInt("Life", life);
        compoundTag.putInt("LifeRay", getLifeRay());
        compoundTag.putInt("LifeRayLast", getLifeRayLast());
        compoundTag.putInt("RayCount", getRayCount());
        compoundTag.putDouble("SummonTod", summonTod);
        compoundTag.putDouble("SummonTod2", summonTod2);
        compoundTag.putDouble("SummonTod3", summonTod3);
        compoundTag.putDouble("XSpeedRay", xSpeedRay);
        compoundTag.putDouble("YSpeedRay", ySpeedRay);
        compoundTag.putDouble("ZSpeedRay", zSpeedRay);
        compoundTag.putDouble("XStartRay", xStartRay);
        compoundTag.putDouble("YStartRay", yStartRay);
        compoundTag.putDouble("ZStartRay", zStartRay);
        compoundTag.putDouble("XLastRay", getLastX());
        compoundTag.putDouble("YLastRay", getLastY());
        compoundTag.putDouble("ZLastRay", getLastZ());
        compoundTag.putBoolean("IsUseful", this.isUseful());
        compoundTag.putBoolean("IsHitUseful", this.isHitUseful());
        compoundTag.putBoolean("IsFirst", this.isFirst());
    }
    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.spellLevelMainEffectTime = compoundTag.getInt("SpellLevelMainEffectTime");
        this.spellLevelMainEffectLevel = compoundTag.getInt("SpellLevelMainEffectLevel");
        this.life = compoundTag.getInt("Life");
        this.setLifeRay(compoundTag.getInt("LifeRay"));
        this.setLifeRayLast(compoundTag.getInt("LifeRayLast"));
        this.setRayCount(compoundTag.getInt("RayCount"));
        this.summonTod = compoundTag.getDouble("SummonTod");
        this.summonTod2 = compoundTag.getDouble("SummonTod2");
        this.summonTod3 = compoundTag.getDouble("SummonTod3");
        this.xSpeedRay = compoundTag.getDouble("XSpeedRay");
        this.ySpeedRay = compoundTag.getDouble("YSpeedRay");
        this.zSpeedRay = compoundTag.getDouble("ZSpeedRay");
        this.setRayStartPos(new Vec3(compoundTag.getDouble("XStartRay"), compoundTag.getDouble("YStartRay"), compoundTag.getDouble("ZStartRay")));
        this.setRayLastPos(new Vec3(compoundTag.getDouble("XLastRay"), compoundTag.getDouble("YLastRay"), compoundTag.getDouble("ZLastRay")));
        this.setUseful(compoundTag.getBoolean("IsUseful"));
        this.setHitUseful(compoundTag.getBoolean("IsHitUseful"));
        this.setFirst(compoundTag.getBoolean("IsFirst"));
    }
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(USEFUL, true);
        this.getEntityData().define(HIT_USEFUL, true);
        this.getEntityData().define(LIFE_RAY, 0);
        this.getEntityData().define(LIFE_RAY_LAST, 0);
        this.getEntityData().define(RAY_COUNT, 0);
        this.getEntityData().define(FIRST, false);
        this.getEntityData().define(LAST_X, 0.0f);
        this.getEntityData().define(LAST_Y, 0.0f);
        this.getEntityData().define(LAST_Z, 0.0f);
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
        if (!this.level().isClientSide()) {
            this.setLifeRay(this.getLifeRay() + 1);
            if (this.isHitUseful() && !this.isUseful())
                this.setLifeRayLast(this.getLifeRayLast() + 1);
        }
        if (this.life >= this.getMaxLife()) {
            if (!this.level().isClientSide()) {
                this.setHitUseful(false);
                this.setUseful(false);
            }
        }
        super.tick();

        if (this.isUseful()) {
            //撞墙
            if (this.life > 1) {
                if (!Main.hasLineOfSightPos(new Vec3(this.getLastX(), this.getLastY(), this.getLastZ()), this.getPosition(0), getOwner(), this.level())) {
                    this.afterHasLineOfSight();
                }
            }
            //选择目标
            Vec3 start = new Vec3(getLastX(), getLastY(), getLastZ());
            Vec3 end = this.position();
            double radius = this.getBbWidth();
            if (radius < 0.25D) {
                radius = 0.25D;
            }
            AABB beamBox = new AABB(start, end).inflate(radius);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, beamBox, livingEntity -> livingEntity != this.getOwner() && this.canHitEntity(livingEntity) && !AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity) && !isHurt(livingEntity.getUUID()));
            Vec3 direction = end.subtract(start);
            double lenSq = direction.lengthSqr();
            targets.sort((a, b) -> {
                double ta = a.getBoundingBox().getCenter().subtract(start).dot(direction) / lenSq;
                double tb = b.getBoundingBox().getCenter().subtract(start).dot(direction) / lenSq;
                return Double.compare(ta, tb);
            });
            for (LivingEntity target : targets) {
                AABB targetBox = target.getBoundingBox().inflate(radius);
                Optional<Vec3> hitResult = targetBox.clip(start, end);
                if (hitResult.isPresent()) {
                    this.hitEntity(target);
                }
            }
            //超过距离
            if (this.position().distanceTo(new Vec3(this.xStartRay, this.yStartRay, this.zStartRay)) > getMaxReach()) {
                this.setUseful(false);
            }
        }

        if (this.getOwner() != null && this.isUseful()) {
            BaseRayEntity ray = this.getRay();
            ray.setOwner(this.getOwner());
            ray.setPos(this.getX() + this.xSpeedRay, this.getY() + this.ySpeedRay, this.getZ() + this.zSpeedRay);
            ray.setRot(this.getYRot(), this.getXRot());
            ray.setRayStartPos(new Vec3(this.xStartRay, this.yStartRay, this.zStartRay));
            ray.setRayDeltaMovement(this.getRayDeltaMovement());
            if (!this.level().isClientSide()) {
                ray.setUseful(true);
                ray.setFirst(false);
                ray.setLifeRay(this.getLifeRay());
                ray.setRayCount(this.getRayCount() + 1);
                ray.setRayLastPos(new Vec3(this.getX(), this.getY(), this.getZ()));
            }
            ray.life = this.life;;
            ray.setLastHurt(this.getLastHurt());
            List<UUID> listTag = this.getHurtUUIDs();
            for (UUID uuid : listTag) {
                ray.addHurtUUID(uuid);
            }
            this.level().addFreshEntity(ray);

            this.setHitUseful(false);
            this.setUseful(false);
            if (this.showParticle()) {
                xSpeedRay = this.getDeltaMovement().x;
                ySpeedRay = this.getDeltaMovement().y;
                zSpeedRay = this.getDeltaMovement().z;
                if (life > 5) {
                    if (this.getTrailParticle() != null) {
                        for (int i = -1; i < 5; ++i) {
                            this.level().addParticle(this.getTrailParticle(), this.getX() + this.xSpeedRay * i * 0.2, this.getY() + this.ySpeedRay * i * 0.2 + this.getBbHeight() / 2, this.getZ() + this.zSpeedRay * i * 0.2, 0.0d, 0.0d, 0.0d);
                        }
                    }
                }
            }
        }
        else {
            if (this.showParticle()) {
                this.setRayDeltaMovement(0d, 0d, 0d);
                if (this.getTrailParticle() != null) {
                    for (int i = -1; i < 5; ++i) {
                        this.level().addParticle(this.getTrailParticle(), this.getX() + this.xSpeedRay * i * 0.2, this.getY() + this.ySpeedRay * i * 0.2 + this.getBbHeight() / 2, this.getZ() + this.zSpeedRay * i * 0.2, 0.0d, 0.0d, 0.0d);
                    }
                }
            }
        }
        if (this.showParticle()) {
            if (this.getTrailParticle() != null) {
                if (life > 1) {
                    this.level().addParticle(this.getTrailParticle(), this.getX(), this.getY() + this.getBbHeight() / 2, this.getZ(), 0.0d, 0.0d, 0.0d);
                }
            }
        }
    }
    private double distanceSquaredToSegment(Vec3 point, Vec3 start, Vec3 end) {
        Vec3 ab = end.subtract(start);
        Vec3 ac = point.subtract(start);
        double t = ac.dot(ab);
        if (t <= 0.0) {
            return ac.lengthSqr();
        }
        double len2 = ab.lengthSqr();
        if (t >= len2) {
            return point.subtract(end).lengthSqr();
        }
        Vec3 projection = start.add(ab.scale(t / len2));
        return point.subtract(projection).lengthSqr();
    }

    public abstract BaseRayEntity getRay();

    @Override
    public int getMaxLife() {
        return 40;
    }
    public int getMaxReach() {
        return 18;
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
    public boolean showParticle() {
        return false;
    }
    public boolean showHitParticle() {
        return true;
    }
    protected void afterHasLineOfSight() {
    }
    protected void hitEntity(Entity entity) {
        if (entity == this.getLastHurt() && !this.canNotHurtLastHurt()) {
            return;
        }
        if (isHurt(entity.getUUID()) && this.useUUIDFindHurtAndDisable()) {
            return;
        }
        if (entity == this.getOwner()) {
            return;
        }
        //法术取消
        //event
        JerotesStopSpellEvent event = new JerotesStopSpellEvent(getOwner(), null, this, entity);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        this.setLastHurt(entity);
        this.addHurtUUID(entity.getUUID());
    }

    @Override
    protected float getInertia() {
        return 1.0f;
    }
    //@Override
    protected float getLiquidInertia() {
        return 1.0f;
    }

    public boolean isOnFire() {
        return false;
    }

    public boolean showBeam() {
        return true;
    }
    public float beamScale() {
        return 1.5f;
    }
    public int beamLightI() {
        return 0x64a63a;
    }
    public int beamLightII() {
        return 0xbfe970;
    }
}
