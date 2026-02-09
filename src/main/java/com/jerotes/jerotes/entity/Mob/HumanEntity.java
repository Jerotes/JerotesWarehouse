package com.jerotes.jerotes.entity.Mob;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.*;
import com.jerotes.jerotes.goal.*;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesGameRules;
import com.jerotes.jerotes.init.JerotesSoundEvents;
import com.jerotes.jerotes.item.Tool.ItemToolBaseCrossbow;
import com.jerotes.jerotes.spell.MagicType;
import com.jerotes.jerotes.spell.SpellListByString;
import com.jerotes.jerotes.spell.SpellRegistry;
import com.jerotes.jerotes.spell.SpellTypeInterface;
import com.jerotes.jerotes.util.EntityFactionFind;
import com.jerotes.jerotes.util.Main;
import com.jerotes.jerotes.util.PlayerName;
import com.jerotes.jerotes.util.PlayerSkin;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.*;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.EntityArmorInvWrapper;
import net.minecraftforge.items.wrapper.EntityHandsInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class HumanEntity extends PathfinderMob implements SpellUseEntity, UseDaggerEntity, PlayerSkinEntity, SkinEntity, ShiftKeyDownEntity, WizardEntity, UseThrowEntity, UseThrownJavelinEntity, InventoryEntity, CrossbowAttackMob, NeutralMob, RangedAttackMob, InventoryCarrier, Npc, UseCrossbowEntity, UseBowEntity, UseShieldEntity, JerotesEntity {
	private static final EntityDataAccessor<Integer> COMBAT_STYLE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> USE_SELF_NOT_SPELL_LIST = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SPELL_TICK = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> BOW_LEVEL = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SHIELD_LEVEL = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> NAME_FIRST = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> NAME_SECOND = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> IS_ARCHMAGE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<String> FACTION = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.STRING);
	//
	private static final EntityDataAccessor<Boolean> IS_FEMALE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SKIN_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SKIN_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> EYE_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> EYE_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> HAIR_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> HAIR_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ADD_TYPE_1 = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ADD_TYPE_2 = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ADD_TYPE_3 = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> JACKET_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> JACKET_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> PANTS_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> PANTS_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> GLOVES_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> GLOVES_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> BAUBLE_TYPE_1 = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> BAUBLE_TYPE_2 = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> BAUBLE_TYPE_3 = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SHOES_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> SHOES_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> HAT_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> HAT_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> OVERCOAT_TYPE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> OVERCOAT_COLOR = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	//
	private static final EntityDataAccessor<Boolean> CAN_CHANGE_INVENTORY = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> CAN_CHANGE_MELEE_OR_RANGE = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> NO_COMBAT_EMPTY_WEAPON = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> NO_COMBAT_EMPTY_SHIELD = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> CHANGE_INVENTORY_COOLDOWN_TICK = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<String> SKIN_NAME = SynchedEntityData.defineId(HumanEntity.class, EntityDataSerializers.STRING);
	public final SimpleContainer inventory = new SimpleContainer(inventoryCount());
	private LazyOptional<?> itemHandler = null;
	public AnimationState armWideScaleAnimationState = new AnimationState();
	public AnimationState armSlimScaleAnimationState = new AnimationState();

	public HumanEntity(EntityType<? extends PathfinderMob> type, Level world) {
		super(type, world);
		setMaxUpStep(0.6f);
		setPersistenceRequired();
		this.applyOpenDoorsAbility();
		this.setCanPickUpLoot(true);
		this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, 0.0f);
		this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0f);
		this.itemHandler = LazyOptional.of(this::createCombinedHandler);
	}
	private IItemHandler createCombinedHandler() {
		IItemHandlerModifiable handsHandler = new EntityHandsInvWrapper(this);
		IItemHandlerModifiable armorHandler = new EntityArmorInvWrapper(this);
		IItemHandlerModifiable customHandler = new InvWrapper(this.inventory);
		return new CombinedInvWrapper(
				armorHandler,
				handsHandler,
				customHandler
		);
	}
	@Override
	public String getFactionTypeName() {
		return getFaction();
	}

	@Override
	public boolean isFactionWith(Entity entity) {
		if (getFaction().isEmpty()) {
			return entity instanceof LivingEntity livingEntity && livingEntity.getType() == JerotesEntityType.HUMAN.get() && this.getType() == JerotesEntityType.HUMAN.get();
		}
		return entity instanceof LivingEntity livingEntity && EntityFactionFind.getTrueFaction(livingEntity).equals(getFaction());
	}

	@VisibleForDebug
	@Override
	public SimpleContainer getInventory() {
		return this.inventory;
	}

	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		return capability == ForgeCapabilities.ITEM_HANDLER && itemHandler != null &&
				this.isAlive() ? itemHandler.cast() : super.getCapability(capability, facing);
	}
	public void invalidateCaps() {
		super.invalidateCaps();
		if (this.itemHandler != null) {
			LazyOptional<?> oldHandler = this.itemHandler;
			this.itemHandler = null;
			oldHandler.invalidate();
		}
	}

	protected ItemStack addToInventory(ItemStack itemStack) {
		return this.inventory.addItem(itemStack);
	}

	protected boolean canAddToInventory(ItemStack itemStack) {
		return this.inventory.canAddItem(itemStack);
	}

	@Override
	public ItemStack equipItemIfPossible(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
		ItemStack itemStack2 = this.getItemBySlot(equipmentSlot);
		boolean bl = this.canReplaceCurrentItem(itemStack, itemStack2);
		if (equipmentSlot.isArmor() && !bl) {
			equipmentSlot = EquipmentSlot.MAINHAND;
			itemStack2 = this.getItemBySlot(equipmentSlot);
			bl = itemStack2.isEmpty();
		}
		if (bl && this.canHoldItem(itemStack)) {
			double d = this.getEquipmentDropChance(equipmentSlot);
			if (this.canAddToInventory(itemStack2)) {
				this.addToInventory(itemStack2);
			}
			else {
				this.spawnAtLocation(itemStack2);
			}

			if (equipmentSlot.isArmor() && itemStack.getCount() > 1) {
				ItemStack itemStack3 = itemStack.copyWithCount(1);
				this.setItemSlotAndDropWhenKilled(equipmentSlot, itemStack3);
				return itemStack3;
			}
			this.setItemSlotAndDropWhenKilled(equipmentSlot, itemStack);
			return itemStack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected boolean canReplaceCurrentItem(ItemStack newItem, ItemStack oldItem) {
		return InventoryEntity.canReplaceCurrentItem(this, newItem, oldItem);
	}

	@Override
	protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
	}

	public static AttributeSupplier.Builder createAttributes() {
		AttributeSupplier.Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 20);
		builder = builder.add(Attributes.ARMOR, 0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 2);
		builder = builder.add(Attributes.FOLLOW_RANGE, 48);
		return builder;
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource damageSource, int n, boolean bl) {
		super.dropCustomDeathLoot(damageSource, n, bl);
		this.inventory.removeAllItems().forEach(this::spawnAtLocation);
	}

	private void applyOpenDoorsAbility() {
		if (GoalUtils.hasGroundPathNavigation(this)) {
			((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
		}
	}

	@Override
	protected void registerGoals() {
		this.getNavigation().getNodeEvaluator().setCanOpenDoors(true);
		this.goalSelector.addGoal(0, new OpenDoorGoal(this, true));
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new JerotesShiftKeyDownGoal(this));
		this.goalSelector.addGoal(1, new JerotesMainSpellAttackGoal(this, this.getSpellLevel(), 60, 240, 0.5f));
		this.goalSelector.addGoal(1, new JerotesAddSpellAttackGoal(this, this.getSpellLevel(), 180, 240, 0.5f));
		this.goalSelector.addGoal(1, new JerotesCombatIMagicAttackGoal<>(this, 0.5, 15.0f, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesCombatIIMagicAttackGoal<>(this, 0.2, true, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesCombatIIIMagicAttackGoal<>(this, 0.5, 15.0f, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesCombatIVMagicAttackGoal<>(this, 0.5, 15.0f, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesRangedBowAttackGoal<HumanEntity>(this, 0.5, 20, 15.0f));
		this.goalSelector.addGoal(1, new JerotesRangedCrossbowAttackGoal<HumanEntity>(this, 1.0, 15.0f));
		this.goalSelector.addGoal(1, new JerotesRangedJavelinAttackGoal<>(this, 1.0, 60, 12.0F, this.meleeOrRangeDistance()));
		this.goalSelector.addGoal(1, new JerotesSpearUseGoal<>(this, 1.0, 1.0, 10.0f, 2.0f));
		this.goalSelector.addGoal(1, new JerotesPikeUseGoal(this, 1.2, true));
		this.goalSelector.addGoal(1, new JerotesRangedThrowAttackGoal<>(this, 0.4, 40, 12.0F));
		this.goalSelector.addGoal(2, new JerotesMeleeAttackGoal(HumanEntity.this, 1.2, true));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0f));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, HumanEntity.class, 6.0f));
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new JerotesHelpSameFactionGoal(this, LivingEntity.class, false, false, livingEntity -> livingEntity instanceof LivingEntity));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 5, false, false, livingEntity -> EntityFactionFind.isHateFaction(this, livingEntity)));
		this.targetSelector.addGoal(1, new JerotesHelpAlliesGoal(this, LivingEntity.class, false, false, livingEntity -> livingEntity instanceof LivingEntity));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<Player>(this, Player.class, 10, true, false, this::isAngryAt));
		this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<HumanEntity>(this, true));
	}

	@Override
	public void performRangedAttack(LivingEntity livingEntity, float f) {
		ItemStack handItem = this.getMainHandItem();
		if ((this.getMainHandItem().isEmpty() || InventoryEntity.isMeleeWeapon(this.getMainHandItem())) && !this.getOffhandItem().isEmpty()) {
			handItem = this.getOffhandItem();
		}
		if (InventoryEntity.isRangeJavelin(handItem)) {
			useTridentShoot(this, livingEntity);
		}
		if (InventoryEntity.isBow(handItem)) {
			useBowShoot(this, livingEntity, f, getBowLevel(), 20);
		}
		if (InventoryEntity.isThrow(handItem)) {
			useThrowShoot(this, livingEntity);
			if (!this.isSilent()) {
				this.level().playSound(null, this.getX(), this.getY(), this.getZ(), JerotesSoundEvents.ITEM_THROW, this.getSoundSource(), 0.5f, 0.4f / (this.level().getRandom().nextFloat() * 0.4f + 0.8f));
			}
		}
		if (InventoryEntity.isCrossbow(handItem)) {
			useCrossbowShoot(this, 3.15F);
		}
		if (JerotesGameRules.JEROTES_RANGE_CAN_BREAK != null && this.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_RANGE_CAN_BREAK)) {
			if (handItem.getItem() instanceof BowItem || handItem.getItem() instanceof TridentItem) {
				handItem.hurtAndBreak(1, this, player -> player.broadcastBreakEvent(EquipmentSlot.MAINHAND));
			}
		}
	}


	@Override
	public ItemStack getProjectile(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ProjectileWeaponItem projectileWeaponItem) {
			Predicate<ItemStack> predicate = projectileWeaponItem.getSupportedHeldProjectiles();
			ItemStack itemStack2 = ProjectileWeaponItem.getHeldProjectile(this, predicate);
			if (predicate.test(this.getMainHandItem())) {
				itemStack2 = this.getMainHandItem();
			}
			else if (predicate.test(this.getOffhandItem())) {
				itemStack2 = this.getOffhandItem();
			}
			else {
				for (int n = 0; n < this.inventoryCount(); ++n) {
					ItemStack finds = this.mobInventory().getItem(n);
					if (predicate.test(finds)) {
						itemStack2 = finds;
						break;
					}
				}
			}
			return itemStack2.isEmpty() ? new ItemStack(Items.ARROW) : itemStack2;
		}
		return ItemStack.EMPTY;
	}

	public boolean isChargingCrossbow() {
		return this.entityData.get(IS_CHARGING_CROSSBOW);
	}

	@Override
	public void setChargingCrossbow(boolean bl) {
		this.entityData.set(IS_CHARGING_CROSSBOW, bl);
	}

	@Override
	public void shootCrossbowProjectile(LivingEntity livingEntity, ItemStack itemStack, Projectile projectile, float f) {
		this.shootCrossbowProjectile(this, livingEntity, projectile, f, itemStack.getItem() instanceof ItemToolBaseCrossbow itemToolBaseCrossbow ? itemToolBaseCrossbow.getShootingPower(itemStack) : 3.15f);
	}
	@Override
	public void shootCrossbowProjectile(LivingEntity livingEntity, LivingEntity livingEntity2, Projectile projectile, float f, float f2) {
		double d0 = livingEntity2.getX() - livingEntity.getX();
		double d1 = livingEntity2.getZ() - livingEntity.getZ();
		double d2 = Math.sqrt(d0 * d0 + d1 * d1);
		double d3 = livingEntity2.getY(0.3333333333333333) - projectile.getY() + d2 * 0.1;
		Vector3f vector3f = this.getProjectileShotVector(livingEntity, new Vec3(d0, d3, d1), f);
		projectile.shoot((double)vector3f.x(), (double)vector3f.y(), (double)vector3f.z(), f2, Math.max(0, 20 - getBowLevel()) / 5f);
		livingEntity.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F / (livingEntity.getRandom().nextFloat() * 0.4F + 0.8F));
	}

	@Override
	public void onCrossbowAttackPerformed() {
		this.noActionTime = 0;
	}

	@Override
	public AbstractArrow getCustomArrow(ItemStack itemStack, float f) {
		return ProjectileUtil.getMobArrow(this, itemStack, f);
	}

	@Override
	public boolean canFireProjectileWeapon(ProjectileWeaponItem projectileWeaponItem) {
		return projectileWeaponItem instanceof BowItem || projectileWeaponItem instanceof CrossbowItem || super.canFireProjectileWeapon(projectileWeaponItem);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		if (this.isDamageSourceBlocked(damageSource)) {
			return SoundEvents.SHIELD_BLOCK;
		}
		if (damageSource.getDirectEntity() instanceof LivingEntity livingEntity &&
				Main.canSee(livingEntity, this) &&
				(livingEntity.getMainHandItem().canDisableShield(this.useItem, this, livingEntity) || livingEntity.canDisableShield()) &&
				this.isBlockingOld()) {
			this.shieldBreakTick = 0;
			return SoundEvents.SHIELD_BLOCK;
		}
		return damageSource.type().effects().sound();
	}
	public boolean isBlockingOld() {
		return this.shieldBreakTick > 0;
	}

	public void stopUsingItem() {
		if (this.getUseItem().getItem() instanceof ShieldItem) {
			this.shieldBreakTick = 1;
		}
		super.stopUsingItem();
	}
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.PLAYER_DEATH;
	}
	@Override
	public boolean canBeLeashed(Player player) {
		return false;
	}
	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
		return this.isBaby() ? 0.86f : 1.62F;
	}
	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
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
		return aabb1.inflate(0.5d, 0.5d, 0.5d);
	}
	@Override
	public boolean isWithinMeleeAttackRange(LivingEntity livingEntity) {
		return this.getAttackBoundingBox().intersects(livingEntity.getBoundingBox());
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
	public void setCustomName(Component component) {
		super.setCustomName(component);
		if (component == null)
			return;
		String string = ChatFormatting.stripFormatting(component.getString());
		if (string != null && !string.isEmpty() && MainConfig.HumanCustomNameWide.contains(string)) {
			this.setFemale(false);
		}
		else if (string != null && !string.isEmpty() && MainConfig.HumanCustomNameSlim.contains(string)) {
			this.setFemale(true);
		}
		else if ("Steve".equals(string)) {
			this.setFemale(false);
		}
		else if ("Alex".equals(string)) {
			this.setFemale(true);
		}
		else if ("Ari".equals(string)) {
			this.setFemale(false);
		}
		else if ("Kai".equals(string)) {
			this.setFemale(false);
		}
		else if ("Noor".equals(string)) {
			this.setFemale(true);
		}
		else if ("Sunny".equals(string)) {
			this.setFemale(false);
		}
		else if ("Zuri".equals(string)) {
			this.setFemale(false);
		}
		else if ("Efe".equals(string)) {
			this.setFemale(true);
		}
		else if ("Makena".equals(string)) {
			this.setFemale(true);
		}
		this.reapplyPosition();
		this.refreshDimensions();
	}

	public int newSpawn;
	public int shieldCoolDown;
	public int shieldBreakTick = 0;
	public int shieldCanUse = 1;
	@Override
	public boolean shieldCanUse() {
		return this.shieldCanUse == 1 && this.shieldCoolDown <= 0;
	}
	@Override
	public boolean isBaby() {
		return this.getEntityData().get(DATA_BABY_ID);
	}
	@Override
	public void setBaby(boolean bl) {
		this.getEntityData().set(DATA_BABY_ID, bl);
	}
	public int getCombatStyle() {
		return this.getEntityData().get(COMBAT_STYLE);
	}
	public void setCombatStyle(int n) {
		this.getEntityData().set(COMBAT_STYLE, n);
	}
	public boolean isUseSelfNotStringSpellList() {
		return this.getEntityData().get(USE_SELF_NOT_SPELL_LIST);
	}
	public void setUseSelfNotStringSpellList(boolean bl) {
		this.getEntityData().set(USE_SELF_NOT_SPELL_LIST, bl);
	}
	public boolean isArchmage() {
		return this.getEntityData().get(IS_ARCHMAGE);
	}
	public void setArchmage(boolean bl) {
		this.getEntityData().set(IS_ARCHMAGE, bl);
	}
	public String getFaction() {
		return this.getEntityData().get(FACTION);
	}
	public void setFaction(String string) {
		this.getEntityData().set(FACTION, string);
	}
	//性别
	@Override
	public boolean IsFemale() {
		return this.getEntityData().get(IS_FEMALE) || this.getUsername() != null && !this.getUsername().getSkinName().isEmpty() && PlayerSkin.getPlayerSkinType(this.getProfile()) == PlayerSkin.SkinType.SLIM;
	}
	public void setFemale(boolean bl) {
		this.getEntityData().set(IS_FEMALE, bl);
	}
	//皮肤
	@Override
	public int SkinType() {
		return this.getEntityData().get(SKIN_TYPE);
	}
	public void setSkinType(int n) {
		this.getEntityData().set(SKIN_TYPE, n);
	}
	@Override
	public int SkinColor() {
		return this.getEntityData().get(SKIN_COLOR);
	}
	public void setSkinColor(int n) {
		this.getEntityData().set(SKIN_COLOR, n);
	}
	@Override
	public int MaxSkinColor() {
		return 9;
	}
	//眼睛
	@Override
	public int EyeType() {
		return this.getEntityData().get(EYE_TYPE);
	}
	public void setEyeType(int n) {
		this.getEntityData().set(EYE_TYPE, n);
	}
	@Override
	public int EyeColor() {
		return this.getEntityData().get(EYE_COLOR);
	}
	public void setEyeColor(int n) {
		this.getEntityData().set(EYE_COLOR, n);
	}
	@Override
	public int MaxEyeColor() {
		return 9;
	}
	//头发
	@Override
	public int HairType() {
		return this.getEntityData().get(HAIR_TYPE);
	}
	public void setHairType(int n) {
		this.getEntityData().set(HAIR_TYPE, n);
	}
	@Override
	public int HairColor() {
		return this.getEntityData().get(HAIR_COLOR);
	}
	public void setHairColor(int n) {
		this.getEntityData().set(HAIR_COLOR, n);
	}
	@Override
	public int MaxHairColor() {
		return 18;
	}
	//附件-1
	@Override
	public int AddType_1() {
		return this.getEntityData().get(ADD_TYPE_1);
	}
	public void setAddType_1(int n) {
		this.getEntityData().set(ADD_TYPE_1, n);
	}
	//附件-2
	@Override
	public int AddType_2() {
		return this.getEntityData().get(ADD_TYPE_2);
	}
	public void setAddType_2(int n) {
		this.getEntityData().set(ADD_TYPE_2, n);
	}
	//附件-3
	@Override
	public int AddType_3() {
		return this.getEntityData().get(ADD_TYPE_3);
	}
	public void setAddType_3(int n) {
		this.getEntityData().set(ADD_TYPE_3, n);
	}
	//上衣
	@Override
	public int JacketType() {
		return this.getEntityData().get(JACKET_TYPE);
	}
	public void setJacketType(int n) {
		this.getEntityData().set(JACKET_TYPE, n);
	}
	@Override
	public int MaxJacketType() {
		return 9;
	}
	@Override
	public int JacketColor() {
		return this.getEntityData().get(JACKET_COLOR);
	}
	public void setJacketColor(int n) {
		this.getEntityData().set(JACKET_COLOR, n);
	}
	@Override
	public int MaxJacketColor() {
		return 18;
	}
	//裤子
	@Override
	public int PantsType() {
		return this.getEntityData().get(PANTS_TYPE);
	}
	public void setPantsType(int n) {
		this.getEntityData().set(PANTS_TYPE, n);
	}
	@Override
	public int MaxPantsType() {
		return 9;
	}
	@Override
	public int PantsColor() {
		return this.getEntityData().get(PANTS_COLOR);
	}
	public void setPantsColor(int n) {
		this.getEntityData().set(PANTS_COLOR, n);
	}
	@Override
	public int MaxPantsColor() {
		return 18;
	}
	//手套
	@Override
	public int GlovesType() {
		return this.getEntityData().get(GLOVES_TYPE);
	}
	public void setGlovesType(int n) {
		this.getEntityData().set(GLOVES_TYPE, n);
	}
	@Override
	public int GlovesColor() {
		return this.getEntityData().get(GLOVES_COLOR);
	}
	public void setGlovesColor(int n) {
		this.getEntityData().set(GLOVES_COLOR, n);
	}
	@Override
	public int MaxGlovesColor() {
		return 18;
	}
	//饰品-1
	@Override
	public int BaubleType_1() {
		return this.getEntityData().get(BAUBLE_TYPE_1);
	}
	public void setBaubleType_1(int n) {
		this.getEntityData().set(BAUBLE_TYPE_1, n);
	}
	//饰品-2
	@Override
	public int BaubleType_2() {
		return this.getEntityData().get(BAUBLE_TYPE_2);
	}
	public void setBaubleType_2(int n) {
		this.getEntityData().set(BAUBLE_TYPE_2, n);
	}
	//饰品-3
	@Override
	public int BaubleType_3() {
		return this.getEntityData().get(BAUBLE_TYPE_3);
	}
	public void setBaubleType_3(int n) {
		this.getEntityData().set(BAUBLE_TYPE_3, n);
	}
	//鞋子
	@Override
	public int ShoesType() {
		return this.getEntityData().get(SHOES_TYPE);
	}
	public void setShoesType(int n) {
		this.getEntityData().set(SHOES_TYPE, n);
	}
	@Override
	public int MaxShoesType() {
		return 9;
	}
	@Override
	public int ShoesColor() {
		return this.getEntityData().get(SHOES_COLOR);
	}
	public void setShoesColor(int n) {
		this.getEntityData().set(SHOES_COLOR, n);
	}
	@Override
	public int MaxShoesColor() {
		return 18;
	}
	//帽子
	@Override
	public int HatType() {
		return this.getEntityData().get(HAT_TYPE);
	}
	public void setHatType(int n) {
		this.getEntityData().set(HAT_TYPE, n);
	}
	@Override
	public int HatColor() {
		return this.getEntityData().get(HAT_COLOR);
	}
	public void setHatColor(int n) {
		this.getEntityData().set(HAT_COLOR, n);
	}
	@Override
	public int MaxHatColor() {
		return 18;
	}
	//外套
	@Override
	public int OvercoatType() {
		return this.getEntityData().get(OVERCOAT_TYPE);
	}
	public void setOvercoatType(int n) {
		this.getEntityData().set(OVERCOAT_TYPE, n);
	}
	@Override
	public int OvercoatColor() {
		return this.getEntityData().get(OVERCOAT_COLOR);
	}
	public void setOvercoatColor(int n) {
		this.getEntityData().set(OVERCOAT_COLOR, n);
	}
	@Override
	public int MaxOvercoatColor() {
		return 18;
	}
	@Override
	public SimpleContainer mobInventory() {
		return inventory;
	}
	@Override
	public int inventoryCount() {
		return 8;
	}
	@Override
	public boolean isCanChangeInventory() {
		return this.getEntityData().get(CAN_CHANGE_INVENTORY);
	}
	@Override
	public void setCanChangeInventory(boolean bl) {
		this.getEntityData().set(CAN_CHANGE_INVENTORY, bl);
	}
	@Override
	public boolean isCanChangeMeleeOrRange() {
		return this.getEntityData().get(CAN_CHANGE_MELEE_OR_RANGE);
	}
	@Override
	public void setCanChangeMeleeOrRange(boolean bl) {
		this.getEntityData().set(CAN_CHANGE_MELEE_OR_RANGE, bl);
	}
	@Override
	public boolean NonCombatEmptyWeapon() {
		return this.isNoCombatEmptyWeapon();
	}
	@Override
	public boolean NonCombatEmptyShield() {
		return this.isNoCombatEmptyShield();
	}
	@Override
	public int changeInventoryCooldownTick() {
		return this.getChangeInventoryCooldownTick();
	}
	public boolean isNoCombatEmptyWeapon() {
		return this.getEntityData().get(NO_COMBAT_EMPTY_WEAPON);
	}
	public void setNoCombatEmptyWeapon(boolean bl) {
		this.getEntityData().set(NO_COMBAT_EMPTY_WEAPON, bl);
	}
	public boolean isNoCombatEmptyShield() {
		return this.getEntityData().get(NO_COMBAT_EMPTY_SHIELD);
	}
	public void setNoCombatEmptyShield(boolean bl) {
		this.getEntityData().set(NO_COMBAT_EMPTY_SHIELD, bl);
	}
	public int getChangeInventoryCooldownTick() {
		return this.getEntityData().get(CHANGE_INVENTORY_COOLDOWN_TICK);
	}
	public void setChangeInventoryCooldownTick(int n) {
		this.getEntityData().set(CHANGE_INVENTORY_COOLDOWN_TICK, n);
	}
	@Override
	public void SpellUseAfterAttack(String string, MagicType magicType, MagicType magicType2) {
		if (!this.level().isClientSide()) {
			this.setSpellTick(10);
		}
	}
	@Override
	public List<SpellTypeInterface> SelfMainSpellList() {

			if (isArchmage()) {
				List<SpellTypeInterface> spellList = new ArrayList<>();
				for (String spellId : SpellRegistry.getRegisteredSpellIds()) {
					SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(spellId);
					if (SpellListByString.getSpell(3, this, this, spellTypeInterface).getMagicType2() == MagicType.MAIN) {
						spellList.add(SpellRegistry.getSpellTypeById(spellId));
					}
				}
				return spellList;
			}

		return new ArrayList<>();
	}
	@Override
	public List<SpellTypeInterface> SelfAddSpellList() {
		if (isArchmage()) {
			List<SpellTypeInterface> spellList = new ArrayList<>();
			for (String spellId : SpellRegistry.getRegisteredSpellIds()) {
				SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(spellId);
				if (SpellListByString.getSpell(3, this, this, spellTypeInterface).getMagicType2() == MagicType.ADD) {
					spellList.add(SpellRegistry.getSpellTypeById(spellId));
				}
			}
			return spellList;
		}
		return new ArrayList<>();
	}
	public int spellLevel = 1;
	@Override
	public int getSpellLevel() {
		if (isArchmage()) {
			return Math.max(10, this.spellLevel);
		}
		return this.spellLevel;
	}
	public void setSpellTick(int n){
		this.getEntityData().set(SPELL_TICK, n);
	}
	public int getSpellTick(){
		return this.getEntityData().get(SPELL_TICK);
	}
	public void setBowLevel(int n){
		this.getEntityData().set(BOW_LEVEL, n);
	}
	public int getBowLevel(){
		return this.getEntityData().get(BOW_LEVEL);
	}
	public void setShieldLevel(int n){
		this.getEntityData().set(SHIELD_LEVEL, n);
	}
	public int getShieldLevel(){
		return this.getEntityData().get(SHIELD_LEVEL);
	}
	public void setNameFirst(int n){
		this.getEntityData().set(NAME_FIRST, n);
	}
	public int getNameFirst(){
		return this.getEntityData().get(NAME_FIRST);
	}
	public void setNameSecond(int n){
		this.getEntityData().set(NAME_SECOND, n);
	}
	public int getNameSecond(){
		return this.getEntityData().get(NAME_SECOND);
	}
	public boolean isMagicUseStyle() {
		return this.getSpellTick() > 0;
	}
	@Override
	public boolean isSpellHumanoid() {
		return true;
	}
	@Override
	public @NotNull Component getName() {
		Component component = this.getCustomName();
		return component == null ? getHumanName() : super.getName();
	}
	public Component getHumanName() {
        return Component.translatable("name.jerotes.human.name_1_" + this.getNameFirst()).append(Component.translatable("name.jerotes.human.name_2_" + this.getNameSecond()));
	}
	@Override
	public void addAdditionalSaveData(CompoundTag compoundTag) {
		super.addAdditionalSaveData(compoundTag);
		compoundTag.putInt("NewSpawn", this.newSpawn);
		compoundTag.putInt("NameFirst", this.getNameFirst());
		compoundTag.putInt("NameSecond", this.getNameSecond());
		compoundTag.putInt("ShieldCoolDown", this.shieldCoolDown);
		compoundTag.putInt("ShieldBreakTick", this.shieldBreakTick);
		compoundTag.putInt("ShieldCanUse", this.shieldCanUse);
		compoundTag.putBoolean("IsBaby", this.isBaby());
		compoundTag.putInt("SpellLevel", this.spellLevel);
		compoundTag.putInt("SpellTick", this.getSpellTick());
		compoundTag.putInt("BowLevel", this.getBowLevel());
		compoundTag.putInt("ShieldLevel", this.getShieldLevel());
		compoundTag.putInt("CombatStyle", this.getCombatStyle());
		compoundTag.putBoolean("UseSelfNotStringSpellList", this.isUseSelfNotStringSpellList());
		compoundTag.putBoolean("IsArchmage", this.isArchmage());
		compoundTag.putString("Faction", this.getFaction());

		compoundTag.putBoolean("IsNoCombatEmptyWeapon", this.isNoCombatEmptyWeapon());
		compoundTag.putBoolean("IsNoCombatEmptyShield", this.isNoCombatEmptyShield());
		compoundTag.putInt("ChangeInventoryCooldownTick", this.getChangeInventoryCooldownTick());
		compoundTag.putBoolean("IsCanChangeInventory", this.isCanChangeInventory());
		compoundTag.putBoolean("IsCanChangeMeleeOrRange", this.isCanChangeMeleeOrRange());
		compoundTag.putBoolean("IsFemale", this.IsFemale());
		compoundTag.putInt("SkinType", this.SkinType());
		compoundTag.putInt("SkinColor", this.SkinColor());
		compoundTag.putInt("EyeType", this.EyeType());
		compoundTag.putInt("EyeColor", this.EyeColor());
		compoundTag.putInt("HairType", this.HairType());
		compoundTag.putInt("HairColor", this.HairColor());
		compoundTag.putInt("AddType_1", this.AddType_1());
		compoundTag.putInt("AddType_2", this.AddType_2());
		compoundTag.putInt("AddType_3", this.AddType_3());
		compoundTag.putInt("JacketType", this.JacketType());
		compoundTag.putInt("JacketColor", this.JacketColor());
		compoundTag.putInt("PantsType", this.PantsType());
		compoundTag.putInt("PantsColor", this.PantsColor());
		compoundTag.putInt("GlovesType", this.GlovesType());
		compoundTag.putInt("GlovesColor", this.GlovesColor());
		compoundTag.putInt("BaubleType_1", this.BaubleType_1());
		compoundTag.putInt("BaubleType_2", this.BaubleType_2());
		compoundTag.putInt("BaubleType_3", this.BaubleType_3());
		compoundTag.putInt("ShoesType", this.ShoesType());
		compoundTag.putInt("ShoesColor", this.ShoesColor());
		compoundTag.putInt("HatType", this.HatType());
		compoundTag.putInt("HatColor", this.HatColor());
		compoundTag.putInt("OvercoatType", this.OvercoatType());
		compoundTag.putInt("OvercoatColor", this.OvercoatColor());

		compoundTag.putString("SkinName", this.getSkinName());
		this.addPersistentAngerSaveData(compoundTag);
		this.writeInventoryToTag(compoundTag);
	}
	@Override
	public void readAdditionalSaveData(CompoundTag compoundTag) {
		super.readAdditionalSaveData(compoundTag);
		this.spellLevel = compoundTag.getInt("SpellLevel");
		this.setSpellTick(compoundTag.getInt("SpellTick"));
		this.newSpawn = compoundTag.getInt("NewSpawn");
		this.setNameFirst(compoundTag.getInt("NameFirst"));
		this.setNameSecond(compoundTag.getInt("NameSecond"));
		this.shieldCoolDown = compoundTag.getInt("ShieldCoolDown");
		this.shieldBreakTick = compoundTag.getInt("ShieldBreakTick");
		this.shieldCanUse = compoundTag.getInt("ShieldCanUse");
		this.setBaby(compoundTag.getBoolean("IsBaby"));
		this.setBowLevel(compoundTag.getInt("BowLevel"));
		this.setShieldLevel(compoundTag.getInt("ShieldLevel"));
		this.setCombatStyle(compoundTag.getInt("CombatStyle"));
		this.setUseSelfNotStringSpellList(compoundTag.getBoolean("UseSelfNotStringSpellList"));
		this.setArchmage(compoundTag.getBoolean("IsArchmage"));
		this.setFaction(compoundTag.getString("Faction"));

		this.setNoCombatEmptyWeapon(compoundTag.getBoolean("IsNoCombatEmptyWeapon"));
		this.setNoCombatEmptyShield(compoundTag.getBoolean("IsNoCombatEmptyShield"));
		this.setChangeInventoryCooldownTick(compoundTag.getInt("ChangeInventoryCooldownTick"));
		this.setCanChangeInventory(compoundTag.getBoolean("IsCanChangeInventory"));
		this.setCanChangeMeleeOrRange(compoundTag.getBoolean("IsCanChangeMeleeOrRange"));
		if (compoundTag.contains("IsFemale")) {
			this.setFemale(compoundTag.getBoolean("IsFemale"));
		}
		if (compoundTag.contains("SkinType")) {
			this.setSkinType(compoundTag.getInt("SkinType"));
		}
		if (compoundTag.contains("SkinColor")) {
			this.setSkinColor(compoundTag.getInt("SkinColor"));
		}
		if (compoundTag.contains("EyeType")) {
			this.setEyeType(compoundTag.getInt("EyeType"));
		}
		if (compoundTag.contains("EyeColor")) {
			this.setEyeColor(compoundTag.getInt("EyeColor"));
		}
		if (compoundTag.contains("HairType")) {
			this.setHairType(compoundTag.getInt("HairType"));
		}
		if (compoundTag.contains("HairColor")) {
			this.setHairColor(compoundTag.getInt("HairColor"));
		}
		if (compoundTag.contains("AddType_1")) {
			this.setAddType_1(compoundTag.getInt("AddType_1"));
		}
		if (compoundTag.contains("AddType_2")) {
			this.setAddType_2(compoundTag.getInt("AddType_2"));
		}
		if (compoundTag.contains("AddType_3")) {
			this.setAddType_3(compoundTag.getInt("AddType_3"));
		}
		if (compoundTag.contains("JacketType")) {
			this.setJacketType(compoundTag.getInt("JacketType"));
		}
		if (compoundTag.contains("JacketColor")) {
			this.setJacketColor(compoundTag.getInt("JacketColor"));
		}
		if (compoundTag.contains("PantsType")) {
			this.setPantsType(compoundTag.getInt("PantsType"));
		}
		if (compoundTag.contains("PantsColor")) {
			this.setPantsColor(compoundTag.getInt("PantsColor"));
		}
		if (compoundTag.contains("GlovesType")) {
			this.setGlovesType(compoundTag.getInt("GlovesType"));
		}
		if (compoundTag.contains("GlovesColor")) {
			this.setGlovesColor(compoundTag.getInt("GlovesColor"));
		}
		if (compoundTag.contains("BaubleType_1")) {
			this.setBaubleType_1(compoundTag.getInt("BaubleType_1"));
		}
		if (compoundTag.contains("BaubleType_2")) {
			this.setBaubleType_2(compoundTag.getInt("BaubleType_2"));
		}
		if (compoundTag.contains("BaubleType_3")) {
			this.setBaubleType_3(compoundTag.getInt("BaubleType_3"));
		}
		if (compoundTag.contains("ShoesType")) {
			this.setShoesType(compoundTag.getInt("ShoesType"));
		}
		if (compoundTag.contains("ShoesColor")) {
			this.setShoesColor(compoundTag.getInt("ShoesColor"));
		}
		if (compoundTag.contains("HatType")) {
			this.setHatType(compoundTag.getInt("HatType"));
		}
		if (compoundTag.contains("HatColor")) {
			this.setHatColor(compoundTag.getInt("HatColor"));
		}
		if (compoundTag.contains("OvercoatType")) {
			this.setOvercoatType(compoundTag.getInt("OvercoatType"));
		}
		if (compoundTag.contains("OvercoatColor")) {
			this.setOvercoatColor(compoundTag.getInt("OvercoatColor"));
		}

		this.setSkinName(compoundTag.getString("SkinName"));
		String skinName = compoundTag.getString("SkinName");
		if (!StringUtil.isNullOrEmpty(skinName)) {
			setUsername(skinName);
		}
		this.readPersistentAngerSaveData(this.level(), compoundTag);
		this.readInventoryFromTag(compoundTag);
	}
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(SPELL_TICK, 0);
		this.getEntityData().define(BOW_LEVEL, 15);
		this.getEntityData().define(SHIELD_LEVEL, 1);
		this.getEntityData().define(NAME_FIRST, 1);
		this.getEntityData().define(NAME_SECOND, 1);
		this.getEntityData().define(USE_SELF_NOT_SPELL_LIST, true);
		this.getEntityData().define(COMBAT_STYLE, 1);
		this.getEntityData().define(IS_CHARGING_CROSSBOW, false);
		this.getEntityData().define(DATA_BABY_ID, false);
		this.getEntityData().define(NO_COMBAT_EMPTY_WEAPON, false);
		this.getEntityData().define(NO_COMBAT_EMPTY_SHIELD, false);
		this.getEntityData().define(CHANGE_INVENTORY_COOLDOWN_TICK, 50);
		this.getEntityData().define(IS_ARCHMAGE, false);
		this.getEntityData().define(FACTION, "");

		this.getEntityData().define(IS_FEMALE, false);
		this.getEntityData().define(SKIN_TYPE, 1);
		this.getEntityData().define(SKIN_COLOR, 1);
		this.getEntityData().define(EYE_TYPE, 1);
		this.getEntityData().define(EYE_COLOR, 1);
		this.getEntityData().define(HAIR_TYPE, 1);
		this.getEntityData().define(HAIR_COLOR, 1);
		this.getEntityData().define(ADD_TYPE_1, 0);
		this.getEntityData().define(ADD_TYPE_2, 0);
		this.getEntityData().define(ADD_TYPE_3, 0);
		this.getEntityData().define(JACKET_TYPE, 1);
		this.getEntityData().define(JACKET_COLOR, 1);
		this.getEntityData().define(PANTS_TYPE, 1);
		this.getEntityData().define(PANTS_COLOR, 1);
		this.getEntityData().define(GLOVES_TYPE, 0);
		this.getEntityData().define(GLOVES_COLOR, 1);
		this.getEntityData().define(BAUBLE_TYPE_1, 0);
		this.getEntityData().define(BAUBLE_TYPE_2, 0);
		this.getEntityData().define(BAUBLE_TYPE_3, 0);
		this.getEntityData().define(SHOES_TYPE, 1);
		this.getEntityData().define(SHOES_COLOR, 1);
		this.getEntityData().define(HAT_TYPE, 0);
		this.getEntityData().define(HAT_COLOR, 1);
		this.getEntityData().define(OVERCOAT_TYPE, 0);
		this.getEntityData().define(OVERCOAT_COLOR, 1);

		this.getEntityData().define(CAN_CHANGE_INVENTORY, true);
		this.getEntityData().define(CAN_CHANGE_MELEE_OR_RANGE, true);

		this.getEntityData().define(SKIN_NAME, "");
	}
	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
		if (IS_FEMALE.equals(entityDataAccessor)) {
			this.refreshDimensions();
		}
		if (DATA_BABY_ID.equals(entityDataAccessor)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(entityDataAccessor);
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
		if (!this.isAggressive() && this.getTarget() == null && MainConfig.MobSayQuestionMark) {
			if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer && interactionHand == InteractionHand.MAIN_HAND) {
				serverPlayer.sendSystemMessage(Component.translatable("talk.jerotes.normal", this.getDisplayName()).withStyle(ChatFormatting.WHITE));
			}
		}
		return super.mobInteract(player, interactionHand);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.IsFemale()) {
			this.armSlimScaleAnimationState.stop();
			this.armWideScaleAnimationState.startIfStopped(this.tickCount);
		} else {
			this.armWideScaleAnimationState.stop();
			this.armSlimScaleAnimationState.startIfStopped(this.tickCount);
		}
	}
	@Override
	public void aiStep() {
		super.aiStep();
		if (this.random.nextInt(900) == 1 && this.deathTime == 0) {
			this.heal(3.0f);
		}
		this.updateSwingTime();
		this.updateNoActionTime();
		if (!this.level().isClientSide) {
			this.updatePersistentAnger((ServerLevel)this.level(), true);
		}
		//停止战斗
		if (this.getTarget() != null && (!this.getTarget().isAlive() || this.getTarget() instanceof Player player && (player.isCreative() || player.isSpectator()))){
			this.setTarget(null);
		}
		//
		//法术
		if (!this.level().isClientSide()) {
			this.setSpellTick(Math.max(0, this.getSpellTick() - 1));
		}
		//
		if (this.shieldCoolDown > 0){
			this.shieldCoolDown -= 1;
			this.shieldCanUse = 0;
		}
		if (this.shieldCoolDown <= 0){
			this.shieldCanUse = 1;
		}
		if (this.shieldBreakTick > 0){
			this.shieldBreakTick -= 1;
		}
		//摧毁骑乘物
		Main.destroyRides(this);
		//使用盾牌和双手武器
		useBlockingItem(this);
		//替换自己的物品
		changeInventory(this);
	}

	protected void updateNoActionTime() {
		float f = this.getLightLevelDependentMagicValue();
		if (f > 0.5f) {
			this.noActionTime += 1.1;
		}
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
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
	protected void blockUsingShield(LivingEntity entityIn) {
		super.blockUsingShield(entityIn);
		if (entityIn instanceof BreakShieldEntity breakShield) this.disableShieldBreak(breakShield.getShieldBreakStrength());
		if (entityIn.getMainHandItem().canDisableShield(this.useItem, this, entityIn) || entityIn.canDisableShield()) this.disableShield();
		this.disableShieldTry();
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
	protected void hurtCurrentlyUsedShield(float f) {
		if (JerotesGameRules.JEROTES_MELEE_CAN_BREAK != null && this.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_SHIELD_CAN_BREAK)) {
			if (this.useItem.canPerformAction(ToolActions.SHIELD_BLOCK)) {
				if (f >= 3.0f) {
					int n = 1 + Mth.floor(f);
					InteractionHand interactionHand = this.getUsedItemHand();
					this.useItem.hurtAndBreak(n, this, player -> player.broadcastBreakEvent(interactionHand));
					if (this.useItem.isEmpty()) {
						if (interactionHand == InteractionHand.MAIN_HAND) {
							this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
						} else {
							this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
						}
						this.useItem = ItemStack.EMPTY;
						this.playSound(SoundEvents.SHIELD_BREAK, 0.8f, 0.8f + this.level().random.nextFloat() * 0.4f);
					}
				}
			}
		}
	}

	@Override
	public void disableShield() {
		if (this.getRandom().nextFloat() <= (1 - (this.getShieldLevel() - 1) * 0.2f)) {
			if (this.shieldCoolDown < 100) {
				this.shieldCoolDown = 100;
			}
			this.shieldCanUse = 0;
			if (this.getUseItem().getItem() instanceof ShieldItem) {
				this.stopUsingItem();
			}
		}
	}
	@Override
	public void disableShieldTry() {
		if (this.getRandom().nextFloat() <= (0.15 - (this.getShieldLevel() - 1) * 0.05f)) {
			if (this.shieldCoolDown < 100) {
				this.shieldCoolDown = 100;
			}
			this.shieldCanUse = 0;
			if (this.getUseItem().getItem() instanceof ShieldItem) {
				this.stopUsingItem();
			}
		}
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

	@Override
	protected void hurtArmor(DamageSource damageSource, float damage) {
		if (JerotesGameRules.JEROTES_ARMOR_CAN_BREAK != null && this.level().getLevelData().getGameRules().getBoolean(JerotesGameRules.JEROTES_ARMOR_CAN_BREAK)) {
			if (damage >= 0.0F) {
				damage = damage / 4.0F;
				if (damage < 1.0F) {
					damage = 1.0F;
				}
				ItemStack head = this.getItemBySlot(EquipmentSlot.HEAD);
				ItemStack chest = this.getItemBySlot(EquipmentSlot.CHEST);
				ItemStack legs = this.getItemBySlot(EquipmentSlot.LEGS);
				ItemStack feet = this.getItemBySlot(EquipmentSlot.FEET);
				if ((!damageSource.is(DamageTypes.ON_FIRE) || !head.getItem().isFireResistant() && !damageSource.is(DamageTypeTags.BYPASSES_ARMOR))) {
					if (head.getItem() instanceof ArmorItem) {
						head.hurtAndBreak((int) damage, this, player -> player.broadcastBreakEvent(EquipmentSlot.HEAD));
					}
					if (chest.getItem() instanceof ArmorItem) {
						chest.hurtAndBreak((int) damage, this, player -> player.broadcastBreakEvent(EquipmentSlot.CHEST));
					}
					if (legs.getItem() instanceof ArmorItem) {
						legs.hurtAndBreak((int) damage, this, player -> player.broadcastBreakEvent(EquipmentSlot.LEGS));
					}
					if (feet.getItem() instanceof ArmorItem) {
						feet.hurtAndBreak((int) damage, this, player -> player.broadcastBreakEvent(EquipmentSlot.FEET));
					}
				}
			}
		}
	}

	@Override
	public boolean isAlliedTo(Entity entity) {
		if (entity == null) {
			return false;
		}
		if (entity == this) {
			return true;
		}
		if (super.isAlliedTo(entity)) {
			return true;
		}
		if (entity instanceof LivingEntity livingEntity && EntityFactionFind.isFaction(this, livingEntity)) {
			return this.getTeam() == null && entity.getTeam() == null;
		}
		return false;
	}

	@Override
	public boolean canAttack(LivingEntity livingEntity) {
		if (((this.getTeam() == null && livingEntity.getTeam() == null) || this.getTeam() == livingEntity.getTeam()) &&
				EntityFactionFind.isFaction(this, livingEntity)
		) {
			return false;
		}
		return super.canAttack(livingEntity);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
		super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
		if (mobSpawnType != MobSpawnType.CONVERSION) {
			if (newSpawn == 0) {
				RandomSource randomSource = serverLevelAccessor.getRandom();
				this.populateDefaultEquipmentSlots(randomSource, difficultyInstance);

				if (serverLevelAccessor.getRandom().nextFloat() < 0.5f) {
					this.setFemale(true);
				}
				this.setSkinType(this.MaxSkinType());
				this.setSkinColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxSkinColor()));
				if (serverLevelAccessor.getRandom().nextFloat() < 0.01f) {
					this.setEyeType(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxEyeType()));
				}
				else {
					this.setEyeType(1);
				}
				this.setEyeColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxEyeColor()));
				if (serverLevelAccessor.getRandom().nextFloat() < 0.2f) {
					this.setAddType_1(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxAddType()));
					if (serverLevelAccessor.getRandom().nextFloat() < 0.3f) {
						this.setAddType_2(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxAddType()));
						if (this.AddType_1() == this.AddType_2()) {
							this.setAddType_2(0);
						}
						if (serverLevelAccessor.getRandom().nextFloat() < 0.4f) {
							this.setAddType_3(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxAddType()));
							if (this.AddType_2() == this.AddType_3()) {
								this.setAddType_3(0);
							}
						}
					}
				}
				this.setHairType(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxHairType()));
				if (serverLevelAccessor.getRandom().nextFloat() < 0.01f) {
					this.setHairType(0);
				}
				this.setHairColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxHairColor()));
				this.setJacketType(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxJacketType()));
				this.setJacketColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxJacketColor()));
				this.setPantsType(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxPantsType()));
				this.setPantsColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxPantsColor()));
				this.setGlovesType(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxGlovesType()));
				this.setGlovesColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxGlovesColor()));
				if (serverLevelAccessor.getRandom().nextFloat() < 0.2f) {
					this.setBaubleType_1(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxBaubleType()));
					if (serverLevelAccessor.getRandom().nextFloat() < 0.3f) {
						this.setBaubleType_2(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxBaubleType()));
						if (this.BaubleType_1() == this.BaubleType_2()) {
							this.setBaubleType_2(0);
						}
						if (serverLevelAccessor.getRandom().nextFloat() < 0.4f) {
							this.setBaubleType_3(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxBaubleType()));
							if (this.BaubleType_2() == this.BaubleType_3()) {
								this.setBaubleType_3(0);
							}
						}
					}
				}
				this.setShoesType(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxShoesType()));
				this.setShoesColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxShoesColor()));
				if (serverLevelAccessor.getRandom().nextFloat() < 0.2f) {
					this.setHatType(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxHairType()));
				}
				this.setHatColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxHairColor()));
				if (serverLevelAccessor.getRandom().nextFloat() < 0.6f) {
					this.setOvercoatType(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxOvercoatType()));
				}
				this.setOvercoatColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, this.MaxOvercoatColor()));

				this.setNameFirst(Main.randomReach(serverLevelAccessor.getRandom(), 1, 30));
				this.setNameSecond(Main.randomReach(serverLevelAccessor.getRandom(), 1, 30));

				float weaponRandom = this.random.nextFloat();
				float offhandRandom = this.random.nextFloat();
				this.setItemSlot(EquipmentSlot.MAINHAND, this.createSpawnWeapon(weaponRandom));
				this.setItemSlot(EquipmentSlot.OFFHAND, this.createSpawnOffhand(offhandRandom, weaponRandom));
				this.populateDefaultEquipmentEnchantments(randomSource, difficultyInstance);
				newSpawn = 1;
			}
		}
		return spawnGroupData;
	}
	@Override
	protected void onOffspringSpawnedFromEgg(Player player, Mob child) {
		super.onOffspringSpawnedFromEgg(player, child);
		if (child instanceof HumanEntity humanEntity && humanEntity.level() instanceof ServerLevel serverLevelAccessor) {
			if (serverLevelAccessor.getRandom().nextFloat() < 0.5f) {
				humanEntity.setFemale(true);
			}
			humanEntity.setSkinType(humanEntity.MaxSkinType());
			humanEntity.setSkinColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxSkinColor()));
			if (serverLevelAccessor.getRandom().nextFloat() < 0.01f) {
				humanEntity.setEyeType(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxEyeType()));
			}
			else {
				humanEntity.setEyeType(1);
			}
			humanEntity.setEyeColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxEyeColor()));
			if (serverLevelAccessor.getRandom().nextFloat() < 0.2f) {
				humanEntity.setAddType_1(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxAddType()));
				if (serverLevelAccessor.getRandom().nextFloat() < 0.3f) {
					humanEntity.setAddType_2(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxAddType()));
					if (humanEntity.AddType_1() == humanEntity.AddType_2()) {
						humanEntity.setAddType_2(0);
					}
					if (serverLevelAccessor.getRandom().nextFloat() < 0.4f) {
						humanEntity.setAddType_3(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxAddType()));
						if (humanEntity.AddType_2() == humanEntity.AddType_3()) {
							humanEntity.setAddType_3(0);
						}
					}
				}
			}
			humanEntity.setHairType(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxHairType()));
			if (serverLevelAccessor.getRandom().nextFloat() < 0.01f) {
				humanEntity.setHairType(0);
			}
			humanEntity.setHairColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxHairColor()));
			humanEntity.setJacketType(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxJacketType()));
			humanEntity.setJacketColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxJacketColor()));
			humanEntity.setPantsType(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxPantsType()));
			humanEntity.setPantsColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxPantsColor()));
			humanEntity.setGlovesType(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxGlovesType()));
			humanEntity.setGlovesColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxGlovesColor()));
			if (serverLevelAccessor.getRandom().nextFloat() < 0.2f) {
				humanEntity.setBaubleType_1(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxBaubleType()));
				if (serverLevelAccessor.getRandom().nextFloat() < 0.3f) {
					humanEntity.setBaubleType_2(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxBaubleType()));
					if (humanEntity.BaubleType_1() == humanEntity.BaubleType_2()) {
						humanEntity.setBaubleType_2(0);
					}
					if (serverLevelAccessor.getRandom().nextFloat() < 0.4f) {
						humanEntity.setBaubleType_3(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxBaubleType()));
						if (humanEntity.BaubleType_2() == humanEntity.BaubleType_3()) {
							humanEntity.setBaubleType_3(0);
						}
					}
				}
			}
			humanEntity.setShoesType(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxShoesType()));
			humanEntity.setShoesColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxShoesColor()));
			if (serverLevelAccessor.getRandom().nextFloat() < 0.2f) {
				humanEntity.setHatType(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxHairType()));
			}
			humanEntity.setHatColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxHairColor()));
			if (serverLevelAccessor.getRandom().nextFloat() < 0.6f) {
				humanEntity.setOvercoatType(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxOvercoatType()));
			}
			humanEntity.setOvercoatColor(Main.randomReach(serverLevelAccessor.getRandom(), 1, humanEntity.MaxOvercoatColor()));

			humanEntity.setNameFirst(Main.randomReach(serverLevelAccessor.getRandom(), 1, 30));
			humanEntity.setNameSecond(Main.randomReach(serverLevelAccessor.getRandom(), 1, 30));
		}
	}

	public ItemStack createSpawnWeapon(float weaponRandom) {
		return new ItemStack(Items.AIR);
	}

	public ItemStack createSpawnOffhand(float offhandRandom, float weaponRandom) {
		return new ItemStack(Items.AIR);
	}

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


	public void setSkinName(String string) {
		this.getEntityData().set(SKIN_NAME, string);
	}
	public String getSkinName() {
		return this.getEntityData().get(SKIN_NAME);
	}
	@Nullable
	private GameProfile profile;
	@Nullable
	private ResourceLocation skin;
	private boolean skinAvailable;
	@OnlyIn(Dist.CLIENT)
	public SkinManager.SkinTextureCallback getSkinCallback() {
		return (type, location, profileTexture) -> {
			switch (type) {
				case SKIN -> {
					skin = location;
					skinAvailable = true;
				}
			}
		};
	}
	@Nullable
	public GameProfile getProfile() {
		if (profile == null && hasUsername()) {
			profile = new GameProfile(null, getUsername().getSkinName());
			PlayerSkin.ProfileUpdater.updateProfile(this);
		}
		return profile;
	}
	public void setProfile(@Nullable GameProfile profile) {
		this.profile = profile;
	}
	public boolean hasUsername() {
		return !StringUtil.isNullOrEmpty(getEntityData().get(SKIN_NAME));
	}
	public PlayerName getUsername() {
		return new PlayerName(getEntityData().get(SKIN_NAME));
	}
	public void setUsername(String username) {
		PlayerName playerName = new PlayerName(username);
		setUsername(playerName);
	}
	public void setUsername(PlayerName name) {
		PlayerName oldName = hasUsername() ? getUsername(): null;
		getEntityData().set(SKIN_NAME, name.getCombinedNames());

		if (!Objects.equals(oldName, name)) {
			setProfile(null);
			getProfile();
		}
	}
	@OnlyIn(Dist.CLIENT)
	public boolean isTextureAvailable(MinecraftProfileTexture.Type type) {
		return skinAvailable;
	}
	@SuppressWarnings("ConstantConditions")
	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getTexture(MinecraftProfileTexture.Type type) {
		return skin;
	}
}
