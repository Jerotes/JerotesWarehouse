package com.jerotes.jerotes.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

public class SpawnRules {
	//生成
	//Base
	public static boolean checkSurfaceWaterSpawnRules(EntityType<? extends Mob> mob, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		int i = levelAccessor.getSeaLevel();
		int j = i - 13;
		return blockPos.getY() >= j && blockPos.getY() <= i && levelAccessor.getFluidState(blockPos.below()).is(FluidTags.WATER) && levelAccessor.getBlockState(blockPos.above()).is(Blocks.WATER);
	}
	//正常
	public static boolean NeutralSpawn(int num, double area, EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && Mob.checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//蝙蝠
	public static boolean BatSpawn(int num, double area, EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && checkBatSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	public static boolean checkBatSpawnRules(EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		if (blockPos.getY() >= levelAccessor.getSeaLevel()) {
			return false;
		}
		int n = levelAccessor.getMaxLocalRawBrightness(blockPos);
		int n2 = 4;
		if (isHalloween() ) {
			n2 = 7;
		} else if (randomSource.nextBoolean()) {
			return false;
		}
		if (n > randomSource.nextInt(n2)) {
			return false;
		}
		return Bat.checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	public static boolean isHalloween() {
		LocalDate $$0 = LocalDate.now();
		int $$1 = $$0.get(ChronoField.DAY_OF_MONTH);
		int $$2 = $$0.get(ChronoField.MONTH_OF_YEAR);
		return $$2 == 10 && $$1 >= 20 || $$2 == 11 && $$1 <= 3;
	}
	//铜刻类型生物
	public static boolean CarvedSpawn(int num, double area, EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && Mob.checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//动物
	public static boolean AnimalSpawn(int num, double area, EntityType<? extends Animal> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && Animal.checkAnimalSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//水生动物
	public static boolean WaterAnimalSpawn(int num, double area, EntityType<? extends WaterAnimal> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//水生生物
	public static boolean WaterMobSpawn(int num, double area, EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && checkSurfaceWaterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//黑暗怪物
	public static boolean DarkMonsterSpawn(int num, double area, EntityType<? extends Monster> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<LivingEntity> list = serverLevelAccessor.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && Monster.checkMonsterSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//暗乱
	public static boolean DarisorderSpawn(int num, double area, EntityType<? extends Monster> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<LivingEntity> list = serverLevelAccessor.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && Monster.checkMonsterSpawnRules(entityType, serverLevelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//发光鱿鱼
	public static boolean GlowSquidSpawn(int num, double area, EntityType<? extends Mob> entityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<LivingEntity> list = serverLevelAccessor.getEntitiesOfClass(LivingEntity.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && blockPos.getY() <= serverLevelAccessor.getSeaLevel() - 33 && serverLevelAccessor.getRawBrightness(blockPos, 0) == 0 && serverLevelAccessor.getBlockState(blockPos).is(Blocks.WATER);
	}
	//阳光怪物
	public static boolean LightMonsterSpawn(int num, double area, EntityType<? extends Monster> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && Monster.checkAnyLightMonsterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//NPC
	public static boolean NPCSpawn(int num, double area, EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && Mob.checkMobSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}
	//水生生物
	public static boolean WaterNPCSpawn(int num, double area, EntityType<? extends Mob> entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
		List<Mob> list = levelAccessor.getEntitiesOfClass(Mob.class, AABB.ofSize(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), area, area, area));
		list.removeIf(entity -> entity.getType() != entityType);
		return list.size() < num && checkSurfaceWaterSpawnRules(entityType, levelAccessor, mobSpawnType, blockPos, randomSource);
	}

}