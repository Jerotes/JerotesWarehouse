package com.jerotes.jerotes.entity.Other.OtherSpell;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Interface.JerotesChangeServerPlayer;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.init.JerotesParticleTypes;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class MirrorImageEntity extends Mob implements JerotesEntity, OwnableEntity, MagicAbout {
	private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> EYE_HEIGHT = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> ORDER = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> ADD_X = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> ADD_Y = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> ADD_Z = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.OPTIONAL_UUID);

	public MirrorImageEntity(EntityType<? extends MirrorImageEntity> type, Level world) {
		super(type, world);
		this.fixupDimensions();
	}

	@Override
	protected void registerGoals() {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
		builder = builder.add(Attributes.MAX_HEALTH, 8);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 0);
		return builder;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.GENERIC_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 1.0f;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
		return getScaleEyeHeight();
	}

	@Override
	protected void doPush(Entity entity) {
	}

	public int spellLevelDamage = 1;

	@Override
	public int getSpellLevel() {
		return this.spellLevelDamage;
	}

	private int life;
	@Override
	public UUID getOwnerUUID() {
		return this.entityData.get(DATA_OWNER_UUID).orElse(null);
	}
	public void setOwnerUUID(@Nullable UUID uuid) {
		this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(uuid));
	}
	public int getOwnerId() {
		return this.entityData.get(DATA_OWNER_ID);
	}
	public void setOwnerId(int id) {
		this.entityData.set(DATA_OWNER_ID, id);
	}

	@Nullable
	@Override
	public LivingEntity getOwner() {
		if (!this.level().isClientSide) {
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : Main.getLivingEntityByUUID(this.level(), uuid);
		} else {
			int id = this.getOwnerId();
			return id <= -1 ? null : this.level().getEntity(id) instanceof LivingEntity living ? living : null;
		}
	}

	public void setOwner(@Nullable LivingEntity livingEntity) {
		if (livingEntity != null) {
			this.setOwnerUUID(livingEntity.getUUID());
			if (!this.level().isClientSide()) {
				this.setOwnerId(livingEntity.getId());
			}
		} else {
			this.setOwnerUUID(null);
			this.setOwnerId(-1);
		}
		this.setBoundingBox(makeBoundingBox());
	}

	@Override
	protected AABB makeBoundingBox() {
		return AABB.ofSize(new Vec3(this.getX(), this.getY() + this.getScaleHeight() / 2, this.getZ()), this.getScaleWidth(), this.getScaleHeight(), this.getScaleWidth());
	}

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

	public void setScale(float f, float f2) {
		this.entityData.set(WIDTH, f);
		this.entityData.set(HEIGHT, f2);
		this.setBoundingBox(this.makeBoundingBox());
		this.reapplyPosition();
		this.refreshDimensions();
	}
	public void setScaleEyeHeight(float f) {
		this.entityData.set(EYE_HEIGHT, f);
	}

	public float getScaleWidth() {
		return this.entityData.get(WIDTH);
	}
	public float getScaleHeight() {
		return this.entityData.get(HEIGHT);
	}
	public float getScaleEyeHeight() {
		return this.entityData.get(EYE_HEIGHT);
	}

	public void setOrder(int n) {
		this.entityData.set(ORDER, n);
	}

	public int getOrder() {
		return this.entityData.get(ORDER);
	}

	public float getAddX() {
		return this.entityData.get(ADD_X);
	}

	public float getAddY() {
		return this.entityData.get(ADD_Y);
	}

	public float getAddZ() {
		return this.entityData.get(ADD_Z);
	}

	public void setAddX(float x) {
		this.entityData.set(ADD_X, x);
	}

	public void setAddY(float y) {
		this.entityData.set(ADD_Y, y);
	}

	public void setAddZ(float z) {
		this.entityData.set(ADD_Z, z);
	}

	public void setAdd(float x, float y, float z) {
		this.entityData.set(ADD_X, x);
		this.entityData.set(ADD_Y, y);
		this.entityData.set(ADD_Z, z);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Order", this.getOrder());
		compoundTag.putFloat("ScaleWidth", this.getScaleWidth());
		compoundTag.putFloat("ScaleHeight", this.getScaleHeight());
		compoundTag.putFloat("ScaleEyeHeight", this.getScaleEyeHeight());
		compoundTag.putFloat("AddX", this.getAddX());
		compoundTag.putFloat("AddY", this.getAddY());
		compoundTag.putFloat("AddZ", this.getAddZ());
		compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
		compoundTag.putInt("Life", this.life);
		if (this.getOwnerUUID() != null) {
			compoundTag.putUUID("Owner", this.getOwnerUUID());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.setOrder(compoundTag.getInt("Order"));
		this.setScale(compoundTag.getFloat("ScaleWidth"), compoundTag.getFloat("ScaleHeight"));
		this.setScaleEyeHeight(compoundTag.getFloat("ScaleEyeHeight"));
		this.setAddX(compoundTag.getFloat("AddX"));
		this.setAddY(compoundTag.getFloat("AddY"));
		this.setAddZ(compoundTag.getFloat("AddZ"));
		this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
		this.life = compoundTag.getInt("Life");
		UUID uuid;
		if (compoundTag.hasUUID("Owner")) {
			uuid = compoundTag.getUUID("Owner");
		} else {
			String string = compoundTag.getString("Owner");
			uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), string);
		}
		if (uuid != null) {
			try {
				this.setOwnerUUID(uuid);
			} catch (Throwable throwable) {
			}
		}
		this.setBoundingBox(this.makeBoundingBox());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(WIDTH, 0.6f);
		this.getEntityData().define(HEIGHT, 1.8f);
		this.getEntityData().define(EYE_HEIGHT, 1.62F);
		this.getEntityData().define(ORDER, 0);
		this.getEntityData().define(ADD_X, 0.0f);
		this.getEntityData().define(ADD_Y, 0.0f);
		this.getEntityData().define(ADD_Z, 0.0f);
		this.getEntityData().define(DATA_OWNER_ID, -1);
		this.getEntityData().define(DATA_OWNER_UUID, Optional.empty());
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		if (WIDTH.equals(entityDataAccessor) || HEIGHT.equals(entityDataAccessor)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(entityDataAccessor);
	}

	@Override
	public void refreshDimensions() {
		double d = this.getX();
		double d2 = this.getY();
		double d3 = this.getZ();
		super.refreshDimensions();
		this.setPos(d, d2, d3);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.life += 1;
		if (this.life >= 60 * 20) {
			this.discardNormal();
		}
		if (this.getOwner() != null && !this.getOwner().isAlive()) {
			this.discardNormal();
		}
	}

	@Override
	public void tick() {
		super.tick();
		this.noPhysics = true;
		this.setNoGravity(true);

		if (getOwner() != null) {
			if (!getOwner().level().isClientSide()) {
				getOwner().addEffect(new MobEffectInstance(JerotesMobEffects.MIRROR_IMAGE.get(), 5, 0, false, false));
			}
		}
		if (!this.level().isClientSide() && this.getOwner() != null && this.getOwnerId() == -1) {
			this.setOwnerId(this.getOwner().getId());
		}

		if (this.getOwner() != null && !this.getOwner().isInvisible()) {
			if (this.level() instanceof ServerLevel serverLevel) {
				for (int i2 = 0; i2 < 1; ++i2) {
					for (int i = 0; i < 3; ++i) {
						serverLevel.sendParticles(JerotesParticleTypes.MIRROR_IMAGE.get(),
								this.getOwner().getRandomX(0.5) + this.getAddX(), this.getOwner().getRandomY() + this.getAddY(), this.getOwner().getRandomZ(0.5) + this.getAddZ(),
								0, 0, 0, 0, 0);
						serverLevel.sendParticles(JerotesParticleTypes.MIRROR_IMAGE.get(),
								this.getOwner().getRandomX(0.25) + this.getAddX(), this.getOwner().getRandomY() + this.getAddY(), this.getOwner().getRandomZ(0.25) + this.getAddZ(),
								0, 0, 0, 0, 0);
					}
					serverLevel.sendParticles(JerotesParticleTypes.MIRROR_IMAGE.get(), this.getOwner().getRandomX(0.5), this.getOwner().getRandomY(), this.getOwner().getRandomZ(0.5), 0, 0, 0, 0, 0);
					serverLevel.sendParticles(JerotesParticleTypes.MIRROR_IMAGE.get(), this.getOwner().getRandomX(0.25), this.getOwner().getRandomY(), this.getOwner().getRandomZ(0.25), 0, 0, 0, 0, 0);
				}
			}

			if (this.tickCount % 20 == 0) {
				if (!this.level().isClientSide()) {
					this.setScale(Main.mobWidth(this.getOwner()), Main.mobHeight(this.getOwner()));
				}
				this.setBoundingBox(this.makeBoundingBox());
				this.refreshDimensions();
			}

			double targetX = this.getOwner().getX() + this.getAddX();
			double targetY = this.getOwner().getY() + this.getAddY();
			double targetZ = this.getOwner().getZ() + this.getAddZ();

			if (this.life <= 1) {
				this.moveTo(targetX, targetY, targetZ, this.getOwner().getYRot(), this.getOwner().getXRot());
			} else {
				this.setPos(targetX, targetY, targetZ);
				this.setXRot(this.getOwner().getXRot());
				this.setYRot(this.getOwner().getYRot());
			}

			this.yBodyRot = this.getOwner().getYRot();
			this.yHeadRot = this.getOwner().getYRot();

			this.setDeltaMovement(this.getOwner().getDeltaMovement());
		}

		if (this.getOwner() != null && this.getOwner() instanceof Player player && player.swinging && this.life > 5) {
			if (Main.getTargetedEntity(player, 32) == this && this.tickCount % 5 == 0) {
				this.discardByOwner();
				player.swinging = false;
			}
		}
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
		this.setAddX((serverLevelAccessor.getRandom().nextFloat() - 0.5f) * 2 * 4);
		this.setAddY(0);
		this.setAddZ((serverLevelAccessor.getRandom().nextFloat() - 0.5f) * 2 * 4);
		this.setScale(0.6f, 1.8f);
		return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
	}

	@Override
	public boolean hurt(DamageSource damagesource, float amount) {
		if (damagesource.getEntity() == this.getOwner()) {
			this.discardByOwner();
		}
		if (damagesource.getEntity() != null && damagesource.getEntity() != this.getOwner()) {
			this.discardNormal();
		}
		return true;
	}

	public void discardByOwner() {
		this.discard();
	}

	public void discardNormal() {
		this.discard();
	}

	@Override
	public void tickDeath() {
		++this.deathTime;
		if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
			this.remove(RemovalReason.DISCARDED);
		}
	}
}