package com.jerotes.jerotes.entity.Mob;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import com.google.common.collect.ImmutableList;
import com.jerotes.jerotes.control.WaterAndGroundMoveControl;
import com.jerotes.jerotes.entity.Interface.InventoryEntity;
import com.jerotes.jerotes.entity.Interface.JerotesPlayerBaseEntity;
import com.jerotes.jerotes.goal.*;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.item.Interface.ItemTwoHanded;
import com.jerotes.jerotes.item.Tool.*;
import com.jerotes.jerotes.navigation.WaterAndGroundPathNavigation;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class JerotesPlayerEntity extends HumanEntity implements JerotesPlayerBaseEntity {
	//其他内容↓
	//信标效果BeaconBlockEntityMixin
	//被生物主动攻击NearestAttackableTargetGoalMixin
	//其他适用玩家的判定
	public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F);
	private static final ImmutableMap<Object, Object> POSES = ImmutableMap.builder().put(Pose.STANDING, STANDING_DIMENSIONS).put(Pose.SLEEPING, SLEEPING_DIMENSIONS).put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.CROUCHING, EntityDimensions.scalable(0.6F, 1.5F)).put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F)).build();
	private static final EntityDataAccessor<Integer> LOOK_LEVEL = SynchedEntityData.defineId(JerotesPlayerEntity.class, EntityDataSerializers.INT);

	@Nullable
	private Pose forcedPose;
	private final Abilities abilities = new Abilities();

	public JerotesPlayerEntity(EntityType<? extends HumanEntity> type, Level world) {
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
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 1);
		builder = builder.add(Attributes.FOLLOW_RANGE, 128);
		builder = builder.add(ForgeMod.ENTITY_REACH.get(), 3);
		builder = builder.add(Attributes.ATTACK_SPEED, 4);
		return builder;
	}


	@Override
	protected void registerGoals() {
		this.getNavigation().getNodeEvaluator().setCanOpenDoors(true);
		this.goalSelector.addGoal(0, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(0, new JerotesPlayerFloatGoal(this));
		this.goalSelector.addGoal(1, new JerotesShiftKeyDownGoal(this));
		//this.goalSelector.addGoal(1, new JerotesPlayerLeapAtTargetAndLookGoal(this, 0.4f));
		this.goalSelector.addGoal(1, new JerotesMainSpellAttackGoal(this, this.getSpellLevel(), 60, 240, 0.5f));
		this.goalSelector.addGoal(1, new JerotesAddSpellAttackGoal(this, this.getSpellLevel(), 180, 240, 0.5f));
		this.goalSelector.addGoal(1, new JerotesCombatIMagicAttackGoal<>(this, 0.5, 15.0f, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesCombatIIMagicAttackGoal<>(this, 0.2, true, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesCombatIIIMagicAttackGoal<>(this, 0.5, 15.0f, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesCombatIVMagicAttackGoal<>(this, 0.5, 15.0f, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesRangedBowAttackGoal<HumanEntity>(this, 0.5, 20, 15.0f));
		this.goalSelector.addGoal(1, new JerotesRangedCrossbowAttackGoal<HumanEntity>(this, 1.0, 15.0f));
		this.goalSelector.addGoal(1, new JerotesRangedJavelinAttackGoal<>(this, 1.0, 60, 12.0F, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesSpearUseGoal<>(this,  1.6, 1.6, 10.0f, 2.0f));
		this.goalSelector.addGoal(1, new JerotesPikeUseGoal(this,  1.3, true));
		this.goalSelector.addGoal(1, new JerotesRangedThrowAttackGoal<>(this, 0.4, 40, 12.0F));
		this.goalSelector.addGoal(2, new JerotesMeleeAttackGoal(JerotesPlayerEntity.this, 1.4, true, true, true));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0f));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, HumanEntity.class, 6.0f));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
			@Override
			public void start() {
				super.start();
				this.unseenMemoryTicks = 18000;
			}
		});
		this.targetSelector.addGoal(1, new JerotesHelpSameFactionGoal(this, LivingEntity.class, false, false, livingEntity -> livingEntity instanceof LivingEntity));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 5, false, false, livingEntity -> EntityFactionFind.isHateFaction(this, livingEntity)) {
			@Override
			public void start() {
				super.start();
				this.unseenMemoryTicks = 18000;
			}
		});
		this.targetSelector.addGoal(1, new JerotesHelpAlliesGoal(this, LivingEntity.class, false, false, livingEntity -> livingEntity instanceof LivingEntity));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, this::isAngryAt) {
			@Override
			public void start() {
				super.start();
				this.unseenMemoryTicks = 18000;
			}
		});
		this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<HumanEntity>(this, true));
		//提前反击目标为自己的生物
		this.targetSelector.addGoal(3, new JerotesPlayerTargetGoal<Mob>(this, Mob.class, false) {
			@Override
			public void start() {
				super.start();
				this.unseenMemoryTicks = 18000;
			}
		});
	}

	@Override
	public boolean beTargetAsPlayer() {
		return true;
	}
	@Override
	public boolean beLookAsPlayer() {
		return true;
	}
	@Override
	public boolean beAnesthetizedAsPlayer() {
		return true;
	}
	@Override
	public boolean otherAttackReachAsPlayer() {
		return true;
	}
	@Override
	public boolean usePikeAsPlayer() {
		return true;
	}
	@Override
	public boolean useSpearAsPlayer() {
		return true;
	}
	@Override
	public boolean useBeaconAsPlayer() {
		return true;
	}
	@Override
	public boolean attackDamageSourceAsPlayer() {
		return true;
	}
	@Override
	public boolean hurtByEnderPearlAsPlayer() {
		return true;
	}
	@Override
	//使用盾牌和双手武器
	public void useBlockingItem(Mob mob) {
		if (!mob.level().isClientSide()) {
			if (mob.getTarget() != null && (
					getAttackBoundingBox().inflate(4.5f).intersects(mob.getTarget().getBoundingBox()) ||
					mob.getTarget().getBoundingBox().inflate(4.5f).intersects(mob.getBoundingBox())) || mustShieldTick > 0) {
				//副手盾牌
				if (shieldCanUse() && notBowCrossbow(mob, InteractionHand.MAIN_HAND) && mob.getOffhandItem().getItem() instanceof ShieldItem && (!mob.isUsingItem() || mob.getUseItem().getItem() instanceof ShieldItem) && mob.attackAnim <= 0.0F) {
					if (!mob.isUsingItem()) {
						mob.startUsingItem(InteractionHand.OFF_HAND);
						this.setMustShieldTick(Math.max(10, this.mustShieldTick));
					}
				}
				//主手盾牌
				else if (shieldCanUse() && notBowCrossbow(mob, InteractionHand.OFF_HAND) && mob.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShieldItem && (!mob.isUsingItem() || mob.getUseItem().getItem() instanceof ShieldItem) && mob.attackAnim <= 0.0F) {
					if (!mob.isUsingItem()) {
						mob.startUsingItem(InteractionHand.MAIN_HAND);
						this.setMustShieldTick(Math.max(10, this.mustShieldTick));
					}
				}
				//主手双手武器
				else if (mob.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemTwoHanded itemTwoHanded && !(mob.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ItemToolBasePike) && itemTwoHanded.canBlock() && mob.getOffhandItem().isEmpty() && (!mob.isUsingItem() || mob.getUseItem().getItem() instanceof ItemTwoHanded) && mob.attackAnim <= 0.0F) {
					if (!mob.isUsingItem()) {
						mob.startUsingItem(InteractionHand.MAIN_HAND);
						this.setMustShieldTick(Math.max(10, this.mustShieldTick));
					}
				}
				//副手双手武器
				else if (mob.getOffhandItem().getItem() instanceof ItemTwoHanded itemTwoHanded && !(mob.getOffhandItem().getItem() instanceof ItemToolBasePike) && itemTwoHanded.canBlock() && mob.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && (!mob.isUsingItem() || mob.getUseItem().getItem() instanceof ItemTwoHanded) && mob.attackAnim <= 0.0F) {
					if (!mob.isUsingItem()) {
						mob.startUsingItem(InteractionHand.OFF_HAND);
						this.setMustShieldTick(Math.max(10, this.mustShieldTick));
					}
				}
				//其他状态下停止使用
				else {
					stopUse(mob);
				}
			}
			//非愤怒状态下停止使用
			else {
				stopUse(mob);
			}
		}
	}
	@Override
	public float meleeOrRangeDistance() {
		//无法触及
		if (canNotAttackTargetTick >= 240) {
			return 2f;
		}
		//无法触及
		double d = Math.max(2f, this.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 1) + 4;
		return (float) d;
	}
	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.PLAYER_SWIM;
	}
	@Override
	protected SoundEvent getSwimSplashSound() {
		return SoundEvents.PLAYER_SPLASH;
	}
	@Override
	protected SoundEvent getSwimHighSpeedSplashSound() {
		return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
	}
	@Override
	public AbstractArrow getCustomArrow(ItemStack itemStack, float f) {
		return getMobArrow(this, itemStack, f);
	}
	public static AbstractArrow getMobArrow(LivingEntity p_37301_, ItemStack p_37302_, float p_37303_) {
		ArrowItem arrowitem = (ArrowItem)(p_37302_.getItem() instanceof ArrowItem ? p_37302_.getItem() : Items.ARROW);
		AbstractArrow abstractarrow = arrowitem.createArrow(p_37301_.level(), p_37302_, p_37301_);
		if (p_37302_.is(Items.TIPPED_ARROW) && abstractarrow instanceof Arrow) {
			((Arrow)abstractarrow).setEffectsFromItem(p_37302_);
		}
		return abstractarrow;
	}
	@Override
	public float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
		switch (pose) {
			case SWIMMING:
			case FALL_FLYING:
			case SPIN_ATTACK:
				return 0.4F * ((this.isBaby() ? 0.86f : 1.62F) / 1.62F);
			case CROUCHING:
				return 1.27F * ((this.isBaby() ? 0.86f : 1.62F) / 1.62F);
			default:
				return 1.62F * ((this.isBaby() ? 0.86f : 1.62F) / 1.62F);
		}
	}
	public boolean canBeSeenAsEnemy() {
		return !this.getAbilities().invulnerable && super.canBeSeenAsEnemy();
	}
	protected boolean onSoulSpeedBlock() {
		return !this.abilities.flying && super.onSoulSpeedBlock();
	}
	public boolean isAffectedByFluids() {
		return !this.abilities.flying;
	}
	protected Vec3 maybeBackOffFromEdge(Vec3 vec3, MoverType moverType) {
		if (!this.abilities.flying && vec3.y <= 0.0 && (moverType == MoverType.SELF || moverType == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround()) {
			double d0 = vec3.x;
			double d1 = vec3.z;
			double d2 = 0.05;

			while(true) {
				while(d0 != 0.0 && this.level().noCollision(this, this.getBoundingBox().move(d0, (double)(-this.maxUpStep()), 0.0))) {
					if (d0 < 0.05 && d0 >= -0.05) {
						d0 = 0.0;
					} else if (d0 > 0.0) {
						d0 -= 0.05;
					} else {
						d0 += 0.05;
					}
				}

				while(true) {
					while(d1 != 0.0 && this.level().noCollision(this, this.getBoundingBox().move(0.0, (double)(-this.maxUpStep()), d1))) {
						if (d1 < 0.05 && d1 >= -0.05) {
							d1 = 0.0;
						} else if (d1 > 0.0) {
							d1 -= 0.05;
						} else {
							d1 += 0.05;
						}
					}

					while(true) {
						while(d0 != 0.0 && d1 != 0.0 && this.level().noCollision(this, this.getBoundingBox().move(d0, (double)(-this.maxUpStep()), d1))) {
							if (d0 < 0.05 && d0 >= -0.05) {
								d0 = 0.0;
							} else if (d0 > 0.0) {
								d0 -= 0.05;
							} else {
								d0 += 0.05;
							}

							if (d1 < 0.05 && d1 >= -0.05) {
								d1 = 0.0;
							} else if (d1 > 0.0) {
								d1 -= 0.05;
							} else {
								d1 += 0.05;
							}
						}

						vec3 = new Vec3(d0, vec3.y, d1);
						return vec3;
					}
				}
			}
		} else {
			return vec3;
		}
	}
	private boolean isAboveGround() {
		return this.onGround() || this.fallDistance < this.maxUpStep() && !this.level().noCollision(this, this.getBoundingBox().move(0.0, (double)(this.fallDistance - this.maxUpStep()), 0.0));
	}
	public Abilities getAbilities() {
		return this.abilities;
	}
	private boolean canFallAtLeast(double d, double d2, double d3) {
		AABB aABB = this.getBoundingBox();
		return this.level().noCollision(this, new AABB(aABB.minX + 1.0E-7 + d, aABB.minY - d3 - 1.0E-7, aABB.minZ + 1.0E-7 + d2, aABB.maxX - 1.0E-7 + d, aABB.minY, aABB.maxZ - 1.0E-7 + d2));
	}
	public boolean isSecondaryUseActive() {
		return this.isShiftKeyDown();
	}
	protected boolean wantsToStopRiding() {
		return this.isShiftKeyDown();
	}
	protected boolean isStayingOnGroundSurface() {
		return this.isShiftKeyDown();
	}
	protected void updatePlayerPose() {
		if (!this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.SWIMMING)) {
			return;
		}
		Pose pose = this.getDesiredPose();
		Pose pose2 = this.isSpectator() || this.isPassenger() || this.canPlayerFitWithinBlocksAndEntitiesWhen(pose) ? pose : (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING) ? Pose.CROUCHING : Pose.SWIMMING);
		this.setPose(pose2);
	}
	protected boolean canPlayerFitWithinBlocksAndEntitiesWhen(Pose pose) {
		return this.level().noCollision(this, this.getDimensions(pose).makeBoundingBox(this.position()).deflate(1.0E-7));
	}
	private Pose getDesiredPose() {
		if (this.isSleeping()) {
			return Pose.SLEEPING;
		}
		if (this.isSwimming()) {
			return Pose.SWIMMING;
		}
		if (this.isFallFlying()) {
			return Pose.FALL_FLYING;
		}
		if (this.isAutoSpinAttack()) {
			return Pose.SPIN_ATTACK;
		}
		if (this.isShiftKeyDown() && !this.abilities.flying) {
			return Pose.CROUCHING;
		}
		return Pose.STANDING;
	}
	public void setForcedPose(@Nullable Pose pose) {
		this.forcedPose = pose;
	}
	@Nullable
	public Pose getForcedPose() {
		return this.forcedPose;
	}
	public ItemStack eat(Level level, ItemStack itemStack) {
		this.heal(itemStack.getFoodProperties(this) != null ? Objects.requireNonNull(itemStack.getFoodProperties(this)).getNutrition() : 1f);
		level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_BURP, this.getSoundSource(), 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
		return super.eat(level, itemStack);
	}
	public boolean canSprint() {
		return true;
	}
	public int getPortalWaitTime() {
		return this.abilities.invulnerable ? 1 : 80;
	}
	public void travel(Vec3 vec3f) {
		double d0 = this.getX();
		double d1 = this.getY();
		double d2 = this.getZ();
		double d5;
		if (this.isSwimming() && !this.isPassenger()) {
			d5 = this.getLookAngle().y;
			double d4 = d5 < -0.2 ? 0.085 : 0.06;
			if (d5 <= 0.0 || this.jumping || !this.level().getBlockState(BlockPos.containing(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).getFluidState().isEmpty()) {
				Vec3 vec3 = this.getDeltaMovement();
				this.setDeltaMovement(vec3.add(0.0, (d5 - vec3.y) * d4, 0.0));
			}
		}
		if (this.abilities.flying && !this.isPassenger()) {
			d5 = this.getDeltaMovement().y;
			super.travel(vec3f);
			Vec3 vec31 = this.getDeltaMovement();
			this.setDeltaMovement(vec31.x, d5 * 0.6, vec31.z);
			this.resetFallDistance();
			this.setSharedFlag(7, false);
		}
		else if (this.isEffectiveAi() && this.isInWater()) {
			this.moveRelative(this.getSpeed(), vec3f);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.75));
		}
		else {
			super.travel(vec3f);
		}
	}
	public boolean causeFallDamage(float p_150093_, float p_150094_, DamageSource p_150095_) {
		if (this.abilities.mayfly) {
			return false;
		} else {
			return super.causeFallDamage(p_150093_, p_150094_, p_150095_);
		}
	}
	public void updateSwimming() {
		if (this.abilities.flying) {
			this.setSwimming(false);
		} else {
			super.updateSwimming();
		}
	}
	public void makeStuckInBlock(BlockState p_36196_, Vec3 p_36197_) {
		if (!this.abilities.flying) {
			super.makeStuckInBlock(p_36196_, p_36197_);
		}
	}
	protected Entity.MovementEmission getMovementEmission() {
		return !this.abilities.flying && (!this.onGround() || !this.isDiscrete()) ? MovementEmission.ALL : MovementEmission.NONE;
	}
	public void onUpdateAbilities() {
		if (this.isSpectator()) {
			this.removeEffectParticles();
			this.setInvisible(true);
		} else {
			super.updateInvisibilityStatus();
		}
	}
	@Override
	public boolean isSwimming() {
		return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
	}
	@Override
	public boolean isPushedByFluid() {
		return !this.abilities.flying;
	}
	@Override
	public void setRemainingFireTicks(int p_36353_) {
		super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min(p_36353_, 1) : p_36353_);
	}
	@Override
	protected float getBlockSpeedFactor() {
		return !this.abilities.flying && !this.isFallFlying() ? super.getBlockSpeedFactor() : 1.0F;
	}
	@Override
	protected boolean shouldRemoveSoulSpeed(BlockState p_36262_) {
		return this.abilities.flying || super.shouldRemoveSoulSpeed(p_36262_);
	}
	@Override
	protected float getFlyingSpeed() {
		if (this.abilities.flying && !this.isPassenger()) {
			return this.isSprinting() ? this.abilities.getFlyingSpeed() * 2.0F : this.abilities.getFlyingSpeed();
		} else {
			return this.isSprinting() ? 0.025999999F : 0.02F;
		}
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
		AABB aabb1 = aabb.inflate(Math.sqrt(2.04F) - (double)0.6F, 0.0D, Math.sqrt(2.04F) - (double)0.6F);
		double d = Math.max(0.5f, this.getAttributeValue(ForgeMod.ENTITY_REACH.get()) - 2);
		return aabb1.inflate(d, d, d);
	}
	@Override
	public boolean isWithinMeleeAttackRange(LivingEntity livingEntity) {
		return this.getAttackBoundingBox().intersects(livingEntity.getBoundingBox());
	}

	public boolean isLandNavigatorType = true;
	public int sprintingCooldown;
	public int mustShieldTick;
	public int canNotAttackTargetTick;
	public int useFireworkRocketCooldown;
	public void setSprintingCooldown(int n) {
		this.sprintingCooldown = n;
	}
	public void setCanNotAttackTargetTick(int n) {
		this.canNotAttackTargetTick = n;
	}
	public void setMustShieldTick(int n) {
		this.mustShieldTick = n;
	}
	public boolean canEatOrDrinkHand() {
		return true;
	}
	public boolean needHeal() {
		return this.getTarget() == null && this.getHealth() <= this.getMaxHealth() * 0.9 || this.getHealth() <= this.getMaxHealth() * 0.5;
	}
	public boolean isNotOffHandItem(ItemStack itemStack) {
		return super.isNotOffHandItem(itemStack) || itemStack.getItem() == Items.GLASS_BOTTLE;
	}
	@Override
	public boolean howToEatOrDrinkHand(Mob mob, ItemStack itemStack) {
		//药水
		if (itemStack.getItem() instanceof PotionItem && !(itemStack.getItem() instanceof ThrowablePotionItem)) {
			//防火
			if (PotionUtils.getPotion(itemStack) == Potions.LONG_FIRE_RESISTANCE || PotionUtils.getPotion(itemStack) == Potions.FIRE_RESISTANCE) {
				if (this.getRemainingFireTicks() > 0 && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
					return true;
				}
			}
			//水肺
			if (PotionUtils.getPotion(itemStack) == Potions.WATER_BREATHING ||
					PotionUtils.getPotion(itemStack) == Potions.LONG_WATER_BREATHING) {
				if (this.getAirSupply() <= 200 && !this.hasEffect(MobEffects.WATER_BREATHING)) {
					return true;
				}
			}
			//治疗
			if (PotionUtils.getPotion(itemStack) == Potions.HEALING ||
					PotionUtils.getPotion(itemStack) == Potions.STRONG_HEALING) {
				if (needHeal()) {
					return true;
				}
			}
			if (PotionUtils.getPotion(itemStack) == Potions.REGENERATION ||
					PotionUtils.getPotion(itemStack) == Potions.LONG_REGENERATION ||
					PotionUtils.getPotion(itemStack) == Potions.STRONG_REGENERATION) {
				if (needHeal() && !this.hasEffect(MobEffects.REGENERATION)) {
					return true;
				}
			}
		}


		//食物
		if (itemStack.isEdible()) {
			if (itemStack.is(Items.GOLDEN_APPLE)) {
				if (needHeal() || this.getTarget() != null && !(this.hasEffect(MobEffects.ABSORPTION) || this.hasEffect(MobEffects.DAMAGE_RESISTANCE) || this.getRemainingFireTicks() > 0 && !this.hasEffect(MobEffects.FIRE_RESISTANCE))) {
					return true;
				}
			}
			if (itemStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
				if (needHeal() || this.getTarget() != null && !this.hasEffect(MobEffects.ABSORPTION)) {
					return true;
				}
			}
			if (itemStack.getFoodProperties(this) != null && Objects.requireNonNull(itemStack.getFoodProperties(this)).getEffects().isEmpty()) {
				if (needHeal()) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isCanEatItem(ItemStack itemStack) {
		//药水
		if (itemStack.getItem() instanceof PotionItem && !(itemStack.getItem() instanceof ThrowablePotionItem)) {
			//防火
			if (PotionUtils.getPotion(itemStack) == Potions.LONG_FIRE_RESISTANCE || PotionUtils.getPotion(itemStack) == Potions.FIRE_RESISTANCE) {
				return true;
			}
			//水肺
			if (PotionUtils.getPotion(itemStack) == Potions.WATER_BREATHING ||
					PotionUtils.getPotion(itemStack) == Potions.LONG_WATER_BREATHING) {
				return true;
			}
			//治疗
			if (PotionUtils.getPotion(itemStack) == Potions.HEALING ||
					PotionUtils.getPotion(itemStack) == Potions.STRONG_HEALING ||
					PotionUtils.getPotion(itemStack) == Potions.REGENERATION ||
					PotionUtils.getPotion(itemStack) == Potions.LONG_REGENERATION ||
					PotionUtils.getPotion(itemStack) == Potions.STRONG_REGENERATION
			) {
				return true;
			}
		}
		//食物
		if (itemStack.isEdible()) {
			return true;
		}
		return false;
	}
	@Override
	public boolean isOffHandItem(ItemStack itemStack) {
		if (this.isFallFlying() && this.isAggressive() && itemStack.is(Items.FIREWORK_ROCKET)) {
			return true;
		}
		else if (howToEatOrDrinkHand(this, itemStack)) {
			return true;
		}
		return false;
	}
	@Override
	public boolean canHoldItem(ItemStack itemStack) {
		if (isCanEatItem(itemStack)) {
			return true;
		}
		return InventoryEntity.isThrow(this, itemStack) ||
				InventoryEntity.isMeleeWeapon(this, itemStack) ||
				InventoryEntity.isShield(this, itemStack) ||
				InventoryEntity.isPike(this, itemStack) ||
				InventoryEntity.isCrossbow(this, itemStack) ||
				InventoryEntity.isOtherRange(this, itemStack) ||
				InventoryEntity.isBow(this, itemStack) ||
				InventoryEntity.isRangeJavelin(this, itemStack) ||
				InventoryEntity.isHelmet(this, itemStack) ||
				InventoryEntity.isChestplate(this, itemStack) ||
				InventoryEntity.isLeggings(this, itemStack) ||
				InventoryEntity.isBoots(this, itemStack) ||
				InventoryEntity.isMagicItem(this, itemStack);
	}
	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		InventoryCarrier.pickUpItem(this, this, itemEntity);
	}
	@Override
	public EntityDimensions getDimensions(Pose p_36166_) {
		return (EntityDimensions)POSES.getOrDefault(p_36166_, STANDING_DIMENSIONS);
	}
	@Override
	public ImmutableList<Pose> getDismountPoses() {
		return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
	}
	@Override
	public Vec3 getRopeHoldPosition(float f) {
		double d = 0.22 * (this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0);
		float f2 = Mth.lerp(f * 0.5f, this.getXRot(), this.xRotO) * ((float)Math.PI / 180);
		float f3 = Mth.lerp(f, this.yBodyRotO, this.yBodyRot) * ((float)Math.PI / 180);
		if (this.isFallFlying() || this.isAutoSpinAttack()) {
			float f4;
			Vec3 vec3 = this.getViewVector(f);
			Vec3 vec32 = this.getDeltaMovement();
			double d2 = vec32.horizontalDistanceSqr();
			double d3 = vec3.horizontalDistanceSqr();
			if (d2 > 0.0 && d3 > 0.0) {
				double d4 = (vec32.x * vec3.x + vec32.z * vec3.z) / Math.sqrt(d2 * d3);
				double d5 = vec32.x * vec3.z - vec32.z * vec3.x;
				f4 = (float)(Math.signum(d5) * Math.acos(d4));
			} else {
				f4 = 0.0f;
			}
			return this.getPosition(f).add(new Vec3(d, -0.11, 0.85).zRot(-f4).xRot(-f2).yRot(-f3));
		}
		if (this.isVisuallySwimming()) {
			return this.getPosition(f).add(new Vec3(d, 0.2, -0.15).xRot(-f2).yRot(-f3));
		}
		double d6 = this.getBoundingBox().getYsize() - 1.0;
		double d7 = this.isCrouching() ? -0.2 : 0.07;
		return this.getPosition(f).add(new Vec3(d, d6, d7).yRot(-f3));
	}
	public void setLookLevel(int n){
		this.getEntityData().set(LOOK_LEVEL, n);
	}
	public int getLookLevel(){
		return this.getEntityData().get(LOOK_LEVEL);
	}
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		this.abilities.addSaveData(compoundTag);
		compoundTag.putInt("LookLevel", this.getLookLevel());
		compoundTag.putInt("MustShieldTick", this.mustShieldTick);
		compoundTag.putInt("UseFireworkRocketCooldown", this.useFireworkRocketCooldown);
		compoundTag.putInt("SprintingCooldown", this.sprintingCooldown);
		compoundTag.putInt("CanNotAttackTargetTick", this.canNotAttackTargetTick);
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.abilities.loadSaveData(compoundTag);
		this.setLookLevel(compoundTag.getInt("LookLevel"));
		this.mustShieldTick = compoundTag.getInt("MustShieldTick");
		this.useFireworkRocketCooldown = compoundTag.getInt("UseFireworkRocketCooldown");
		this.sprintingCooldown = compoundTag.getInt("SprintingCooldown");
		this.canNotAttackTargetTick = compoundTag.getInt("CanNotAttackTargetTick");
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(LOOK_LEVEL, 1);
	}

	@Override
	public void tick() {
		if (this.abilities.flying && !this.isPassenger()) {
			this.resetFallDistance();
		}
		super.tick();
		//姿势
		this.updatePlayerPose();

	}
	@Override
	public void aiStep() {
		if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
			if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
				this.heal(1.0F);
			}
		}
		super.aiStep();
		//使用鞘翅
		if (this.isAggressive() && this.onGround() && this.tickCount % 10 == 0 &&
				this.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem && (this.getMainHandItem().is(Items.FIREWORK_ROCKET) || this.getOffhandItem().is(Items.FIREWORK_ROCKET))) {
			if (!this.level().isClientSide()) {
				this.playerJump();
			}
		}
		boolean flag = this.fallDistance > 2 && this.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem ||
				this.isFallFlying() && this.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem ||
				this.getTarget() != null && !this.getTarget().onGround() && this.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem ||
				this.isAggressive() && !this.onGround() && this.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem && (this.getMainHandItem().is(Items.FIREWORK_ROCKET) || this.getOffhandItem().is(Items.FIREWORK_ROCKET));
		if (flag && !this.onGround() && !this.isPassenger() && !this.hasEffect(MobEffects.LEVITATION)) {
			this.setSharedFlag(7, true);
		} else {
			flag = false;
		}
		if (!this.level().isClientSide()) {
			this.setSharedFlag(7, flag);
		}
		if (this.isFallFlying() && this.getTarget() != null) {
			this.lookAt(this.getTarget(), 360f, 360f);
			this.getLookControl().setLookAt(this.getTarget(), 360f, 360f);
		}
		//火箭加速
		if (this.getMainHandItem().is(Items.FIREWORK_ROCKET) && this.isFallFlying() && useFireworkRocketCooldown <= 0 && this.isAggressive()) {
			if (!this.level().isClientSide) {
				FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(this.level(), this.getMainHandItem(), this);
				this.level().addFreshEntity(fireworkrocketentity);
				if (!this.getAbilities().instabuild) {
					this.getMainHandItem().shrink(1);
				}
			}
			this.swing(InteractionHand.MAIN_HAND);
			this.useFireworkRocketCooldown = 20;
		}
		else if (this.getOffhandItem().is(Items.FIREWORK_ROCKET) && this.isFallFlying() && useFireworkRocketCooldown <= 0 && this.isAggressive()) {
			if (!this.level().isClientSide) {
				FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(this.level(), this.getOffhandItem(), this);
				this.level().addFreshEntity(fireworkrocketentity);
				if (!this.getAbilities().instabuild) {
					this.getOffhandItem().shrink(1);
				}
			}
			this.swing(InteractionHand.OFF_HAND);
			this.useFireworkRocketCooldown = 20;
		}
		this.useFireworkRocketCooldown = Math.max(0, useFireworkRocketCooldown - 1);
		//冲刺
		if (!this.level().isClientSide()) {
			this.setSprinting(this.sprintingCooldown <= 0 && (
					(this.isAggressive() &&
							!(this.xOld == this.getX() && this.zOld == this.getZ()) &&
							!(this.getTarget() != null && InventoryEntity.isMeleeWeapon(this, this.getMainHandItem()) && this.getAttackBoundingBox().deflate(0.5f).intersects(this.getTarget().getBoundingBox())) &&
							this.getDeltaMovement().multiply(1, 0, 1).length() > 0.001 &&
							!this.isShiftKeyDown() &&
							!(this.getTarget() != null && this.getSensing().hasLineOfSight(this.getTarget()) && this.getAttackBoundingBox().inflate(0.5f).intersects(this.getTarget().getBoundingBox()) && !this.isInFluidType() && !this.onGround()) &&
							!(this.isUsingItem() && !(this.getUseItem().getItem() instanceof ItemToolBaseSpearBase)))

							||

							(this.getAirSupply() <= 100 && (this.isInFluidType())))
			);
			if (this.sprintingCooldown > 0) {
				this.setSprinting(false);
			}
		}
		this.setSprintingCooldown(Math.max(0, this.sprintingCooldown - 1));
		//盾牌
		if (this.getTarget() != null) {
			List<Projectile> projectiles = this.level()
					.getEntitiesOfClass(Projectile.class,
							this.getBoundingBox().inflate(6.0),
							p -> isValidProjectile(p));
			if (!projectiles.isEmpty()) {
				this.setMustShieldTick(30);
			}
		}
		this.setMustShieldTick(Math.max(0, this.mustShieldTick - 1));
		//无法触及目标
		if (this.getTarget() != null && !this.isWithinMeleeAttackRange(this.getTarget()) && this.getSensing().hasLineOfSight(this.getTarget())) {
			this.canNotAttackTargetTick = (Math.min(240, this.canNotAttackTargetTick + (this.getTarget().getY() > this.getY() + 1 || this.getTarget().getY() < this.getY() - 1 ? 4 : 1)));
		}
		else {
			this.canNotAttackTargetTick = (Math.max(0, this.canNotAttackTargetTick - 1));
		}
		//水陆切换
		if (this.isInWater() && this.isLandNavigatorType) {
			this.moveControl = new WaterAndGroundMoveControl(this);
			this.navigation = new WaterAndGroundPathNavigation(this, level());
			this.isLandNavigatorType = false;
		}
		if (!this.isInWater() && !this.isLandNavigatorType) {
			this.moveControl = new MoveControl(this);
			this.navigation = new GroundPathNavigation(this, level());
			this.isLandNavigatorType = true;
		}
		//腾跃
		if (this.getNavigation().getPath() != null && !this.getNavigation().isDone()) {
			BlockState blockState = this.level().getBlockState(this.getNavigation().getPath().getTarget());
			if ((!blockState.getFluidState().isEmpty() && !blockState.isAir() || this.getTarget() != null && this.getTarget().onGround()) && this.isInFluidType() && this.horizontalCollision) {
				this.setDeltaMovement(this.getDeltaMovement().add(0,0.25f,0));
			}
		}
		//食用
		if (this.getTarget() != null && this.distanceTo(this.getTarget()) < 6 && this.isUsingItem() && InventoryEntity.isFoodOrPotion(this, this.getUseItem())) {
			float f1 = this.getYRot();
			float f2 = this.getXRot();
			float f3 = -Mth.sin(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
			float f4 = -Mth.sin(f2 * 0.017453292f);
			float f5 = Mth.cos(f1 * 0.017453292f) * Mth.cos(f2 * 0.017453292f);
			float f6 = Mth.sqrt(f3 * f3 + f4 * f4 + f5 * f5);
			float f7 = -0.04f;
			this.setSprintingCooldown(10);
			if (this.getDeltaMovement().x <= 0.15 && this.getDeltaMovement().z <= 0.15)
				this.setDeltaMovement(this.getDeltaMovement().add(f3 *= f7 / f6, 0, f5 *= f7 / f6));
		}
		//投矛
		if (this.getTarget() != null){
			//应该近战时
			if (this.distanceTo(this.getTarget()) <= this.meleeOrRangeDistance() && !(this.distanceTo(this.getTarget()) > this.meleeOrRangeDistance() / 2 && !this.getTarget().onGround()) && canNotAttackTargetTick < 240) {
				//战斗物品
				if (this.getMainHandItem().getItem() instanceof ItemToolBaseThrowingSpearOfJavelin itemToolBaseThrowingSpearOfJavelin) {
					ItemStack itemStack1 = new ItemStack(itemToolBaseThrowingSpearOfJavelin.getOtherMode());
					itemStack1.deserializeNBT(this.getMainHandItem().getOrCreateTag());
					itemStack1.setTag(this.getMainHandItem().getOrCreateTag());
					itemStack1.setDamageValue(this.getMainHandItem().getDamageValue());
					this.setItemInHand(InteractionHand.MAIN_HAND, itemStack1);
					this.canNotAttackTargetTick = 0;
				}
			}
			//应该远程时
			else {
				//战斗物品
				if (this.getMainHandItem().getItem() instanceof ItemToolBaseThrowingSpearOfSpear itemToolBaseThrowingSpearOfSpear) {
					ItemStack itemStack1 = new ItemStack(itemToolBaseThrowingSpearOfSpear.getOtherMode());
					itemStack1.deserializeNBT(this.getMainHandItem().getOrCreateTag());
					itemStack1.setTag(this.getMainHandItem().getOrCreateTag());
					itemStack1.setDamageValue(this.getMainHandItem().getDamageValue());
					this.setItemInHand(InteractionHand.MAIN_HAND, itemStack1);
				}
			}
		}
		//招架盾
		if (this.isUsingItem() && this.getUseItem().getItem() instanceof ItemToolBaseParryShield itemToolBaseParryShield &&
				this.isAggressive() && this.getTarget() != null &&
				this.getRandom().nextFloat() > 0.2f) {
			if ((this.getPersistentData().getDouble("jerotes_shield_parry_cooldown") <= 0 ||
					this.getPersistentData().get("jerotes_shield_parry_cooldown") == null)) {
				this.getPersistentData().putDouble("jerotes_shield_parry_cooldown", itemToolBaseParryShield.parryCooldownTicks);
				this.getPersistentData().putDouble("jerotes_shield_parry_tick", itemToolBaseParryShield.parryDurationTicks);
				if (!this.level().isClientSide()) {
					itemToolBaseParryShield.makeParrySound(this);
				}
			}
		}
	}
	public boolean shouldShiftKeyDown() {
		//投矛
		if (this.getTarget() != null){
			//应该近战时
			if (this.distanceTo(this.getTarget()) <= this.meleeOrRangeDistance() && !(this.distanceTo(this.getTarget()) > this.meleeOrRangeDistance() / 2 && !this.getTarget().onGround())) {
				//战斗物品
				if (this.getMainHandItem().getItem() instanceof ItemToolBaseThrowingSpearOfJavelin) {
					return true;
				}
			}
			//应该远程时
			else {
				//战斗物品
				if (this.getMainHandItem().getItem() instanceof ItemToolBaseThrowingSpearOfSpear && canNotAttackTargetTick > 240) {
					return true;
				}
			}
		}
		return baseShouldShiftKeyDown();
	}

	private boolean isValidProjectile(Projectile projectile) {
		if (projectile instanceof AbstractArrow abstractArrow && !abstractArrow.hasImpulse)
			return false;

		if (
				projectile.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(this) ||
						AttackFind.SameFactionAvoidDamage(projectile.getOwner(), this) ||
						AttackFind.SameFactionAvoidDamage(projectile, this) ||
						projectile.getOwner() instanceof LivingEntity owners && AttackFind.FindCanNotAttack(owners, this)
		) {
			return false;
		}

		if (!Main.canSee(projectile, this)) {
			return false;
		}

		if (projectile.onGround())
			return false;
		Vec3 velocity = projectile.getDeltaMovement();
		if (velocity.lengthSqr() < 0.5 * 0.5) {
			return false;
		}

		Vec3 toMob = this.position().subtract(projectile.position()).normalize();
		double angle = velocity.normalize().dot(toMob);
		return angle >= 0.7;
	}

	public boolean canAttackJump() {
		if (InventoryEntity.isBow(this, this.getMainHandItem()))
			return false;
		if (InventoryEntity.isSpear(this, this.getMainHandItem()))
			return false;
		if (InventoryEntity.isPike(this, this.getMainHandItem()))
			return false;
		if (InventoryEntity.isThrow(this, this.getMainHandItem()))
			return false;
		if (InventoryEntity.isCrossbow(this, this.getMainHandItem()))
			return false;
		if (this.getTarget() != null && this.getMainHandItem().getItem() instanceof ItemToolBaseDagger itemToolBaseDagger && itemToolBaseDagger.canShiftKeyDownUse(this.getTarget()))
			return false;
		if (InventoryEntity.isOtherRange(this, this.getMainHandItem()))
			return false;
		if (this.getMainHandItem().getItem().getDescriptionId().contains("modern_kinetic_gun"))
			return false;
		return !(this.isUsingItem() && !(this.getUseItem().getItem() instanceof ItemTwoHanded itemTwoHanded && itemTwoHanded.canBlock()) && !(this.getUseItem().getItem() instanceof ShieldItem)) && !this.swinging;
	}
	@Override
	public void playerJump() {
		super.jumpFromGround();
	}
	public int getAttackSpeed() {
		double d = this.getAttributeValue(Attributes.ATTACK_SPEED);
		int n = (int) (20 / d);
		return n + (this.getMainHandItem().isEmpty() ? 7 : 3);
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		attack(entity, 1f);
		this.canNotAttackTargetTick = 0;
		return true;
	}

	public void attack(Entity entity, float scale) {
		if (entity.isAttackable() && !entity.skipAttackInteraction(this)) {
			float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
			float f1;
			if (entity instanceof LivingEntity) {
				f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)entity).getMobType());
			} else {
				f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);
			}

			float f2 = scale;
			f *= 0.2F + f2 * f2 * 0.8F;
			f1 *= f2;
			if (f > 0.0F || f1 > 0.0F) {
				boolean flag = f2 > 0.9F;
				boolean flag1 = false;
				float i = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
				i += (float)EnchantmentHelper.getKnockbackBonus(this);
				if (this.isSprinting() && flag) {
					this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
					++i;
					flag1 = true;
				}

				boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround() && !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && entity instanceof LivingEntity;
				flag2 = flag2 && !this.isSprinting();
				if (flag2) {
					f *= 1.5f;
				}

				f += f1;
				boolean flag3 = false;
				double d0 = (double)(this.walkDist - this.walkDistO);
				if (flag && !flag2 && !flag1 && this.onGround() && d0 < (double)this.getSpeed()) {
					ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
					flag3 = itemstack.canPerformAction(ToolActions.SWORD_SWEEP);
				}

				float f4 = 0.0F;
				boolean flag4 = false;
				int j = EnchantmentHelper.getFireAspect(this);
				if (entity instanceof LivingEntity) {
					f4 = ((LivingEntity)entity).getHealth();
					if (j > 0 && !entity.isOnFire()) {
						flag4 = true;
						entity.setSecondsOnFire(1);
					}
				}

				Vec3 vec3 = entity.getDeltaMovement();
				DamageSource damageSource = new DamageSource(this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK), this);
				boolean flag5 = entity.hurt(damageSource, f);
				if (flag5) {
					if (i > 0.0F) {
						if (entity instanceof LivingEntity) {
							((LivingEntity)entity).knockback((double)(i * 0.5F), (double) Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
						} else {
							entity.push((double)(-Mth.sin(this.getYRot() * 0.017453292F) * i * 0.5F), 0.1, (double)(Mth.cos(this.getYRot() * 0.017453292F) * i * 0.5F));
						}

						this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
						this.setSprinting(false);
					}

					if (flag3) {
						float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * f;
						Iterator var19 = this.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(1.0, 0.25, 1.0)).iterator();

						label173:
						while(true) {
							LivingEntity livingentity;
							double entityReachSq;
							do {
								do {
									do {
										do {
											if (!var19.hasNext()) {
												this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
												Main.sweepAttack(this);
												break label173;
											}

											livingentity = (LivingEntity)var19.next();
											entityReachSq = Mth.square(this.getEntityReach());
										} while(livingentity == this);
									} while(livingentity == entity);
								} while(this.isAlliedTo(livingentity));
							} while(livingentity instanceof ArmorStand && ((ArmorStand)livingentity).isMarker());

							if (this.distanceToSqr(livingentity) < entityReachSq) {
								livingentity.knockback(0.4000000059604645, (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
								livingentity.hurt(damageSource, f3);
							}
						}
					}

					if (entity instanceof ServerPlayer && entity.hurtMarked) {
						((ServerPlayer)entity).connection.send(new ClientboundSetEntityMotionPacket(entity));
						entity.hurtMarked = false;
						entity.setDeltaMovement(vec3);
					}

					if (flag2) {
						this.level().playSound( null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
						if (this.level() instanceof ServerLevel serverLevel) {
							serverLevel.getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(entity, 4));
						}
						if (this.level().isClientSide()) {
							Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, ParticleTypes.CRIT);
						}
					}

					if (!flag2 && !flag3) {
						if (flag) {
							this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
						} else {
							this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
						}
					}

					if (f1 > 0.0F) {
						if (this.level() instanceof ServerLevel serverLevel) {
							serverLevel.getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(entity, 5));
						}
						if (this.level().isClientSide()) {
							Minecraft.getInstance().particleEngine.createTrackingEmitter(entity, ParticleTypes.ENCHANTED_HIT);
						}
					}

					this.setLastHurtMob(entity);
					if (entity instanceof LivingEntity) {
						EnchantmentHelper.doPostHurtEffects((LivingEntity)entity, this);
					}

					EnchantmentHelper.doPostDamageEffects(this, entity);
					ItemStack itemstack1 = this.getMainHandItem();
					Entity entitys = entity;
					if (entitys instanceof PartEntity) {
						entitys = ((PartEntity)entity).getParent();
					}

					if (!this.level().isClientSide && !itemstack1.isEmpty() && entitys instanceof LivingEntity) {
						if (JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && this.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_MELEE_CAN_BREAK)) {
							ItemStack hand = this.getMainHandItem();
							hand.hurtAndBreak(1, this, player -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
						}
					}

					if (entity instanceof LivingEntity) {
						float f5 = f4 - ((LivingEntity)entity).getHealth();
						if (j > 0) {
							entity.setSecondsOnFire(j * 4);
						}

						if (this.level() instanceof ServerLevel && f5 > 2.0F) {
							int k = (int)((double)f5 * 0.5);
							((ServerLevel)this.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, entity.getX(), entity.getY(0.5), entity.getZ(), k, 0.1, 0.0, 0.1, 0.2);
						}
					}

				} else {
					this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
					if (flag4) {
						entity.clearFire();
					}
				}
			}
		}
	}

	public double getEntityReach() {
		double range = this.self().getAttributeValue((Attribute)ForgeMod.ENTITY_REACH.get());
		return range == 0.0 ? 0.0 : range + (double)(this.abilities.instabuild ? 3 : 0);
	}
	@Override
	public boolean hurt(DamageSource damageSource, float f) {
		if (this.abilities.invulnerable && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		}
		this.noActionTime = 0;
		if (this.isDeadOrDying()) {
			return false;
		} else {
			if (isDamageSourceBlocked(damageSource)) {
				this.setMustShieldTick(30);
			}
			if (damageSource.scalesWithDifficulty()) {
				if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
					f = 0.0F;
				}
				if (this.level().getDifficulty() == Difficulty.EASY) {
					f = Math.min(f / 2.0F + 1.0F, f);
				}
				if (this.level().getDifficulty() == Difficulty.HARD) {
					f = f * 3.0F / 2.0F;
				}
			}
			if (f == 0.0F)
				return false;
			return super.hurt(damageSource, f);
		}
	}

	public boolean isInvulnerableTo(DamageSource p_36249_) {
		if (super.isInvulnerableTo(p_36249_)) {
			return true;
		} else if (p_36249_.is(DamageTypeTags.IS_DROWNING)) {
			return !this.level().getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
		} else if (p_36249_.is(DamageTypeTags.IS_FALL)) {
			return !this.level().getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
		} else if (p_36249_.is(DamageTypeTags.IS_FIRE)) {
			return !this.level().getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
		} else if (p_36249_.is(DamageTypeTags.IS_FREEZING)) {
			return !this.level().getGameRules().getBoolean(GameRules.RULE_FREEZE_DAMAGE);
		} else {
			return false;
		}
	}


	public void handleEntityEvent(byte by) {
		switch (by) {
			case 29:
				this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + this.level().random.nextFloat() * 0.4F);
				break;
			case 30:
				this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
				break;
			default:
				super.handleEntityEvent(by);
		}
	}

	@Override
	public void disableShield() {
		if (this.shieldCoolDown < 100) {
			this.shieldCoolDown = 100;
		}
		this.shieldCanUse = 0;
		if (this.getUseItem().getItem() instanceof ShieldItem) {
			this.stopUsingItem();
		}
	}
	@Override
	public void disableShieldTry() {
	}
	@Override
	public void disableShieldBreak(int tick) {
		if (tick == 0) {
			return;
		}
		if (this.shieldCoolDown < tick) {
			this.shieldCoolDown = tick;
		}
		this.shieldCanUse = 0;
		if (this.getUseItem().getItem() instanceof ShieldItem) {
			this.stopUsingItem();
		}
	}


	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
		if (mobSpawnType != MobSpawnType.CONVERSION) {
			if (newSpawn == 0) {
				this.setChangeInventoryCooldownTick(20);
				this.setBowLevel(20);
				this.setShieldLevel(3);
			}
		}
		super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
		return spawnGroupData;
	}
}
