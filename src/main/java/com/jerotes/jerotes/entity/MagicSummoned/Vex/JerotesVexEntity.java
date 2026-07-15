package com.jerotes.jerotes.entity.MagicSummoned.Vex;

import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.MagicSummoned.MagicSummonedEntity;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.util.EntityAndItemFind;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class JerotesVexEntity extends MagicSummonedEntity implements JerotesEntity {
	public static final int TICKS_PER_FLAP = Mth.ceil(3.9269907F);
	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(JerotesVexEntity.class, EntityDataSerializers.BYTE);

	public JerotesVexEntity(EntityType<? extends JerotesVexEntity> entityType, Level level) {
		super(entityType, level);
		this.setMaxUpStep(0.6f);
		this.xpReward = 10;
		this.moveControl = new JerotesVexMoveControl(this);
	}
	public boolean isJerotesFlyingMob() {
		return true;
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.15);
		builder = builder.add(Attributes.FLYING_SPEED, 1.25);
		builder = builder.add(Attributes.MAX_HEALTH, 14);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		return builder;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new JerotesVexChargeAttackGoal());
		this.goalSelector.addGoal(4, new JerotesVexRandomMoveGoal());
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
	}
	class JerotesVexChargeAttackGoal extends Goal {
		public JerotesVexChargeAttackGoal() {
			this.setFlags(EnumSet.of(Flag.MOVE));
		}
		public boolean canUse() {
			if (JerotesVexEntity.this.getTarget() == null && JerotesVexEntity.this.getOwner() != null)
				return false;
			LivingEntity livingentity = JerotesVexEntity.this.getTarget();
			if (livingentity != null && livingentity.isAlive() && !JerotesVexEntity.this.getMoveControl().hasWanted() && JerotesVexEntity.this.random.nextInt(reducedTickDelay(7)) == 0) {
				return JerotesVexEntity.this.distanceToSqr(livingentity) > 4.0D;
			} else {
				return false;
			}
		}
		public boolean canContinueToUse() {
			if (JerotesVexEntity.this.getTarget() == null && JerotesVexEntity.this.getOwner() != null)
				return false;
			return JerotesVexEntity.this.getMoveControl().hasWanted() &&
					JerotesVexEntity.this.isCharging() &&
					JerotesVexEntity.this.getTarget() != null &&
					JerotesVexEntity.this.getTarget().isAlive();
		}
		public void start() {
			LivingEntity livingentity = JerotesVexEntity.this.getTarget();
			if (livingentity != null) {
				Vec3 vec3 = livingentity.getEyePosition();
				JerotesVexEntity.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
			}
			JerotesVexEntity.this.setIsCharging(true);
			JerotesVexEntity.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
		}
		public void stop() {
			JerotesVexEntity.this.setIsCharging(false);
			JerotesVexEntity.this.moveControl.setWantedPosition(JerotesVexEntity.this.getX(), JerotesVexEntity.this.getY(), JerotesVexEntity.this.getZ(), 1.0D);
			if (JerotesVexEntity.this.moveControl instanceof JerotesVexMoveControl jerotesVexMoveControl) {
				jerotesVexMoveControl.startWait();
			}
			JerotesVexEntity.this.getNavigation().stop();
		}
		public boolean requiresUpdateEveryTick() {
			return true;
		}
		public void tick() {
			LivingEntity livingentity = JerotesVexEntity.this.getTarget();
			if (livingentity != null) {
				if (JerotesVexEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
					JerotesVexEntity.this.doHurtTarget(livingentity);
					JerotesVexEntity.this.setIsCharging(false);
				} else {
					double d0 = JerotesVexEntity.this.distanceToSqr(livingentity);
					if (d0 < 9.0D) {
						Vec3 vec3 = livingentity.getEyePosition();
						JerotesVexEntity.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0D);
					}
				}
			}
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.VEX_AMBIENT;
	}
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.VEX_DEATH;
	}
	@Override
	protected SoundEvent getHurtSound(DamageSource p_34023_) {
		return SoundEvents.VEX_HURT;
	}
	@Override
	public boolean isLeftHanded() {
		return false;
	}
	public float getLightLevelDependentMagicValue() {
		return 1.0F;
	}
	public boolean isCharging() {
		return this.getVexFlag(1);
	}
	public void setIsCharging(boolean p_34043_) {
		this.setVexFlag(1, p_34043_);
	}
	public void setLimitedLife(int p_33988_) {
		this.hasLimitedLife = true;
		this.limitedLifeTicks = p_33988_;
	}
	private boolean getVexFlag(int p_34011_) {
		int i = this.entityData.get(DATA_FLAGS_ID);
		return (i & p_34011_) != 0;
	}
	private void setVexFlag(int p_33990_, boolean p_33991_) {
		int i = this.entityData.get(DATA_FLAGS_ID);
		if (p_33991_) {
			i |= p_33990_;
		} else {
			i &= ~p_33990_;
		}
		this.entityData.set(DATA_FLAGS_ID, (byte)(i & 255));
	}
	@Nullable
	public BlockPos getBoundOrigin() {
		return this.boundOrigin;
	}
	public void setBoundOrigin(@Nullable BlockPos p_34034_) {
		this.boundOrigin = p_34034_;
	}
	@Override
	public boolean causeFallDamage(float l, float d, DamageSource source) {
		return false;
	}
	@Override
	protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}
	protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
		return entityDimensions.height - 0.28125F;
	}
	@Override
	protected PathNavigation createNavigation(Level world) {
		return new FlyingPathNavigation(this, world);
	}
	public boolean isFlapping() {
		return this.tickCount % TICKS_PER_FLAP == 0;
	}
	public void move(MoverType p_33997_, Vec3 p_33998_) {
		super.move(p_33997_, p_33998_);
		this.checkInsideBlocks();
	}
	public AABB getAttackBoundingBox() {
		Entity entity = this.getVehicle();
		AABB aabb;
		if (entity != null) {
			AABB aabb1 = entity.getBoundingBox();
			AABB aabb2 = this.getBoundingBox();
			aabb = new AABB(Math.min(aabb2.minX, aabb1.minX), aabb2.minY, Math.min(aabb2.minZ, aabb1.minZ), Math.max(aabb2.maxX, aabb1.maxX), aabb2.maxY, Math.max(aabb2.maxZ, aabb1.maxZ));
		} else {
			aabb = this.getBoundingBox();
		}
		AABB aabb1 = aabb.inflate(Math.sqrt((double)2.04F) - (double)0.6F, 0.0D, Math.sqrt((double)2.04F) - (double)0.6F);
		return aabb1.inflate(0.25d, 0.25d, 0.25d);
	}
	@Override
	public boolean isWithinMeleeAttackRange(LivingEntity livingEntity) {
		return this.getAttackBoundingBox().intersects(livingEntity.getBoundingBox());
	}

	public float thisTickRenderTime = 0;
	public float lastTickRenderTime = 6;
	@Nullable
	private BlockPos boundOrigin;
	private boolean hasLimitedLife;
	private int limitedLifeTicks;
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		if (this.boundOrigin != null) {
			compoundTag.putInt("BoundX", this.boundOrigin.getX());
			compoundTag.putInt("BoundY", this.boundOrigin.getY());
			compoundTag.putInt("BoundZ", this.boundOrigin.getZ());
		}
		if (this.hasLimitedLife) {
			compoundTag.putInt("LifeTicks", this.limitedLifeTicks);
		}
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		if (compoundTag.contains("BoundX")) {
			this.boundOrigin = new BlockPos(compoundTag.getInt("BoundX"), compoundTag.getInt("BoundY"), compoundTag.getInt("BoundZ"));
		}
		if (compoundTag.contains("LifeTicks")) {
			this.setLimitedLife(compoundTag.getInt("LifeTicks"));
		}
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(DATA_FLAGS_ID, (byte)0);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		super.onSyncedDataUpdated(entityDataAccessor);
	}

	public boolean isInWallSelf() {
		float f = this.getBbWidth() * 0.8F;
		AABB aabb = AABB.ofSize(this.getEyePosition(), (double)f, 1.0E-6, (double)f);
		return BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
			BlockState blockstate = this.level().getBlockState(p_201942_);
			return !blockstate.isAir() && blockstate.isSuffocating(this.level(), p_201942_) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.level(), p_201942_).move((double)p_201942_.getX(), (double)p_201942_.getY(), (double)p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
		});
	}
	public void tick() {
		this.noPhysics = true;
		super.tick();
		this.noPhysics = false;
		this.setNoGravity(true);
		if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
			this.limitedLifeTicks = 20;
			this.hurt(this.damageSources().starve(), 4.0F);
		}
		if (level().isClientSide()) {
			lastTickRenderTime = thisTickRenderTime;
			thisTickRenderTime = 0;
		}
	}
	public void aiStep() {
		super.aiStep();
		if (!this.level().isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
			this.heal(1.0F);
		}
		Vec3 vec3 = this.getDeltaMovement();
		//上升
		if (this.getOnPos().getY() < this.level().getMinBuildHeight() && this.getTarget() == null || this.isInWallSelf()) {
			this.setDeltaMovement(this.getDeltaMovement().add(0.0, (0.3 - vec3.y) * 0.3, 0.0));
		}
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (!this.level().isClientSide()) {
			int attackRandom = this.getRandom().nextInt(30);
			this.setAnimTick(20);
			if (attackRandom > 15) {
				this.setAnimationState("attack1");
			}
			else {
				this.setAnimationState("attack2");
			}
		}
		boolean bl = super.doHurtTarget(entity);
		if (bl) {
			if (JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && this.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK)) {
				ItemStack hand = this.getMainHandItem();
				hand.hurtAndBreak(1, this, player -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}
		}
		return bl;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		if (damageSource.is(DamageTypeTags.IS_FALL)
				|| damageSource.is(DamageTypeTags.IS_FIRE)
		)
			return true;
		return super.isInvulnerableTo(damageSource);
	}

	@Override
	public boolean hurt(DamageSource damagesource, float amount) {
		if (isInvulnerableTo(damagesource)) {
			return super.hurt(damagesource, amount);
		}
		return super.hurt(damagesource, amount);
	}
	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34002_, DifficultyInstance p_34003_, MobSpawnType p_34004_, @Nullable SpawnGroupData p_34005_, @Nullable CompoundTag p_34006_) {
		RandomSource randomsource = p_34002_.getRandom();
		this.populateDefaultEquipmentSlots(randomsource, p_34003_);
		this.populateDefaultEquipmentEnchantments(randomsource, p_34003_);
		return super.finalizeSpawn(p_34002_, p_34003_, p_34004_, p_34005_, p_34006_);
	}

	@Override
	public void tickDeath() {
		if(deathTime <= 0){
			this.setAnimTick(20);
			this.setAnimationState("dead");
		}
		++this.deathTime;
		if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
			this.level().broadcastEntityEvent(this, (byte)60);
			this.remove(RemovalReason.KILLED);
		}
	}

	protected void populateDefaultEquipmentSlots(RandomSource p_219135_, DifficultyInstance p_219136_) {
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
		this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
	}

	class JerotesVexMoveControl extends FlyingMoveControl {
		public JerotesVexMoveControl(JerotesVexEntity jerotesVexEntity) {
			super(jerotesVexEntity, 10, true);
		}

		public void startWait() {
			this.operation = Operation.MOVE_TO;
		}
		public void tick() {
			if (this.operation == Operation.MOVE_TO) {
				Vec3 vec3 = new Vec3(this.wantedX - JerotesVexEntity.this.getX(), this.wantedY - JerotesVexEntity.this.getY(), this.wantedZ - JerotesVexEntity.this.getZ());
				double d0 = vec3.length();
				if (d0 < JerotesVexEntity.this.getBoundingBox().getSize()) {
					this.operation = Operation.WAIT;
					JerotesVexEntity.this.setDeltaMovement(JerotesVexEntity.this.getDeltaMovement().scale(0.5D));
				} else {
					JerotesVexEntity.this.setDeltaMovement(JerotesVexEntity.this.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05D / d0)));
					if (JerotesVexEntity.this.getTarget() == null) {
						Vec3 vec31 = JerotesVexEntity.this.getDeltaMovement();
						JerotesVexEntity.this.setYRot(-((float)Mth.atan2(vec31.x, vec31.z)) * (180F / (float)Math.PI));
						JerotesVexEntity.this.yBodyRot = JerotesVexEntity.this.getYRot();
					} else {
						double d2 = JerotesVexEntity.this.getTarget().getX() - JerotesVexEntity.this.getX();
						double d1 = JerotesVexEntity.this.getTarget().getZ() - JerotesVexEntity.this.getZ();
						JerotesVexEntity.this.setYRot(-((float)Mth.atan2(d2, d1)) * (180F / (float)Math.PI));
						JerotesVexEntity.this.yBodyRot = JerotesVexEntity.this.getYRot();
					}
				}

			}
		}
	}
	class JerotesVexRandomMoveGoal extends Goal {
		public JerotesVexRandomMoveGoal() {
			this.setFlags(EnumSet.of(Flag.MOVE));
		}

		public boolean canUse() {
			return !JerotesVexEntity.this.getMoveControl().hasWanted() && JerotesVexEntity.this.random.nextInt(reducedTickDelay(7)) == 0;
		}

		public boolean canContinueToUse() {
			return false;
		}

		public void tick() {
			BlockPos blockpos = JerotesVexEntity.this.getBoundOrigin();
			if (blockpos == null) {
				blockpos = JerotesVexEntity.this.blockPosition();
			}

			for(int i = 0; i < 3; ++i) {
				BlockPos blockpos1 = blockpos.offset(JerotesVexEntity.this.random.nextInt(15) - 7, JerotesVexEntity.this.random.nextInt(11) - 5, JerotesVexEntity.this.random.nextInt(15) - 7);
				if (JerotesVexEntity.this.level().isEmptyBlock(blockpos1)) {
					JerotesVexEntity.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
					if (JerotesVexEntity.this.getTarget() == null) {
						JerotesVexEntity.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}
					break;
				}
			}

		}
	}
}

