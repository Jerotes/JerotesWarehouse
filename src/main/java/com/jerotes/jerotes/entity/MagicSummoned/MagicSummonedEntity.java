package com.jerotes.jerotes.entity.MagicSummoned;

import com.jerotes.jerotes.entity.Interface.ChangePoseAbout;
import com.jerotes.jerotes.entity.Interface.FactionEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.Interface.TameMobEntity;
import com.jerotes.jerotes.goal.*;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.scores.Team;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MagicSummonedEntity extends PathfinderMob implements ChangePoseAbout, NeutralMob, JerotesEntity, FactionEntity, OwnableEntity, TameMobEntity {
	public static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Boolean> IS_WANDER = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> GOING_HOME = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.INT);
	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID_ID = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.OPTIONAL_UUID);
	public static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> ANIM_TICK = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> START_TICK = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ATTACK_ANIM = SynchedEntityData.defineId(MagicSummonedEntity.class, EntityDataSerializers.INT);

	public MagicSummonedEntity(EntityType<? extends MagicSummonedEntity> entityType, Level level) {
		super(entityType, level);
		setPersistenceRequired();
		this.reassessTameGoals();
	}

	@Override
	protected void registerGoals() {
		this.addGoals();
		if (!(this.isJerotesFlyingMob())) {
			this.goalSelector.addGoal(3, new JerotesChangeFollowOwnerGoal(this, 1.3, 5.0f, 1.0f, false));
		} else {
			this.goalSelector.addGoal(3, new JerotesChangeFollowOwnerGoal(this, 1.3, 5.0f, 1.0f, true));
		}
	}
	protected void addGoals() {
		this.goalSelector.addGoal(0, new JerotesChangeSitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(MagicSummonedGoHomeGoalUse(), new MagicSummonedGoHomeGoal(this, 1.0f));
		this.goalSelector.addGoal(3, new JerotesChangeFollowMobOwnerGoal(this, 1.0f));
		this.targetSelector.addGoal(1, new JerotesChangeHelpMobOwnerGoal(this));
		this.targetSelector.addGoal(1, new JerotesChangeOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new JerotesChangeOwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, this::isAngryAt));
		this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<MagicSummonedEntity>(this, false));
	}

	public int MagicSummonedGoHomeGoalUse() {
		return 3;
	}
	@Override
	public boolean removeWhenFarAway(double d) {
		return false;
	}
	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}
	protected void reassessTameGoals() {
	}
	public void tame(Player player) {
		this.setTame(true);
		this.setOwnerUUID(player.getUUID());
	}

	//动画
	public void setAnimTick(int n){
		this.getEntityData().set(ANIM_TICK, n);
	}
	public int getAnimTick(){
		return this.getEntityData().get(ANIM_TICK);
	}
	public void setAnimationState(String input) {
		this.setAnimationState(this.getAnimationState(input));
	}
	public void setAnimationState(int id) {
		this.entityData.set(ANIM_STATE, id);
	}
	public int getAnimationState(String animation) {
		return 0;
	}
	public List<AnimationState> getAllAnimations(){
		List<AnimationState> list = new ArrayList<>();
		return list;
	}
	public void stopMostAnimation(AnimationState exception){
		for (AnimationState state : this.getAllAnimations()){
			if (state != exception) {
				state.stop();
			}
		}
	}
	public void stopAllAnimation(){
		for (AnimationState state : this.getAllAnimations()){
			state.stop();
		}
	}
	//
	public void setStartTick(int n){
		this.getEntityData().set(START_TICK, n);
	}
	public int getStartTick(){
		return this.getEntityData().get(START_TICK);
	}
	public int getAttackAnim() {
		return this.getEntityData().get(ATTACK_ANIM);
	}
	public void setAttackAnim(int n) {
		this.getEntityData().set(ATTACK_ANIM, n);
	}
	public boolean orderedToSit;
	public void setHomePos(BlockPos blockPos) {
		this.entityData.set(HOME_POS, blockPos);
	}
	public BlockPos getHomePos() {
		return this.entityData.get(HOME_POS);
	}
	public boolean isGoingHome() {
		return this.entityData.get(GOING_HOME);
	}
	public void setGoingHome(boolean bl) {
		this.entityData.set(GOING_HOME, bl);
	}
	@Override
	public boolean isTame() {
		return (this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
	}
	@Override
	public boolean isInSittingPose() {
		return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
	}
	public boolean isOwnedBy(LivingEntity livingEntity) {
		return livingEntity == this.getOwner();
	}
	@Override
	public boolean isOrderedToSit() {
		return this.orderedToSit;
	}
	public void setOwnerUUID(@Nullable UUID uuid) {
		this.entityData.set(DATA_OWNER_UUID_ID, Optional.ofNullable(uuid));
	}
	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.entityData.get(DATA_OWNER_UUID_ID).orElse((UUID)null);
	}
	public void setTame(boolean bl) {
		byte bl1 = this.entityData.get(DATA_FLAGS_ID);
		if (bl) {
			this.entityData.set(DATA_FLAGS_ID, (byte)(bl1 | 4));
		} else {
			this.entityData.set(DATA_FLAGS_ID, (byte)(bl1 & -5));
		}
		this.reassessTameGoals();
	}
	@Nullable
	@Override
	public LivingEntity getOwner() {
		if (!this.level().isClientSide){
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : getLivingEntityByUUID(this.level(), uuid);
		} else {
			int id = this.getOwnerId();
			return id <= -1 ? null : this.level().getEntity(this.getOwnerId()) instanceof LivingEntity living && living != this ? living : null;
		}
	}
	public int getOwnerId(){
		return this.entityData.get(DATA_OWNER_ID);
	}
	public void setOwnerId(int id){
		this.entityData.set(DATA_OWNER_ID, id);
	}
	public static LivingEntity getLivingEntityByUUID(Level level, UUID uuid) {
		return getLivingEntityByUUID(level.getServer(), uuid);
	}

	public static LivingEntity getLivingEntityByUUID(MinecraftServer server, UUID uuid){
		if (uuid != null && server != null) {
			for (ServerLevel world : server.getAllLevels()) {
				Entity entity = world.getEntity(uuid);
				if (entity instanceof LivingEntity livingEntity){
					return livingEntity;
				}
			}
		}
		return null;
	}
	@Override
	//1.20.4↑//
	//public PlayerTeam getTeam() {
	//1.20.1//
	public Team getTeam() {
		if (this.isTame()) {
			LivingEntity livingentity = this.getOwner();
			if (livingentity != null) {
				return livingentity.getTeam();
			}
		}
		return super.getTeam();
	}
	public void setOrderedToSit(boolean bl) {
		this.orderedToSit = bl;
		if (bl) {
			if (!this.level().isClientSide()) {
				this.setHomePos(this.blockPosition());
			}
		}
	}
	@Override
	public void setInSittingPose(boolean bl) {
		byte b0 = this.entityData.get(DATA_FLAGS_ID);
		if (bl) {
			this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
		} else {
			this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
		}
	}
	public boolean isWander() {
		return this.getEntityData().get(IS_WANDER);
	}
	public void setWander(boolean bl) {
		this.getEntityData().set(IS_WANDER, bl);
	}
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("StartTick", this.getStartTick());
		compoundTag.putInt("AnimTick", this.getAnimTick());
		compoundTag.putInt("AttackAnim", this.getAttackAnim());
		compoundTag.putBoolean("IsWander", this.isWander());
		if (this.getOwnerUUID() != null) {compoundTag.putUUID("Owner", this.getOwnerUUID());}
		compoundTag.putBoolean("Sitting", this.orderedToSit);
		compoundTag.putBoolean("IsGoingHome", this.isGoingHome());
		compoundTag.putInt("HomePosX", this.getHomePos().getX());
		compoundTag.putInt("HomePosY", this.getHomePos().getY());
		compoundTag.putInt("HomePosZ", this.getHomePos().getZ());
		this.addPersistentAngerSaveData(compoundTag);
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.setStartTick(compoundTag.getInt("StartTick"));
		this.setAnimTick(compoundTag.getInt("AnimTick"));
		this.setAttackAnim(compoundTag.getInt("AttackAnim"));
		this.setWander(compoundTag.getBoolean("IsWander"));
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
				this.setTame(true);
			} catch (Throwable throwable) {
				this.setTame(false);
			}
		}
		this.orderedToSit = compoundTag.getBoolean("Sitting");
		this.setGoingHome(compoundTag.getBoolean("IsGoingHome"));
		int n = compoundTag.getInt("HomePosX");
		int n2 = compoundTag.getInt("HomePosY");
		int n3 = compoundTag.getInt("HomePosZ");
		this.setHomePos(new BlockPos(n, n2, n3));
		this.setInSittingPose(this.orderedToSit);
		this.readPersistentAngerSaveData((Level)this.level(), compoundTag);
	}
	@Override
	public boolean canChangeDimensions() {
		return super.canChangeDimensions() && this.getChangeType() != 3;
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(ATTACK_ANIM, 0);
		this.getEntityData().define(START_TICK, 0);
		this.getEntityData().define(ANIM_STATE, 0);
		this.getEntityData().define(ANIM_TICK, 0);
		this.getEntityData().define(IS_WANDER, false);
		this.getEntityData().define(DATA_OWNER_ID, -1);
		this.getEntityData().define(DATA_FLAGS_ID, (byte)0);
		this.getEntityData().define(DATA_OWNER_UUID_ID, Optional.empty());
		this.getEntityData().define(HOME_POS, BlockPos.ZERO);
		this.getEntityData().define(GOING_HOME, false);
	}
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		if (ANIM_STATE.equals(entityDataAccessor)) {
			if (this.level().isClientSide()) {
				switch (this.entityData.get(ANIM_STATE)){
					case 0:
						this.stopAllAnimation();
						break;
				}
			}
		}
		super.onSyncedDataUpdated(entityDataAccessor);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		InteractionResult retval = InteractionResult.sidedSuccess(this.level().isClientSide());
		if (this.level().isClientSide()) {
			retval = (this.isTame() && this.isOwnedBy(player)) ? InteractionResult.sidedSuccess(this.level().isClientSide()) : InteractionResult.PASS;
		}
		else {
			if (this.isTame()) {
				InteractionResult interactionResult;
				if (!this.isVehicle() && player == this.getOwner()) {
					int pose = this.getChangeType() + 1;
					if (pose > 4) {
						pose = 1;
					}
					this.setChangeType(pose, player);
				}
			}
			else {
				if (itemStack.is(TagKey.create(Registries.ITEM, new ResourceLocation("jerotes", "aa_creative_claw")))) {
					if (!this.isTame()) {
						this.tame(player);
						this.navigation.stop();
						this.setTarget(null);
						setChangeType(1, player);
						this.level().broadcastEntityEvent(this, (byte) 7);
						return InteractionResult.SUCCESS;
					}
					else {
						this.level().broadcastEntityEvent(this, (byte) 6);
					}
					this.setPersistenceRequired();
					return InteractionResult.SUCCESS;
				}
				else {
					retval = super.mobInteract(player, interactionHand);
					if (retval == InteractionResult.SUCCESS || retval == InteractionResult.CONSUME)
						this.setPersistenceRequired();
				}
			}
		}
		return retval;
	}
	public int getChangeType() {
		return (this.isOrderedToSit() ? 1 : 2) + (this.isWander() ? 2 : 0);
	}
	public void setChangeType(int n) {
		n = Mth.clamp(n, 1, 4);
		switch (n) {
			case 1:
				this.setOrderedToSit(true);
				if (!this.level().isClientSide()) {
					this.setWander(false);
				}
				this.jumping = false;
				this.navigation.stop();
				this.setTarget(null);
				break;
			case 2:
				this.setOrderedToSit(false);
				if (!this.level().isClientSide()) {
					this.setWander(false);
				}
				this.jumping = false;
				this.navigation.stop();
				this.setTarget(null);
				break;
			case 3:
				this.setOrderedToSit(true);
				if (!this.level().isClientSide()) {
					this.setWander(true);
					this.setHomePos(this.blockPosition());
				}
				this.jumping = false;
				this.navigation.stop();
				this.setTarget(null);
				break;
			case 4:
				this.setOrderedToSit(false);
				if (!this.level().isClientSide()) {
					this.setWander(true);
				}
				this.jumping = false;
				this.navigation.stop();
				this.setTarget(null);
				break;
		}
	}
	public void setChangeType(int n, Player player) {
		this.setChangeType(n);
		if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
			serverPlayer.sendSystemMessage(Component.translatable("talk.jerotes.pose_" + n, this.getDisplayName()).withStyle(ChatFormatting.WHITE));
		}
	}

	protected void spawnTamingParticles(boolean bl) {
		ParticleOptions particleoptions = ParticleTypes.HEART;
		if (!bl) {
			particleoptions = ParticleTypes.SMOKE;
		}
		for(int i = 0; i < 7; ++i) {
			double d0 = this.random.nextGaussian() * 0.02D;
			double d1 = this.random.nextGaussian() * 0.02D;
			double d2 = this.random.nextGaussian() * 0.02D;
			this.level().addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (this.getOwner() != null && this.getOwnerId() == -1) {
			this.setOwnerId(this.getOwner().getId());
		}
		if (this.isWander() && this.isInSittingPose()) {
			if (!this.level().isClientSide) {
				this.setInSittingPose(false);
			}
		}
		if (this.random.nextInt(900) == 1 && this.isAlive()) {
			this.heal(3.0f);
		}
		if (!this.level().isClientSide()) {
			this.updatePersistentAnger((ServerLevel) this.level(), true);
		}
		//停止战斗
		if (this.getTarget() != null && (!this.getTarget().isAlive() || this.getTarget() instanceof Player player && (player.isCreative() || player.isSpectator()))) {
			this.setTarget(null);
		}
		//
		//清除动画
		if (!this.level().isClientSide()) {
			this.setAnimTick(Math.max(-1, this.getAnimTick() - 1));
		}
		if (this.getAnimTick() == 0) {
			if (!this.level().isClientSide()) {
				this.setAnimationState(0);
			}
		}
		if (!this.level().isClientSide()) {
			this.setStartTick(this.getStartTick() + 1);
		}

		if (this.getTarget() != null) {
			if (!this.level().isClientSide()) {
				this.setAttackAnim(Math.min(this.getAttackAnim() + 1, 10));
			}
		}
		else {
			if (!this.level().isClientSide()) {
				this.setAttackAnim(Math.max(this.getAttackAnim() - 1, 0));
			}
		}
	}

	@Override
	public boolean hurt(DamageSource damageSource, float amount) {
		if (isInvulnerableTo(damageSource)) {
			return super.hurt(damageSource, amount);
		}
		boolean bl = super.hurt(damageSource, amount);
		if (bl) {
			//取消坐下
			if (!this.level().isClientSide()) {
				if (this.getChangeType() == 1) {
					if (this.getOwner() instanceof Player player) {
						this.setChangeType(2, player);
					}
					else {
						this.setChangeType(2);
					}
				}
			}
		}
		return bl;
	}

	@Override
	public void handleEntityEvent(byte by) {
		if (by == 7) {
			this.spawnTamingParticles(true);
		} else if (by == 6) {
			this.spawnTamingParticles(false);
		} else {
			super.handleEntityEvent(by);
		}
	}

	@Override
	public boolean isAlliedTo(Entity entity) {
		if (this.isTame()) {
			LivingEntity livingentity = this.getOwner();
			if (entity == livingentity) {
				return true;
			}
			if (livingentity != null) {
				return livingentity.isAlliedTo(entity);
			}
		}
		return super.isAlliedTo(entity);
	}

	@Override
	public boolean wantsToAttack(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return AttackFind.wantsToAttack(this, livingEntity, livingEntity2);
	}


	@Override
	public boolean canAttack(LivingEntity livingEntity) {
		if (((this.getTeam() == null && livingEntity.getTeam() == null) || this.getTeam() == livingEntity.getTeam()) &&
				EntityFactionFind.isFaction(this, livingEntity)
		) {
			return false;
		}
		return !this.isOwnedBy(livingEntity) && super.canAttack(livingEntity);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
		this.setHomePos(this.blockPosition());
		return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
	}

	@Override
	public void die(DamageSource damageSource) {
		Component deathMessage = this.getCombatTracker().getDeathMessage();
		super.die(damageSource);
		if (this.dead)
			if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
				this.getOwner().sendSystemMessage(deathMessage);
		}
	}

	@Nullable
	private UUID persistentAngerTarget;
	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	private int remainingPersistentAngerTime;

	@Override
	public int getRemainingPersistentAngerTime() {
		return this.remainingPersistentAngerTime;
	}

	@Override
	public void setRemainingPersistentAngerTime(int n) {
		this.remainingPersistentAngerTime = n;
	}


	@Override
	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
	}

	@Nullable
	@Override
	public UUID getPersistentAngerTarget() {
		return this.persistentAngerTarget;
	}

	@Override
	public void setPersistentAngerTarget(@Nullable UUID uUID) {
		this.persistentAngerTarget = uUID;
	}
}

