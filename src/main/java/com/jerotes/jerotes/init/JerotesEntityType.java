package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.MagicSummoned.Blaze.JerotesBlazeEntity;
import com.jerotes.jerotes.entity.MagicSummoned.IronGolem.JerotesIronGoelmEntity;
import com.jerotes.jerotes.entity.MagicSummoned.SnowGolem.JerotesSnowGoelmEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Vex.JerotesVexEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Zombie.FingerOfDeathZombieEntity;
import com.jerotes.jerotes.entity.MagicSummoned.Zombie.JerotesZombieEntity;
import com.jerotes.jerotes.entity.Mob.*;
import com.jerotes.jerotes.entity.Other.OtherSpell.*;
import com.jerotes.jerotes.entity.Other.OtherSpell.HoldPersonMainEntity;
import com.jerotes.jerotes.entity.Other.ShootTargetSpell.FireballEntity;
import com.jerotes.jerotes.entity.Other.SpellCloud.RainSpellCloudEntity;
import com.jerotes.jerotes.entity.Other.SpellCloud.SpellCloudEntity;
import com.jerotes.jerotes.entity.Shoot.Arrow.*;
import com.jerotes.jerotes.entity.Shoot.Magic.Breath.PoisonBreathEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicMissile.MagicMissileEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicShoot.*;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicThrow.IceKnifeShardEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicThrow.WitchBoltEntity;
import com.jerotes.jerotes.entity.Shoot.Magic.Ray.*;
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
import net.minecraft.world.level.block.Blocks;
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
			EntityType.Builder.of(HumanEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<JerotesPlayerEntity>> JEROTES_PLAYER = register("jerotes_player",
			EntityType.Builder.of(JerotesPlayerEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<JerotesHorseEntity>> JEROTES_HORSE = register("jerotes_horse",
			EntityType.Builder.of(JerotesHorseEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(1.4F, 1.6F));
	public static final RegistryObject<EntityType<TestEntity>> TEST = register("test",
			EntityType.Builder.of(TestEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8192)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<BigBeastEntity>> BIG_BEAST = register("big_beast",
			EntityType.Builder.of(BigBeastEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8192)
					.sized(4f, 4.625f));
	public static final RegistryObject<EntityType<AddHandEntity>> ADD_HAND = register("add_hand",
			EntityType.Builder.of(AddHandEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<MirrorImageEntity>> MIRROR_IMAGE = register("mirror_image",
			EntityType.Builder.of(MirrorImageEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(1.0f, 1.0f));

	public static final RegistryObject<EntityType<BaseBeamEntity>> TEST_BEAM = register("test_beam",
			EntityType.Builder.of(BaseBeamEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<JerotesEvokerFangEntity>> JEROTES_EVOKER_FANGS = register("jerotes_evoker_fangs",
			EntityType.Builder.<JerotesEvokerFangEntity>of(JerotesEvokerFangEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(0.8f, 1.0f));

	public static final RegistryObject<EntityType<SpellCloudEntity>> SPELL_CLOUD = register("spell_cloud",
			EntityType.Builder.<SpellCloudEntity>of(SpellCloudEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(6.0F, 0.5F));
	public static final RegistryObject<EntityType<RainSpellCloudEntity>> RAIN_SPELL_CLOUD = register("rain_spell_cloud",
			EntityType.Builder.<RainSpellCloudEntity>of(RainSpellCloudEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(6.0F, 0.5F));

	public static final RegistryObject<EntityType<CloudOfDaggersEntity>> CLOUD_OF_DAGGERS = register("cloud_of_daggers",
			EntityType.Builder.<CloudOfDaggersEntity>of(CloudOfDaggersEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(3F, 3F));
	public static final RegistryObject<EntityType<MagicBonfireEntity>> MAGIC_BONFIRE = register("magic_bonfire",
			EntityType.Builder.<MagicBonfireEntity>of(MagicBonfireEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(3F, 3F));
	public static final RegistryObject<EntityType<InsectPlagueEntity>> INSECT_PLAGUE = register("insect_plague",
			EntityType.Builder.<InsectPlagueEntity>of(InsectPlagueEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(12.0F, 12.0F));
	public static final RegistryObject<EntityType<LifeMendEntity>> LIFE_MEND = register("life_mend",
			EntityType.Builder.<LifeMendEntity>of(LifeMendEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(1.0F, 2.0F));
	public static final RegistryObject<EntityType<CureWoundsEntity>> CURE_WOUNDS = register("cure_wounds",
			EntityType.Builder.<CureWoundsEntity>of(CureWoundsEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(1.0F, 2.0F));
	public static final RegistryObject<EntityType<MendingEntity>> MENDING = register("mending",
			EntityType.Builder.<MendingEntity>of(MendingEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(1.0F, 2.0F));
	public static final RegistryObject<EntityType<ColorMagicWindEntity>> COLOR_MAGIC_WIND = register("color_magic_wind",
			EntityType.Builder.<ColorMagicWindEntity>of(ColorMagicWindEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(1.0F, 2.0F));
	public static final RegistryObject<EntityType<HoldMobEntity>> HOLD_MOB = register("hold_mob",
			EntityType.Builder.<HoldMobEntity>of(HoldMobEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(1.0F, 2.0F));
	public static final RegistryObject<EntityType<HoldPersonMainEntity>> HOLD_PERSON_MAIN = register("hold_person_main",
			EntityType.Builder.<HoldPersonMainEntity>of(HoldPersonMainEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<MirrorImageMainEntity>> MIRROR_IMAGE_MAIN = register("mirror_image_main",
			EntityType.Builder.<MirrorImageMainEntity>of(MirrorImageMainEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<InvisibilityEntity>> INVISIBILITY = register("invisibility",
			EntityType.Builder.<InvisibilityEntity>of(InvisibilityEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<InvisiblePassageEntity>> INVISIBLE_PASSAGE = register("invisible_passage",
			EntityType.Builder.<InvisiblePassageEntity>of(InvisiblePassageEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<PhantasmalKillerEntity>> PHANTASMAL_KILLER = register("phantasmal_killer",
			EntityType.Builder.<PhantasmalKillerEntity>of(PhantasmalKillerEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<FireballEntity>> FIREBALL = register("fireball",
			EntityType.Builder.<FireballEntity>of(FireballEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(1.0f, 1.0f));
	public static final RegistryObject<EntityType<IceStormEntity>> ICE_STORM = register("ice_storm",
			EntityType.Builder.<IceStormEntity>of(IceStormEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(12f, 12f));
	public static final RegistryObject<EntityType<ThunderwaveEntity>> THUNDERWAVE = register("thunderwave",
			EntityType.Builder.<ThunderwaveEntity>of(ThunderwaveEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(4.5f, 4.5f));
	public static final RegistryObject<EntityType<PowerWordKillEntity>> POWER_WORD_KILL = register("power_word_kill",
			EntityType.Builder.<PowerWordKillEntity>of(PowerWordKillEntity::new, MobCategory.MISC).fireImmune().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
					.sized(1.0F, 2.0F));


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

	//仆从
	public static final RegistryObject<EntityType<JerotesVexEntity>> JEROTES_VEX = register("jerotes_vex",
			EntityType.Builder.of(JerotesVexEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).fireImmune()
					.sized(0.4F, 0.8F));
	public static final RegistryObject<EntityType<JerotesIronGoelmEntity>> JEROTES_IRON_GOLEM = register("jerotes_iron_golem",
			EntityType.Builder.of(JerotesIronGoelmEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(10)
					.sized(1.4F, 2.7F));
	public static final RegistryObject<EntityType<JerotesSnowGoelmEntity>> JEROTES_SNOW_GOLEM = register("jerotes_snow_golem",
			EntityType.Builder.of(JerotesSnowGoelmEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).immuneTo(Blocks.POWDER_SNOW)
					.sized(0.7F, 1.9F));
	public static final RegistryObject<EntityType<JerotesBlazeEntity>> JEROTES_BLAZE = register("jerotes_blaze",
			EntityType.Builder.of(JerotesBlazeEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8).fireImmune()
					.sized(0.6F, 1.8F));
	public static final RegistryObject<EntityType<JerotesZombieEntity>> JEROTES_ZOMBIE = register("jerotes_zombie",
			EntityType.Builder.<JerotesZombieEntity>of(JerotesZombieEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8)
					.sized(0.6F, 1.95F));
	public static final RegistryObject<EntityType<FingerOfDeathZombieEntity>> FINGER_OF_DEATH_ZOMBIE = register("finger_of_death_zombie",
			EntityType.Builder.<FingerOfDeathZombieEntity>of(FingerOfDeathZombieEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(8)
					.sized(0.6F, 1.95F));
	//法术
	public static final RegistryObject<EntityType<MagicMissileEntity>> MAGIC_MISSILE = register("magic_missile",
			EntityType.Builder.<MagicMissileEntity>of(MagicMissileEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<BlazeFireballEntity>> BLAZE_FIREBALL = register("blaze_fireball",
			EntityType.Builder.<BlazeFireballEntity>of(BlazeFireballEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<BreezeWindChargeEntity>> BREEZE_WIND_CHARGE = register("breeze_wind_charge",
			EntityType.Builder.<BreezeWindChargeEntity>of(BreezeWindChargeEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4).clientTrackingRange(4).updateInterval(10)
					.sized(0.3125f, 0.3125f));
	public static final RegistryObject<EntityType<GhastFireballEntity>> GHAST_FIREBALL = register("ghast_fireball",
			EntityType.Builder.<GhastFireballEntity>of(GhastFireballEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.0F, 1.0F));
		public static final RegistryObject<EntityType<EnderdragonFireballEntity>> ENDERDRAGON_FIREBALL = register("enderdragon_fireball",
			EntityType.Builder.<EnderdragonFireballEntity>of(EnderdragonFireballEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.0F, 1.0F));
	public static final RegistryObject<EntityType<PoisonBreathEntity>> POISON_BREATH = register("poison_breath",
			EntityType.Builder.<PoisonBreathEntity>of(PoisonBreathEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.6f, 1.6f));
	public static final RegistryObject<EntityType<FireBoltEntity>> FIRE_BOLT = register("fire_bolt",
			EntityType.Builder.<FireBoltEntity>of(FireBoltEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<FrostBoltEntity>> FROST_BOLT = register("frost_bolt",
			EntityType.Builder.<FrostBoltEntity>of(FrostBoltEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<ShockBoltEntity>> SHOCK_BOLT = register("shock_bolt",
			EntityType.Builder.<ShockBoltEntity>of(ShockBoltEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<WitchBoltEntity>> WITCH_BOLT = register("witch_bolt",
			EntityType.Builder.<WitchBoltEntity>of(WitchBoltEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<IceKnifeEntity>> ICE_KNIFE = register("ice_knife",
			EntityType.Builder.<IceKnifeEntity>of(IceKnifeEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<IceKnifeShardEntity>> ICE_KNIFE_SHARD = register("ice_knife_shard",
			EntityType.Builder.<IceKnifeShardEntity>of(IceKnifeShardEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.25f, 0.25f));
	public static final RegistryObject<EntityType<RayofFireEntity>> RAY_OF_FIRE = register("ray_of_fire",
			EntityType.Builder.<RayofFireEntity>of(RayofFireEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<RayofFrostEntity>> RAY_OF_FROST = register("ray_of_frost",
			EntityType.Builder.<RayofFrostEntity>of(RayofFrostEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<RayofLightningEntity>> RAY_OF_LIGHTNING = register("ray_of_lightning",
			EntityType.Builder.<RayofLightningEntity>of(RayofLightningEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<EldritchBlastEntity>> ELDRITCH_BLAST = register("eldritch_blast",
			EntityType.Builder.<EldritchBlastEntity>of(EldritchBlastEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<RayofSicknessEntity>> RAY_OF_SICKNESS = register("ray_of_sickness",
			EntityType.Builder.<RayofSicknessEntity>of(RayofSicknessEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<RayofEnfeeblementEntity>> RAY_OF_ENFEEBLEMENT = register("ray_of_enfeeblement",
			EntityType.Builder.<RayofEnfeeblementEntity>of(RayofEnfeeblementEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<DisintegrateEntity>> DISINTEGRATE = register("disintegrate",
			EntityType.Builder.<DisintegrateEntity>of(DisintegrateEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<FingerOfDeathEntity>> FINGER_OF_DEATH = register("finger_of_death",
			EntityType.Builder.<FingerOfDeathEntity>of(FingerOfDeathEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));
	public static final RegistryObject<EntityType<LightningBoltEntity>> LIGHTNING_BOLT = register("lightning_bolt",
			EntityType.Builder.<LightningBoltEntity>of(LightningBoltEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<ChainLightningEntity>> CHAIN_LIGHTNING = register("chain_lightning",
			EntityType.Builder.<ChainLightningEntity>of(ChainLightningEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(32)
					.sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<InfestationEntity>> INFESTATION = register("infestation",
			EntityType.Builder.<InfestationEntity>of(InfestationEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.8f, 0.8f));
	public static final RegistryObject<EntityType<ShockingGraspEntity>> SHOCKING_GRASP = register("shocking_grasp",
			EntityType.Builder.<ShockingGraspEntity>of(ShockingGraspEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.8f, 0.8f));
	public static final RegistryObject<EntityType<GuardianLaserEntity>> GUARDIAN_LASER = register("guardian_laser",
			EntityType.Builder.<GuardianLaserEntity>of(GuardianLaserEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.8f, 0.8f));
	public static final RegistryObject<EntityType<ViciousMockeryEntity>> VICIOUS_MOCKERY = register("vicious_mockery",
			EntityType.Builder.<ViciousMockeryEntity>of(ViciousMockeryEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.8f, 0.8f));
	public static final RegistryObject<EntityType<BestowCurseEntity>> BESTOW_CURSE = register("bestow_curse",
			EntityType.Builder.<BestowCurseEntity>of(BestowCurseEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<BlindnessCurseEntity>> BLINDNESS_CURSE = register("blindness_curse",
			EntityType.Builder.<BlindnessCurseEntity>of(BlindnessCurseEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<DarknessCurseEntity>> DARKNESS_CURSE = register("darkness_curse",
			EntityType.Builder.<DarknessCurseEntity>of(DarknessCurseEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<FatigueCurseEntity>> FATIGUE_CURSE = register("fatigue_curse",
			EntityType.Builder.<FatigueCurseEntity>of(FatigueCurseEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<EyebiteEntity>> EYEBITE = register("eyebite",
			EntityType.Builder.<EyebiteEntity>of(EyebiteEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<HarmEntity>> HARM = register("harm",
			EntityType.Builder.<HarmEntity>of(HarmEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(1.5f, 1.5f));
	public static final RegistryObject<EntityType<JerotesWitherSkullEntity>> JEROTES_WITHER_SKULL = register("jerotes_wither_skull",
			EntityType.Builder.<JerotesWitherSkullEntity>of(JerotesWitherSkullEntity::new, MobCategory.MISC).setShouldReceiveVelocityUpdates(true).setTrackingRange(4)
					.sized(0.3125F, 0.3125F));

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
			SpawnPlacements.register(JerotesEntityType.JEROTES_VEX.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.JEROTES_IRON_GOLEM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.JEROTES_SNOW_GOLEM.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.JEROTES_BLAZE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.JEROTES_ZOMBIE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
					SpawnRules.NeutralSpawn(8, 64, arg_0, arg_1, arg_2, arg_3, arg_4));
			SpawnPlacements.register(JerotesEntityType.FINGER_OF_DEATH_ZOMBIE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (arg_0, arg_1, arg_2, arg_3, arg_4) ->
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
		event.put(JEROTES_VEX.get(), JerotesVexEntity.createAttributes().build());
		event.put(JEROTES_IRON_GOLEM.get(), JerotesIronGoelmEntity.createAttributes().build());
		event.put(JEROTES_SNOW_GOLEM.get(), JerotesSnowGoelmEntity.createAttributes().build());
		event.put(JEROTES_BLAZE.get(), JerotesBlazeEntity.createAttributes().build());
		event.put(JEROTES_ZOMBIE.get(), JerotesZombieEntity.createAttributes().build());
		event.put(FINGER_OF_DEATH_ZOMBIE.get(), FingerOfDeathZombieEntity.createAttributes().build());
		event.put(MIRROR_IMAGE.get(), MirrorImageEntity.createAttributes().build());
	}
}

