package com.jerotes.jerotes.util;

import com.jerotes.jerotes.client.animation.SpearAnimations;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.JerotesChangeEntity;
import com.jerotes.jerotes.entity.Mob.AddHandEntity;
import com.jerotes.jerotes.entity.Mob.MirrorImageEntity;
import com.jerotes.jerotes.entity.Other.FallingBlock.JerotesEarthrendBlock;
import com.jerotes.jerotes.entity.Other.FallingBlock.JerotesUnevenBlock;
import com.jerotes.jerotes.entity.Part.BasePartEntity;
import com.jerotes.jerotes.entity.Other.FallingBlock.JerotesFallingBlock;
import com.jerotes.jerotes.entity.Interface.SpecialItemInHandEntity;
import com.jerotes.jerotes.init.JerotesEntityType;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.item.AAExplorationEye;
import com.jerotes.jerotes.item.Interface.ItemSpecialInHand;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import com.jerotes.jerotes.world.inventory.MobInventoryGUIMenu;
import com.jerotes.jerotes.world.inventory.SuchInventoryMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class Main {
	public static CompoundTag getJerotesPersistentData(Entity entity) {
		if (entity instanceof JerotesChangeEntity jerotesChangeEntity)
			return jerotesChangeEntity.getJerotesPersistentData();
		return new CompoundTag();
	}

	//幸运值
	public static double luck(LivingEntity livingEntity) {
		return livingEntity.getAttribute(Attributes.LUCK) != null ? livingEntity.getAttributeValue(Attributes.LUCK) : 0;
	}

	//其他类型观察
	public static boolean isOtherScoping(LivingEntity player) {
		return (player.isUsingItem() && (player.getUseItem().getItem() instanceof AAExplorationEye));
	}

	//打开gui
	public static void openMobInventoryGui(ServerPlayer serverPlayer, LivingEntity livingEntity, boolean bl, boolean bl2, boolean canUseMainHand, boolean canUseOffHand, boolean canUseHelmet, boolean canUseChestplate, boolean canUseLeggings, boolean canUseBoots) {
		if (!livingEntity.isAlive()) {
			return;
		}
		//先获得基础类型
		String string = EntityFactionFind.getFindFaction(livingEntity);
		String string2 = "jerotes";
		//如果有自己的阵营而不是类型
		if (!EntityFactionFind.getTrueFaction(livingEntity).isEmpty()) {
			string = EntityFactionFind.getTrueFaction(livingEntity);
		}

		//不属于基础类型有自定义阵营的就优先选择自己的modid
		List<String> list = EntityFactionFind.getAllFindFaction(livingEntity, true);
		if (!list.contains(string) && !string.equals(EntityFactionFind.getFindFaction(livingEntity))) {
			string2 = EntityFactionFind.getTrueMobTypeNameModId(livingEntity);
		}
		//如果有主人
		if (AttackFind.getOwnerTrue(livingEntity) != null) {
			LivingEntity owner = AttackFind.getOwnerTrue(livingEntity);
			//不属于基础类型的主人有自定义阵营
			if (!list.contains(EntityFactionFind.getTrueFaction(owner)) && !EntityFactionFind.getTrueFaction(owner).isEmpty()) {
				//先试着用主人的modid
				if (!EntityFactionFind.getTrueMobTypeNameModId(owner).isEmpty()) {
					string2 = EntityFactionFind.getTrueMobTypeNameModId(livingEntity);
				}
				//主人没有自定义modid，就得跟随主人的基础modid
				else {
					string2 = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(owner.getType())).getNamespace();
				}
			}
		}

		if (!Main.getJerotesPersistentData(livingEntity).getString("jerotes_mob_faction").isEmpty()) {
			string = Main.getJerotesPersistentData(livingEntity).getString("jerotes_mob_faction");
		}
		if (!Main.getJerotesPersistentData(livingEntity).getString("jerotes_mob_faction_mod_id").isEmpty()) {
			string2 = Main.getJerotesPersistentData(livingEntity).getString("jerotes_mob_faction_mod_id");
		}
		String finalString = string;
		String finalString2 = string2;

      NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("mob_inventory_gui");
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                packetBuffer.writeBlockPos(serverPlayer.blockPosition());
                packetBuffer.writeByte(0);
                packetBuffer.writeVarInt(livingEntity.getId());
                packetBuffer.writeUtf(finalString);
                packetBuffer.writeUtf(finalString2);
                return new MobInventoryGUIMenu(id, inventory, packetBuffer, bl, bl2, canUseMainHand, canUseOffHand, canUseHelmet, canUseChestplate, canUseLeggings, canUseBoots);
			}
        }, buf -> {
            buf.writeBlockPos(serverPlayer.blockPosition());
            buf.writeByte(0);
            buf.writeVarInt(livingEntity.getId());
        buf.writeUtf(finalString);
        buf.writeUtf(finalString2);
        });
	}
	//打开gui
	public static void openSuchInventoryGui(ServerPlayer serverPlayer, LivingEntity livingEntity) {
		if (!livingEntity.isAlive()) {
			return;
		}
      NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return livingEntity.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                packetBuffer.writeVarInt(livingEntity.getId());
                return new SuchInventoryMenu(id, inventory, packetBuffer);
            }
        }, buf -> {
            buf.writeVarInt(livingEntity.getId());
        });
	}
	//腾跃
	public static void WaterLeap(PathfinderMob mob, float strength) {
		if (mob.tickCount % 10 != 0) {
			return;
		}
		if (!mob.isInWater() || !mob.horizontalCollision) {
			return;
		}
		Path path = mob.getNavigation().getPath();
		LivingEntity target = mob.getTarget();
		boolean shouldLeap = false;
		if (path != null && !mob.getNavigation().isDone()) {
			BlockPos targetPos = path.getTarget();
			BlockState targetState = mob.level().getBlockState(targetPos);
			if (!targetState.getFluidState().isEmpty()) {
				shouldLeap = true;
			}
		}
		if (!shouldLeap && target != null && target.onGround()) {
			shouldLeap = true;
		}
		if (shouldLeap) {
			for (int n = 0; n < 18; ++n) {
				if (mob.level() instanceof ServerLevel serverLevel) {
					serverLevel.sendParticles(ParticleTypes.BUBBLE, mob.getRandomX(0.5), mob.getY() + 0.5f, mob.getRandomZ(0.5),
							0, (mob.getRandom().nextFloat() - 0.5) * 0.2f, 0.2, (mob.getRandom().nextFloat() - 0.5) * 0.2f, 0.5);
				}
			}
			mob.setDeltaMovement(mob.getDeltaMovement().add(0, strength, 0));
		}
	}

	//摧毁骑乘物
	public static void destroyRides(Mob mob) {
		if (mob.getVehicle() instanceof Boat boat && mob.isAggressive()) {
			mob.swing(InteractionHand.MAIN_HAND);
			mob.stopRiding();
			mob.spawnAtLocation(boat.getDropItem());
			boat.discard();
		}
		if (mob.getVehicle() instanceof Minecart minecart && mob.getVehicle().getType() == EntityType.MINECART && mob.isAggressive()) {
			mob.swing(InteractionHand.MAIN_HAND);
			mob.stopRiding();
			mob.spawnAtLocation(Items.MINECART);
			minecart.discard();
		}
	}
	//随机
	public static int randomReach(RandomSource randomSource, int n, int n2) {
		if (n2 < n) {
			n = n2;
		}
		return randomSource.nextInt(n, n2 + 1);
	}

	//横扫粒子
	public static void sweepAttack(Entity entity) {
		double d = -Mth.sin(entity.getYRot() * 0.017453292f);
		double d2 = Mth.cos(entity.getYRot() * 0.017453292f);
		if (entity.level() instanceof ServerLevel serverLevel) {
			serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, entity.getX() + d, entity.getY(0.5), entity.getZ() + d2, 0, d, 0.0, d2, 0.0);
		}
	}
	//是液体
	public static boolean isInFluid(LivingEntity livingEntity) {
		return livingEntity.isInFluidType();
	}
	//效果最大时间
	public static void addEffectMax(LivingEntity livingEntity, LivingEntity livingEntity2, MobEffect mobEffect, int n, int n2, boolean bl, boolean bl2, int max) {
		if (!livingEntity.level().isClientSide) {
			if (livingEntity.hasEffect(mobEffect)
					&& Objects.requireNonNull(livingEntity.getEffect(mobEffect)).getAmplifier() <= n2
					&& Objects.requireNonNull(livingEntity.getEffect(mobEffect)).getDuration() < max) {
				livingEntity.addEffect(new MobEffectInstance(mobEffect, Objects.requireNonNull(livingEntity.getEffect(mobEffect)).getAmplifier() + n, n2, bl, bl2), livingEntity2);
			}
			else {
				livingEntity.addEffect(new MobEffectInstance(mobEffect, max, n2, bl, bl2), livingEntity2);
			}
		}
	}

	//面前
	public static boolean canSee(Entity target, Entity self) {
		float f = 0.0f * ((float)Math.PI / 180F);
		float f1 = -self.getYHeadRot() * ((float)Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		Vec3 vec3 = new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
		Vec3 object = target.getPosition(0.5f);
		Vec3 vec32 = object.vectorTo(self.position());
		vec32 = new Vec3(vec32.x, 0.0, vec32.z).normalize();
		return vec32.dot(vec3) < 0.0;
	}
	public static boolean canSee(Vec3 target, Entity self) {
		float f = 0.0f * ((float)Math.PI / 180F);
		float f1 = -self.getYHeadRot() * ((float)Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		Vec3 vec3 = new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
		Vec3 vec32 = target.vectorTo(self.position());
		vec32 = new Vec3(vec32.x, 0.0, vec32.z).normalize();
		return vec32.dot(vec3) < 0.0;
	}
	public static boolean canSee(LivingEntity target, Vec3 self) {
		if (self != null) {
			Vec3 lookingVector = target.getViewVector(1.0F);
			Vec3 attackAngleVector = self.subtract(target.position()).normalize();
			double horizontalDot = new Vec3(attackAngleVector.x, 0.0, attackAngleVector.z)
					.dot(new Vec3(lookingVector.x, 0.0, lookingVector.z));
			double verticalDot = attackAngleVector.y * lookingVector.y;
			return horizontalDot < -0.5 && verticalDot < -0.5;
		}
		return false;
	}
	//面前角度
	public static boolean canSeeAngle(Entity see, Vec3 beSeen, double angleThreshold) {
		float f = 0.0f * ((float)Math.PI / 180F);
		float f1 = -see.getYHeadRot() * ((float)Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);
		Vec3 vec3 = new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
		Vec3 vec32 = beSeen.vectorTo(see.position());
		vec32 = new Vec3(vec32.x, 0.0, vec32.z).normalize();
		return vec32.dot(vec3) < (-1 + (angleThreshold/180));
	}
	//直线
	public static boolean hasLineOfSightEntity(Entity target, Entity self) {
		if (target.level() != self.level()) {
			return false;
		}
		Vec3 vec3 = new Vec3(self.getX(), self.getEyeY(), self.getZ());
		Vec3 vec32 = new Vec3(target.getX(), target.getEyeY(), target.getZ());
		return self.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, self)).getType() == HitResult.Type.MISS;
	}
	public static boolean hasLineOfSightPos(Vec3 vec3, Entity self) {
		Vec3 vec32 = new Vec3(self.getX(), self.getEyeY(), self.getZ());
		return self.level().clip(new ClipContext(vec32, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, self)).getType() == HitResult.Type.MISS;
	}
	public static boolean hasLineOfSightPos(Vec3 vec3, Vec3 self, Entity entity, Level level) {
		return level.clip(new ClipContext(self, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
	}
	//玩家视线
	public static Entity getTargetedEntityBase(Player player, double maxDistance, boolean bl) {
		Level world = player.level();
		Vec3 startVec = player.getEyePosition(1.0F);
		Vec3 lookVec = player.getViewVector(1.0F).scale(maxDistance);
		Vec3 endVec = startVec.add(lookVec);

		AABB searchBox = new AABB(startVec, endVec).inflate(1.0D);
		List<Entity> entities = world.getEntities(player, searchBox, entity -> entity.isPickable() && entity != player);
		Entity closestEntity = null;
		double closestDistance = maxDistance;

		for (Entity entity : entities) {
			AABB entityBox = entity.getBoundingBox().inflate(0.5D);
			Optional<Vec3> optionalHit = entityBox.clip(startVec, endVec);
			if (optionalHit.isPresent()) {
				double distance = startVec.distanceTo(optionalHit.get());
				if (distance < closestDistance) {
					if (!(bl && (entity.isInvisible()|| !player.hasLineOfSight(entity)))) {
						closestDistance = distance;
						closestEntity = entity;
					}
				}
			}
		}
		return closestEntity;
	}
	public static Entity getTargetedEntity(Player player, double maxDistance) {
		return getTargetedEntity(player, maxDistance, false);
	}
	public static Entity getTargetedEntity(Player player, double maxDistance, boolean bl) {
		Entity entity = getTargetedEntityBase(player, maxDistance, bl);
		if (bl && entity != null && entity.isInvisible()) {
			return null;
		}
		if (bl && entity != null && !player.hasLineOfSight(entity)) {
			return null;
		}
		return entity;
	}
	//正在看我
	static boolean isLookingAtMe(LivingEntity livingEntity, LivingEntity self) {
		ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
		if (livingEntity instanceof Player player) {
			itemStack = player.getInventory().armor.get(3);
		}
		if (itemStack.is(Blocks.CARVED_PUMPKIN.asItem())) {
			return false;
		}
		if (self instanceof EnderMan enderMan && livingEntity instanceof Player player) {
			if (ForgeHooks.shouldSuppressEnderManAnger(enderMan, player, itemStack)) {
				return false;
			}
		}
		Vec3 vec3 = livingEntity.getViewVector(1.0f).normalize();
		Vec3 vec32 = new Vec3(self.getX() - livingEntity.getX(), self.getEyeY() - livingEntity.getEyeY(), self.getZ() - livingEntity.getZ());
		double d = vec32.length();
		double d2 = vec3.dot(vec32 = vec32.normalize());
		if (d2 > 1.0 - 0.025 / d) {
			return livingEntity.hasLineOfSight(self);
		}
		return false;
	}

	//加载身体
	public static void updatePart(@Nullable final BasePartEntity part, @NotNull final LivingEntity parent) {
		if (part == null || !(parent.level() instanceof ServerLevel serverLevel) || parent.isRemoved()) {
			return;
		}
		if (!part.shouldContinuePersisting()) {
			UUID uuid = part.getUUID();
			Entity existing = serverLevel.getEntity(uuid);

			if (existing != null && existing != part) {
				while (serverLevel.getEntity(uuid) != null) {
					uuid = Mth.createInsecureUUID(parent.getRandom());
				}
			}
			part.setUUID(uuid);
			serverLevel.addFreshEntity(part);
		}
		part.setParent(parent);
	}

	//生物宽
	public static float mobWidth(Entity entity) {
		return (float) ((entity.getBoundingBox().getXsize() + entity.getBoundingBox().getZsize())/2);
	}
	//生物高
	public static float mobHeight(Entity entity) {
		return (float) entity.getBoundingBox().getYsize();
	}
	//体型选择
	public static boolean mobSizeFind(Entity entity, float average, float max) {
		return !(Main.mobWidth(entity) >= max) && !(Main.mobHeight(entity) >= max) && !(Main.mobWidth(entity) >= average && Main.mobHeight(entity) >= average);
	}
	//小形生物
	public static boolean mobSizeSmall(Entity entity) {
		return Main.mobSizeFind(entity, 0.75f, 1.75f);
	}
	//中形生物
	public static boolean mobSizeMedium(Entity entity) {
		return Main.mobSizeFind(entity, 1.75f, 3.5f) && !mobSizeSmall(entity);
	}
	//大型生物
	public static boolean mobSizeLarge(Entity entity) {
		return Main.mobSizeFind(entity, 3.5f, 6f) && !mobSizeSmall(entity) && !mobSizeMedium(entity);
	}
	//巨形生物
	public static boolean mobSizeGiant(Entity entity) {
		return !mobSizeSmall(entity) && !mobSizeMedium(entity) && !mobSizeLarge(entity);
	}


	public static AABB blockDestroyAABB(Mob mob) {
		AABB aABB = mob.getBoundingBox();
		if (mob.getNavigation().getPath() != null) {
			if ((mob.getNavigation().getPath().getTarget().getY() - mob.getY()) > 1) {
				aABB = mob.getBoundingBox().move(0, 1, 0);
			} else if ((mob.getNavigation().getPath().getTarget().getY() - mob.getY()) < -1) {
				aABB = mob.getBoundingBox().move(0, -1, 0);
			}
		}
		return aABB;
	}
	//生物破坏方块范围
	public static boolean BlockDestroy(Mob mob, float destroy) {
		if (!mob.level().isClientSide) {
			boolean bl = mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && MainConfig.MobDestroyBlock;
			boolean bl1 = mob instanceof OwnableEntity ownable && ownable.getOwner() != null && MainConfig.TamedMobDestroyBlock || !(mob instanceof OwnableEntity ownables) || ownables.getOwner() == null;
			if (bl && bl1) {
				AABB aABB = Main.blockDestroyAABB(mob);
				boolean bl2 = false;
				for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aABB.minX), Mth.floor(aABB.minY), Mth.floor(aABB.minZ), Mth.floor(aABB.maxX), Mth.floor(aABB.maxY), Mth.floor(aABB.maxZ))) {
					BlockState blockState = mob.level().getBlockState(blockPos);
					float block = blockState.getDestroySpeed(mob.level(), blockPos);
					if (blockState.isAir()) continue;
					if (block >= destroy || block < 0f) continue;
					if (!ForgeEventFactory.onEntityDestroyBlock(mob, blockPos, blockState)) continue;
					if ((blockState.is(BlockTags.REPLACEABLE_BY_TREES)
							|| blockState.is(BlockTags.DIRT)
							|| blockState.is(BlockTags.SCULK_REPLACEABLE)
							|| blockState.is(BlockTags.SAND)
							|| blockState.is(BlockTags.STONE_ORE_REPLACEABLES)) && mob.getRandom().nextFloat() > 0.005) {
						boolean bl3 = mob.level().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
						if (bl3) {
							bl2 = true;
						}
					}
					else {
						boolean bl3 = mob.level().destroyBlock(blockPos, true, mob);
						if (bl3) {
							bl2 = true;
						}
					}
				}
				return bl2;
			}
		}
		return false;
	}

	//附近
	public static BlockPos findSpawnPositionNear(LevelReader levelReader, BlockPos blockPos, int n) {
		BlockPos blockPos2 = blockPos.above();
		for (int i = 0; i < 128; ++i) {
			int n3;
			int n4 = blockPos.getX() + RandomSource.create().nextInt(n * 2) - n;
			BlockPos blockPos3 = new BlockPos(n4, blockPos.getY() + RandomSource.create().nextInt(n), blockPos.getZ() + RandomSource.create().nextInt(n * 2) - n).above().above().above();
			BlockState blockState = levelReader.getBlockState(blockPos3);
			if (!(blockState.isAir())) continue;
			blockPos2 = blockPos3;
			break;
		}
		return blockPos2;
	}
	//生物附近生成
	public static BlockPos findSpawnPositionNearFill(LevelReader levelReader, BlockPos blockPos, int n) {
		if (levelReader instanceof ServerLevel serverLevel) {
			PoiManager poiManager = serverLevel.getPoiManager();
			Optional<BlockPos> optional = poiManager.find(holder -> holder.is(PoiTypes.MEETING), blockPos1 -> true, blockPos, n, PoiManager.Occupancy.ANY);
			BlockPos blockPos3 = optional.orElse(blockPos);
			return findSpawnPositionNear(serverLevel, blockPos3, n);
		}
		else {
			return findSpawnPositionNear(levelReader, blockPos, n);
		}
	}
	public static BlockPos findSpawnPositionNearFillOnBlock(LevelReader levelReader, BlockPos blockPos, int n) {
		BlockPos blockPos2 = findSpawnPositionNearFill(levelReader, blockPos, n);
		if (levelReader.getBlockState(new BlockPos(blockPos2.getX(), blockPos2.getY() - 1, blockPos2.getZ())).isAir()) {
			for (int i = 0; i < n * 4; ++i) {
				if (!levelReader.getBlockState(new BlockPos(blockPos2.getX(), blockPos2.getY() - i, blockPos2.getZ())).isAir()) {
					return new BlockPos(blockPos2.getX(), blockPos2.getY() - i + 1, blockPos2.getZ());
				}
			}
		}
		return blockPos2;
	}
	//
	public static BlockPos findSpawnPositionNearFill(Entity entity, int n) {
		return findSpawnPositionNearFill(entity.level(), entity.blockPosition(), n);
	}
	public static BlockPos findSpawnPositionNearFillOnBlock(Entity entity, int n) {
		return findSpawnPositionNearFillOnBlock(entity.level(), entity.blockPosition(), n);
	}

	//下落方块
	public static void spawnFallingBlockByPos(ServerLevel level, BlockPos pos, float fallingFactor) {
		Random random = new Random();
		BlockPos abovePos = new BlockPos(pos).above();
		BlockState block = level.getBlockState(pos);
		BlockState blockAbove = level.getBlockState(abovePos);
		if (random.nextBoolean()) {
			fallingFactor += (float) (0.4 + random.nextGaussian() * 0.2);
		} else {
			fallingFactor -= (float) Mth.clamp(0.2 + random.nextGaussian() * 0.2, 0.2, fallingFactor - 0.1);
		}
		if (!block.isAir() && block.isRedstoneConductor(level, pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
			JerotesFallingBlock fallingBlock = new JerotesFallingBlock(level, block, (float) (0.32 + fallingFactor * 0.2));
			fallingBlock.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			level.addFreshEntity(fallingBlock);
		}
	}
	public static void spawnFallingBlockByPosChance(ServerLevel level, BlockPos pos, float chance) {
		RandomSource random = RandomSource.create();
		BlockPos abovePos = new BlockPos(pos).above();
		BlockState block = level.getBlockState(pos);
		BlockState blockAbove = level.getBlockState(abovePos);
		if (!block.isAir() && block.isRedstoneConductor(level, pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
			if (level.getRandom().nextFloat() <= chance) {
				JerotesFallingBlock fallingBlock = new JerotesFallingBlock(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, block, 10);
				fallingBlock.push(0, 0.2 + random.nextGaussian() * 0.2, 0);
				level.addFreshEntity(fallingBlock);
			}
		}
	}
	public static void spawnFallingBlockByPos(ServerLevel level, BlockPos pos) {
		spawnFallingBlockByPos(level, pos, 1);
	}
	//原版下落方块
	public static void spawnMinecraftFallingBlockByPos(ServerLevel level, BlockPos pos) {
		RandomSource random = RandomSource.create();
		BlockPos abovePos = new BlockPos(pos).above();
		BlockState block = level.getBlockState(pos);
		BlockState blockAbove = level.getBlockState(abovePos);
		if (!block.isAir() && block.isRedstoneConductor(level, pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
			FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), block);
			fallingBlock.dropItem = false;
			fallingBlock.push(0, 0.2 + random.nextGaussian() * 0.2, 0);
			level.addFreshEntity(fallingBlock);
		}
	}
	//拔地方块
	public static void spawnFallingBlockBySelf(ServerLevel serverLevel, LivingEntity caster, int maxDistance, float pushDistance, int pushAngle, int pushTick, float attackDamage) {
		AABB aabb = AABB.ofSize(new Vec3(caster.getX(), caster.getY() - 1, caster.getZ()), maxDistance*1.25f, maxDistance*1.25f, maxDistance*1.25f);
		for (BlockPos blockPos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
			if (caster.position().distanceTo(blockPos.getCenter()) > maxDistance) continue;
			BlockPos abovePos = new BlockPos(blockPos).above();
			BlockState block = serverLevel.getBlockState(blockPos);
			BlockState blockAbove = serverLevel.getBlockState(abovePos);
			if (!block.isAir() && block.isRedstoneConductor(serverLevel, blockPos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
				double d = (-Mth.sin(caster.getYRot() * 0.017453292F));
				double d2 = Mth.cos(caster.getYRot() * 0.017453292F);
				JerotesEarthrendBlock fallingBlock = new JerotesEarthrendBlock(serverLevel, (int) (blockPos.getX() + 0.5 + d * 3), blockPos.getY() + 1, (int) (blockPos.getZ() + 0.5 + d2 * 5), block, 20);
				fallingBlock.setOwner(caster);
				fallingBlock.push(0, 0.3 + Math.max(0, (fallingBlock.distanceTo(caster) / (maxDistance - maxDistance / 6f))) * 1.5f, 0);
				if (Main.canSeeAngle(caster, fallingBlock.getPosition(0.5f), pushAngle) && fallingBlock.distanceTo(caster) < pushDistance) {
					fallingBlock.setPushTick(pushTick);
					fallingBlock.setAttackDamage(attackDamage);
				}
				serverLevel.addFreshEntity(fallingBlock);
			}
		}
	}
	//凹凸方块
	public static void spawnUnevenBlock(ServerLevel level, BlockPos pos) {
		BlockPos abovePos = new BlockPos(pos).above();
		BlockState block = level.getBlockState(pos);
		BlockState blockAbove = level.getBlockState(abovePos);
		if (!block.isAir() && block.isRedstoneConductor(level, pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
			JerotesUnevenBlock fallingBlock = new JerotesUnevenBlock(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, block, 30);
			fallingBlock.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			level.addFreshEntity(fallingBlock);
		}
	}
	public static void spawnUnevenBlockByPos(ServerLevel level, BlockPos blockPos, int reach) {
		Vec3 startPos = blockPos.getCenter().add(0,8,0);
		AABB aabb = AABB.ofSize(blockPos.getCenter(), reach * 2 + 1, reach, reach * 2 + 1).move(0, Math.min(0, -(reach - 2)),0);
		for (BlockPos pos : BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ))) {
			double distance = blockPos.getCenter().distanceTo(pos.getCenter());
			if (distance <= reach) {
				BlockPos abovePos = new BlockPos(pos).above();
				BlockState block = level.getBlockState(pos);
				BlockState blockAbove = level.getBlockState(abovePos);
				if (!block.isAir() && block.isRedstoneConductor(level, pos) && !block.hasBlockEntity() && !blockAbove.blocksMotion()) {
					JerotesUnevenBlock fallingBlock = new JerotesUnevenBlock(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, block, 30);
					fallingBlock.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
					fallingBlock.setAddHeight(0.15f + Math.min(0.845f, (float) distance/ (Math.max(1f, reach)) * (Math.max(1f, reach)/6f)));
					fallingBlock.lookAt(startPos, 360f, 360f);

					int n = RandomSource.create().nextInt(20);
					fallingBlock.setStartTick(Math.max(0, (int)(distance/5f)));
					fallingBlock.setStopTick(Math.max(15, (int)(15+distance/5f) + n));
					fallingBlock.setDuration(Math.max(20, (int)(20+distance/5f) + (int)(n * (1 + RandomSource.create().nextFloat() * 0.5f))));
					level.addFreshEntity(fallingBlock);
				}
			}
		}
	}

	//判定位置
	public static Vec3 adjustPositionForSolidHit(BlockHitResult hitResult, Vec3 startPos, Vec3 viewVector, double maxDistance) {
		Vec3 adjustedPos = hitResult.getLocation();
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			Direction hitDirection = hitResult.getDirection();
			adjustedPos = adjustedPos.subtract(
					hitDirection.getStepX() * 0.1,
					hitDirection.getStepY() * 0.1,
					hitDirection.getStepZ() * 0.1
			);
		}
		double distance = startPos.distanceTo(adjustedPos);
		return distance > maxDistance ?
				startPos.add(viewVector.scale(maxDistance)) :
				adjustedPos;
	}
	public static BlockPos findSafePosition(ServerLevel level, Vec3 targetPos) {
		BlockPos.MutableBlockPos basePos = new BlockPos.MutableBlockPos(
				(int) targetPos.x,
				(int) targetPos.y,
				(int) targetPos.z
		);
		BlockPos solidPos = findSolidGround(level, basePos);
		if (solidPos != null) return solidPos;
		BlockPos fluidPos = findFluidPosition(level, basePos);
		return fluidPos;
	}
	public static BlockPos findSolidGround(ServerLevel level, BlockPos.MutableBlockPos pos) {
		int maxDownSearch = 10;
		BlockPos.MutableBlockPos currentPos = pos.mutable().move(Direction.DOWN);

		for (int i = 0; i < maxDownSearch; i++) {
			if (currentPos.getY() < level.getMinBuildHeight()) break;

			if (isSolidWalkable(level, currentPos)) {
				return currentPos.above().immutable();
			}
			currentPos.move(Direction.DOWN);
		}
		for (int r = 1; r <= 2; r++) {
			for (int x = -r; x <= r; x++) {
				for (int z = -r; z <= r; z++) {
					BlockPos checkPos = pos.offset(x, 0, z);
					if (isSolidWalkable(level, checkPos)) {
						return checkPos.above().immutable();
					}
				}
			}
		}
		return null;
	}
	public static boolean isSolidWalkable(ServerLevel level, BlockPos pos) {
		BlockState floor = level.getBlockState(pos);
		BlockState feet = level.getBlockState(pos.above());
		BlockState head = level.getBlockState(pos.above(2));
		return floor.blocksMotion() &&
				feet.isAir() &&
				head.isAir();
	}
	public static BlockPos findFluidPosition(ServerLevel level, BlockPos.MutableBlockPos pos) {
		int maxDownSearch = 5;
		BlockPos.MutableBlockPos currentPos = pos.mutable();

		for (int i = 0; i < maxDownSearch; i++) {
			BlockState state = level.getBlockState(currentPos);

			if (state.getFluidState().isEmpty()) {
				currentPos.move(Direction.DOWN);
				continue;
			}
			if (state.getFluidState().getAmount() >= 8 &&
					level.getBlockState(currentPos.above()).isAir()) {
				return currentPos.above().immutable();
			}

			currentPos.move(Direction.DOWN);
		}
		return null;
	}

	//计时
	public static void persistentDataDoubleReduceToZero(LivingEntity livingEntity, String string, boolean clear) {
		if (Main.getJerotesPersistentData(livingEntity).getDouble(string) > 0) {
			Main.getJerotesPersistentData(livingEntity).putDouble(string, Main.getJerotesPersistentData(livingEntity).getDouble(string) - 1);
		}
		else if (clear && Main.getJerotesPersistentData(livingEntity).get(string) != null) {
			Main.getJerotesPersistentData(livingEntity).remove(string);
		}
	}
	public static void persistentDataDoubleAddTo(LivingEntity livingEntity, String string, double d) {
		if (Main.getJerotesPersistentData(livingEntity).getDouble(string) < d) {
			Main.getJerotesPersistentData(livingEntity).putDouble(string, Main.getJerotesPersistentData(livingEntity).getDouble(string) + 1);
		}
	}
	public static void persistentDataRemove(LivingEntity livingEntity, String string) {
		if (Main.getJerotesPersistentData(livingEntity).get(string) != null) {
			Main.getJerotesPersistentData(livingEntity).remove(string);
		}
	}

	//不能移动
	public static boolean isCanNotMoveTag(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null) {
			return compoundTag.getBoolean("JerotesCanNotMove");
		}
		return false;
	}
	public static boolean isCanNotMove(ItemStack itemStack) {
		return itemStack.getEnchantmentLevel(Enchantments.BINDING_CURSE) > 0 || isCanNotMoveTag(itemStack);
	}

	//判定是否信任
	public static boolean isTrusted(LivingEntity entity, LivingEntity target, boolean onlyYou) {
		try {
			CompoundTag compoundTag = entity.saveWithoutId(new CompoundTag());
			if (!onlyYou && compoundTag.getBoolean("Trusting")) {
				return true;
			}
			if (compoundTag.contains("Trusted", Tag.TAG_LIST)) {
				ListTag trustedList = compoundTag.getList("Trusted", Tag.TAG_INT_ARRAY);
				UUID targetUuid = target.getUUID();
				for (Tag tag : trustedList) {
					if (tag == null || tag.getId() != Tag.TAG_INT_ARRAY) {
						continue;
					}
					try {
						UUID uuid = NbtUtils.loadUUID(tag);
						if (targetUuid.equals(uuid)) {
							return true;
						}
					} catch (Exception e) {
						continue;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}


	public static void spearInHandLayer(EntityModel<?> entityModel, LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
		if (livingEntity instanceof SpecialItemInHandEntity specialItemInHandEntity && specialItemInHandEntity.stopSpecialItemInHand(itemStack))
			return;
		if (itemStack.getItem() instanceof ItemSpecialInHand itemSpecialInHand) {
			itemSpecialInHand.specialInHandLayer(entityModel, livingEntity, itemStack, itemDisplayContext, humanoidArm, poseStack, multiBufferSource, n);
		}
	}

	public static void spearInHandLayerSpear(EntityModel<?> entityModel, LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
		if (itemStack.getItem() instanceof ItemToolBaseSpearBase itemToolBaseSpearBase && !itemToolBaseSpearBase.otherAnimSpear()) {
			if (entityModel.attackTime > 0.0 && livingEntity.getMainArm() == humanoidArm) {
				SpearAnimations.thirdPersonAttackItem(entityModel.attackTime, poseStack, livingEntity);
			}
			float f;
			if ((f = livingEntity.getTicksUsingItem()) != 0.0f) {
				if (humanoidArm == HumanoidArm.RIGHT &&
						(livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntity.getMainArm() == HumanoidArm.RIGHT || livingEntity.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntity.getMainArm() != HumanoidArm.RIGHT)
				) {
					SpearAnimations.thirdPersonUseItem(entityModel.attackTime, poseStack, f, humanoidArm, itemStack, livingEntity, Minecraft.getInstance().getPartialTick());
				}
				if (humanoidArm == HumanoidArm.LEFT &&
						(livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND && livingEntity.getMainArm() == HumanoidArm.LEFT || livingEntity.getUsedItemHand() == InteractionHand.OFF_HAND && livingEntity.getMainArm() != HumanoidArm.LEFT)
				) {
					SpearAnimations.thirdPersonUseItem(entityModel.attackTime, poseStack, f, humanoidArm, itemStack, livingEntity, Minecraft.getInstance().getPartialTick());
				}
			}
		}
	}
	public static void spearInHandLayerPike(EntityModel<?> entityModel, LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource multiBufferSource, int n) {
		if (itemStack.getItem() instanceof ItemToolBasePike) {
			if (entityModel.attackTime > 0.0 && livingEntity.getMainArm() == humanoidArm) {
				ItemToolBasePike.thirdPersonAttackItem(entityModel.attackTime, poseStack, livingEntity);
			}
		}
	}


	//附加手臂
	public static AddHandEntity spawnAddHand(LivingEntity owner, int tick, int size) {
		if (owner.level() instanceof ServerLevel serverLevel) {
			PlayerTeam teams = (PlayerTeam) owner.getTeam();
			AddHandEntity addHandEntity = JerotesEntityType.ADD_HAND.get().create(serverLevel, (CompoundTag)null, (Consumer<AddHandEntity>)null, BlockPos.containing(owner.getX(), owner.getY(), owner.getZ()), MobSpawnType.MOB_SUMMONED, false, false);
			if (addHandEntity != null) {
				addHandEntity.setOwner(owner);
				if (!addHandEntity.level().isClientSide()) {
					addHandEntity.setSize(size);
				}
				addHandEntity.refreshDimensions();
				addHandEntity.setMaxLife(tick);

				addHandEntity.setLeftHanded(owner.getMainArm() == HumanoidArm.LEFT);

				addHandEntity.setPos(owner.getX(), owner.getY(), owner.getZ());
				addHandEntity.setRotSelf(owner.getYRot(), owner.getXRot());
				addHandEntity.setYRot(owner.getYRot());
				addHandEntity.setXRot(owner.getXRot());
				addHandEntity.setYHeadRot(owner.getYHeadRot());
				addHandEntity.setYBodyRot(owner.yBodyRot);

				addHandEntity.xOld = owner.xOld;
				addHandEntity.xo = owner.xo;
				addHandEntity.yOld = owner.yOld;
				addHandEntity.yo = owner.yo;
				addHandEntity.zOld = owner.zOld;
				addHandEntity.zo = owner.zo;
				addHandEntity.xRotO = owner.xRotO;
				addHandEntity.yRotO = owner.yRotO;
				addHandEntity.yHeadRotO = owner.yHeadRotO;
				addHandEntity.yBodyRotO = owner.yBodyRotO;

				addHandEntity.yRotO = addHandEntity.yBodyRot = addHandEntity.yHeadRot = addHandEntity.getYRot();

				addHandEntity.hasImpulse = true;

				addHandEntity.setDeltaMovement(owner.getDeltaMovement());

				if (teams != null) {
					serverLevel.getScoreboard().addPlayerToTeam(addHandEntity.getStringUUID(), teams);
				}
				serverLevel.addFreshEntity(addHandEntity);
			}
			return addHandEntity;
		}
		return null;
	}


	//Curios
	public static ItemStack findCurios(LivingEntity livingEntity, Item item) {
		if (livingEntity == null || !ModList.get().isLoaded("curios")) {
			return ItemStack.EMPTY;
		}
		try {
			Class<?> curiosApiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
			Method getCuriosInventory = curiosApiClass.getMethod("getCuriosInventory", LivingEntity.class);
			Object lazyOptional = getCuriosInventory.invoke(null, livingEntity);

			Method resolve = lazyOptional.getClass().getMethod("resolve");
			Optional<?> handlerOptional = (Optional<?>) resolve.invoke(lazyOptional);

			if (handlerOptional.isPresent()) {
				Object handler = handlerOptional.get();
				Method findFirstCurio = handler.getClass().getMethod("findFirstCurio", Item.class);
				Optional<?> slotResultOptional = (Optional<?>) findFirstCurio.invoke(handler, item);

				if (slotResultOptional.isPresent()) {
					Object slotResult = slotResultOptional.get();
					Method stackMethod = slotResult.getClass().getMethod("stack");
					return (ItemStack) stackMethod.invoke(slotResult);
				}
			}
		} catch (Exception e) {
		}
		return ItemStack.EMPTY;
	}
	public static int findCuriosCount(LivingEntity livingEntity, Item item) {
		if (livingEntity == null || !ModList.get().isLoaded("curios")) {
			return 0;
		}
		try {
			Class<?> curiosApiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
			Method getCuriosInventory = curiosApiClass.getMethod("getCuriosInventory", LivingEntity.class);
			Object lazyOptional = getCuriosInventory.invoke(null, livingEntity);

			Method resolve = lazyOptional.getClass().getMethod("resolve");
			Optional<?> handlerOptional = (Optional<?>) resolve.invoke(lazyOptional);

			if (handlerOptional.isPresent()) {
				Object handler = handlerOptional.get();
				Method findCurios = handler.getClass().getMethod("findCurios", Item.class);
				List<?> slotResults = (List<?>) findCurios.invoke(handler, item);
				return slotResults.size();
			}
		} catch (Exception e) {
		}
		return 0;
	}
	public static boolean hasCurios(LivingEntity livingEntity, Item item) {
		return !findCurios(livingEntity, item).isEmpty();
	}
}