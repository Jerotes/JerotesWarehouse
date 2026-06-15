package com.jerotes.jerotes.util;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.CanBeIllagerFactionEntity;
import com.jerotes.jerotes.entity.Interface.CanBeSerponFactionEntity;
import com.jerotes.jerotes.entity.Interface.JerotesEntity;
import com.jerotes.jerotes.forge.JerotesFactionEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityFactionFind {
	public static boolean isThisEntity(EntityType type, String string) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(string)));
	}

	//选择类人
	public static boolean isHumanoid(LivingEntity hummanoid) {
		return     (hummanoid instanceof Player
				|| hummanoid instanceof AbstractVillager
				|| hummanoid instanceof AbstractIllager
				|| hummanoid instanceof AbstractPiglin
				|| hummanoid instanceof Witch
				|| hummanoid instanceof EnderMan
				|| isHumanoid(hummanoid.getType()) && hummanoid.getMobType() != MobType.UNDEAD);
	}
	//选择龙
	public static boolean isDragon(LivingEntity dragon) {
		return     (dragon instanceof EnderDragon
				|| isDragon(dragon.getType()) && dragon.getMobType() != MobType.UNDEAD);
	}
	//选择真龙
	public static boolean isTrueDragon(LivingEntity trueDragon) {
		return     (trueDragon instanceof EnderDragon
				|| isTrueDragon(trueDragon.getType()) && trueDragon.getMobType() != MobType.UNDEAD);
	}
	//选择精类
	public static boolean isFey(LivingEntity fey) {
		return     (fey instanceof Allay
				|| fey instanceof Vex
				|| isFey(fey.getType()) && fey.getMobType() != MobType.UNDEAD);
	}
	//选择泥怪
	public static boolean isOoze(LivingEntity ooze) {
		return     (ooze instanceof Slime
				|| isOoze(ooze.getType()) && ooze.getMobType() != MobType.UNDEAD);
	}
	//选择植物
	public static boolean isPlant(LivingEntity plant) {
		return     (isPlant(plant.getType()) && plant.getMobType() != MobType.UNDEAD);
	}
	//选择元素
	public static boolean isElemental(LivingEntity elemental) {
		return     (elemental instanceof Blaze
				|| isElemental(elemental.getType()) && elemental.getMobType() != MobType.UNDEAD);
	}
	//选择异怪
	public static boolean isAberration(LivingEntity aberration) {
		return     (isAberration(aberration.getType()) && aberration.getMobType() != MobType.UNDEAD);
	}
	//选择天族
	public static boolean isCelestial(LivingEntity celestial) {
		return     (isCelestial(celestial.getType()) && celestial.getMobType() != MobType.UNDEAD);
	}
	//选择邪魔
	public static boolean isFiend(LivingEntity fiend) {
		return     (isFiend(fiend.getType()) && fiend.getMobType() != MobType.UNDEAD);
	}
	//选择机械
	public static boolean isMachine(LivingEntity livingEntity) {
		return    isMachine(livingEntity.getType());
	}
	//选择傀儡
	public static boolean isConstruct(LivingEntity livingEntity) {
		return    livingEntity instanceof AbstractGolem
				|| isConstruct(livingEntity.getType());
	}

	//是否类人
	public static boolean isHumanoid(EntityType hummanoid) {
		return hummanoid.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "humanoid")));
	}
	//是否精类
	public static boolean isFey(EntityType fey) {
		return fey.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "fey")));
	}
	//是否泥怪
	public static boolean isOoze(EntityType ooze) {
		return ooze.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "ooze")));
	}
	//是否植物
	public static boolean isPlant(EntityType plant) {
		return plant.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "plant")));
	}
	//是否元素
	public static boolean isElemental(EntityType elemental) {
		return elemental.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "elemental")));
	}
	//是否异怪
	public static boolean isAberration(EntityType aberration) {
		return aberration.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "aberration")));
	}
	//是否天族
	public static boolean isCelestial(EntityType celestial) {
		return celestial.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "celestial")));
	}
	//是否邪魔
	public static boolean isFiend(EntityType fiend) {
		return fiend.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "fiend")));
	}
	//是否机械
	public static boolean isMachine(EntityType machine) {
		return machine.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "machine")));
	}
	//是否龙类
	public static boolean isDragon(EntityType dragon) {
		return dragon.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "dragon")));
	}
	//是否真龙
	public static boolean isTrueDragon(EntityType trueDragon) {
		return trueDragon.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "true_dragon")));
	}
	//是否傀儡
	public static boolean isConstruct(EntityType construct) {
		return construct.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "construct")));
	}

	//紫沙姐妹会认同
	public static boolean isPurpleSandSisterhoodIdentifyWith(LivingEntity entity) {
		if (EntityFactionFind.getTrueFaction(entity).equals("purple_sand_sisterhood"))
			return true;
		boolean helmet = entity != null && entity.getItemBySlot(EquipmentSlot.HEAD).is(ItemTags.create(new ResourceLocation("jerotes:purple_sand_sisterhood_clothes")));
		boolean chestplate = entity != null && entity.getItemBySlot(EquipmentSlot.CHEST).is(ItemTags.create(new ResourceLocation("jerotes:purple_sand_sisterhood_clothes")));
		boolean legs = entity != null && entity.getItemBySlot(EquipmentSlot.LEGS).is(ItemTags.create(new ResourceLocation("jerotes:purple_sand_sisterhood_clothes")));
		boolean feet = entity != null && entity.getItemBySlot(EquipmentSlot.FEET).is(ItemTags.create(new ResourceLocation("jerotes:purple_sand_sisterhood_clothes")));
		return entity != null && !(helmet || chestplate || legs || feet);
	}

	//袭击
	public static boolean isRaider(LivingEntity entity) {
		return entity instanceof Raider || isIllagerFaction(entity);
	}
	//灾厄村民阵营
	public static boolean isIllagerFaction(LivingEntity entity) {
		return entity.getMobType() == MobType.ILLAGER
				|| entity.getType().is(EntityTypeTags.RAIDERS)
				|| entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("minecraft:illager")))
				|| entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("minecraft:illager_friends")))
				|| entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "illager")))
				|| (entity instanceof CanBeIllagerFactionEntity canBeIllagerFactionEntity && canBeIllagerFactionEntity.isIllagerFaction());
	}
	//猪灵
	public static boolean isPiglin(LivingEntity livingEntity) {
		return livingEntity instanceof AbstractPiglin
				|| livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "piglin")));
	}
	//凋灵骷髅
	public static boolean isWitherSkeleton(LivingEntity livingEntity) {
		return livingEntity instanceof WitherSkeleton
				|| livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "wither_skeleton")));
	}
	//凋灵类
	public static boolean isWither(LivingEntity livingEntity) {
		return livingEntity instanceof WitherBoss
				|| livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "wither")));
	}
	//末影人
	public static boolean isEnderman(LivingEntity livingEntity) {
		return livingEntity instanceof EnderMan
				|| livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "enderman")));
	}
	//村民
	public static boolean isVillager(LivingEntity livingEntity) {
		return livingEntity instanceof AbstractVillager
				|| livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "villager")));
	}
	//灾厄村民
	public static boolean isIllager(LivingEntity livingEntity) {
		return livingEntity instanceof AbstractIllager
				|| livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("minecraft:illager")))
				|| livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "illager")));
	}
	//女巫
	public static boolean isWitch(LivingEntity livingEntity) {
		return livingEntity instanceof Witch
				|| livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "witch")));
	}

	//是同阵营
	public static boolean isFaction(LivingEntity livingEntity, LivingEntity livingEntity2) {
		if (livingEntity == null || livingEntity2 == null) {
			return false;
		}
		//event
		JerotesFactionEvent event = new JerotesFactionEvent(livingEntity, livingEntity2);
		MinecraftForge.EVENT_BUS.post(event);
		return event.isFriend();
	}
	//是敌对阵营
	public static boolean isHateFaction(LivingEntity livingEntity, LivingEntity livingEntity2) {
		if (livingEntity == null || livingEntity2 == null) {
			return false;
		}
		//event
		JerotesFactionEvent event = new JerotesFactionEvent(livingEntity, livingEntity2);
		MinecraftForge.EVENT_BUS.post(event);
		return event.isEnemy();
	}
	//
	public static String getTrueFaction(LivingEntity livingEntity) {
		if (livingEntity == null) {
			return "";
		}
		String base = "";
		if (AttackFind.getOwnerTrue(livingEntity) != null) {
			return getTrueFaction(AttackFind.getOwnerTrue(livingEntity));
		}
		if (!Main.getJerotesPersistentData(livingEntity).getString("jerotes_mob_faction").isEmpty()) {
			return Main.getJerotesPersistentData(livingEntity).getString("jerotes_mob_faction");
		}
		if (livingEntity instanceof JerotesEntity jerotes) {
			return jerotes.getFactionTypeName();
		}
		return base;
	}
	public static String getTrueMobTypeNameModId(LivingEntity livingEntity) {
		if (livingEntity == null) {
			return "";
		}
		String base = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(livingEntity.getType())).getNamespace();
		if (AttackFind.getOwnerTrue(livingEntity) != null) {
			return getTrueMobTypeNameModId(AttackFind.getOwnerTrue(livingEntity));
		}
		if (!Main.getJerotesPersistentData(livingEntity).getString("jerotes_mob_faction_mod_id").isEmpty()) {
			return Main.getJerotesPersistentData(livingEntity).getString("jerotes_mob_faction_mod_id");
		}
		if (livingEntity instanceof JerotesEntity jerotes && !jerotes.getMobTypeNameModId().isEmpty()) {
			return jerotes.getMobTypeNameModId();
		}
		return base;
	}

	//是否铜刻类型生物
	public static boolean isCarved(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "carved")));
	}
	//是否蛇龙阵营
	public static boolean isSerponFaction(EntityType type) {
		return type.is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "serpon_faction")));
	}
	//蛇龙战斗组 Serpon Combat Team
	public static boolean isFactionSerponCombatTeam(LivingEntity livingEntity) {
		return EntityFactionFind.isSerponFaction(livingEntity.getType()) && !(livingEntity instanceof CanBeSerponFactionEntity canBeSerponFaction && canBeSerponFaction.isNotSerpon())
				|| EntityFactionFind.getTrueFaction(livingEntity).equals("serpon_combat_team");
	}

