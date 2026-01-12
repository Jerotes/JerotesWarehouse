package com.jerotes.jerotes.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class BasePartLivingEntity extends LivingEntity implements EntityTrueBasePart {
	private static final EntityDataAccessor<Integer> BODYINDEX = SynchedEntityData.defineId(BasePartLivingEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Optional<UUID>> PARENT_UUID = SynchedEntityData.defineId(BasePartLivingEntity.class, EntityDataSerializers.OPTIONAL_UUID);

	public BasePartLivingEntity(EntityType t, Level world) {
		super(t, world);
		multipartSize = t.getDimensions();
	}

	public BasePartLivingEntity(EntityType t, LivingEntity parent, float radius, float angleYaw, float offsetY) {
		super(t, parent.level());
		this.setParent(parent);
		this.radius = radius;
		this.angleYaw = (angleYaw + 90.0f) * Mth.DEG_TO_RAD;
		this.offsetY = offsetY;
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35);
		builder = builder.add(Attributes.FLYING_SPEED, 1.0);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
		builder = builder.add(Attributes.FOLLOW_RANGE, 64);
		return builder;
	}

	@Override
	public boolean is(@NotNull Entity entity) {
		return this == entity || this.getParent() == entity;
	}
	@Override
	public boolean isPickable() {
		return true;
	}
	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.RIGHT;
	}
	@Override
	public boolean startRiding(Entity entityIn) {
		return false;
	}
	@Override
	public boolean canBeSeenAsEnemy() {
		return false;
	}
	@Override
	public void pushEntities() {
	}
	@Nullable
	public ItemStack getPickResult() {
		Entity parent = this.getParent();
		return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
	}
	@Override
	protected void doPush(Entity entity) {
		if (!(entity.getType() == this.getType() || (this.getParent() != null && entity.getType() == this.getParent().getType()))) {
			super.doPush(entity);
		}
	}
	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return ImmutableList.of();
	}
	@Override
	public ItemStack getItemBySlot(EquipmentSlot slotIn) {
		return ItemStack.EMPTY;
	}
	@Override
	public void setItemSlot(EquipmentSlot slotIn, ItemStack stack) {
	}
	@Override
	public void playBlockFallSound() {
	}
	@Override
	public boolean canFreeze() {
		if (this.getParent() != null && !this.getParent().canFreeze()) {
			return false;
		}
		return super.canFreeze();
	}
	@Override
	public boolean isFreezing() {
		if (this.getParent() != null && !this.getParent().isFreezing()) {
			return false;
		}
		return super.isFreezing();
	}
	@Override
	public boolean isOnFire() {
		if (this.getParent() != null && !this.getParent().isOnFire()) {
			return false;
		}
		return super.isOnFire();
	}
	@Override
	protected void checkFallDamage(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		if (this.getParent() != null) {
		}
		else {
			super.checkFallDamage(d, bl, blockState, blockPos);
		}
	}
	public EntityDimensions multipartSize;
	public float radius;
	public float angleYaw;
	public float offsetY;
	public float damageMultiplier = 0.8f;
	public Entity cachedParent;
	@Nullable
	public UUID getParentId() {
		return this.entityData.get(PARENT_UUID).orElse(null);
	}
	public void setParentId(@Nullable UUID uniqueId) {
		this.entityData.set(PARENT_UUID, Optional.ofNullable(uniqueId));
	}
	public void setInitialPartPos(Entity parent) {
		this.setPos(parent.getX() + this.radius * Math.cos(parent.getYRot() * Mth.DEG_TO_RAD + this.angleYaw),
				parent.getY() + this.offsetY,
				parent.getZ() + this.radius * Math.sin(parent.getYRot() * Mth.DEG_TO_RAD + this.angleYaw));
	}
	public int getBodyIndex() {
		return this.entityData.get(BODYINDEX);
	}
	public void setBodyIndex(int index) {
		this.entityData.set(BODYINDEX, index);
	}
	public Entity getParent() {
		UUID id = getParentId();
		if (id != null && level() instanceof ServerLevel serverLevel) {
			return serverLevel.getEntity(id);
		}
		return null;
	}
	public void setParent(Entity entity) {
		this.setParentId(entity.getUUID());
	}
	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (this.getParentId() != null) {
			compound.putUUID("ParentUUID", this.getParentId());
		}
		compound.putInt("BodyIndex", this.getBodyIndex());
		compound.putFloat("AngleYaw", this.angleYaw);
		compound.putFloat("Radius", this.radius);
		compound.putFloat("OffsetY", this.offsetY);
		compound.putFloat("DamageMultiplier", this.damageMultiplier);
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.hasUUID("ParentUUID")) {
			this.setParentId(compound.getUUID("ParentUUID"));
		}
		this.setBodyIndex(compound.getInt("BodyIndex"));
		this.angleYaw = compound.getFloat("AngleYaw");
		this.radius = compound.getFloat("Radius");
		this.offsetY = compound.getFloat("OffsetY");
		this.damageMultiplier = compound.getFloat("DamageMultiplier");

	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(PARENT_UUID, Optional.empty());
		this.getEntityData().define(BODYINDEX, 0);
	}
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		if (PARENT_UUID.equals(entityDataAccessor)) {
			this.refreshDimensions();
		}
		if (BODYINDEX.equals(entityDataAccessor)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(entityDataAccessor);
	}

	@Override
	public void tick() {
		this.isInsidePortal = false;
		if (this.tickCount > 10) {
			if (this.cachedParent == null || this.tickCount % 20 == 0) {
				this.cachedParent = this.getParent();
			}
			Entity parent = this.cachedParent;
			if (this.tickCount % 10 == 0) {
				refreshDimensions();
			}
			if (parent != null && !this.level().isClientSide) {
				this.setNoGravity(parent.isNoGravity());
				double targetX = parent.xo + this.radius * Math.cos(parent.yRotO * Mth.DEG_TO_RAD + this.angleYaw);
				double targetY = parent.yo + this.offsetY;
				double targetZ = parent.zo + this.radius * Math.sin(parent.yRotO * Mth.DEG_TO_RAD + this.angleYaw);
				this.setPos(Mth.lerp(0.75, this.getX(), targetX),
						Mth.lerp(0.75, this.getY(), targetY),
						Mth.lerp(0.75, this.getZ(), targetZ));

				final double d0 = parent.getX() - this.getX();
				final double d1 = parent.getY() - this.getY();
				final double d2 = parent.getZ() - this.getZ();
				final float f2 = -((float) (Mth.atan2(d1, Mth.sqrt((float)(d0 * d0 + d2 * d2))) * Mth.RAD_TO_DEG));
				this.setXRot(this.limitAngle(this.getXRot(), f2, 5.0f));
				if (parent instanceof LivingEntity livingParent && livingParent.hurtTime > 0) {
					this.markHurt();
				}

				this.setYRot(Mth.lerp(0.75f, this.getYRot(), parent.yRotO));
				this.yHeadRot = this.getYRot();
				this.yBodyRot = Mth.lerp(0.25f, this.getYRot(), parent.yRotO);

				if (parent instanceof LivingEntity livingParent) {
					if (!this.level().isClientSide && (livingParent.hurtTime > 0 || livingParent.deathTime > 0)) {
						this.hurtTime = livingParent.hurtTime;
						this.deathTime = livingParent.deathTime;
					}
				}
				if (parent.isRemoved() && !this.level().isClientSide) {
					this.remove(RemovalReason.DISCARDED);
				}
			}
			else if (tickCount > 20 && !this.level().isClientSide) {
				remove(RemovalReason.DISCARDED);
			}
		}
		Collection<MobEffectInstance> collection = this.getActiveEffects();
		if (!collection.isEmpty() && getParent() != null && getParent() instanceof LivingEntity livingEntity) {
			for (MobEffectInstance effect : collection) {
				if (!livingEntity.hasEffect(effect.getEffect())) {
					livingEntity.addEffect(new MobEffectInstance(effect));
				}
			}
			this.removeAllEffects();
		}
		if (this.getRemainingFireTicks() > 0) {
			if (getParent() instanceof LivingEntity livingEntity && this.getRemainingFireTicks() > livingEntity.getRemainingFireTicks()) {
				livingEntity.setRemainingFireTicks(this.getRemainingFireTicks());
			}
			this.clearFire();
		}
		if (this.getTicksFrozen() > 0) {
			if (getParent() instanceof LivingEntity livingEntity && this.getTicksFrozen() > livingEntity.getTicksFrozen()) {
				livingEntity.setTicksFrozen(this.getTicksFrozen());
			}
			this.setTicksFrozen(0);
		}
		super.tick();
	}

	@Override
	public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
		Entity parent = getParent();
		return parent != null ? parent.interact(player, hand) : InteractionResult.PASS;
	}

	@Override
	public boolean hurt(DamageSource damagesource, float amount) {
		if (isInvulnerableTo(damagesource)) {
			return super.hurt(damagesource, amount);
		}
		final Entity parent = getParent();
		return parent != null && parent.hurt(damagesource, amount * this.damageMultiplier);
	}
	@Override
	public boolean isInvulnerableTo(@NotNull DamageSource source) {
		return source.is(DamageTypeTags.IS_FALL)
				|| source.is(DamageTypeTags.IS_DROWNING)
				|| source.is(DamageTypes.IN_WALL)
				|| source.is(DamageTypes.FALLING_BLOCK)
				|| source.is(DamageTypes.LAVA)
				|| source.is(DamageTypeTags.IS_FIRE)
				|| source.is(DamageTypes.CRAMMING)
				|| source.is(DamageTypes.DRY_OUT)
				|| source.is(DamageTypes.CACTUS)
				|| source.is(DamageTypes.SWEET_BERRY_BUSH)
				|| super.isInvulnerableTo(source);
	}

	@Override
	public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
		if (parent.deathTime > 0) {
			this.deathTime = parent.deathTime;
		}
		if (parent.hurtTime > 0) {
			this.hurtTime = parent.hurtTime;
		}
	}
}