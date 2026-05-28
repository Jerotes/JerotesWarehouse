package com.jerotes.jerotes.entity.Shoot.Arrow;

import com.google.common.collect.Lists;
import com.jerotes.jerotes.entity.Interface.UseShieldEntity;
import com.jerotes.jerotes.util.AttackFind;
import com.jerotes.jerotes.util.EntityFactionFind;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class BaseJavelinEntity extends BaseAbstractArrowEntity {
	public static final EntityDataAccessor<Byte> ID_LOYALTY = SynchedEntityData.defineId(BaseJavelinEntity.class, EntityDataSerializers.BYTE);
	public static final EntityDataAccessor<Boolean> ID_FOIL = SynchedEntityData.defineId(BaseJavelinEntity.class, EntityDataSerializers.BOOLEAN);
	private ItemStack tridentItem = new ItemStack(Items.TRIDENT);
	public boolean dealtDamage;
	public int time;
	public int clientSideReturnTridentTickCount;
	public float damage;
	public SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
	public float soundLevel = 1.0f;
	@Nullable
	private IntOpenHashSet piercingIgnoreEntityIds;
	@Nullable
	public List<Entity> piercedAndKilledEntities;
	public BaseJavelinEntity(EntityType<? extends AbstractArrow> entityType, Level level, ItemStack itemStack, float damages) {
		super(entityType, level, itemStack);
		damage = damages;
	}
	public BaseJavelinEntity(Level level, LivingEntity livingEntity, ItemStack itemStack, EntityType<? extends AbstractArrow> entityType, float damages) {
		super(entityType, livingEntity, level, itemStack);
		this.tridentItem = itemStack.copy();
		if (this.canLoyalty()) {
			this.entityData.set(ID_LOYALTY, (byte) EnchantmentHelper.getLoyalty(itemStack));
		}
		this.entityData.set(ID_FOIL, itemStack.hasFoil());
		damage = damages;
	}

	public float specialDamage(Entity entity) {
		return 0f;
	}
	public boolean canLoyalty() {
		return true;
	}
	public boolean mustLoyalty() {
		return false;
	}
	public boolean mustLoyaltyOnlyToPlayer() {
		return false;
	}
	public boolean customDealtDamage() {
		return false;
	}
	public boolean breakShield() {
		return false;
	}
	public boolean breakShieldOnlyNotLoyalty() {
		return true;
	}
	public boolean canHurtEnderman() {
		return false;
	}
	public void hitUse(Entity entity) {
		if (this.level() instanceof ServerLevel && this.level().isThundering() && this.isChanneling()) {
			LightningBolt lightningBolt;
			BlockPos blockPos = entity.blockPosition();
			if (this.level().canSeeSky(blockPos) && (lightningBolt = EntityType.LIGHTNING_BOLT.create(this.level())) != null) {
				lightningBolt.moveTo(Vec3.atBottomCenterOf(blockPos));
				lightningBolt.setCause(this.getOwner() instanceof ServerPlayer serverPlayer ? serverPlayer : null);
				this.level().addFreshEntity(lightningBolt);
				soundEvent = SoundEvents.TRIDENT_THUNDER;
				soundLevel = 5.0f;
			}
		}
	}
	public void rebound() {
		if (this.dealtDamage)
			this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
	}
	public void reboundBlock() {
	}
	public DamageSource getDamageSource(Entity entity) {
		return this.damageSources().trident(this, this.getOwner() == null ? this : this.getOwner());
	}
	public int basePierce() {
		return 0;
	}

	public void setPierceLevel(byte by) {
		super.setPierceLevel(by);
	}
	public byte getPierceLevelBase() {
		return super.getPierceLevel();
	}
	public byte getPierceLevel() {
		return (byte) (this.getPierceLevelBase() + this.basePierce());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		if (canLoyalty()) {
			this.getEntityData().define(ID_LOYALTY, (byte) 0);
		}
		this.getEntityData().define(ID_FOIL, false);
	}

	@Override
	public void tick() {
		if (!this.customDealtDamage()) {
			if (this.inGroundTime > 4) {
				this.dealtDamage = true;
			}
		}
		if (this.canLoyalty()) {
			Entity entity = this.getOwner();
			byte by = this.entityData.get(ID_LOYALTY);
			if (by > 0) {
				if ((this.dealtDamage || this.isNoPhysics()) && entity != null) {
					if (!this.isAcceptibleReturnOwner()) {
						if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
							this.spawnAtLocation(this.getPickupItem(), 0.1f);
						}
						this.discard();
					}
					else {
						this.setNoPhysics(true);
						Vec3 vec3 = entity.getEyePosition().subtract(this.position());
						this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * (double) by, this.getZ());
						if (this.level().isClientSide) {
							this.yOld = this.getY();
						}
						double d = 0.05 * (double) by;
						this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d)));
						if (this.clientSideReturnTridentTickCount == 0) {
							this.playSound(SoundEvents.TRIDENT_RETURN, 10.0f, 1.0f);
						}
						++this.clientSideReturnTridentTickCount;
					}
				}
			}
			else if (mustLoyalty()) {
				if ((this.dealtDamage || this.isNoPhysics()) && entity != null) {
					if (!this.isAcceptibleReturnOwner() || !(this.getOwner() instanceof Player) && this.mustLoyaltyOnlyToPlayer()) {
						if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
							this.spawnAtLocation(this.getPickupItem(), 0.1f);
						}
						this.discard();
					}
					else {
						this.setNoPhysics(true);
						Vec3 vec3 = entity.getEyePosition().subtract(this.position());
						this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.01, this.getZ());
						if (this.level().isClientSide) {
							this.yOld = this.getY();
						}
						double d = 0.3;
						this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d)));
						if (this.clientSideReturnTridentTickCount == 0) {
							if (by > 0) {
								this.playSound(SoundEvents.TRIDENT_RETURN, 10.0f, 1.0f);
							}
						}
						++this.clientSideReturnTridentTickCount;
					}
				}
			}
		}
		super.tick();
	}
	protected boolean canHitEntity(Entity p_36743_) {
		if (this.getOwner() != null && p_36743_ instanceof LivingEntity livingEntity && AttackFind.SameFactionAvoidDamage(this.getOwner(), livingEntity)) {
			return false;
		}
		if (this.piercingIgnoreEntityIds != null && this.piercingIgnoreEntityIds.size() >= (int) this.getPierceLevel()) {
			return false;
		}
		return super.canHitEntity(p_36743_) &&
				(this.piercingIgnoreEntityIds == null ||
						!this.piercingIgnoreEntityIds.contains(p_36743_.getId()));
	}

	private void resetPiercedEntities() {
		if (this.piercedAndKilledEntities != null) {
			this.piercedAndKilledEntities.clear();
		}
		if (this.piercingIgnoreEntityIds != null) {
			this.piercingIgnoreEntityIds.clear();
		}
	}


	private boolean isAcceptibleReturnOwner() {
		Entity entity = this.getOwner();
		if (entity == null || !entity.isAlive()) {
			return false;
		}
		return !(entity instanceof ServerPlayer) || !entity.isSpectator();
	}
	@Override
	protected ItemStack getPickupItem() {
		return this.tridentItem.copy();
	}
	public boolean isFoil() {
		return this.entityData.get(ID_FOIL);
	}
	@Override
	protected EntityHitResult findHitEntity(Vec3 vec3, Vec3 vec32) {
		if (this.dealtDamage) {
			return null;
		}
		return super.findHitEntity(vec3, vec32);
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		if (this.piercingIgnoreEntityIds != null && this.piercingIgnoreEntityIds.size() >= (int) this.getPierceLevel()) {
			return;
		}
		Entity entity = this.getOwner();
		Entity entity2 = entityHitResult.getEntity();
		float f = damage;
		float f1 = 0;
		float special = this.specialDamage(entity2);
		if (entity2 instanceof LivingEntity livingEntity) {
			f += EnchantmentHelper.getDamageBonus(this.tridentItem, livingEntity.getMobType());
		}
		f1 += (float) EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, this.tridentItem);
		f1 += (float) EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, this.tridentItem);
		DamageSource damageSource = this.getDamageSource(entity2);
		int pierce = this.getPierceLevel();
		if (pierce > 0) {
			if (this.piercingIgnoreEntityIds == null) {
				this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
			}
			if (this.piercedAndKilledEntities == null) {
				this.piercedAndKilledEntities = Lists.newArrayListWithCapacity((int)5);
			}
			int count = pierce + 1;
			if (this.piercingIgnoreEntityIds != null && this.piercingIgnoreEntityIds.size() < count) {
				this.piercingIgnoreEntityIds.add(entity2.getId());
			}
			else {
				this.dealtDamage = true;
			}
		}
		else {
			this.dealtDamage = true;
		}
		soundEvent = this.getDefaultHitSoundEvent();
		boolean deflectTridents = entity2.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("deflects_tridents")));
		if (entity2.hurt(damageSource, f + special)) {
			if (entity2 instanceof LivingEntity livingEntity) {
				if (f1 > 0) {
					double d0 = Math.max(0.0D, 1.0D - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
					Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)f1 * 0.6D * d0);
					if (vec3.lengthSqr() > 0.0D) {
						livingEntity.push(vec3.x, 0.1D, vec3.z);
					}
				}
				if (this.breakShield() && (this.entityData.get(ID_LOYALTY) <= 0 || !this.breakShieldOnlyNotLoyalty())) {
					if (livingEntity.getUseItem().getItem() instanceof ShieldItem) {
						if (livingEntity instanceof Player player) {
							player.disableShield(true);
						} else if (livingEntity instanceof UseShieldEntity useShieldEntity) {
							useShieldEntity.disableShield();
						}
					}
				}
				if (!entity2.isAlive() && this.piercedAndKilledEntities != null) {
					this.piercedAndKilledEntities.add(livingEntity);
				}
			}

			if (entity2 instanceof LivingEntity livingEntity && EntityFactionFind.isEnderman(livingEntity) && !this.canHurtEnderman()) {
				return;
			}

			if (entity2 instanceof LivingEntity livingEntity) {
				if (entity instanceof LivingEntity living) {
					EnchantmentHelper.doPostHurtEffects(livingEntity, entity);
					EnchantmentHelper.doPostDamageEffects(living, livingEntity);
				}
				this.doPostHurtEffects(livingEntity);
			}
		}
		else if (deflectTridents) {
			if (!this.canHurtEnderman()) {
				this.deflect();
				return;
			}
		}
		this.rebound();
		soundLevel = 1.0f;
		this.hitUse(entity2);
		this.playSound(soundEvent, soundLevel, 1.0f);
	}

	public void deflect() {
		float f = this.random.nextFloat() * 360.0F;
		this.setDeltaMovement(this.getDeltaMovement().yRot(f * ((float)Math.PI / 180F)).scale(0.5D));
		this.setYRot(this.getYRot() + f);
		this.yRotO += f;
	}

	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		this.reboundBlock();
		this.resetPiercedEntities();
	}

	@Override
	protected void onHit(HitResult hitResult) {
		super.onHit(hitResult);
	}

	public boolean isChanneling() {
		return EnchantmentHelper.hasChanneling(this.tridentItem);
	}

	@Override
	protected boolean tryPickup(Player player) {
		return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.TRIDENT_HIT_GROUND;
	}
	protected SoundEvent getDefaultHitSoundEvent() {
		return SoundEvents.TRIDENT_HIT;
	}

	@Override
	public void playerTouch(Player player) {
		if (this.ownedBy(player) || this.getOwner() == null) {
			super.playerTouch(player);
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		if (compoundTag.contains("Trident", 10)) {
			this.tridentItem = ItemStack.of(compoundTag.getCompound("Trident"));
		}
		this.dealtDamage = compoundTag.getBoolean("DealtDamage");
		if (this.canLoyalty()) {
			this.entityData.set(ID_LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentItem));
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.put("Trident", this.tridentItem.save(new CompoundTag()));
		compoundTag.putBoolean("DealtDamage", this.dealtDamage);
	}

	@Override
	public void tickDespawn() {
		if (this.canLoyalty()) {
			byte by = this.entityData.get(ID_LOYALTY);
			if (this.mustLoyalty()) {
				if (this.pickup != Pickup.ALLOWED) {
					super.tickDespawn();
				}
			}
			else {
				if (this.pickup != Pickup.ALLOWED || by <= 0) {
					super.tickDespawn();
				}
			}
		}
		else {
			super.tickDespawn();
		}
	}

	@Override
	protected float getWaterInertia() {
		return 0.6f;
	}

	@Override
	public boolean shouldRender(double d, double d2, double d3) {
		return true;
	}

	public boolean getDealtDamage() {
		return this.dealtDamage;
	}
}
