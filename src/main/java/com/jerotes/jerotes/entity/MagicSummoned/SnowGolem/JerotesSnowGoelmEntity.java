package com.jerotes.jerotes.entity.MagicSummoned.SnowGolem;

import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.entity.MagicSummoned.MagicSummonedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IForgeShearable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class JerotesSnowGoelmEntity extends MagicSummonedEntity implements JerotesEntity, RangedAttackMob, IForgeShearable, Shearable {
	private static final EntityDataAccessor<Byte> DATA_PUMPKIN_ID = SynchedEntityData.defineId(JerotesSnowGoelmEntity.class, EntityDataSerializers.BYTE);

	public JerotesSnowGoelmEntity(EntityType<? extends JerotesSnowGoelmEntity> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2F);
		builder = builder.add(Attributes.MAX_HEALTH, 4.0D);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		return builder;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
		this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D, 1.0000001E-5F));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
	}

	public void performRangedAttack(LivingEntity p_29912_, float p_29913_) {
		Snowball snowball = new Snowball(this.level(), this);
		double d0 = p_29912_.getEyeY() - (double)1.1F;
		double d1 = p_29912_.getX() - this.getX();
		double d2 = d0 - snowball.getY();
		double d3 = p_29912_.getZ() - this.getZ();
		double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double)0.2F;
		snowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
		this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.level().addFreshEntity(snowball);
	}

	@Nullable
	protected SoundEvent getAmbientSound() {
		return SoundEvents.SNOW_GOLEM_AMBIENT;
	}
	@Nullable
	protected SoundEvent getHurtSound(DamageSource p_29929_) {
		return SoundEvents.SNOW_GOLEM_HURT;
	}
	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.SNOW_GOLEM_DEATH;
	}
	protected float getStandingEyeHeight(Pose p_29917_, EntityDimensions p_29918_) {
		return 1.7F;
	}
	public boolean isSensitiveToWater() {
		return true;
	}
	public Vec3 getLeashOffset() {
		return new Vec3(0.0D, (double)(0.75F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
	}

	public boolean hasPumpkin() {
		return (this.entityData.get(DATA_PUMPKIN_ID) & 16) != 0;
	}
	public void setPumpkin(boolean p_29937_) {
		byte b0 = this.entityData.get(DATA_PUMPKIN_ID);
		if (p_29937_) {
			this.entityData.set(DATA_PUMPKIN_ID, (byte)(b0 | 16));
		} else {
			this.entityData.set(DATA_PUMPKIN_ID, (byte)(b0 & -17));
		}
	}
	public void addAdditionalSaveData(CompoundTag p_29923_) {
		super.addAdditionalSaveData(p_29923_);
		p_29923_.putBoolean("Pumpkin", this.hasPumpkin());
	}
	public void readAdditionalSaveData(CompoundTag p_29915_) {
		super.readAdditionalSaveData(p_29915_);
		if (p_29915_.contains("Pumpkin")) {
			this.setPumpkin(p_29915_.getBoolean("Pumpkin"));
		}
	}
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_PUMPKIN_ID, (byte)16);
	}

	protected InteractionResult mobInteract(Player p_29920_, InteractionHand p_29921_) {
		ItemStack itemStack = p_29920_.getItemInHand(p_29921_);
		Item item = itemStack.getItem();
		if (readyForShearing() && (item instanceof ShearsItem ||
				itemStack.is(TagKey.create(Registries.ITEM, new ResourceLocation("forge:shears"))) ||
				itemStack.is(TagKey.create(Registries.ITEM, new ResourceLocation("forge:tools/shears"))))) {
			return InteractionResult.PASS;
		}
		if ((itemStack.getItem() == Items.CARVED_PUMPKIN || itemStack.getItem() == Items.JACK_O_LANTERN) && this.readyForShearing()) {
			this.level().playSound(null, this, SoundEvents.PUMPKIN_CARVE, p_29920_ == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
			if (!this.level().isClientSide()) {
				this.setPumpkin(true);
			}
			return InteractionResult.sidedSuccess(this.level().isClientSide);
		} else {
			return super.mobInteract(p_29920_, p_29921_);
		}
	}
	@Deprecated
	public void shear(SoundSource p_29907_) {
		this.level().playSound((Player)null, this, SoundEvents.SNOW_GOLEM_SHEAR, p_29907_, 1.0F, 1.0F);
		if (!this.level().isClientSide()) {
			this.setPumpkin(false);
			this.spawnAtLocation(new ItemStack(Items.CARVED_PUMPKIN), 1.7F);
		}
	}
	@NotNull
	@Override
	public java.util.List<ItemStack> onSheared(@Nullable Player player, @org.jetbrains.annotations.NotNull ItemStack item, Level world, BlockPos pos, int fortune) {
		world.playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
		this.gameEvent(GameEvent.SHEAR, player);
		if (!world.isClientSide()) {
			setPumpkin(false);
			return java.util.Collections.singletonList(new ItemStack(Items.CARVED_PUMPKIN));
		}
		return java.util.Collections.emptyList();
	}
	@Override
	public boolean isShearable(@NotNull ItemStack item, Level world, BlockPos pos) {
		return readyForShearing();
	}
	@Override
	public boolean readyForShearing() {
		return this.isAlive() && this.hasPumpkin();
	}

	public void aiStep() {
		super.aiStep();
		if (!this.level().isClientSide) {
			if (this.level().getBiome(this.blockPosition()).is(BiomeTags.SNOW_GOLEM_MELTS)) {
				this.hurt(this.damageSources().onFire(), 1.0F);
			}
			if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
				return;
			}
			BlockState blockstate = Blocks.SNOW.defaultBlockState();
			for(int i = 0; i < 4; ++i) {
				int j = Mth.floor(this.getX() + (double)((float)(i % 2 * 2 - 1) * 0.25F));
				int k = Mth.floor(this.getY());
				int l = Mth.floor(this.getZ() + (double)((float)(i / 2 % 2 * 2 - 1) * 0.25F));
				BlockPos blockpos = new BlockPos(j, k, l);
				if (this.level().isEmptyBlock(blockpos) && blockstate.canSurvive(this.level(), blockpos)) {
					this.level().setBlockAndUpdate(blockpos, blockstate);
					this.level().gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(this, blockstate));
				}
			}
		}
	}
}

