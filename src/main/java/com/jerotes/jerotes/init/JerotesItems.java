package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.ArmorEntity;
import com.jerotes.jerotes.entity.arrow.AnestheticArrowEntity;
import com.jerotes.jerotes.item.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class JerotesItems implements JerotesItemsAdd {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, JerotesWarehouse.MODID);
	public static final RegistryObject<Item> AA_CREATIVE_CLAW = REGISTRY.register("aa_creative_claw", () -> new AACreativeClaw());
	public static final RegistryObject<Item> AA_EXPLORATION_EYE = REGISTRY.register("aa_exploration_eye", () -> new AAExplorationEye());
	public static final RegistryObject<Item> AA_SPELL_JAVELIN = REGISTRY.register("aa_spell_javelin", () -> new AASpellJavelin());
	public static final RegistryObject<Item> TEST_SPAWN_EGG = REGISTRY.register("test_spawn_egg", () -> new ForgeSpawnEggItem(JerotesEntityType.TEST, 0xffffff, 0xffffff, new Item.Properties()));
	public static final RegistryObject<Item> HUMAN_SPAWN_EGG = REGISTRY.register("human_spawn_egg", () -> new ForgeSpawnEggItem(JerotesEntityType.HUMAN, 0xffffff, 0xffffff, new Item.Properties()));
	public static final RegistryObject<Item> JEROTES_PLAYER_SPAWN_EGG = REGISTRY.register("jerotes_player_spawn_egg", () -> new JerotesPlayerSpawnEgg());

	public static final RegistryObject<Item> HIGH_STRENGTH_STRING = REGISTRY.register("high_strength_string", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> RESILIENT_BANDAGE = REGISTRY.register("resilient_bandage", () -> new ResilientBandage());
	public static final RegistryObject<Item> ANESTHETIC_BANDAGE = REGISTRY.register("anesthetic_bandage", () -> new AnestheticBandage());
	public static final RegistryObject<Item> ANESTHETIC_ARROW = REGISTRY.register("anesthetic_arrow", () -> new AnestheticArrow());

	public static final RegistryObject<Item> SIMPLE_JAVELIN = REGISTRY.register("simple_javelin", () -> new SimpleJavelin());
	public static final RegistryObject<Item> HEAL_JAVELIN = REGISTRY.register("heal_javelin", () -> new HealJavelin());
	public static final RegistryObject<Item> ANESTHETIC_JAVELIN = REGISTRY.register("anesthetic_javelin", () -> new AnestheticJavelin());
	public static final RegistryObject<Item> TRANSPORT_JAVELIN = REGISTRY.register("transport_javelin", () -> new TransportJavelin());
	public static final RegistryObject<Item> EXPLOSIVE_JAVELIN = REGISTRY.register("explosive_javelin", () -> new ExplosiveJavelin());
	public static final RegistryObject<Item> WOODEN_JAVELIN = REGISTRY.register("wooden_javelin", () -> new JavelinWooden());
	public static final RegistryObject<Item> STONE_JAVELIN = REGISTRY.register("stone_javelin", () -> new JavelinStone());
	public static final RegistryObject<Item> COPPER_JAVELIN = REGISTRY.register("copper_javelin", () -> new JavelinCopper());
	public static final RegistryObject<Item> IRON_JAVELIN = REGISTRY.register("iron_javelin", () -> new JavelinIron());
	public static final RegistryObject<Item> GOLDEN_JAVELIN = REGISTRY.register("golden_javelin", () -> new JavelinGolden());
	public static final RegistryObject<Item> DIAMOND_JAVELIN = REGISTRY.register("diamond_javelin", () -> new JavelinDiamond());
	public static final RegistryObject<Item> NETHERITE_JAVELIN = REGISTRY.register("netherite_javelin", () -> new JavelinNetherite());
	//动画时间
	//伤害倍率
	//攻击所需抬起时间
	public static final RegistryObject<Item> WOODEN_SPEAR = REGISTRY.register("wooden_spear", () -> new BaseSpear(Tiers.WOOD, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON),
			0.65f, 0.7f, 0.75f, 5.0f, 14.0f, 10.0f, 5.1f, 15.0f, 4.6f, true));
	public static final RegistryObject<Item> STONE_SPEAR = REGISTRY.register("stone_spear", () -> new BaseSpear(Tiers.STONE, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON),
			0.75f, 0.82f, 0.7f, 4.5f, 10.0f, 9.0f, 5.1f, 13.75f, 4.6f, false));
	public static final RegistryObject<Item> COPPER_SPEAR = REGISTRY.register("copper_spear", () -> new BaseSpear(JerotesTiers.COPPER, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON),
			0.85f, 0.82f, 0.65f, 4.0f, 9.0f, 8.25f, 5.1f, 12.5f, 4.6f, false));
	public static final RegistryObject<Item> IRON_SPEAR = REGISTRY.register("iron_spear", () -> new BaseSpear(Tiers.IRON, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON),
			0.95f, 0.95f, 0.6f, 2.5f, 8.0f, 6.75f, 5.1f, 11.25f, 4.6f, false));
	public static final RegistryObject<Item> GOLDEN_SPEAR = REGISTRY.register("golden_spear", () -> new BaseSpear(Tiers.GOLD, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON),
			0.95f, 0.7f, 0.7f, 3.5f, 10.0f, 8.5f, 5.1f, 13.75f, 4.6f, false));
	public static final RegistryObject<Item> DIAMOND_SPEAR = REGISTRY.register("diamond_spear", () -> new BaseSpear(Tiers.DIAMOND, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON),
			1.05f, 1.075f, 0.5f, 3.0f, 7.5f, 6.5f, 5.1f, 10.0f, 4.6f, false));
	public static final RegistryObject<Item> NETHERITE_SPEAR = REGISTRY.register("netherite_spear", () -> new BaseSpear(Tiers.NETHERITE, new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).fireResistant(),
			1.15f, 1.2f, 0.4f, 2.5f, 7.0f, 5.5f, 5.1f, 8.75f, 4.6f, false));

	public static final RegistryObject<Item> BONE_THROWING_SPEAR_OF_SPEAR = REGISTRY.register("bone_throwing_spear_of_spear", () -> new BoneThrowingSpear.BoneThrowingSpearOfSpear());
	public static final RegistryObject<Item> BONE_THROWING_SPEAR_OF_JAVELIN = REGISTRY.register("bone_throwing_spear_of_javelin", () -> new BoneThrowingSpear.BoneThrowingSpearOfJavelin());

	public static final RegistryObject<Item> IRON_PIKE = REGISTRY.register("iron_pike", () -> new IronPike());

	public static final RegistryObject<Item> BEAST_ARMOR = REGISTRY.register("beast_armor_base", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> WAR_BEAST_ARMOR_BASE = REGISTRY.register("war_beast_armor_base", () -> new ItemWarBeastArmor(0, 0xffffff, JerotesWarehouse.MODID, "base", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> LEATHER_WAR_BEAST_ARMOR = REGISTRY.register("leather_war_beast_armor", () -> new ItemWarBeastArmorDyeable(5, 0x804a15, JerotesWarehouse.MODID, "leather", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> CHAINMAIL_WAR_BEAST_ARMOR = REGISTRY.register("chainmail_war_beast_armor", () -> new ItemWarBeastArmor(9, 0x818181, JerotesWarehouse.MODID, "chainmail", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> COPPER_WAR_BEAST_ARMOR = REGISTRY.register("copper_war_beast_armor", () -> new ItemWarBeastArmor(7, 0xd66d48, JerotesWarehouse.MODID, "copper", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> IRON_WAR_BEAST_ARMOR = REGISTRY.register("iron_war_beast_armor", () -> new ItemWarBeastArmor(11, 0x8c8c8c, JerotesWarehouse.MODID, "iron", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> GOLDEN_WAR_BEAST_ARMOR = REGISTRY.register("golden_war_beast_armor", () -> new ItemWarBeastArmor(8, 0xffd83d, JerotesWarehouse.MODID, "gold", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> DIAMOND_WAR_BEAST_ARMOR = REGISTRY.register("diamond_war_beast_armor", () -> new ItemWarBeastArmor(14, 4, 0, 0x29d0c0, JerotesWarehouse.MODID, "diamond", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> NETHERITE_WAR_BEAST_ARMOR = REGISTRY.register("netherite_war_beast_armor", () -> new ItemWarBeastArmor(14, 6, 2, 0x3c3232, JerotesWarehouse.MODID, "netherite", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).fireResistant()));
	public static final RegistryObject<Item> NETHERITE_OLD_WAR_BEAST_ARMOR = REGISTRY.register("netherite_old_war_beast_armor", () -> new ItemWarBeastArmor(14, 6, 2, 0x3c3232, JerotesWarehouse.MODID, "netherite_old", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).fireResistant()));
	public static final RegistryObject<Item> GIANT_BEAST_ARMOR_BASE = REGISTRY.register("giant_beast_armor_base", () -> new ItemGiantBeastArmor(0, 0xffffff, JerotesWarehouse.MODID, "base", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> LEATHER_GIANT_BEAST_ARMOR = REGISTRY.register("leather_giant_beast_armor", () -> new ItemGiantBeastArmorDyeable(6, 0x804a15, JerotesWarehouse.MODID, "leather", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> CHAINMAIL_GIANT_BEAST_ARMOR = REGISTRY.register("chainmail_giant_beast_armor", () -> new ItemGiantBeastArmor(11, 0x818181, JerotesWarehouse.MODID, "chainmail", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> COPPER_GIANT_BEAST_ARMOR = REGISTRY.register("copper_giant_beast_armor", () -> new ItemGiantBeastArmor(9, 0xd66d48, JerotesWarehouse.MODID, "copper", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> IRON_GIANT_BEAST_ARMOR = REGISTRY.register("iron_giant_beast_armor", () -> new ItemGiantBeastArmor(13, 0x8c8c8c, JerotesWarehouse.MODID, "iron", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> GOLDEN_GIANT_BEAST_ARMOR = REGISTRY.register("golden_giant_beast_armor", () -> new ItemGiantBeastArmor(10, 0xffd83d, JerotesWarehouse.MODID, "gold", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> DIAMOND_GIANT_BEAST_ARMOR = REGISTRY.register("diamond_giant_beast_armor", () -> new ItemGiantBeastArmor(17, 6, 0, 0x29d0c0, JerotesWarehouse.MODID, "diamond", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
	public static final RegistryObject<Item> NETHERITE_GIANT_BEAST_ARMOR = REGISTRY.register("netherite_giant_beast_armor", () -> new ItemGiantBeastArmor(17, 9, 3, 0x3c3232, JerotesWarehouse.MODID, "netherite", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).fireResistant()));
	public static final RegistryObject<Item> NETHERITE_OLD_GIANT_BEAST_ARMOR = REGISTRY.register("netherite_old_giant_beast_armor", () -> new ItemGiantBeastArmor(17, 9, 3, 0x3c3232, JerotesWarehouse.MODID, "netherite_old", new Item.Properties().stacksTo(1).rarity(Rarity.COMMON).fireResistant()));

	//发射物
	public static final RegistryObject<Item> MAGIC_MISSILE = REGISTRY.register("magic_missile", () -> new Shoot());
	public static final RegistryObject<Item> POISON_BREATH = REGISTRY.register("poison_breath", () -> new Shoot());
	public static final RegistryObject<Item> RAY_OF_SICKNESS = REGISTRY.register("ray_of_sickness", () -> new Shoot());
	public static final RegistryObject<Item> RAY_OF_ENFEEBLEMENT = REGISTRY.register("ray_of_enfeeblement", () -> new Shoot());
	public static final RegistryObject<Item> LIGHTNING_BLOT = REGISTRY.register("lightning_bolt", () -> new Shoot());
	public static final RegistryObject<Item> VICIOUS_MOCKERY = REGISTRY.register("vicious_mockery", () -> new Shoot());
	public static final RegistryObject<Item> HOLD_PERSON = REGISTRY.register("hold_person", () -> new Shoot());
	public static final RegistryObject<Item> BESTOW_CURSE = REGISTRY.register("bestow_curse", () -> new Shoot());
	public static final RegistryObject<Item> PHANTASMAL_KILLER = REGISTRY.register("phantasmal_killer", () -> new Shoot());
	public static final RegistryObject<Item> EYEBITE = REGISTRY.register("eyebite", () -> new Shoot());

	public static void setup() {
		//麻醉箭
		DispenserBlock.registerBehavior(JerotesItems.ANESTHETIC_ARROW.get(), new AbstractProjectileDispenseBehavior(){
			@Override
			protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
				AnestheticArrowEntity arrow = new AnestheticArrowEntity(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1));
				arrow.pickup = AbstractArrow.Pickup.ALLOWED;
				return arrow;
			}
		});
		//战兽铠
		OptionalDispenseItemBehavior optionalDispenseItemBehaviorHorseArmor = new OptionalDispenseItemBehavior(){
			@Override
			protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
				BlockPos blockPos = JerotesItemsAdd.getPos(blockSource).relative(JerotesItemsAdd.getBlockState(blockSource).getValue(DispenserBlock.FACING));
				List<AbstractHorse> listHorse = JerotesItemsAdd.getLevel(blockSource).getEntitiesOfClass(AbstractHorse.class, new AABB(blockPos), abstractHorse -> abstractHorse.isAlive() && abstractHorse.canWearArmor());
				for (AbstractHorse abstractHorse2 : listHorse) {
					if (!abstractHorse2.isArmor(itemStack) || abstractHorse2.isWearingArmor() || !abstractHorse2.isTamed()) continue;
					abstractHorse2.getSlot(401).set(itemStack.split(1));
					this.setSuccess(true);
					return itemStack;
				}
				List<Mob> list = JerotesItemsAdd.getLevel(blockSource).getEntitiesOfClass(Mob.class, new AABB(blockPos), abstractHorse -> abstractHorse.isAlive());
				for (Mob abstractHorse : list) {
					if (!(abstractHorse instanceof ArmorEntity abstractHorse2)) continue;
					if (!abstractHorse2.isWarBeastArmor() || !abstractHorse2.canWearArmor() || !abstractHorse2.isArmor(itemStack) || abstractHorse2.isWearingArmor() || (abstractHorse2 instanceof OwnableEntity ownable && ownable.getOwner() == null)) continue;
                    Mob mob = (Mob) abstractHorse2;
                    mob.getSlot(abstractHorse2.getAddNumber() + 1).set(itemStack.split(1));
                    this.setSuccess(true);
                    return itemStack;
				}
				return super.execute(blockSource, itemStack);
			}
		};
		DispenserBlock.registerBehavior(JerotesItems.WAR_BEAST_ARMOR_BASE.get(), optionalDispenseItemBehaviorHorseArmor);
		DispenserBlock.registerBehavior(JerotesItems.LEATHER_WAR_BEAST_ARMOR.get(), optionalDispenseItemBehaviorHorseArmor);
		DispenserBlock.registerBehavior(JerotesItems.CHAINMAIL_WAR_BEAST_ARMOR.get(), optionalDispenseItemBehaviorHorseArmor);
		DispenserBlock.registerBehavior(JerotesItems.IRON_WAR_BEAST_ARMOR.get(), optionalDispenseItemBehaviorHorseArmor);
		DispenserBlock.registerBehavior(JerotesItems.GOLDEN_WAR_BEAST_ARMOR.get(), optionalDispenseItemBehaviorHorseArmor);
		DispenserBlock.registerBehavior(JerotesItems.DIAMOND_WAR_BEAST_ARMOR.get(), optionalDispenseItemBehaviorHorseArmor);
		DispenserBlock.registerBehavior(JerotesItems.NETHERITE_WAR_BEAST_ARMOR.get(), optionalDispenseItemBehaviorHorseArmor);
		DispenserBlock.registerBehavior(JerotesItems.NETHERITE_OLD_WAR_BEAST_ARMOR.get(), optionalDispenseItemBehaviorHorseArmor);
		//巨兽铠
		OptionalDispenseItemBehavior optionalDispenseItemBehaviorGiantBeastArmor = new OptionalDispenseItemBehavior(){
			@Override
			protected ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
				BlockPos blockPos = JerotesItemsAdd.getPos(blockSource).relative(JerotesItemsAdd.getBlockState(blockSource).getValue(DispenserBlock.FACING));
				List<Mob> list = JerotesItemsAdd.getLevel(blockSource).getEntitiesOfClass(Mob.class, new AABB(blockPos), abstractHorse -> abstractHorse.isAlive());
				for (Mob abstractHorse : list) {
					if (!(abstractHorse instanceof ArmorEntity abstractHorse2)) continue;
					if (!abstractHorse2.isGiantBeastArmor() || !abstractHorse2.canWearArmor() || !abstractHorse2.isArmor(itemStack) || abstractHorse2.isWearingArmor() || (abstractHorse2 instanceof OwnableEntity ownable && ownable.getOwner() == null)) continue;
                    Mob mob = (Mob) abstractHorse2;
                    mob.getSlot(abstractHorse2.getAddNumber() + 1).set(itemStack.split(1));
                    this.setSuccess(true);
                    return itemStack;
				}
				return super.execute(blockSource, itemStack);
			}
		};
		DispenserBlock.registerBehavior(JerotesItems.GIANT_BEAST_ARMOR_BASE.get(), optionalDispenseItemBehaviorGiantBeastArmor);
		DispenserBlock.registerBehavior(JerotesItems.LEATHER_GIANT_BEAST_ARMOR.get(), optionalDispenseItemBehaviorGiantBeastArmor);
		DispenserBlock.registerBehavior(JerotesItems.CHAINMAIL_GIANT_BEAST_ARMOR.get(), optionalDispenseItemBehaviorGiantBeastArmor);
		DispenserBlock.registerBehavior(JerotesItems.IRON_GIANT_BEAST_ARMOR.get(), optionalDispenseItemBehaviorGiantBeastArmor);
		DispenserBlock.registerBehavior(JerotesItems.GOLDEN_GIANT_BEAST_ARMOR.get(), optionalDispenseItemBehaviorGiantBeastArmor);
		DispenserBlock.registerBehavior(JerotesItems.DIAMOND_GIANT_BEAST_ARMOR.get(), optionalDispenseItemBehaviorGiantBeastArmor);
		DispenserBlock.registerBehavior(JerotesItems.NETHERITE_GIANT_BEAST_ARMOR.get(), optionalDispenseItemBehaviorGiantBeastArmor);
		DispenserBlock.registerBehavior(JerotesItems.NETHERITE_OLD_GIANT_BEAST_ARMOR.get(), optionalDispenseItemBehaviorGiantBeastArmor);
	}
}
