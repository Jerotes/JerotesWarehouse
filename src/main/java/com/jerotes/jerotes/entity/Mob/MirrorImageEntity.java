package com.jerotes.jerotes.entity.Mob;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Interface.JerotesChangeServerPlayer;
import com.jerotes.jerotes.util.Main;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import java.util.UUID;

public class MirrorImageEntity extends PathfinderMob implements JerotesEntity, OwnableEntity {
	private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> ORDER = SynchedEntityData.defineId(MirrorImageEntity.class, EntityDataSerializers.INT);

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
	protected void doPush(Entity entity) {
	}

	public int spellLevelDamage = 1;
	private int life;
	@Nullable
	private LivingEntity owner;
	@Nullable
	private UUID ownerUUID;
	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return ownerUUID;
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
	public void setOwner(@Nullable LivingEntity livingEntity) {
		this.owner = livingEntity;
		this.ownerUUID = livingEntity == null ? null : livingEntity.getUUID();
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
	@Override//1.20.1//Team
	public Team getTeam() {
		LivingEntity livingEntity;
		if (this.getOwner() != null && (livingEntity = this.getOwner()) != null) {
			return livingEntity.getTeam();
		}
		return super.getTeam();
	}
	public double addX = 0;
	public double addY = 0;
	public double addZ = 0;
	@VisibleForTesting
	public void setScale(float f, float f2) {
		this.entityData.set(WIDTH, f);
		this.entityData.set(HEIGHT, f2);
		this.setBoundingBox(this.makeBoundingBox());
		this.reapplyPosition();
		this.refreshDimensions();
	}
	public float getScaleWidth() {
		return this.entityData.get(WIDTH);
	}
	public float getScaleHeight() {
		return this.entityData.get(HEIGHT);
	}
	public void setOrder(int n) {
		this.entityData.set(ORDER, n);
	}
	public int getOrder() {
		return this.entityData.get(ORDER);
	}
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Order", this.getOrder());
		compoundTag.putFloat("ScaleWidth", this.getScaleWidth());
		compoundTag.putFloat("ScaleHeight", this.getScaleHeight());
		compoundTag.putDouble("AddX", this.addX);
		compoundTag.putDouble("AddY", this.addY);
		compoundTag.putDouble("AddZ", this.addZ);
		compoundTag.putInt("SpellLevelDamage", this.spellLevelDamage);
		compoundTag.putInt("Life", this.life);
		if (this.ownerUUID != null) {
			compoundTag.putUUID("Owner", this.ownerUUID);
		}
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.setOrder(compoundTag.getInt("Order"));
		this.setScale(compoundTag.getFloat("ScaleWidth"), compoundTag.getFloat("ScaleHeight"));
		this.addX = compoundTag.getDouble("AddX");
		this.addY = compoundTag.getDouble("AddY");
		this.addZ = compoundTag.getDouble("AddZ");
		this.spellLevelDamage = compoundTag.getInt("SpellLevelDamage");
		this.life = compoundTag.getInt("Life");
		if (compoundTag.hasUUID("Owner")) {
			this.ownerUUID = compoundTag.getUUID("Owner");
		}
		this.setBoundingBox(this.makeBoundingBox());
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(WIDTH, 0.6f);
		this.getEntityData().define(HEIGHT, 1.8f);
		this.getEntityData().define(ORDER, 0);
	}
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		if (WIDTH.equals(entityDataAccessor)) {
			this.refreshDimensions();
		}
		if (HEIGHT.equals(entityDataAccessor)) {
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
		//复制位置
		if (getOwner() != null) {
			//
			String tick = "jerotes_has_mirror_image_" + this.getOrder() + "_tick";
			String x = "jerotes_has_mirror_image_" + this.getOrder() + "_x";
			String y = "jerotes_has_mirror_image_" + this.getOrder() + "_y";
			String z = "jerotes_has_mirror_image_" + this.getOrder() + "_z";
			Main.getJerotesPersistentData(getOwner()).putDouble(tick, 2);
			Main.getJerotesPersistentData(getOwner()).putDouble(x, this.addX);
			Main.getJerotesPersistentData(getOwner()).putDouble(y, this.addY);
			Main.getJerotesPersistentData(getOwner()).putDouble(z, this.addZ);
			getOwner().addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, false, false));
			if (this.level() instanceof ServerLevel serverLevel) {
				for (int i2 = 0; i2 < Math.min(6 * Main.mobHeight(getOwner()), 120); ++i2) {
					//散发粒子
					for (int i = 0; i < 3; ++i) {
						serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT,
								getOwner().getRandomX(0.5) + addX, getOwner().getRandomY() + addY, getOwner().getRandomZ(0.5) + addZ,
								0, 0, 0, 0, 0);
						serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT,
								getOwner().getRandomX(0.25) + addX, getOwner().getRandomY() + addY, getOwner().getRandomZ(0.25) + addZ,
								0, 0, 0, 0, 0);
					}
					serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, getOwner().getRandomX(0.5), getOwner().getRandomY(), getOwner().getRandomZ(0.5), 0, 0, 0, 0, 0);
					serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, getOwner().getRandomX(0.25), getOwner().getRandomY(), getOwner().getRandomZ(0.25), 0, 0, 0, 0, 0);
				}
			}
			//大小
			 if (this.tickCount % 20 == 0) {
				 if (!this.level().isClientSide()) {
					 this.setScale(Main.mobWidth(getOwner()), Main.mobHeight(getOwner()));
				 }
				 this.setBoundingBox(makeBoundingBox());
				 this.refreshDimensions();
			 }
			 //位置
			this.moveTo(getOwner().getX() + addX + (getOwner() instanceof JerotesChangeServerPlayer jerotesChangeServerPlayer ? jerotesChangeServerPlayer.jerotesGetKnownMovement().x : getOwner().getDeltaMovement().x),
					getOwner().getY() + addY,
					getOwner().getZ() + addZ + (getOwner() instanceof JerotesChangeServerPlayer jerotesChangeServerPlayer ? jerotesChangeServerPlayer.jerotesGetKnownMovement().z : getOwner().getDeltaMovement().z),
					getOwner().getYRot(),
					getOwner().getXRot());
			this.setDeltaMovement(getOwner().getDeltaMovement());
			this.setXRot(this.getOwner().getXRot());
			this.setYRot(this.getOwner().getYRot());
		}
		//主人解除
		if (getOwner() != null && getOwner() instanceof Player player && player.swinging) {
			if (Main.getTargetedEntity(player, 32) == this && this.tickCount % 5 == 0) {
				this.discardByOwner();
				player.swinging = false;
			}
		}
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
		this.addX = (serverLevelAccessor.getRandom().nextFloat() - 0.5f) * 2 * 4;
		this.addY = 0;
		this.addZ = (serverLevelAccessor.getRandom().nextFloat() - 0.5f) * 2 * 4;
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
