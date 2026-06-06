package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Mob.*;
import com.jerotes.jerotes.entity.Shoot.Arrow.*;
import com.jerotes.jerotes.entity.Shoot.Magic.Breath.PoisonBreathEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicMissile.MagicMissileEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.LightningBoltEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.RayofEnfeeblementEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.RayofSicknessEntity;
import com.jerotes.jerotes.entity.Other.FallingBlock.JerotesEarthrendBlock;
import com.jerotes.jerotes.entity.Other.FallingBlock.JerotesFallingBlock;
import com.jerotes.jerotes.entity.Other.FallingBlock.JerotesUnevenBlock;
import com.jerotes.jerotes.entity.Other.Beam.BaseBeamEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Target.*;
import com.jerotes.jerotes.util.SpawnRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JerotesEntityType {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JerotesWarehouse.MODID);


	public static final RegistryObject<EntityType<HumanEntity>> HUMAN = register("human",
			EntityType.Builder.<HumanEntity>of(HumanEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<JerotesPlayerEntity>> JEROTES_PLAYER = register("jerotes_player",
			EntityType.Builder.<JerotesPlayerEntity>of(JerotesPlayerEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<JerotesHorseEntity>> JEROTES_HORSE = register("jerotes_horse",
			EntityType.Builder.<JerotesHorseEntity>of(JerotesHorseEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(1.4F, 1.6F));
	public static final RegistryObject<EntityType<TestEntity>> TEST = register("test",
			EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8192)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<BigBeastEntity>> BIG_BEAST = register("big_beast",
			EntityType.Builder.<BigBeastEntity>of(BigBeastEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8192)
					.sized(4f, 4.625f));
	public static final RegistryObject<EntityType<AddHandEntity>> ADD_HAND = register("add_hand",
			EntityType.Builder.<AddHandEntity>of(AddHandEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<MirrorImageEntity>> MIRROR_IMAGE = register("mirror_image",
			EntityType.Builder.<MirrorImageEntity>of(MirrorImageEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(1.0f, 1.0f));

	public static final RegistryObject<EntityType<BaseBeamEntity>> TEST_BEAM = register("test_beam",
			EntityType.Builder.<BaseBeamEntity>of(BaseBeamEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.6f, 0.6f));

	public static final RegistryObject<EntityType<JerotesFallingBlock>> JEROTES_FALLING_BLOCK = register("jerotes_falling_block",
			EntityType.Builder.<JerotesFallingBlock>of(JerotesFallingBlock::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(0.99f, 0.99f));
	public static final RegistryObject<EntityType<JerotesEarthrendBlock>> JEROTES_EARTHREND_BLOCK = register("jerotes_earthrend_block",
			EntityType.Builder.<JerotesEarthrendBlock>of(JerotesEarthrendBlock::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(0.99f, 0.99f));
	public static final RegistryObject<EntityType<JerotesUnevenBlock>> JEROTES_UNEVEN_BLOCK = register("jerotes_uneven_block",
			EntityType.Builder.<JerotesUnevenBlock>of(JerotesUnevenBlock::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(0.99f, 0.99f));
	//发射物
	public static final RegistryObject<EntityType<ThrownSimpleJavelinEntity>> THROWN_SIMPLE_JAVELIN = register("projectile_throw_simple_javelin",
			EntityType.Builder.<ThrownSimpleJavelinEntity>of(ThrownSimpleJavelinEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownHealJavelinEntity>> THROWN_HEAL_JAVELIN = register("projectile_throw_heal_javelin",
			EntityType.Builder.<ThrownHealJavelinEntity>of(ThrownHealJavelinEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownAnestheticJavelinEntity>> THROWN_ANESTHETIC_JAVELIN = register("projectile_throw_anesthetic_javelin",
			EntityType.Builder.<ThrownAnestheticJavelinEntity>of(ThrownAnestheticJavelinEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownTransportJavelinEntity>> THROWN_TRANSPORT_JAVELIN = register("projectile_throw_transport_javelin",
			EntityType.Builder.<ThrownTransportJavelinEntity>of(ThrownTransportJavelinEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownExplosiveJavelinEntity>> THROWN_EXPLOSIVE_JAVELIN = register("projectile_throw_explosive_javelin",
			EntityType.Builder.<ThrownExplosiveJavelinEntity>of(ThrownExplosiveJavelinEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownJavelinWoodenEntity>> THROWN_WOODEN_JAVELIN = register("projectile_throw_wooden_javelin",
			EntityType.Builder.<ThrownJavelinWoodenEntity>of(ThrownJavelinWoodenEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownJavelinStoneEntity>> THROWN_STONE_JAVELIN = register("projectile_throw_stone_javelin",
			EntityType.Builder.<ThrownJavelinStoneEntity>of(ThrownJavelinStoneEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownJavelinCopperEntity>> THROWN_COPPER_JAVELIN = register("projectile_throw_copper_javelin",
			EntityType.Builder.<ThrownJavelinCopperEntity>of(ThrownJavelinCopperEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownJavelinIronEntity>> THROWN_IRON_JAVELIN = register("projectile_throw_iron_javelin",
			EntityType.Builder.<ThrownJavelinIronEntity>of(ThrownJavelinIronEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownJavelinGoldenEntity>> THROWN_GOLDEN_JAVELIN = register("projectile_throw_golden_javelin",
			EntityType.Builder.<ThrownJavelinGoldenEntity>of(ThrownJavelinGoldenEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownJavelinDiamondEntity>> THROWN_DIAMOND_JAVELIN = register("projectile_throw_diamond_javelin",
			EntityType.Builder.<ThrownJavelinDiamondEntity>of(ThrownJavelinDiamondEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownJavelinNetheriteEntity>> THROWN_NETHERITE_JAVELIN = register("projectile_throw_netherite_javelin",
			EntityType.Builder.<ThrownJavelinNetheriteEntity>of(ThrownJavelinNetheriteEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThrownBoneThrowingSpearEntity>> THROWN_BONE_THROWING_SPEAR = register("projectile_throw_bone_throwing_spear",
			EntityType.Builder.<ThrownBoneThrowingSpearEntity>of(ThrownBoneThrowingSpearEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<AnestheticArrowEntity>> ANESTHETIC_ARROW = register("projectile_anesthetic_arrow",
			EntityType.Builder.<AnestheticArrowEntity>of(AnestheticArrowEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.sized(0.5f, 0.5f));


	public static final RegistryObject<EntityType<MagicMissileEntity>> MAGIC_MISSILE = register("magic_missile",
			EntityType.Builder.<MagicMissileEntity>of(MagicMissileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<PoisonBreathEntity>> POISON_BREATH = register("poison_breath",
			EntityType.Builder.<PoisonBreathEntity>of(PoisonBreathEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.6f, 1.6f));
	public static final RegistryObject<EntityType<RayofSicknessEntity>> RAY_OF_SICKNESS = register("ray_of_sickness",
			EntityType.Builder.<RayofSicknessEntity>of(RayofSicknessEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<RayofEnfeeblementEntity>> RAY_OF_ENFEEBLEMENT = register("ray_of_enfeeblement",
			EntityType.Builder.<RayofEnfeeblementEntity>of(RayofEnfeeblementEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<LightningBoltEntity>> LIGHTNING_BOLT = register("lightning_bolt",
			EntityType.Builder.<LightningBoltEntity>of(LightningBoltEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<ViciousMockeryEntity>> VICIOUS_MOCKERY = register("vicious_mockery",
			EntityType.Builder.<ViciousMockeryEntity>of(ViciousMockeryEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.8f, 0.8f));
	public static final RegistryObject<EntityType<HoldPersonEntity>> HOLD_PERSON = register("hold_person",
			EntityType.Builder.<HoldPersonEntity>of(HoldPersonEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<BestowCurseEntity>> BESTOW_CURSE = register("bestow_curse",
			EntityType.Builder.<BestowCurseEntity>of(BestowCurseEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<PhantasmalKillerEntity>> PHANTASMAL_KILLER = register("phantasmal_killer",
			EntityType.Builder.<PhantasmalKillerEntity>of(PhantasmalKillerEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<EyebiteEntity>> EYEBITE = register("eyebite",
			EntityType.Builder.<EyebiteEntity>of(EyebiteEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			SpawnPlacements.register(JerotesEntityType.TEST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.BIG_BEAST.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.ADD_HAND.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.HUMAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.JEROTES_PLAYER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.JEROTES_HORSE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.MIRROR_IMAGE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(TEST.get(), TestEntity.createAttributes().build());
		event.put(BIG_BEAST.get(), BigBeastEntity.createAttributes().build());
		event.put(ADD_HAND.get(), AddHandEntity.createAttributes().build());
		event.put(HUMAN.get(), HumanEntity.createAttributes().build());
		event.put(JEROTES_PLAYER.get(), JerotesPlayerEntity.createAttributes().build());
		event.put(JEROTES_HORSE.get(), JerotesHorseEntity.createAttributes().build());
		event.put(MIRROR_IMAGE.get(), MirrorImageEntity.createAttributes().build());
	}
}

