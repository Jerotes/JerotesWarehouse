package com.jerotes.jerotes.entity.Mob;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.entity.Interface.JerotesChangeServerPlayer;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.UUID;

public class AddHandEntity extends PathfinderMob implements JerotesEntity, OwnableEntity {
	private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(AddHandEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> MAX_LIFE = SynchedEntityData.defineId(AddHandEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> IS_FEMALE = SynchedEntityData.defineId(AddHandEntity.class, EntityDataSerializers.BOOLEAN);
	public AnimationState armWideScaleAnimationState = new AnimationState();
	public AnimationState armSlimScaleAnimationState = new AnimationState();

	public AddHandEntity(EntityType<? extends AddHandEntity> type, Level world) {
		super(type, world);
		this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0f;
		this.armorDropChances[EquipmentSlot.CHEST.getIndex()] = 0f;
		this.armorDropChances[EquipmentSlot.LEGS.getIndex()] = 0f;
		this.armorDropChances[EquipmentSlot.FEET.getIndex()] = 0f;
		this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 0f;
		this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 0f;
		this.fixupDimensions();
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 1);
		builder = builder.add(Attributes.FOLLOW_RANGE, 32);
		return builder;
	}

	@Override
	protected void registerGoals() {
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}
	@Override
	public boolean isOnFire() {
		return false;
	}
	@Override
	public boolean canFreeze() {
		return false;
	}
	@Override
	public boolean isFreezing() {
		return false;
	}
	@Override
	protected void doPush(Entity entity) {
	}
	@Override
	public boolean isPushedByFluid() {
		return false;
	}
	@Override
	public boolean isPushable() {
		return false;
	}
	@Override
	public boolean canBeSeenAsEnemy() {
		return false;
	}
	@Override
	public void knockback(double d, double d2, double d3) {
		super.knockback(0, 0, 0);
	}
	@Override
	public void push(double d, double d2, double d3) {
		super.push(0, 0, 0);
	}
	@Override
	public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
		return false;
	}
	@Override
	public boolean addEffect(MobEffectInstance mobEffectInstance, @Nullable Entity entity) {
		return false;
	}
	@Override
	public boolean canCollideWith(Entity entity) {
		return false;
	}
	@Override
	public boolean startRiding(Entity entityIn) {
		return false;
	}

