package com.jerotes.jerotes.entity.Other.Beam;

import com.jerotes.jerotes.init.JerotesDamageTypes;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.*;

public class BaseBeamEntity extends Entity implements OwnableEntity {
	private static final EntityDataAccessor<Float> LIGHT_LOCK_X = SynchedEntityData.defineId(BaseBeamEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> LIGHT_LOCK_Y = SynchedEntityData.defineId(BaseBeamEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> LIGHT_LOCK_Z = SynchedEntityData.defineId(BaseBeamEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> SELF_X = SynchedEntityData.defineId(BaseBeamEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> SELF_Y = SynchedEntityData.defineId(BaseBeamEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> SELF_Z = SynchedEntityData.defineId(BaseBeamEntity.class, EntityDataSerializers.FLOAT);
	private static final double MAX_DISTANCE = 64.0;

	public BaseBeamEntity(EntityType<? extends BaseBeamEntity> entityType, Level level) {
		super(entityType, level);
		this.noCulling = true;
	}

	@Override
	public void setDeltaMovement(double x, double y, double z) {}

	public boolean displayFireAnimation() {
		return false;
	}

	public boolean isInWall() {
		return false;
	}

	public void setLightLockX(float f){
		this.getEntityData().set(LIGHT_LOCK_X, f);
	}
	public float getLightLockX() {return this.getEntityData().get(LIGHT_LOCK_X);}
	public void setLightLockY(float f){
		this.getEntityData().set(LIGHT_LOCK_Y, f);
	}
	public float getLightLockY() {
		return this.getEntityData().get(LIGHT_LOCK_Y);
	}
	public void setLightLockZ(float f){
		this.getEntityData().set(LIGHT_LOCK_Z, f);
	}
	public float getLightLockZ() {
		return this.getEntityData().get(LIGHT_LOCK_Z);
	}
	public void setSelfX(float f){
		this.getEntityData().set(SELF_X, f);
	}
	public float getSelfX() {return this.getEntityData().get(SELF_X);}
	public void setSelfY(float f){
		this.getEntityData().set(SELF_Y, f);
	}
	public float getSelfY() {
		return this.getEntityData().get(SELF_Y);
	}
	public void setSelfZ(float f){
		this.getEntityData().set(SELF_Z, f);
	}
	public float getSelfZ() {
		return this.getEntityData().get(SELF_Z);
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
		if (this.ownerUUID != null) {
			compoundTag.putUUID("Owner", this.ownerUUID);
		}
		compoundTag.putInt("TickCount", tickCount);
		compoundTag.putFloat("LightLockX", this.getLightLockX());
		compoundTag.putFloat("LightLockY", this.getLightLockY());
		compoundTag.putFloat("LightLockZ", this.getLightLockZ());
		compoundTag.putFloat("SelfX", this.getSelfX());
		compoundTag.putFloat("SelfY", this.getSelfY());
		compoundTag.putFloat("SelfZ", this.getSelfZ());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compoundTag) {
		if (compoundTag.hasUUID("Owner")) {
			this.ownerUUID = compoundTag.getUUID("Owner");
		}
		this.tickCount = compoundTag.getInt("TickCount");
		this.setLightLockX(compoundTag.getFloat("LightLockX"));
		this.setLightLockY(compoundTag.getFloat("LightLockY"));
		this.setLightLockZ(compoundTag.getFloat("LightLockZ"));
		this.setSelfX(compoundTag.getFloat("SelfX"));
		this.setSelfY(compoundTag.getFloat("SelfY"));
		this.setSelfZ(compoundTag.getFloat("SelfZ"));
	}

	@Override
	protected void defineSynchedData() {
		this.getEntityData().define(LIGHT_LOCK_X, 0f);
		this.getEntityData().define(LIGHT_LOCK_Y, 0f);
		this.getEntityData().define(LIGHT_LOCK_Z, 0f);
		this.getEntityData().define(SELF_X, 0f);
		this.getEntityData().define(SELF_Y, 0f);
		this.getEntityData().define(SELF_Z, 0f);
	}
	protected int getMaxTick() {
		return 400;
	}
	public float thisTickRenderTime = 0;
	public float lastTickRenderTime = 6;
	public float selfXProgress = 0.0f;
	public float selfYProgress = 0.0f;
	public float selfZProgress = 0.0f;
	public float ownerXProgress = 0.0f;
	public float ownerYProgress = 0.0f;
	public float ownerZProgress = 0.0f;
	@Override
	public void tick() {
		super.tick();

		if (level().isClientSide()) {
			lastTickRenderTime = thisTickRenderTime;
			thisTickRenderTime = 0;
		}

		if (this.tickCount >= getMaxTick()) {
			this.discard();
			return;
		}
		if (this.getOwner() != null) {
			if (!this.level().isClientSide()) {
				this.setLightLockX((float) this.getOwner().getX());
				this.setLightLockY((float) (this.getOwner().getY(0.75f)));
				this.setLightLockZ((float) this.getOwner().getZ());
			}
		}
		else {
			if (!this.level().isClientSide()) {
				this.setLightLockX((float) this.getX());
				this.setLightLockY((float) this.getEyeY());
				this.setLightLockZ((float) this.getZ());
			}
		}

		if (!this.level().isClientSide()) {
			this.setSelfX((float) this.getX());
			this.setSelfY((float) this.getY());
			this.setSelfZ((float) this.getZ());
		}
		if (this.getOwner() == null) {
			return;
		}

		Vec3 prevPos = this.position();
		LivingEntity caster = this.getOwner();

		Vec3 targetPos = calculateTargetPosition(caster);
		List<Entity> hitEntities = findEntitiesOnRay(caster.getEyePosition(), targetPos, caster);
		if (!hitEntities.isEmpty()) {
			for (Entity entity : hitEntities) {
				if (entity == this) continue;
				if (entity == this.getOwner()) continue;
				if (!this.getOwner().hasLineOfSight(entity)) continue;
				this.customHurt(entity);
			}
		}

		double moveSpeed = this.tickCount > 30 ? 0.8 : 0.25;
		Vec3 newPos = prevPos.add(targetPos.subtract(prevPos).scale(moveSpeed));
		if (this.tickCount <= getMaxTick() * 0.95f) {
			this.setPos(newPos.x, newPos.y, newPos.z);
		}
		else {
			this.setPos(this.getX() * 0.8f + this.getOwner().getX() * 0.2f,
					this.getY() * 0.8f + this.getOwner().getY() * 0.2f,
					this.getZ() * 0.8f + this.getOwner().getZ() * 0.2f);
		}
		this.setYRot(caster.getYRot());
		this.setXRot(caster.getXRot());
	}
	protected void customHurt(Entity entity) {
		entity.hurt(AttackFind.findDamageType(this, JerotesDamageTypes.BYPASSES_COOLDOWN_MAGIC, this, this.getOwner()), 8);
	}

	private Vec3 calculateTargetPosition(LivingEntity caster) {
		Vec3 eyePos = caster.getEyePosition();
		Vec3 lookAngle = caster.getLookAngle();
		for (int i = 0; i < MAX_DISTANCE; i++) {
			Vec3 checkPos = eyePos.add(lookAngle.scale(i));
			ClipContext clipContext = new ClipContext(
					eyePos,
					eyePos.add(lookAngle.scale(i + 1)),
					ClipContext.Block.OUTLINE,
					ClipContext.Fluid.NONE,
					this
			);
			BlockHitResult hitResult = this.level().clip(clipContext);
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				return hitResult.getLocation();
			}
		}
		return eyePos.add(lookAngle.scale(MAX_DISTANCE));
	}

	public void lookAt(Entity target, float maxYawChange, float maxPitchChange) {
		if (target == null) return;

		double d0 = target.getX() - this.getX();
		double d1 = target.getY() - this.getEyeY();
		double d2 = target.getZ() - this.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);

		float f = (float)(Mth.atan2(d2, d0) * (180F / Math.PI)) - 90.0F;
		float f1 = (float)(-(Mth.atan2(d1, d3) * (180F / Math.PI)));

		this.setYRot(this.rotlerp(this.getYRot(), f, maxYawChange));
		this.setXRot(this.rotlerp(this.getXRot(), f1, maxPitchChange));
	}

	private float rotlerp(float current, float target, float maxChange) {
		float f = Mth.wrapDegrees(target - current);

		if (f > maxChange) {
			f = maxChange;
		}

		if (f < -maxChange) {
			f = -maxChange;
		}

		return current + f;
	}

	private List<Entity> findEntitiesOnRay(Vec3 start, Vec3 end, LivingEntity caster) {
		List<Entity> result = new ArrayList<>();
		HitResultContext hitContext = new HitResultContext(start, end);
		List<Entity> allEntities = this.level().getEntities(
				caster,
				new AABB(start, end).inflate(2.0),
				entity -> entity != caster && entity instanceof LivingEntity
		);
		for (Entity entity : allEntities) {
			AABB entityBox = entity.getBoundingBox().inflate(0.75f);
			Optional<Vec3> hitPoint = entityBox.clip(start, end);
			if (hitPoint.isPresent()) {
				result.add(entity);
				hitContext.addHit(entity, hitPoint.get());
			}
		}
		hitContext.sortByDistance();
		return hitContext.getEntities();
	}

	private static class HitResultContext {
		private final Vec3 start;
		private final Map<Entity, Vec3> hits = new HashMap<>();
		private final Map<Entity, Double> distances = new HashMap<>();

		public HitResultContext(Vec3 start, Vec3 end) {
			this.start = start;
		}

		public void addHit(Entity entity, Vec3 hitPoint) {
			hits.put(entity, hitPoint);
			distances.put(entity, start.distanceTo(hitPoint));
		}

		public void sortByDistance() {
		}
		public List<Entity> getEntities() {
			List<Entity> sorted = new ArrayList<>(hits.keySet());
			sorted.sort(Comparator.comparingDouble(e -> distances.get(e)));
			return sorted;
		}
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return distance < 4096.0;
	}
}