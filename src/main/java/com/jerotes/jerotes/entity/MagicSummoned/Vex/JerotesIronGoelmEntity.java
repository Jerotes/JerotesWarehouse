package com.jerotes.jerotes.entity.MagicSummoned.Vex;

import com.google.common.collect.ImmutableList;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.MagicSummoned.MagicSummonedEntity;
import com.jerotes.jerotes.goal.JerotesIronGolemOfferFlowerGoal;
import com.jerotes.jerotes.goal.JerotesMeleeAttackGoal;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.util.AttackFind;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
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
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class JerotesIronGoelmEntity extends MagicSummonedEntity implements JerotesEntity {
	protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(JerotesIronGoelmEntity.class, EntityDataSerializers.BYTE);

	public JerotesIronGoelmEntity(EntityType<? extends JerotesIronGoelmEntity> entityType, Level level) {
		super(entityType, level);
		this.setMaxUpStep(1.0F);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25D);
		builder = builder.add(Attributes.MAX_HEALTH, 100.0D);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 15.0D);
		builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		return builder;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new JerotesMeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
		this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6D, false));
		this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6D));
		this.goalSelector.addGoal(5, new JerotesIronGolemOfferFlowerGoal(this));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
	}


	protected SoundEvent getHurtSound(DamageSource p_28872_) {
		return SoundEvents.IRON_GOLEM_HURT;
	}
	protected SoundEvent getDeathSound() {
		return SoundEvents.IRON_GOLEM_DEATH;
	}
	protected void playStepSound(BlockPos p_28864_, BlockState p_28865_) {
		this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
	}
	protected int decreaseAirSupply(int p_28882_) {
		return p_28882_;
	}
	public boolean canSpawnSprintParticle() {
		return this.getDeltaMovement().horizontalDistanceSqr() > (double)2.5000003E-7F && this.random.nextInt(5) == 0;
	}
	private float getAttackDamage() {
		return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
	}
	public JerotesIronGoelmEntity.Crackiness getCrackiness() {
		return JerotesIronGoelmEntity.Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
	}
	public void offerFlower(boolean p_28886_) {
		if (p_28886_) {
			this.offerFlowerTick = 400;
			this.level().broadcastEntityEvent(this, (byte)11);
		} else {
			this.offerFlowerTick = 0;
			this.level().broadcastEntityEvent(this, (byte)34);
		}
	}
	public Vec3 getLeashOffset() {
		return new Vec3(0.0D, (double)(0.875F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
	}

	private int attackAnimationTick = 0;
	private int offerFlowerTick = 0;
	public int getAttackAnimationTick() {
		return this.attackAnimationTick;
	}
	public int getOfferFlowerTick() {
		return this.offerFlowerTick;
	}
	public void addAdditionalSaveData(CompoundTag p_28867_) {
		super.addAdditionalSaveData(p_28867_);
		this.addPersistentAngerSaveData(p_28867_);
	}
	public void readAdditionalSaveData(CompoundTag p_28857_) {
		super.readAdditionalSaveData(p_28857_);
		this.readPersistentAngerSaveData(this.level(), p_28857_);
	}
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_FLAGS_ID, (byte)0);
	}



	protected InteractionResult mobInteract(Player p_28861_, InteractionHand p_28862_) {
		ItemStack itemstack = p_28861_.getItemInHand(p_28862_);
		if (!itemstack.is(Items.IRON_INGOT)) {
			return super.mobInteract(p_28861_, p_28862_);
		} else {
			float f = this.getHealth();
			this.heal(25.0F);
			if (this.getHealth() == f) {
				return super.mobInteract(p_28861_, p_28862_);
			} else {
				float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
				this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
				if (!p_28861_.getAbilities().instabuild) {
					itemstack.shrink(1);
				}
				return InteractionResult.sidedSuccess(this.level().isClientSide);
			}
		}
	}

	public void aiStep() {
		super.aiStep();
		if (this.attackAnimationTick > 0) {
			--this.attackAnimationTick;
		}
		if (this.offerFlowerTick > 0) {
			--this.offerFlowerTick;
		}
	}


	public boolean doHurtTarget(Entity entity) {
		this.attackAnimationTick = 10;
		this.level().broadcastEntityEvent(this, (byte)4);
		AttackFind.attackBegin(this, entity);
		float f = this.getAttackDamage();
		float f1 = (int)f > 0 ? f / 2.0F + (float)this.random.nextInt((int)f) : f;
		boolean bl = AttackFind.attackAfter(this, entity, 1f, 1f, true, f1);
		if (bl) {
			double d2;
			if (entity instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity)entity;
				d2 = livingentity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
			} else {
				d2 = 0.0D;
			}

			double d0 = d2;
			double d1 = Math.max(0.0D, 1.0D - d0);
			entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, (double)0.4F * d1, 0.0D));
		}

		this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		return bl;
	}

	public boolean hurt(DamageSource p_28848_, float p_28849_) {
		JerotesIronGoelmEntity.Crackiness irongolem$crackiness = this.getCrackiness();
		boolean flag = super.hurt(p_28848_, p_28849_);
		if (flag && this.getCrackiness() != irongolem$crackiness) {
			this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F);
		}
		return flag;
	}

	public void handleEntityEvent(byte p_28844_) {
		if (p_28844_ == 4) {
			this.attackAnimationTick = 10;
			this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
		} else if (p_28844_ == 11) {
			this.offerFlowerTick = 400;
		} else if (p_28844_ == 34) {
			this.offerFlowerTick = 0;
		} else {
			super.handleEntityEvent(p_28844_);
		}
	}

	public static enum Crackiness {
		NONE(1.0F),
		LOW(0.75F),
		MEDIUM(0.5F),
		HIGH(0.25F);
		private static final List<JerotesIronGoelmEntity.Crackiness> BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble((p_28904_) -> {
			return (double)p_28904_.fraction;
		})).collect(ImmutableList.toImmutableList());
		private final float fraction;
		private Crackiness(float p_28900_) {
			this.fraction = p_28900_;
		}
		public static JerotesIronGoelmEntity.Crackiness byFraction(float p_28902_) {
			for(JerotesIronGoelmEntity.Crackiness irongolem$crackiness : BY_DAMAGE) {
				if (p_28902_ < irongolem$crackiness.fraction) {
					return irongolem$crackiness;
				}
			}
			return NONE;
		}
	}
}