	public int getSize() {
		return this.entityData.get(ID_SIZE);
	}
	@VisibleForTesting
	public void setSize(int n) {
		int n2 = Mth.clamp(n, 1, 10000);
		this.entityData.set(ID_SIZE, n2);
		this.reapplyPosition();
		this.refreshDimensions();
	}
	public int getMaxLife() {
		return this.entityData.get(MAX_LIFE);
	}
	public void setMaxLife(int n) {
		this.entityData.set(MAX_LIFE, n);
	}
	public boolean IsFemale(){
		return this.getEntityData().get(IS_FEMALE);
	}
	public void setFemale(boolean bl){
		this.getEntityData().set(IS_FEMALE, bl);
	}
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
	public int life;
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Life", this.life);
		compoundTag.putInt("MaxLife", this.getMaxLife());
		compoundTag.putInt("Size", this.getSize());
		compoundTag.putBoolean("IsFemale", this.IsFemale());
		if (this.ownerUUID != null) {
			compoundTag.putUUID("Owner", this.ownerUUID);
		}
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		this.life = compoundTag.getInt("Life");
		this.setMaxLife(compoundTag.getInt("MaxLife"));
		this.setSize(compoundTag.getInt("Size"));
		super.readAdditionalSaveData(compoundTag);
		this.setFemale(compoundTag.getBoolean("IsFemale"));
		if (compoundTag.hasUUID("Owner")) {
			this.ownerUUID = compoundTag.getUUID("Owner");
		}
		this.setBoundingBox(this.makeBoundingBox());
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(IS_FEMALE, false);
		this.getEntityData().define(ID_SIZE, 100);
		this.getEntityData().define(MAX_LIFE, 100);
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
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		if (ID_SIZE.equals(entityDataAccessor)) {
			this.refreshDimensions();
		}
	}
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
		float height = this.getSize() / 100f * 1.62f;
		if (this.isBaby()) {
			return this.getSize() / 100f * 0.86f;
		}
		return height;
	}
	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return super.getDimensions(pose).scale((float)this.getSize() / 100);
	}

	@Override
	public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
		return InteractionResult.PASS;
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
	public void setLastHurtMob(Entity entity) {
		if (this.getOwner() != null) {
			this.getOwner().setLastHurtMob(entity);
		}
		else {
			super.setLastHurtMob(entity);
		}
	}
	private boolean wasOwnerSwinging = false;

	@Override
	public void tick() {
		super.tick();
		this.life += 1;
		if (this.life >= getMaxLife()) {
			this.discard();
		}
		if (!this.IsFemale()) {
			this.armWideScaleAnimationState.startIfStopped(this.tickCount);
			this.armSlimScaleAnimationState.stop();
		}
		else {
			this.armSlimScaleAnimationState.startIfStopped(this.tickCount);
			this.armWideScaleAnimationState.stop();
		}
		//
		if (this.getOwner() != null && !this.getOwner().isAlive()) {
			this.discard();
		}
		//传送
		if (getOwner() != null) {
			this.setPos(getOwner().getX() + (getOwner() instanceof JerotesChangeServerPlayer jerotesChangeServerPlayer ? jerotesChangeServerPlayer.jerotesGetKnownMovement().x : getOwner().getDeltaMovement().x),
					getOwner().getY(),
					getOwner().getZ() + (getOwner() instanceof JerotesChangeServerPlayer jerotesChangeServerPlayer ? jerotesChangeServerPlayer.jerotesGetKnownMovement().z : getOwner().getDeltaMovement().z));
			this.setRot(getOwner().getYRot(), getOwner().getXRot());
			this.setYRot(getOwner().getYRot());
			this.setXRot(getOwner().getXRot());
			this.setYHeadRot(getOwner().getYHeadRot());
			this.setYBodyRot(getOwner().yBodyRot);

			this.xOld = getOwner().xOld;
			this.xo = getOwner().xo;
			this.yOld = getOwner().yOld;
			this.yo = getOwner().yo;
			this.zOld = getOwner().zOld;
			this.zo = getOwner().zo;
			this.xRotO = getOwner().xRotO;
			this.yRotO = getOwner().yRotO;
			this.yHeadRotO = getOwner().yHeadRotO;
			this.yBodyRotO = getOwner().yBodyRotO;

			this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

			this.hasImpulse = true;

			this.setDeltaMovement(getOwner().getDeltaMovement());
		}

		if (getOwner() != null && !this.level().isClientSide) { // 仅在服务端逻辑执行
			boolean isOwnerSwinging = getOwner().swinging; // 检查主人是否在挥臂
			if (isOwnerSwinging && !wasOwnerSwinging) {
				ClientboundAnimatePacket animatePacket = new ClientboundAnimatePacket(this, 0);
				if (this.level() instanceof ServerLevel serverLevel) {
					serverLevel.getChunkSource().broadcast(this, animatePacket);
				}
			}
			wasOwnerSwinging = isOwnerSwinging;
		}
	}
	public void setRotSelf(float p_19916_, float p_19917_) {
		this.setYRot(p_19916_ % 360.0F);
		this.setXRot(p_19917_ % 360.0F);
	}

	@Override
	protected float tickHeadTurn(float p_31644_, float p_31645_) {
		if (getOwner() != null) {
			this.setPos(getOwner().getX() + (getOwner() instanceof JerotesChangeServerPlayer jerotesChangeServerPlayer ? jerotesChangeServerPlayer.jerotesGetKnownMovement().x : getOwner().getDeltaMovement().x),
					getOwner().getY(),
					getOwner().getZ() + (getOwner() instanceof JerotesChangeServerPlayer jerotesChangeServerPlayer ? jerotesChangeServerPlayer.jerotesGetKnownMovement().z : getOwner().getDeltaMovement().z));
			this.setRot(getOwner().getYRot(), getOwner().getXRot());
			this.setYRot(getOwner().getYRot());
			this.setXRot(getOwner().getXRot());
			this.setYHeadRot(getOwner().getYHeadRot());
			this.setYBodyRot(getOwner().yBodyRot);

			this.xOld = getOwner().xOld;
			this.xo = getOwner().xo;
			this.yOld = getOwner().yOld;
			this.yo = getOwner().yo;
			this.zOld = getOwner().zOld;
			this.zo = getOwner().zo;
			this.xRotO = getOwner().xRotO;
			this.yRotO = getOwner().yRotO;
			this.yHeadRotO = getOwner().yHeadRotO;
			this.yBodyRotO = getOwner().yBodyRotO;

			this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();

			this.hasImpulse = true;

			this.setDeltaMovement(getOwner().getDeltaMovement());
		}
		else {
			this.yBodyRotO = this.yRotO;
			this.yBodyRot = this.getYRot();
		}
		return 0.0F;
	}

	@Override
	public void aiStep() {
		super.aiStep();
		this.updateSwingTime();
		this.updateNoActionTime();
	}
	protected void updateNoActionTime() {
		float f = this.getLightLevelDependentMagicValue();
		if (f > 0.5f) {
			this.noActionTime += 1.1;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		if (this.isDamageSourceBlocked(damageSource)) {
			return SoundEvents.SHIELD_BLOCK;
		}
		return damageSource.type().effects().sound();
	}

	@Override
	public boolean hurt(DamageSource damageSource, float f) {
		return false;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
		return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
	}
}
