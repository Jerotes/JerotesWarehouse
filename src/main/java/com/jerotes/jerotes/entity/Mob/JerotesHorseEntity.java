package com.jerotes.jerotes.entity.Mob;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.*;
import com.jerotes.jerotes.goal.*;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;

public class JerotesHorseEntity extends Horse implements PlayerRideableJumping, ControlVehicleEntity, Saddleable, ChangePoseAbout, HasHomePosEntity, OwnableEntity, NeutralMob, JerotesEntity, TameMobEntity {
	public AnimationState idleAnimationState = new AnimationState();
	public AnimationState angry1AnimationState = new AnimationState();
	public AnimationState angry2AnimationState = new AnimationState();
	public AnimationState jumpAnimationState = new AnimationState();
	public AnimationState longJumpAnimationState = new AnimationState();
	public AnimationState sitAnimationState = new AnimationState();
	public AnimationState toSitAnimationState = new AnimationState();
	public AnimationState stopSitAnimationState = new AnimationState();
	public AnimationState eatAnimationState = new AnimationState();
	public AnimationState eatGrassAnimationState = new AnimationState();
	private static final Ingredient FOOD_ITEMS = Ingredient.of(
			ItemTags.create(new ResourceLocation(JerotesWarehouse.MODID, "animal_foods/horse_and_grass_foods"))
	);
	protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Integer> PANIC_TICK = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> IS_WANDER = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> GOING_HOME = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.BOOLEAN);
	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.BYTE);
	protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID_ID = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<Integer> THROWING_TICK = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ANIM_STATE = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ANIM_TICK = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> MANUALLY_CONTROL_COMBAT = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> IS_NORMAL_CONTROL = SynchedEntityData.defineId(JerotesHorseEntity.class, EntityDataSerializers.BOOLEAN);

	public JerotesHorseEntity(EntityType<? extends JerotesHorseEntity> type, Level world) {
		super(type, world);
		this.reassessTameGoals();
		setPersistenceRequired();
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25D);
		builder = builder.add(Attributes.MAX_HEALTH, 30.0F);
		builder = builder.add(Attributes.JUMP_STRENGTH, 0.4F);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 4);
		builder = builder.add(Attributes.FOLLOW_RANGE, 20);
		return builder;
	}
	protected void randomizeAttributes(RandomSource p_218815_) {
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)generateMaxHealth(p_218815_::nextInt));
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(generateSpeed(p_218815_::nextDouble));
		this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateJumpStrength(p_218815_::nextDouble));
	}
	protected static float generateMaxHealth(IntUnaryOperator p_272695_) {
		return 15.0F + (float)p_272695_.applyAsInt(8) + (float)p_272695_.applyAsInt(9);
	}
	protected static double generateJumpStrength(DoubleSupplier p_272718_) {
		return (double)0.4F + p_272718_.getAsDouble() * 0.2D + p_272718_.getAsDouble() * 0.2D + p_272718_.getAsDouble() * 0.2D;
	}
	protected static double generateSpeed(DoubleSupplier p_273691_) {
		return ((double)0.45F + p_273691_.getAsDouble() * 0.3D + p_273691_.getAsDouble() * 0.3D + p_273691_.getAsDouble() * 0.3D) * 0.25D;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new JerotesChangeSitWhenOrderedToGoal(this));
		this.goalSelector.addGoal(1, new JerotesAnimalMeleeAttackGoal(this, 1.2D, true));
		this.goalSelector.addGoal(1, new JerotesAnimalPanicGoal(this, 1.6D) {
			@Override
			public void tick() {
				super.tick();
				if (!JerotesHorseEntity.this.level().isClientSide()) {
					JerotesHorseEntity.this.setPanicTick(20);
				}
			}
		});
		this.goalSelector.addGoal(2, new JerotesHorseRunAroundLikeCrazyGoal(this, 1.2D));
		this.goalSelector.addGoal(2, new JerotesBreedGoal(this, 1.0));
		this.goalSelector.addGoal(3, new JerotesBaseTamableAnimalGoHomeGoal(this, 1.0f));
		this.goalSelector.addGoal(3, new JerotesChangeFollowOwnerGoal(this, 1.3, 5.0f, 1.0f, false));
		this.goalSelector.addGoal(3, new JerotesAnimalChangeTemptGoal(this, 1.1, FOOD_ITEMS, false));
		this.goalSelector.addGoal(4, new JerotesAnimalChangeFollowParentGoal(this, 1.1));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0f));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this) {
			@Override
			public boolean canUse() {
				if (JerotesHorseEntity.this.getControllingPassenger() instanceof Player player && !JerotesHorseEntity.this.isNormalSteeringControlJerotes() && JerotesHorseEntity.this.canBeControlJerotes(player)) {
					return false;
				}
				return super.canUse();
			}    @Override
			public boolean canContinueToUse() {
				if (JerotesHorseEntity.this.getControllingPassenger() instanceof Player player && !JerotesHorseEntity.this.isNormalSteeringControlJerotes() && JerotesHorseEntity.this.canBeControlJerotes(player)) {
					return false;
				}
				return super.canContinueToUse();
			}
		});
		if (this.canPerformRearing()) {
			this.goalSelector.addGoal(9, new RandomStandGoal(this));
		}
		this.targetSelector.addGoal(1, new JerotesChangeHelpMobOwnerGoal(this));
		this.targetSelector.addGoal(1, new JerotesChangeOwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new JerotesChangeOwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, this::isAngryAt));
		this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<JerotesHorseEntity>(this, false));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.HORSE_AMBIENT;
	}
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.HORSE_DEATH;
	}
	@Nullable
	@Override
	protected SoundEvent getEatingSound() {
		return SoundEvents.HORSE_EAT;
	}
	@Override
	protected SoundEvent getHurtSound(DamageSource p_30720_) {
		return SoundEvents.HORSE_HURT;
	}
	@Override
	protected SoundEvent getAngrySound() {
		return SoundEvents.HORSE_ANGRY;
	}
	@Override
	public boolean isFood(ItemStack itemStack) {
		return FOOD_ITEMS.test(itemStack) || super.isFood(itemStack);
	}
	@Override
	public boolean isPushable() {
		return !this.isVehicle();
	}
	@Override
	public void equipArmor(@Nullable Player p_251330_, ItemStack p_248855_) {
		if (this.isArmor(p_248855_)) {
			this.inventory.setItem(1, p_248855_.copyWithCount(1));
			if (p_251330_ != null) {
				if (!p_251330_.getAbilities().instabuild) {
					p_248855_.shrink(1);
				}
			}
		}

	}
	@Override
	public void setBaby(boolean bl) {
		this.setAge(bl ? -42000 : 0);
	}
	//
	protected void positionRider(Entity entity, MoveFunction moveFunction) {
		Vec3 vec3 = this.getPassengerRidingPosition(entity);
		moveFunction.accept(entity, vec3.x, vec3.y + getMyRidingOffset(this), vec3.z);
	}
	public float getMyRidingOffset(Entity entity) {
		return this.ridingOffset(entity) * this.getScale();
	}
	protected float ridingOffset(Entity entity) {
		return -0.9F;
	}
	public Vec3 getPassengerRidingPosition(Entity entity) {
		return (new Vec3(this.getPassengerAttachmentPoint(entity, this.getDimensions(this.getPose()), this.getScale()).rotateY(-this.yBodyRot * ((float)Math.PI / 180F)))).add(this.position());
	}
	protected Vector3f getPassengerAttachmentPoint(Entity entity, EntityDimensions entityDimensions, float f) {
		return new Vector3f(0.0F, entityDimensions.height, 0.0F);
	}
	//
	@Nullable
	@Override
	public JerotesHorseEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
		UUID uUID;
		JerotesHorseEntity rusher = JerotesEntityType.JEROTES_HORSE.get().create(serverLevel);
		if (rusher != null && (uUID = this.getOwnerUUID()) != null) {
			rusher.setOwnerUUID(uUID);
			rusher.setTame(true);
		}
		return rusher;
	}
	@Override
	public boolean canMate(Animal animal) {
		if (animal == this) {
			return false;
		}
		if (animal.getClass() != this.getClass()) {
			return false;
		}
		return this.isInLove() && animal.isInLove();
	}
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
		float height = 2.1f;
		if (this.isBaby()) {
			return height / 2;
		}
		return height;
	}
	@Override
	public boolean canJump() {
		return this.isSaddled();
	}
	@Override
	public void setEating(boolean bl) {
		super.setEating(bl);
		if (!bl)
			return;
		if (this.isVehicle())
			return;
		if (this.isInSittingPose())
			return;
		if (this.getAnimTick() > 0)
			return;
		if (!this.level().isClientSide()) {
			this.setAnimTick(50);
			this.setAnimationState("eatGrass");
		}
	}
	@Override
	public boolean canWearArmor() {
		return true;
	}
	public void tame(Player player) {
		this.setTame(true);
		this.setOwnerUUID(player.getUUID());
		if (player instanceof ServerPlayer) {
			CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
		}
	}
	public void tameLivingEntity(LivingEntity player) {
		this.setTame(true);
		this.setOwnerUUID(player.getUUID());
		if (player instanceof ServerPlayer) {
			CriteriaTriggers.TAME_ANIMAL.trigger((ServerPlayer)player, this);
		}
	}
	public boolean OwnerCanOrderAttack() {
		return false;
	}
	protected void reassessTameGoals() {
	}
	@Override
	protected int calculateFallDamage(float f, float f2) {
		return super.calculateFallDamage(f, f2) - 10;
	}
	@Override
	public boolean isManuallyControlCombatJerotes() {
		return this.getEntityData().get(MANUALLY_CONTROL_COMBAT);
	}
	public boolean isTrueManuallyControlCombatJerotes() {
		return this.getControllingPassenger() instanceof Player player && canBeControlJerotes(player) && isManuallyControlCombatJerotes();
	}
	@Override
	public void setManuallyControlCombatJerotes(boolean bl) {
		this.getEntityData().set(MANUALLY_CONTROL_COMBAT, bl);
		if (this.getControllingPassenger() instanceof Player player) {
			if (bl) {
				player.displayClientMessage(Component.translatable("message.jerotes.change_control_combat_type_0"), true);
			}
			else {
				player.displayClientMessage(Component.translatable("message.jerotes.change_control_combat_type_1"), true);
			}
		}
	}
	@Override
	public float getManuallyControlCombatCameraChangeJerotes() {
		return 1.0f;
	}
	@Override
	public boolean canBeControlJerotes(Player player) {
		return this.isSaddled() && !this.isBaby() && this.getOwner() != null && (player == this.getOwner() || AttackFind.FindCanNotAttack(this.getOwner(), player));
	}
	@Override
	protected void updateControlFlags() {
		boolean flag = !(this.getControllingPassenger() instanceof Mob) &&
				!(this.getControllingPassenger() instanceof Player && !this.isNormalSteeringControlJerotes());
		boolean flag1 = !(this.getVehicle() instanceof Boat);
		boolean controlStopTarget = isTrueManuallyControlCombatJerotes();
		this.goalSelector.setControlFlag(Goal.Flag.MOVE, flag);
		this.goalSelector.setControlFlag(Goal.Flag.JUMP, flag && flag1);
		this.goalSelector.setControlFlag(Goal.Flag.LOOK, flag);
		this.goalSelector.setControlFlag(Goal.Flag.TARGET, !controlStopTarget);
	}
	@Override
	public void pressMainJerotes(Player player) {
		if (!this.isSilent()) {
			this.playSound(SoundEvents.HORSE_ANGRY, 1.0f, 1.0f);
		}
		if (!this.level().isClientSide()) {
			this.setAttackTick(20);
			this.setAnimTick(20);
			this.setAnimationState("angry1");
		}
	}
	@Override
	public void pressAddJerotes(Player player) {
		if (!this.isSilent()) {
			this.playSound(SoundEvents.HORSE_ANGRY, 1.0f, 1.0f);
		}
		if (!this.level().isClientSide()) {
			this.setAttackTick(20);
			this.setAnimTick(20);
			this.setAnimationState("angry1");
		}
	}
	@Override
	public boolean canPressMainJerotes() {
		return this.getAttackTick() <= 0;
	}
	@Override
	public boolean canPressAddJerotes() {
		return this.getAttackTick() <= 0;
	}
	@Override
	public void pressLeftJerotes(Player player) {
		float targetYRot = this.getYRot() - 12.0f;
		targetYRot = targetYRot % 360.0f;
		if (targetYRot < 0) targetYRot += 360.0f;
		this.yRotO = targetYRot;
		this.setYRot(targetYRot);
		this.setYHeadRot(targetYRot);
		this.setYBodyRot(targetYRot);
		this.hasImpulse = true;
	}
	@Override
	public void pressRightJerotes(Player player) {
		float targetYRot = this.getYRot() + 12.0f;
		targetYRot = targetYRot % 360.0f;
		if (targetYRot < 0) targetYRot += 360.0f;
		this.yRotO = targetYRot;
		this.setYRot(targetYRot);
		this.setYHeadRot(targetYRot);
		this.setYBodyRot(targetYRot);
		this.hasImpulse = true;
	}
	public boolean canPressLeftJerotes() {return !this.isNormalSteeringControlJerotes();}
	public boolean canPressRightJerotes() {return !this.isNormalSteeringControlJerotes();}
	public boolean canChangeSteeringControl(Player player) {
		return true;
	}

	private int sitTick = 0;
	private boolean orderedToSit;
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
	@Override
	public boolean isArmor(ItemStack itemStack) {
		return itemStack.getItem() instanceof HorseArmorItem;
	}
	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.entityData.get(DATA_OWNER_UUID_ID).orElse((UUID)null);
	}
	public void setOrderedToSit(boolean bl) {
		this.orderedToSit = bl;
	}
	public ItemStack getArmor() {
		return this.getItemBySlot(EquipmentSlot.CHEST);
	}
	public void setTame(boolean bl) {
		byte by = this.entityData.get(DATA_FLAGS_ID);
		if (bl) {
			this.entityData.set(DATA_FLAGS_ID, (byte)(by | 4));
		} else {
			this.entityData.set(DATA_FLAGS_ID, (byte)(by & 0xFFFFFFFB));
		}
		this.reassessTameGoals();
		this.setFlag(2, bl);
	}
	@Override
	public void setTamed(boolean bl) {
		this.setFlag(2, bl);
		this.setTame(bl);
	}
	@Override
	//1.20.4↑//
	//public PlayerTeam getTeam() {
	//1.20.1//
	public Team getTeam() {
		LivingEntity livingEntity;
		if (this.isTame() && (livingEntity = this.getOwner()) != null) {
			return livingEntity.getTeam();
		}
		return super.getTeam();
	}
	@Override
	public void setInSittingPose(boolean bl) {
		byte by = this.entityData.get(DATA_FLAGS_ID);
		if (bl) {
			this.entityData.set(DATA_FLAGS_ID, (byte)(by | 1));
		} else {
			this.entityData.set(DATA_FLAGS_ID, (byte)(by & -2));
		}
	}
	@Override
	public void setOwnerUUID(@Nullable UUID uUID) {
		this.entityData.set(DATA_OWNER_UUID_ID, Optional.ofNullable(uUID));
	}
	@Nullable
	private LivingEntity thrower;
	@Nullable
	private UUID throwerUUID;
	public void setThrower(@Nullable LivingEntity livingEntity) {
		this.thrower = livingEntity;
		this.throwerUUID = livingEntity == null ? null : livingEntity.getUUID();
	}
	@Nullable
	public LivingEntity getThrower() {
		Entity entity;
		if (this.thrower == null && this.throwerUUID != null && this.level() instanceof ServerLevel && (entity = ((ServerLevel)this.level()).getEntity(this.throwerUUID)) instanceof LivingEntity) {
			this.thrower = (LivingEntity)entity;
		}
		return this.thrower;
	}
	public void setThrowingTick(int n){
		this.getEntityData().set(THROWING_TICK, n);
	}
	public int getThrowingTick(){
		return this.getEntityData().get(THROWING_TICK);
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
		if (Objects.equals(animation, "angry1")){
			return 1;
		}
		else if (Objects.equals(animation, "angry2")){
			return 2;
		}
		else if (Objects.equals(animation, "eatGrass")){
			return 3;
		}
		else {
			return 0;
		}
	}
	public List<AnimationState> getAllAnimations(){
		List<AnimationState> list = new ArrayList<>();
		list.add(this.angry1AnimationState);
		list.add(this.angry2AnimationState);
		list.add(this.eatGrassAnimationState);
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
	public void setPanicTick(int n) {
		this.entityData.set(PANIC_TICK, n);
	}
	public int getPanicTick() {
		return this.entityData.get(PANIC_TICK);
	}
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
	public boolean isWander() {
		return this.getEntityData().get(IS_WANDER);
	}
	public void setWander(boolean bl) {
		this.getEntityData().set(IS_WANDER, bl);
	}
	public void setAttackTick(int n){
		this.getEntityData().set(ATTACK_TICK, n);
	}
	public int getAttackTick(){
		return this.getEntityData().get(ATTACK_TICK);
	}
	public boolean isNormalSteeringControlJerotes() {
		return this.getEntityData().get(IS_NORMAL_CONTROL);
	}
	public void setNormalSteeringControlJerotes(boolean bl) {
		this.getEntityData().set(IS_NORMAL_CONTROL, bl);
		if (this.getControllingPassenger() instanceof Player player) {
			if (bl) {
				player.displayClientMessage(Component.translatable("message.jerotes.change_control_steering_type_0"), true);
			}
			else {
				player.displayClientMessage(Component.translatable("message.jerotes.change_control_steering_type_1"), true);
			}
		}
	}
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("PanicTick", this.getPanicTick());
		compoundTag.putBoolean("IsWander", this.isWander());
		compoundTag.putBoolean("IsGoingHome", this.isGoingHome());
		compoundTag.putInt("HomePosX", this.getHomePos().getX());
		compoundTag.putInt("HomePosY", this.getHomePos().getY());
		compoundTag.putInt("HomePosZ", this.getHomePos().getZ());
		compoundTag.putInt("AnimTick", this.getAnimTick());
		compoundTag.putInt("SitTick", this.sitTick);
		if (this.getOwnerUUID() != null) {
			compoundTag.putUUID("Owner", this.getOwnerUUID());
		}
		compoundTag.putBoolean("Sitting", this.orderedToSit);
		if (!this.inventory.getItem(1).isEmpty()) {
			compoundTag.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
		}
		if (compoundTag.hasUUID("Thrower")) {
			this.throwerUUID = compoundTag.getUUID("Thrower");
		}
		compoundTag.putInt("ThrowingTick", this.getThrowingTick());
		compoundTag.putInt("AttackTick", this.getAttackTick());
		compoundTag.putBoolean("IsManuallyControlCombat", this.isManuallyControlCombatJerotes());
		compoundTag.putBoolean("IsNormalSteeringControlJerotes", this.isNormalSteeringControlJerotes());
		this.addPersistentAngerSaveData(compoundTag);
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		UUID uUID;
		ItemStack itemStack;
		super.readAdditionalSaveData(compoundTag);
		this.setPanicTick(compoundTag.getInt("PanicTick"));
		this.setWander(compoundTag.getBoolean("IsWander"));
		this.setGoingHome(compoundTag.getBoolean("IsGoingHome"));
		int n = compoundTag.getInt("HomePosX");
		int n2 = compoundTag.getInt("HomePosY");
		int n3 = compoundTag.getInt("HomePosZ");
		this.setHomePos(new BlockPos(n, n2, n3));
		this.setAnimTick(compoundTag.getInt("AnimTick"));
		this.sitTick = compoundTag.getInt("SitTick");
		if (compoundTag.hasUUID("Owner")) {
			uUID = compoundTag.getUUID("Owner");
		} else {
			String string = compoundTag.getString("Owner");
			uUID = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), string);
		}
		if (uUID != null) {
			try {
				this.setOwnerUUID(uUID);
				this.setTame(true);
			}
			catch (Throwable throwable) {
				this.setTame(false);
			}
		}
		this.orderedToSit = compoundTag.getBoolean("Sitting");
		this.setInSittingPose(this.orderedToSit);
		if (compoundTag.contains("ArmorItem", 10) && !(itemStack = ItemStack.of(compoundTag.getCompound("ArmorItem"))).isEmpty() && this.isArmor(itemStack)) {
			this.inventory.setItem(1, itemStack);
		}
		if (this.throwerUUID != null) {
			compoundTag.putUUID("Thrower", this.throwerUUID);
		}
		this.setThrowingTick(compoundTag.getInt("ThrowingTick"));
		this.setAttackTick(compoundTag.getInt("AttackTick"));
		this.setManuallyControlCombatJerotes(compoundTag.getBoolean("IsManuallyControlCombat"));
		this.setNormalSteeringControlJerotes(compoundTag.getBoolean("IsNormalSteeringControlJerotes"));
		this.updateContainerEquipment();
		this.readPersistentAngerSaveData(this.level(), compoundTag);
	}
	@Override
	public boolean canChangeDimensions() {
		return super.canChangeDimensions() && this.getChangeType() != 3;
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(PANIC_TICK, 0);
		this.getEntityData().define(IS_WANDER, false);
		this.getEntityData().define(DATA_OWNER_ID, -1);
		this.getEntityData().define(HOME_POS, BlockPos.ZERO);
		this.getEntityData().define(GOING_HOME, false);
		this.getEntityData().define(DATA_FLAGS_ID, (byte)0);
		this.getEntityData().define(DATA_OWNER_UUID_ID, Optional.empty());
		this.getEntityData().define(THROWING_TICK, 0);
		this.getEntityData().define(ANIM_STATE, 0);
		this.getEntityData().define(ANIM_TICK, 0);
		this.getEntityData().define(MANUALLY_CONTROL_COMBAT, false);
		this.getEntityData().define(IS_NORMAL_CONTROL, false);
		this.getEntityData().define(ATTACK_TICK, 0);
	}
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		if (ANIM_STATE.equals(entityDataAccessor)) {
			if (this.level().isClientSide()) {
				switch (this.entityData.get(ANIM_STATE)){
					case 0:
						this.stopAllAnimation();
						break;
					case 1:
						this.angry1AnimationState.startIfStopped(this.tickCount);
						this.stopMostAnimation(this.angry1AnimationState);
						break;
					case 2:
						this.angry2AnimationState.startIfStopped(this.tickCount);
						this.stopMostAnimation(this.angry2AnimationState);
						break;
					case 3:
						this.eatGrassAnimationState.startIfStopped(this.tickCount);
						this.stopMostAnimation(this.eatGrassAnimationState);
						break;
				}
			}
		}
		super.onSyncedDataUpdated(entityDataAccessor);
	}

	@Override
	public void tick() {
		super.tick();
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
		if (!this.level().isClientSide()) {
			this.updatePersistentAnger((ServerLevel)this.level(), true);
		}
		//停止战斗
		if (this.getTarget() != null && (!this.getTarget().isAlive() || this.getTarget() instanceof Player player && (player.isCreative() || player.isSpectator()))){
			this.setTarget(null);
		}
		//
		if (this.random.nextInt(900) == 1 && this.deathTime == 0) {
			this.heal(3.0f);
		}
		//清除动画
		if (!this.level().isClientSide()) {
			this.setAnimTick(Math.max(-1, this.getAnimTick() - 1));
		}
		if (this.getAnimTick() == 0) {
			if (!this.level().isClientSide()) {
				this.setAnimationState(0);
			}
		}
		//战斗
		if (!this.level().isClientSide()) {
			this.setAttackTick(Math.max(0, this.getAttackTick() - 1));
			this.setPanicTick(Math.max(0, this.getPanicTick() - 1));
		}
		if (this.getAttackTick() == 10) {
			this.trueHurt();
		}
		if (this.getAttackTick() > 0 && this.getTarget() != null) {
			this.notLookAt(this.getTarget(), 360f, 360f);
		}
		//抛开
		if (!this.level().isClientSide()) {
			if (this.getThrowingTick() > 0) {
				this.setThrowingTick(this.getThrowingTick() - 1);
			} else if (this.getThrower() != null) {
				if (!this.level().isClientSide()) {
					this.setThrower(null);
				}
			}
		}
		if (this.getThrowingTick() == 1) {
			if (this.onGround()) {
				this.setOnGround(false);
				this.setDeltaMovement(this.getDeltaMovement().add(0,0.25f,0));
			}
			if (this.getThrower() != null && this.distanceTo(this.getThrower()) < 5) {
				this.ejectPassengers();
				this.getThrower().moveTo(this.getPassengerRidingPosition(this.getThrower()));
				AttackFind.attackBegin(this, this.getThrower());
				AttackFind.attackAfter(this, this.getThrower(), this.getRandom().nextFloat() / 3, 1.5f, false, 0f);
				this.getThrower().setOnGround(false);
				this.getThrower().setDeltaMovement(this.getDeltaMovement().add( 0,0.25f,0));
				this.level().broadcastEntityEvent(this, (byte)6);
			}
			if (!this.level().isClientSide()) {
				this.setThrower(null);
			}
		}

		if (!this.isInSittingPose()) {
			this.sitAnimationState.stop();
		}
		if (this.isInSittingPose() && sitTick <= 0){
			this.sitTick = 40;
		}
		if (!this.isInSittingPose() && sitTick > 0){
			this.stopSitAnimationState.start(this.tickCount);
			this.sitTick = 0;
			this.sitAnimationState.stop();
			this.toSitAnimationState.stop();
		}
		if (this.isAlive()) {
			this.idleAnimationState.startIfStopped(this.tickCount);
		}
		if (this.sitTick == 40){
			this.toSitAnimationState.start(this.tickCount);
		}
		if (this.sitTick == 30){
			this.toSitAnimationState.stop();
			this.sitAnimationState.start(this.tickCount);
		}
		if (this.sitTick > 5){
			this.sitTick -= 1;
		}
	}


	public void notLookAt(Entity p_21392_, float p_21393_, float p_21394_) {
		double d0 = p_21392_.getX() - this.getX();
		double d2 = p_21392_.getZ() - this.getZ();
		double d1;
		if (p_21392_ instanceof LivingEntity livingentity) {
			d1 = livingentity.getEyeY() - this.getEyeY();
		} else {
			d1 = (p_21392_.getBoundingBox().minY + p_21392_.getBoundingBox().maxY) / 2.0D - this.getEyeY();
		}

		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		float f = (float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
		float f1 = (float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
		this.setXRot(this.rotlerp(this.getXRot(), f1, p_21394_));
		this.setYRot(this.rotlerp(this.getYRot(), -f, p_21393_));
	}
	private float rotlerp(float p_21377_, float p_21378_, float p_21379_) {
		float f = Mth.wrapDegrees(p_21378_ - p_21377_);
		if (f > p_21379_) {
			f = p_21379_;
		}
		if (f < -p_21379_) {
			f = -p_21379_;
		}
		return p_21377_ + f;
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		if (!itemStack.isEmpty() && !this.isFood(itemStack) && !this.isTamed()) {
			this.makeMad();
		}
		if (this.isTame()) {
			InteractionResult interactionResult;
			if (!player.isShiftKeyDown() && !this.isOrderedToSit()) {
				//喂食
				if (this.isFood(itemStack)) {
					this.eatAnimationState.start(this.tickCount);
					if (!this.isSilent()) {
						this.playSound(getEatingSound(), 0.15f, 1.0f);
					}
					if (this.getHealth() < this.getMaxHealth()) {
						this.heal(4f);
						if (!player.getAbilities().instabuild) {
							itemStack.shrink(1);
						}
						this.gameEvent(GameEvent.EAT, this);
						return InteractionResult.SUCCESS;
					}
					//常规繁殖
					int i = this.getAge();
					if (!this.level().isClientSide && i == 0 && this.canFallInLove()) {
						this.setInLove(player);
						if (!player.getAbilities().instabuild) {
							itemStack.shrink(1);
						}
					}
					if (this.isBaby()) {
						this.usePlayerItem(player, interactionHand, itemStack);
						this.ageUp(getSpeedUpSecondsWhenFeeding(-i), true);
					}
					return InteractionResult.SUCCESS;
				}
				return super.mobInteract(player, interactionHand);
			}
			if (!this.isVehicle() && player == this.getOwner()) {
				int pose = this.getChangeType() + 1;
				if (pose > 4) {
					pose = 1;
				}
				this.setChangeType(pose, player);
			}
			return InteractionResult.SUCCESS;
		}
		if (this.canWearArmor() && this.isArmor(itemStack) && !this.isWearingArmor()) {
			return InteractionResult.sidedSuccess(((Level)this.level()).isClientSide);
		}
		if (!this.isFood(itemStack) || this.isAngry())
			return super.mobInteract(player, interactionHand);
		this.eatAnimationState.start(this.tickCount);
		if (!this.isSilent()) {
			this.playSound(getEatingSound(), 0.15f, 1.0f);
		}
		return this.fedFood(player, itemStack);
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
	@Override
	protected void updateContainerEquipment() {
		super.updateContainerEquipment();
	}

	@Override
	public void containerChanged(Container container) {
		super.containerChanged(container);
	}
	@Override
	protected void spawnTamingParticles(boolean bl) {
		SimpleParticleType simpleParticleType = ParticleTypes.HEART;
		if (!bl) {
			simpleParticleType = ParticleTypes.SMOKE;
		}
		if (this.onGround()) {
			this.setDeltaMovement(this.getDeltaMovement().add(0,0.5f,0));
		}
		for (int i = 0; i < 7; ++i) {
			double d = this.random.nextGaussian() * 0.02;
			double d2 = this.random.nextGaussian() * 0.02;
			double d3 = this.random.nextGaussian() * 0.02;
			((Level)this.level()).addParticle(simpleParticleType, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, d2, d3);
		}
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (this.isTrueManuallyControlCombatJerotes()) {
			return false;
		}
		if (this.getAttackTick() > 0) {
			return false;
		}
		if (!this.level().isClientSide()) {
			this.setAttackTick(20);
			this.setAnimTick(20);
			this.setAnimationState("angry1");
		}
		return true;
	}
	public boolean trueHurt() {
		if (!this.isSilent()) {
			this.playSound(SoundEvents.HORSE_ANGRY, 1.0f, 1.0f);
		}
		float reach = 0.5f;
		List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getAttackBoundingBox().inflate(reach));
		for (LivingEntity hurt : list) {
			if (hurt == null) continue;
			if ((this.distanceToSqr(hurt)) > 64) continue;
			if (AttackFind.FindCanNotAttack(this, hurt)) continue;
			if (!this.hasLineOfSight(hurt)) continue;
			if (Main.canSee(hurt, this)) continue;
			AttackFind.attackBegin(this, hurt);
			boolean bl2 = AttackFind.attackAfter(this, hurt, 1.5f, 1f, false, 0f);
		}
		//横扫效果
		double d = -Mth.sin(this.getYRot() * 0.017453292f);
		double d2 = Mth.cos(this.getYRot() * 0.017453292f);
		if (this.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() - d, this.getY(0.5), this.getZ() - d2, 0, d, 0.0, d2, 0.0);
		}
		if (JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && this.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK)) {
			ItemStack hand = this.getMainHandItem();
			hand.hurtAndBreak(1, this, player -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
		}
		return true;
	}
	@Override
	public void makeMad() {
		if (!this.level().isClientSide()) {
			this.setAnimTick(50);
			this.setAnimationState("angry2");
		}
		super.makeMad();
	}
	public void makeMadOld() {
		super.makeMad();
		if (!this.level().isClientSide()) {
			this.setAnimTick(20);
			this.setAnimationState("angry1");
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
	public void handleStartJump(int n) {
		this.allowStandSliding = true;
		this.standIfPossible();
		this.playJumpSound();
		if (n < 70) {
			this.level().broadcastEntityEvent(this, (byte)101);
		}
		else {
			this.level().broadcastEntityEvent(this, (byte)102);
		}
	}

	@Override
	public void handleStopJump() {
	}

	@Override
	protected float getRiddenSpeed(Player player) {
		return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED));
	}

	@Override
	public double getCustomJump() {
		return this.getAttributeValue(Attributes.JUMP_STRENGTH);
	}

	@Override
	protected void executeRidersJump(float f, Vec3 vec3) {
		double d = this.getCustomJump() * (double)f * (double)this.getBlockJumpFactor();
		double d2 = d + (double)this.getJumpBoostPower();
		Vec3 vec32 = this.getDeltaMovement();
		this.setDeltaMovement(vec32.x, d2, vec32.z);
		this.setIsJumping(true);
		this.hasImpulse = true;
		if (vec3.z > 0.0) {
			float f2 = Mth.sin(this.getYRot() * 0.017453292f);
			float f3 = Mth.cos(this.getYRot() * 0.017453292f);
			this.setDeltaMovement(this.getDeltaMovement().add(-0.4f * f2 * f, 0.0, 0.4f * f3 * f));
		}
	}

	protected void tickRidden(Player p_278233_, Vec3 p_275693_) {
		if (this.isNormalSteeringControlJerotes()) {
			super.tickRidden(p_278233_, p_275693_);
		}
		else {
			Vec2 vec2 = this.getRiddenRotation(p_278233_);
//			this.setRot(vec2.y, vec2.x);
//			this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
			if (this.isControlledByLocalInstance()) {
				if (p_275693_.z <= 0.0D) {
					this.gallopSoundCounter = 0;
				}
				if (this.onGround()) {
					this.setIsJumping(false);
					if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
						this.executeRidersJump(this.playerJumpPendingScale, p_275693_);
					}
					this.playerJumpPendingScale = 0.0F;
				}
			}
		}
	}
	protected Vec3 getRiddenInput(Player player, Vec3 p_275506_) {
		if (this.isNormalSteeringControlJerotes()) {
			return super.getRiddenInput(player, p_275506_);
		}

		if (this.onGround() && this.playerJumpPendingScale == 0.0F && this.isStanding() && !this.allowStandSliding) {
			return Vec3.ZERO;
		} else {
			float f1 = player.zza;
			if (f1 <= 0.0F) {
				f1 *= 0.25F;
			}
			return new Vec3(0, 0.0D, (double)f1);
		}
	}

	@Override
	public void handleEntityEvent(byte by) {
		if (by == 7) {
			this.spawnTamingParticles(true);
		} else if (by == 6) {
			this.spawnTamingParticles(false);
		}
		else if (by == 101) {
			this.jumpAnimationState.start(this.tickCount);
		}
		else if (by == 102) {
			this.longJumpAnimationState.start(this.tickCount);
		} else {
			super.handleEntityEvent(by);
		}
	}

	@Override
	public boolean isAlliedTo(Entity entity) {
		if (this.isTamed()) {
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
	public boolean canAttack(LivingEntity livingEntity) {
		if (this.isOwnedBy(livingEntity)) {
			return false;
		}
		return super.canAttack(livingEntity);
	}

	@Override
	public boolean wantsToAttack(LivingEntity livingEntity, LivingEntity livingEntity2) {
		return AttackFind.wantsToAttack(this, livingEntity, livingEntity2);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
		this.setHomePos(this.blockPosition());
		return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
	}

	@Override
	public void die(DamageSource damageSource) {
		if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
			this.getOwner().sendSystemMessage(this.getCombatTracker().getDeathMessage());
		}
		super.die(damageSource);
	}

	@Nullable
	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	private int remainingPersistentAngerTime;
	private UUID persistentAngerTarget;

	@Override
	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
	}

	@Override
	public void setRemainingPersistentAngerTime(int n) {
		this.remainingPersistentAngerTime = n;
	}

	@Override
	public int getRemainingPersistentAngerTime() {
		return this.remainingPersistentAngerTime;
	}

	@Override
	public void setPersistentAngerTarget(UUID uUID) {
		this.persistentAngerTarget = uUID;
	}

	@Override
	public UUID getPersistentAngerTarget() {
		return this.persistentAngerTarget;
	}
}