package com.jerotes.jerotes.entity.Mob;

import com.google.common.annotations.VisibleForTesting;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Interface.UseShieldEntity;
import com.jerotes.jerotes.goal.JerotesMoveToGoal;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.util.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class TestEntity extends PathfinderMob implements JerotesEntity, UseShieldEntity {
	private static final EntityDataAccessor<Boolean> IS_CAN_MOVE = SynchedEntityData.defineId(TestEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_FEMALE = SynchedEntityData.defineId(TestEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(TestEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SHIELD_LEVEL = SynchedEntityData.defineId(TestEntity.class, EntityDataSerializers.INT);
	public AnimationState armWideScaleAnimationState = new AnimationState();
	public AnimationState armSlimScaleAnimationState = new AnimationState();

	public TestEntity(EntityType<? extends TestEntity> type, Level world) {
		super(type, world);
		this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 2f;
		this.armorDropChances[EquipmentSlot.CHEST.getIndex()] = 2f;
		this.armorDropChances[EquipmentSlot.LEGS.getIndex()] = 2f;
		this.armorDropChances[EquipmentSlot.FEET.getIndex()] = 2f;
		this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2f;
		this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 2f;
		this.fixupDimensions();
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35);
		builder = builder.add(Attributes.MAX_HEALTH, 1024);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 128);
		return builder;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new JerotesMoveToGoal(TestEntity.this, 1.0f, true) {
			@Override
			public boolean canUse() {
				return super.canUse() && TestEntity.this.isCanMove();
			}
		});
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
	}

	protected Vector3f getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
		return new Vector3f(0.0f, entityDimensions.height + 0.0625f * f, 0.0f);
	}
	protected float ridingOffset(Entity entity) {
		return -0.7f;
	}
	public double getMyRidingOffset() {
		return this.isBaby() ? 0.0D : -0.45D;
	}
	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	public boolean isCanMove() {
		return this.getEntityData().get(IS_CAN_MOVE);
	}
	public void setCanMove(boolean bl) {
		this.getEntityData().set(IS_CAN_MOVE, bl);
	}
	public void setShieldLevel(int n){
		this.getEntityData().set(SHIELD_LEVEL, n);
	}
	public int getShieldLevel(){
		return this.getEntityData().get(SHIELD_LEVEL);
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
	public boolean IsFemale(){
		return this.getEntityData().get(IS_FEMALE);
	}
	public void setFemale(boolean bl){
		this.getEntityData().set(IS_FEMALE, bl);
	}
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("Size", this.getSize());
		compoundTag.putBoolean("IsCanMove", this.isCanMove());
		compoundTag.putBoolean("IsFemale", this.IsFemale());
		compoundTag.putInt("ShieldLevel", this.getShieldLevel());
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		this.setSize(compoundTag.getInt("Size"));
		super.readAdditionalSaveData(compoundTag);
		this.setCanMove(compoundTag.getBoolean("IsCanMove"));
		this.setFemale(compoundTag.getBoolean("IsFemale"));
		this.setShieldLevel(compoundTag.getInt("ShieldLevel"));
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(IS_FEMALE, false);
		this.getEntityData().define(IS_CAN_MOVE, false);
		this.getEntityData().define(ID_SIZE, 180);
		this.getEntityData().define(SHIELD_LEVEL, 1);
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
		if (sourceentity.isSecondaryUseActive() && !this.isAggressive()) {
			if (sourceentity instanceof ServerPlayer serverPlayer) {
				Main.openMobInventoryGui(serverPlayer, TestEntity.this, true, true, true, true, true, true, true, true);
			}
			return InteractionResult.sidedSuccess(this.level().isClientSide());
		}
		return super.mobInteract(sourceentity, hand);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.IsFemale()) {
			this.armWideScaleAnimationState.startIfStopped(this.tickCount);
			this.armSlimScaleAnimationState.stop();
		}
		else {
			this.armSlimScaleAnimationState.startIfStopped(this.tickCount);
			this.armWideScaleAnimationState.stop();
		}
	}
	@Override
	public void aiStep() {
		super.aiStep();
		//停止战斗
		if (this.getTarget() != null && (!this.getTarget().isAlive() || this.getTarget() instanceof Player player && (player.isCreative() || player.isSpectator()))) {
			this.setTarget(null);
		}
		//
		//副手盾牌
		if (shieldCanUse() && notBowCrossbow(this, InteractionHand.MAIN_HAND) && this.getOffhandItem().getItem() instanceof ShieldItem && (!this.isUsingItem() || this.getUseItem().getItem() instanceof ShieldItem)) {
			if (!this.isUsingItem()) {
				this.startUsingItem(InteractionHand.OFF_HAND);
			}
		}
		//主手盾牌
		else if (shieldCanUse() && notBowCrossbow(this, InteractionHand.OFF_HAND) && this.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShieldItem && (!this.isUsingItem() || this.getUseItem().getItem() instanceof ShieldItem)) {
			if (!this.isUsingItem()) {
				this.startUsingItem(InteractionHand.MAIN_HAND);
			}
		}
		//主手双手武器
		else if (this.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemTwoHanded itemTwoHanded && !(this.getMainHandItem().getItem() instanceof ItemToolBasePike) && itemTwoHanded.canBlock() && this.getOffhandItem().isEmpty() && (!this.isUsingItem() || this.getUseItem().getItem() instanceof ItemTwoHanded) && this.attackAnim <= 0.0F) {
			if (!this.isUsingItem()) {
				this.startUsingItem(InteractionHand.MAIN_HAND);
			}
		}
		//副手双手武器
		else if (this.getOffhandItem().getItem() instanceof ItemTwoHanded itemTwoHanded && !(this.getOffhandItem().getItem() instanceof ItemToolBasePike) && itemTwoHanded.canBlock() && this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && (!this.isUsingItem() || this.getUseItem().getItem() instanceof ItemTwoHanded) && this.attackAnim <= 0.0F) {
			if (!this.isUsingItem()) {
				this.startUsingItem(InteractionHand.OFF_HAND);
			}
		}
		//其他状态下停止使用
		else {
			stopUse(this);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		if (this.isDamageSourceBlocked(damageSource)) {
			return SoundEvents.SHIELD_BLOCK;
		}
		return damageSource.type().effects().sound();
	}

	private boolean isDisabled(EquipmentSlot equipmentSlot) {
		return false;
	}


	@Override
	public boolean hurt(DamageSource damageSource, float f) {
		if (isInvulnerableTo(damageSource)) {
			return super.hurt(damageSource, f);
		}
		float healthBefore = this.getHealth();
		if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
			return super.hurt(damageSource, f);
		if (super.hurt(damageSource, f)) {
			float healthAfter = this.getHealth();
			this.setCustomName(Component.literal(String.valueOf(healthBefore - healthAfter)));
			this.setHealth(this.getMaxHealth());
			this.deathTime = 0;
		}
		return true;
	}

	@Override
	public boolean shieldCanUse() {
		return true;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
		this.setSize(100);
		return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
	}

	@Override
	public boolean isDamageSourceBlocks(DamageSource damageSource) {
		Vec3 object;
		Entity entity = damageSource.getDirectEntity();
		boolean bl = entity instanceof AbstractArrow && ((AbstractArrow) (AbstractArrow) entity).getPierceLevel() > 0;
		if (!damageSource.is(DamageTypeTags.BYPASSES_SHIELD) && !bl && (object = damageSource.getSourcePosition()) != null) {
			Vec3 vec3 = this.calculateViewVector(0.0f, this.getYHeadRot());
			Vec3 vec32 = object.vectorTo(this.position());
			vec32 = new Vec3(vec32.x, 0.0, vec32.z).normalize();
			return vec32.dot(vec3) < 0.0;
		}
		return false;
	}

	@Override
	public void disableShield() {
	}

	@Override
	public void disableShieldTry() {
	}

	@Override
	public void disableShieldBreak(int n) {
	}
}
