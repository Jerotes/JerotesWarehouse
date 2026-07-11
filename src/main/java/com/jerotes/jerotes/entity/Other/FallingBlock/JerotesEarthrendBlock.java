package com.jerotes.jerotes.entity.Other.FallingBlock;

import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class JerotesEarthrendBlock extends BaseFallingBlock implements OwnableEntity {
	private static final EntityDataAccessor<String> MODE = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Float> ANIM_V_Y = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> PUSH_TICK = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> PUSH_SPEED = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> PUSH_ADD_HEIGHT = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> CAN_BREAK_BLOCK = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> BREAK_BLOCK_LEVEL = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> ATTACK_DAMAGE = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> CAN_PUSH = SynchedEntityData.defineId(JerotesEarthrendBlock.class, EntityDataSerializers.BOOLEAN);

	public JerotesEarthrendBlock(EntityType<JerotesEarthrendBlock> entityType, Level level) {
		super(entityType, level);
		this.setDuration(20);
	}


	public JerotesEarthrendBlock(Level worldIn, BlockState blockState, float vy) {
		super(JerotesEntityType.JEROTES_EARTHREND_BLOCK.get(), worldIn);
		this.setMode(FallingMoveType.RENDER_MOVE);
		setBlockState(blockState);
		setAnimVY(vy);
	}

	public JerotesEarthrendBlock(Level level, double px, double py, double pz, BlockState blockState, int duration) {
		super(JerotesEntityType.JEROTES_EARTHREND_BLOCK.get(), level);
		this.setMode(FallingMoveType.OVERALL_MOVE);
		this.setBlockState(blockState);
		this.setPos(px, py + (double) ((1.0F - this.getBbHeight()) / 2.0F), pz);
		this.setDuration(duration);
		this.xo = px;
		this.yo = py;
		this.zo = pz;
		this.setDeltaMovement(Vec3.ZERO);
	}
	public void push(Entity entity) {
		if (isCanPush()) {
			super.push(entity);
		}
	}
	@Override
	public void setDeltaMovement(double x, double y, double z) {
		if (getMode() == FallingMoveType.OVERALL_MOVE) {
			super.setDeltaMovement(x, y, z);
		}
	}
	public boolean displayFireAnimation() {
		return false;
	}

	public FallingMoveType getMode() {
		String mode = this.getEntityData().get(MODE);
		if (mode.isEmpty()) return FallingMoveType.RENDER_MOVE;
		return FallingMoveType.valueOf(mode);
	}
	public void setMode(FallingMoveType type) {
		this.getEntityData().set(MODE, type.toString());
	}
	public float getAnimVY() {
		return getEntityData().get(ANIM_V_Y);
	}
	private void setAnimVY(float vy) {
		getEntityData().set(ANIM_V_Y, vy);
	}
	public int getDuration() {
		return getEntityData().get(DURATION);
	}
	public void setDuration(int duration) {
		getEntityData().set(DURATION, duration);
	}
	public int getPushTick() {
		return getEntityData().get(PUSH_TICK);
	}
	public void setPushTick(int pushTick) {
		getEntityData().set(PUSH_TICK, pushTick);
	}
	public float getPushSpeed() {
		return getEntityData().get(PUSH_SPEED);
	}
	public void setPushSpeed(float pushSpeed) {
		getEntityData().set(PUSH_SPEED, pushSpeed);
	}
	public float getPushAddHeight() {
		return getEntityData().get(PUSH_ADD_HEIGHT);
	}
	public void setPushAddHeight(float pushAddHeight) {
		getEntityData().set(PUSH_ADD_HEIGHT, pushAddHeight);
	}
	public boolean isCanBreakBlock() {
		return this.entityData.get(CAN_BREAK_BLOCK);
	}
	public void setCanBreakBlock(boolean bl) {
		this.entityData.set(CAN_BREAK_BLOCK, bl);
	}
	public float getBreakBlockLevel() {
		return getEntityData().get(BREAK_BLOCK_LEVEL);
	}
	public void setBreakBlockLevel(float breakBlockLevel) {
		getEntityData().set(BREAK_BLOCK_LEVEL, breakBlockLevel);
	}
	public float getAttackDamage() {
		return getEntityData().get(ATTACK_DAMAGE);
	}
	public void setAttackDamage(float attackDamage) {
		getEntityData().set(ATTACK_DAMAGE, attackDamage);
	}
	public float getGravity() {
		return getEntityData().get(GRAVITY);
	}
	public void setGravity(float gravity) {
		getEntityData().set(GRAVITY, gravity);
	}
	public boolean isCanPush() {
		return this.entityData.get(CAN_PUSH);
	}
	public void setCanPush(boolean bl) {
		this.entityData.set(CAN_PUSH, bl);
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
	@Override//1.20.1//Team
	public Team getTeam() {
		LivingEntity livingEntity;
		if (this.getOwner() != null && (livingEntity = this.getOwner()) != null) {
			return livingEntity.getTeam();
		}
		return super.getTeam();
	}
	@Override
	protected void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		if (this.ownerUUID != null) {
			compoundTag.putUUID("Owner", this.ownerUUID);
		}
		compoundTag.putFloat("AnimVY", getAnimVY());
		compoundTag.putInt("Duration", getDuration());
		compoundTag.putInt("PushTick", getPushTick());
		compoundTag.putFloat("PushSpeed", getPushSpeed());
		compoundTag.putFloat("PushAddHeight", getPushAddHeight());
		compoundTag.putBoolean("CanBreakBlock", isCanBreakBlock());
		compoundTag.putFloat("BreakBlockLevel", getBreakBlockLevel());
		compoundTag.putFloat("AttackDamage", getAttackDamage());
		compoundTag.putFloat("Gravity", getGravity());
		compoundTag.putBoolean("CanPush", isCanPush());
	}
	@Override
	protected void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		if (compoundTag.hasUUID("Owner")) {
			this.ownerUUID = compoundTag.getUUID("Owner");
		}
		this.setAnimVY(compoundTag.getFloat("AnimVY"));
		this.setDuration(compoundTag.getInt("Duration"));
		this.setPushTick(compoundTag.getInt("PushTick"));
		this.setPushSpeed(compoundTag.getInt("PushSpeed"));
		this.setPushAddHeight(compoundTag.getInt("PushAddHeight"));
		this.setCanBreakBlock(compoundTag.getBoolean("CanBreakBlock"));
		this.setBreakBlockLevel(compoundTag.getInt("BreakBlockLevel"));
		this.setAttackDamage(compoundTag.getInt("AttackDamage"));
		this.setGravity(compoundTag.getInt("Gravity"));
		this.setCanPush(compoundTag.getBoolean("CanPush"));
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(MODE, FallingMoveType.OVERALL_MOVE.toString());
		this.getEntityData().define(ANIM_V_Y, 1f);
		this.getEntityData().define(DURATION, 120);
		this.getEntityData().define(PUSH_TICK, -10);
		this.getEntityData().define(PUSH_SPEED, 2.4f);
		this.getEntityData().define(PUSH_ADD_HEIGHT, 0.2f);
		this.getEntityData().define(BREAK_BLOCK_LEVEL, 5f);
		this.getEntityData().define(ATTACK_DAMAGE, 10f);
		this.getEntityData().define(CAN_BREAK_BLOCK, true);
		this.getEntityData().define(GRAVITY, 0.1f);
		this.getEntityData().define(CAN_PUSH, true);
	}

	@Override
	public void tick() {
		if (getMode() == FallingMoveType.RENDER_MOVE)
			setDeltaMovement(0, 0, 0);
		super.tick();
		if (getMode() == FallingMoveType.OVERALL_MOVE) {
			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, getGravity() / 2, 0.0D));
			}
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
			if ((this.onGround() && this.tickCount > this.getDuration()) || this.tickCount > this.getDuration() * 20) {
				this.attack();
			}
			if (this.horizontalCollision || this.verticalCollision) {
				this.attack();
			}
		} else {
			float animVY = getAnimVY();
			prevAnimY = animY;
			animY += animVY;
			setAnimVY(animVY - getGravity());
			if (animY < -0.5)
				attack();
		}
		//推动
		if (this.getPushTick() > 0 && this.getOwner() != null) {
			this.setPushTick(this.getPushTick() - 1);
			if (this.getPushTick() == 0) {
				float f = this.getOwner().getYRot();
				float f2 = this.getOwner().getXRot();
				float f3 = -Mth.sin(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
				float f4 = -Mth.sin(f2 * 0.017453292f);
				float f5 = Mth.cos(f * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
				float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
				float f7 = getPushSpeed();
				this.push(f3 *= f7 / f6, f4 *= f7 / f6 + getPushAddHeight(), f5 *= f7 / f6);
			}
		}
		//命中
		List<LivingEntity> listAttack = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
		listAttack.removeIf(livingEntity -> this.getOwner() != null	&& AttackFind.FindCanNotAttack(this.getOwner(), livingEntity));
		for (LivingEntity hurt : listAttack) {
			if (hurt == null) continue;
			if (hurt == this.getOwner()) continue;
			attack();
			break;
		}
	}
	public void attack() {
		if (RandomSource.create().nextFloat() > 0.75f) {
			this.playSound(SoundEvents.PLAYER_BIG_FALL);
		}
		//攻击
		List<LivingEntity> listAttack = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
		listAttack.removeIf(livingEntity -> this.getOwner() != null	&& AttackFind.FindCanNotAttack(this.getOwner(), livingEntity));
		for (LivingEntity hurt : listAttack) {
			if (hurt == null) continue;
			if (hurt == this.getOwner()) continue;
			if (hurt instanceof OwnableEntity ownable && ownable.getOwner() == this.getOwner()) continue;
			if (this.getOwner() != null	&& AttackFind.FindCanNotAttack(this.getOwner(), hurt)) continue;
			hurt.hurt(this.damageSources().mobProjectile(this, this.getOwner()), getAttackDamage());
		}
		//粒子
		if (this.level() instanceof ServerLevel _level && !this.getBlockState().isAir()) {
			for (int i = 0; i < 3; ++i) {
				_level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockState()), this.getX(), this.getY(), this.getZ(), 15, 0.3, 0, 0.3, 0);
			}
		}
		if (!this.level().isClientSide()) {
			if (this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.isCanBreakBlock() && !(this.getOwner() != null && this.getY() < this.getOwner().getY())) {
				boolean bl = this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
				AABB aABB = this.getBoundingBox().inflate(0.5f).move(0,0.5,0);
				for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
					BlockState blockState = this.level().getBlockState(blockPos);
					float block = blockState.getDestroySpeed(this.level(), blockPos);
					if (block >= getBreakBlockLevel() || block < 0f) continue;
					if (!ForgeEventFactory.onEntityDestroyBlock(this.getOwner(), blockPos, blockState)) continue;
					if (!ForgeHooks.canEntityDestroy(this.level(), blockPos, this.getOwner())) continue;
					if ((blockState.is(BlockTags.REPLACEABLE_BY_TREES) || blockState.is(BlockTags.DIRT) || blockState.is(BlockTags.SCULK_REPLACEABLE) || blockState.is(BlockTags.STONE_ORE_REPLACEABLES)) && this.random.nextFloat() > 0.05) {
						bl = this.level().destroyBlock(blockPos, false, this.getOwner() != null ? this.getOwner() : this) || bl;
					} else {
						bl = this.level().destroyBlock(blockPos, true, this.getOwner() != null ? this.getOwner() : this) || bl;
					}
				}
			}
		}
		this.discard();
	}
}