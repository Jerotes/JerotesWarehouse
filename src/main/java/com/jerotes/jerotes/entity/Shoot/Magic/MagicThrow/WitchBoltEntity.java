package com.jerotes.jerotes.entity.Shoot.Magic.MagicThrow;

import com.jerotes.jerotes.entity.Interface.ConcentrationSpellEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAboutThrowEntity;
import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.SpellType;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class WitchBoltEntity extends MagicAboutThrowEntity implements ConcentrationSpellEntity {
    public static final EntityDataAccessor<Boolean> USEFUL = SynchedEntityData.defineId(WitchBoltEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> DATA_TARGET_UUID = SynchedEntityData.defineId(WitchBoltEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(WitchBoltEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_RELATIVE_YAW = SynchedEntityData.defineId(WitchBoltEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_RELATIVE_PITCH = SynchedEntityData.defineId(WitchBoltEntity.class, EntityDataSerializers.FLOAT);

    public int scaleTick = 0;

    public WitchBoltEntity(EntityType<? extends WitchBoltEntity> entityType, Level level) {
        super(entityType, level);
    }

    public WitchBoltEntity(int spellLevelDamage, Level level, LivingEntity livingEntity) {
        super(JerotesEntityType.WITCH_BOLT.get(), livingEntity, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    public WitchBoltEntity(int spellLevelDamage, Level level, double d, double d2, double d3) {
        super(JerotesEntityType.WITCH_BOLT.get(), d, d2, d3, level);
        this.spellLevelDamage = spellLevelDamage;
    }

    public boolean isUseful() {
        return this.getEntityData().get(USEFUL);
    }

    public void setUseful(boolean bl) {
        this.getEntityData().set(USEFUL, bl);
    }

    @Nullable
    public Entity getTarget() {
        if (!this.level().isClientSide) {
            UUID uuid = this.getTargetUUID();
            return uuid == null ? null : ((ServerLevel) this.level()).getEntity(uuid);
        } else {
            int id = this.getTargetId();
            return id <= -1 ? null : this.level().getEntity(id);
        }
    }

    public void setTarget(@Nullable Entity entity) {
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
        return this.getEntityData().get(DATA_TARGET_UUID).orElse(null);
    }

    public void setTargetUUID(@Nullable UUID uuid) {
        this.getEntityData().set(DATA_TARGET_UUID, Optional.ofNullable(uuid));
    }

    public int getTargetId() {
        return this.getEntityData().get(DATA_TARGET_ID);
    }

    public void setTargetId(int id) {
        this.getEntityData().set(DATA_TARGET_ID, id);
    }

    public float getRelativeYaw() {
        return this.getEntityData().get(DATA_RELATIVE_YAW);
    }

    public float getRelativePitch() {
        return this.getEntityData().get(DATA_RELATIVE_PITCH);
    }

    public void setRelativeYaw(float yaw) {
        this.getEntityData().set(DATA_RELATIVE_YAW, yaw);
    }

    public void setRelativePitch(float pitch) {
        this.getEntityData().set(DATA_RELATIVE_PITCH, pitch);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("IsUseful", this.isUseful());
        compoundTag.putInt("ScaleTick", this.scaleTick);
        if (this.getTargetUUID() != null) {
            compoundTag.putUUID("Target", this.getTargetUUID());
        }
        compoundTag.putFloat("RelativeYaw", this.getRelativeYaw());
        compoundTag.putFloat("RelativePitch", this.getRelativePitch());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setUseful(compoundTag.getBoolean("IsUseful"));
        this.scaleTick = compoundTag.getInt("ScaleTick");
        if (compoundTag.hasUUID("Target")) {
            this.setTargetUUID(compoundTag.getUUID("Target"));
        }
        this.setRelativeYaw(compoundTag.getFloat("RelativeYaw"));
        this.setRelativePitch(compoundTag.getFloat("RelativePitch"));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(USEFUL, false);
        this.getEntityData().define(DATA_TARGET_UUID, Optional.empty());
        this.getEntityData().define(DATA_TARGET_ID, -1);
        this.getEntityData().define(DATA_RELATIVE_YAW, 0f);
        this.getEntityData().define(DATA_RELATIVE_PITCH, 0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() != null) {
            if (this.distanceTo(this.getOwner()) > 30 || !Main.hasLineOfSightEntity(this, this.getOwner())) {
                this.lastBreak();
                this.endConcentration();
                return;
            }
        }
        Entity target = this.getTarget();
        if (this.isUseful() && target != null) {
            this.setDeltaMovement(0, 0, 0);
            this.setNoGravity(true);
            this.setPos(target.position().add(0, target.getBbHeight() / 2, 0));
            this.setRot(target.getYRot() + this.getRelativeYaw(), target.getXRot() + this.getRelativePitch());
            if (!target.isAlive()) {
                this.lastBreak();
                this.endConcentration();
                return;
            }
            if (this.life % 120 == 0 && this.life != 0) {
                if (target instanceof LivingEntity livingTarget) {
                    DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_LIGHTNING, this, this.getOwner());
                    livingTarget.hurt(damageSource,
                            Main.rollDice(1, 12, livingTarget.getRandom())
                    );
                    this.setUseful(true);
                    this.setTarget(livingTarget);
                    scaleTick = 10;
                }
            }
            if (this.level().isClientSide) {
                if (this.life % 120 > 110)
                    this.scaleTick = Math.min(10, this.scaleTick + 1);
                else
                    this.scaleTick = Math.max(0, this.scaleTick - 1);
            }
        }
        if (this.life >= this.getMaxLife() - 5) {
            this.lastBreak();
            this.endConcentration();
        }
    }

    @Override
    public void lastBreak() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.position();
            int count = 40;
            for (int i = 0; i < count; i++) {
                double dx = (random.nextDouble() - 0.5) * 2.0;
                double dy = (random.nextDouble() - 0.5) * 2.0;
                double dz = (random.nextDouble() - 0.5) * 2.0;
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        pos.x, pos.y, pos.z,
                        1, dx, dy, dz, 0.1);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (this.isUseful())
            return;
        super.onHitEntity(entityHitResult);
        if (this.level().isClientSide) {
            return;
        }
        Entity entity = entityHitResult.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            DamageSource damageSource = AttackFind.findDamageType(this, JerotesDamageTypes.LIGHTNING_MAGIC, this, this.getOwner());
            livingEntity.hurt(damageSource,
                    Main.rollDice(2 + (getSpellLevel() - 1), 12, livingEntity.getRandom())
            );
            this.life = 0;
            this.setUseful(true);
            this.setTarget(livingEntity);
            this.addHurtUUID(entity.getUUID());
            this.setRelativeYaw(this.getYRot() - livingEntity.getYRot());
            this.setRelativePitch(this.getXRot() - livingEntity.getXRot());
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
        }
        if (!this.isUseful()) {
            this.lastBreak();
            this.endConcentration();
        }
    }

    @Override
    public int getMaxLife() {
        return 20 * 60 + 5;
    }

    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }

    @Override
    protected float getGravity() {
        return 0f;
    }

    public int roundLightI() {
        return 0x3c44ff;
    }

    public int roundLightII() {
        return 0x6392ff;
    }

    @Override
    public boolean endConcentration() {
        if (getOwner() != null && getOwner() instanceof LivingEntity livingEntity) {
            SpellType.setConcentrationSpellTypeJerotes(livingEntity, "");
            if (getOwner() instanceof Player player) {
                player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
                    capability.setConcentrationSpellTarget("");
                    capability.syncPlayerVariables(player);
                });
            }
            SpellType.setConcentrationSpellTargetJerotes(livingEntity, UUID.fromString("00000000-0000-0000-0000-000000000000"));
            SpellType.endConcentrationMessage(livingEntity, SpellType.JEROTES_WITCH_BOLT.magicSpellGet(1, null, null).getSpellName());
        }
        this.setTarget(null);
        if (!this.level().isClientSide()) {
            this.discard();
        }
        return true;
    }
}