//	public static boolean isFaction(LivingEntity livingEntity, LivingEntity livingEntity2) {
//		return isFactionCopperCarvedCompany(livingEntity) && isFactionCopperCarvedCompany(livingEntity2)
//				|| isFactionWickedRidiculeCalls(livingEntity) && isFactionWickedRidiculeCalls(livingEntity2)
//				|| isFactionEnderStowawayCollective(livingEntity) && isFactionEnderStowawayCollective(livingEntity2)
//				|| isFactionPurpleSandSisterhood(livingEntity) && isFactionPurpleSandSisterhood(livingEntity2)
//				|| isFactionSedimentResurrecter(livingEntity) && isFactionSedimentResurrecter(livingEntity2)
//				|| isFactionOminousBannerRaidForce(livingEntity) && isFactionOminousBannerRaidForce(livingEntity2)
//				|| isFactionPiglinResidentDetachment(livingEntity) && isFactionPiglinResidentDetachment(livingEntity2)
//				|| isFactionMerorMachine(livingEntity) && isFactionMerorMachine(livingEntity2)
//				|| isFactionZsiein(livingEntity) && isFactionZsiein(livingEntity2)
//				|| isFactionMerorCivilization(livingEntity) && isFactionMerorCivilization(livingEntity2)
//				|| isFactionWildernessGiantTribes(livingEntity) && isFactionWildernessGiantTribes(livingEntity2)
//				|| isFactionAncientMerorCivilization(livingEntity) && isFactionAncientMerorCivilization(livingEntity2)
//				|| isFactionChibaoLegion(livingEntity) && isFactionChibaoLegion(livingEntity2)
//				|| isFactionQoaikuAlliance(livingEntity) && isFactionQoaikuAlliance(livingEntity2)
//				|| isFactionQoaikuPirates(livingEntity) && isFactionQoaikuPirates(livingEntity2)
//				|| isFactionBotanizeVillage(livingEntity) && isFactionBotanizeVillage(livingEntity2)
//				|| isFactionWonderlinCaves(livingEntity) && isFactionWonderlinCaves(livingEntity2)
//				|| isFactionSerponCombatTeam(livingEntity) && isFactionSerponCombatTeam(livingEntity2)
//				|| isFactionWickedRidiculeConclave(livingEntity) && isFactionWickedRidiculeConclave(livingEntity2)
//				|| isRaider(livingEntity) && isRaider(livingEntity2)
//				|| isPiglin(livingEntity) && isPiglin(livingEntity2);
//	}



	public static String getFindFaction(LivingEntity livingEntity) {
		//末影 need
		if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_true_dragon")) {
			return "true_dragon";
		}
		//末影 need
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_ender")) {
			return "ender";
		}
		//幽匿 need
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_sculk")) {
			return "sculk";
		}
		//猪灵
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_piglin")) {
			return "piglin";
		}
		//亡灵
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_undead")) {
			return "undead";
		}
		//灾厄村民
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_illager")) {
			return "illager";
		}
		//村民
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_villager")) {
			return "villager";
		}
		//女巫
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_witch")) {
			return "witch";
		}
		//袭击者
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_raider")) {
			return "raider";
		}
		//泥怪
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_ooze")) {
			return "ooze";
		}
		//植物
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_plant")) {
			return "plant";
		}
		//精类
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_fey")) {
			return "fey";
		}
		//元素
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_elemental")) {
			return "elemental";
		}
		//异怪
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/aberration")) {
			return "aberration";
		}
		//天族
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_celestial")) {
			return "celestial";
		}
		//邪魔
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_fiend")) {
			return "fiend";
		}
		//类人
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_humanoid")) {
			return "humanoid";
		}
		//龙类
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_dragon")) {
			return "dragon";
		}
		//节肢
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_arthropod")) {
			return "arthropod";
		}
		//水生动物
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_water_animal")) {
			return "water_animal";
		}
		//动物
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_animal")) {
			return "animal";
		}
		//傀儡
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_construct")) {
			return "construct";
		}
		//机械
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_machine")) {
			return "machine";
		}
		//怪物
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_monster")) {
			return "monster";
		}
		//水生
		else if (EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_water")) {
			return "water";
		}

		//真龙
		else if (EntityFactionFind.isTrueDragon(livingEntity)) {
			return "true_dragon";
		}
		//末影
		else if (isEnderman(livingEntity) || livingEntity instanceof EnderMan || livingEntity instanceof EnderDragon || livingEntity instanceof Endermite || livingEntity instanceof Shulker) {
			return "ender";
		}
		//幽匿
		else if (livingEntity instanceof Warden) {
			return "sculk";
		}
		//猪灵
		else if (isPiglin(livingEntity) || livingEntity instanceof AbstractPiglin) {
			return "piglin";
		}
		//亡灵
		else if (livingEntity.getMobType() == MobType.UNDEAD) {
			return "undead";
		}
		//灾厄村民
		else if (isIllager(livingEntity) || livingEntity instanceof AbstractIllager) {
			return "illager";
		}
		//村民
		else if (isVillager(livingEntity) || livingEntity instanceof AbstractVillager) {
			return "villager";
		}
		//女巫
		else if (isWitch(livingEntity) || livingEntity instanceof Witch) {
			return "witch";
		}
		//袭击者
		else if (isRaider(livingEntity) || livingEntity instanceof Raider) {
			return "raider";
		}
		//泥怪
		else if (EntityFactionFind.isOoze(livingEntity)) {
			return "ooze";
		}
		//植物
		else if (EntityFactionFind.isPlant(livingEntity)) {
			return "plant";
		}
		//精类
		else if (EntityFactionFind.isFey(livingEntity)) {
			return "fey";
		}
		//元素
		else if (EntityFactionFind.isElemental(livingEntity)) {
			return "elemental";
		}
		//异怪
		else if (EntityFactionFind.isAberration(livingEntity)) {
			return "aberration";
		}
		//天族
		else if (EntityFactionFind.isCelestial(livingEntity)) {
			return "celestial";
		}
		//邪魔
		else if (EntityFactionFind.isFiend(livingEntity)) {
			return "fiend";
		}
		//类人
		else if (EntityFactionFind.isHumanoid(livingEntity)) {
			return "humanoid";
		}
		//龙类
		else if (EntityFactionFind.isDragon(livingEntity)) {
			return "dragon";
		}
		//节肢
		else if (livingEntity.getMobType() == MobType.ARTHROPOD || livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("minecraft:arthropod")))) {
			return "arthropod";
		}
		//水生动物
		else if (livingEntity instanceof WaterAnimal || livingEntity instanceof Animal && (livingEntity.getMobType() == MobType.WATER)) {
			return "water_animal";
		}
		//动物
		else if (livingEntity instanceof Animal) {
			return "animal";
		}
		//傀儡
		else if (isConstruct(livingEntity)) {
			return "construct";
		}
		//机械
		else if (EntityFactionFind.isMachine(livingEntity)) {
			return "machine";
		}
		//怪物
		else if (livingEntity instanceof Monster) {
			return "monster";
		}
		//水生
		else if (livingEntity.getMobType() == MobType.WATER) {
			return "water";
		}
		return "other";
	}



	public static List<String> getAllFindFaction(LivingEntity livingEntity) {
		return getAllFindFaction(livingEntity, false);
	}

	public static List<String> getAllFindFaction(LivingEntity livingEntity, boolean bl) {
		List<String> stringList = new ArrayList<>();
		//真龙
		if (bl || EntityFactionFind.isTrueDragon(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_true_dragon")) {
			stringList.add("true_dragon");
		}
		//末影
		if (bl || isEnderman(livingEntity) || livingEntity instanceof EnderMan || livingEntity instanceof EnderDragon || livingEntity instanceof Endermite || livingEntity instanceof Shulker
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_ender")) {
			stringList.add("ender");
		}
		//幽匿
		if (bl || livingEntity instanceof Warden
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_sculk")) {
			stringList.add("sculk");
		}
		//猪灵
		if (bl || isPiglin(livingEntity) || livingEntity instanceof AbstractPiglin
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_piglin")) {
			stringList.add("piglin");
		}
		//亡灵
		if (bl || livingEntity.getMobType() == MobType.UNDEAD || EntityFactionFind.isThisEntity(livingEntity.getType(), "minecraft:undead")
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_undead")) {
			stringList.add("undead");
		}
		//灾厄村民
		if (bl || isIllager(livingEntity) || livingEntity instanceof AbstractIllager
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_illager")) {
			stringList.add("illager");
		}
		//村民
		if (bl || isVillager(livingEntity) || livingEntity instanceof AbstractVillager
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_villager")) {
			stringList.add("villager");
		}
		//女巫
		if (bl || isWitch(livingEntity) || livingEntity instanceof Witch
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_witch")) {
			stringList.add("witch");
		}
		//袭击者
		if (bl || isRaider(livingEntity) || livingEntity instanceof Raider
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_raider")) {
			stringList.add("raider");
		}
		//类人
		if (bl || EntityFactionFind.isHumanoid(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_humanoid")) {
			stringList.add("humanoid");
		}
		//泥怪
		if (bl || EntityFactionFind.isOoze(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_ooze")) {
			stringList.add("ooze");
		}
		//泥怪
		if (bl || EntityFactionFind.isPlant(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_plant")) {
			stringList.add("plant");
		}
		//精类
		if (bl || EntityFactionFind.isFey(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_fey")) {
			stringList.add("fey");
		}
		//元素
		if (bl || EntityFactionFind.isElemental(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_elemental")) {
			stringList.add("elemental");
		}
		//异怪
		if (bl || EntityFactionFind.isAberration(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_aberration")) {
			stringList.add("aberration");
		}
		//天族
		if (bl || EntityFactionFind.isCelestial(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_celestial")) {
			stringList.add("celestial");
		}
		//邪魔
		if (bl || EntityFactionFind.isFiend(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_fiend")) {
			stringList.add("fiend");
		}
		//龙类
		if (bl || EntityFactionFind.isDragon(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_dragon")) {
			stringList.add("dragon");
		}
		//节肢
		if (bl || livingEntity.getMobType() == MobType.ARTHROPOD || livingEntity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("minecraft:arthropod")))
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_arthropod")) {
			stringList.add("arthropod");
		}
		//水生动物
		if (bl || livingEntity instanceof WaterAnimal || livingEntity instanceof Animal && (livingEntity.getMobType() == MobType.WATER)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_water_animal")) {
			stringList.add("water_animal");
		}
		//动物
		if (bl || livingEntity instanceof Animal
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_animal")) {
			stringList.add("animal");
		}
		//傀儡
		if (bl || isConstruct(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_construct")) {
			stringList.add("construct");
		}
		//机械
		if (bl || EntityFactionFind.isMachine(livingEntity)
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_machine")) {
			stringList.add("machine");
		}
		//怪物
		if (bl || livingEntity instanceof Monster
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_monster")) {
			stringList.add("monster");
		}
		//水生
		if (bl || livingEntity.getMobType() == MobType.WATER
				|| EntityFactionFind.isThisEntity(livingEntity.getType(), "jerotes:used_faction/stress_water")) {
			stringList.add("water");
		}
		if (bl || stringList.isEmpty()) {
			stringList.add("other");
		}
		return stringList;
	}
}