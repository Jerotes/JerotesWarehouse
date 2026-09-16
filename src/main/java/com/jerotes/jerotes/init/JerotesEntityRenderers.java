package com.jerotes.jerotes.init;

import com.jerotes.jerotes.client.renderer.*;
import com.jerotes.jerotes.entity.Other.OtherSpell.MagicBonfireEntity;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JerotesEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(JerotesEntityType.HUMAN.get(), HumanRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_PLAYER.get(), JerotesPlayerRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_HORSE.get(), JerotesHorseRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.TEST.get(), TestRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.BIG_BEAST.get(), BigBeastRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.ADD_HAND.get(), AddHandRenderer::new);

        event.registerEntityRenderer(JerotesEntityType.TEST_BEAM.get(), BaseBeamRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.SUNBEAM.get(), SunbeamRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_EVOKER_FANGS.get(), JerotesEvokerFangRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.SPELL_CLOUD.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAIN_SPELL_CLOUD.get(), NoopRenderer::new);

        event.registerEntityRenderer(JerotesEntityType.POISON_BREATH_AREA.get(), BreathAreaRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.CLOUD_OF_DAGGERS.get(), CloudOfDaggersRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.MAGIC_BONFIRE.get(), MagicBonfireRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.INSECT_PLAGUE.get(), InsectPlagueRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.LIFE_MEND.get(), LifeMendRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.CURE_WOUNDS.get(), CureWoundsRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.MENDING.get(), MendingRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.COLOR_MAGIC_WIND.get(), ColorMagicWindRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.HOLD_MOB.get(), HoldMobRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.HOLD_PERSON_MAIN.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.MIRROR_IMAGE_MAIN.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.INVISIBILITY.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.INVISIBLE_PASSAGE.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.PHANTASMAL_KILLER.get(), PhantasmalKillerRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FIREBALL.get(), FireballRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.ICE_STORM.get(), IceStormRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THUNDERWAVE.get(), ThunderwaveRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.POWER_WORD_KILL.get(), PowerWordKillRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.LOCK_AIM.get(), NoopRenderer::new);

        event.registerEntityRenderer(JerotesEntityType.JEROTES_FALLING_BLOCK.get(), BaseFallingBlockRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_EARTHREND_BLOCK.get(), BaseFallingBlockRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_UNEVEN_BLOCK.get(), JerotesUnevenBlockRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.INVISIBLE_LIGHTNING_BOLT.get(), NoopRenderer::new);

        event.registerEntityRenderer(JerotesEntityType.MIRROR_IMAGE.get(), MirrorImageRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_SIMPLE_JAVELIN.get(), ThrownSimpleJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_HEAL_JAVELIN.get(), ThrownHealJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_ANESTHETIC_JAVELIN.get(), ThrownAnestheticJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_TRANSPORT_JAVELIN.get(), ThrownTransportJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_EXPLOSIVE_JAVELIN.get(), ThrownExplosiveJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_WOODEN_JAVELIN.get(), ThrownWoodenJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_STONE_JAVELIN.get(), ThrownStoneJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_COPPER_JAVELIN.get(), ThrownCopperJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_IRON_JAVELIN.get(), ThrownIronJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_GOLDEN_JAVELIN.get(), ThrownGoldenJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_DIAMOND_JAVELIN.get(), ThrownDiamondJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_NETHERITE_JAVELIN.get(), ThrownNetheriteJavelinRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.THROWN_BONE_THROWING_SPEAR.get(), ThrownBoneThrowingSpearRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.ANESTHETIC_ARROW.get(), AnestheticArrowRenderer::new);
        //仆从
        event.registerEntityRenderer(JerotesEntityType.JEROTES_VEX.get(), JerotesVexRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_IRON_GOLEM.get(), JerotesIronGoelmRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_SNOW_GOLEM.get(), JerotesSnowGoelmRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_BLAZE.get(), JerotesBlazeRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_ZOMBIE.get(), JerotesZombieRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FINGER_OF_DEATH_ZOMBIE.get(), FingerOfDeathZombieRenderer::new);
        //法术
        event.registerEntityRenderer(JerotesEntityType.MAGIC_MISSILE.get(), MagicMissileRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.BLAZE_FIREBALL.get(), (p_174084_) -> new ShootRenderer<>(p_174084_, 0.75f, true));
        event.registerEntityRenderer(JerotesEntityType.BREEZE_WIND_CHARGE.get(), BreezeWindChargeRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.GHAST_FIREBALL.get(), (p_174084_) -> new ShootRenderer<>(p_174084_, 3f, true));
        event.registerEntityRenderer(JerotesEntityType.ENDERDRAGON_FIREBALL.get(), EnderdragonFireballRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.POISON_BREATH.get(), ShootRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FIRE_BOLT.get(), MagicBoltRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FROST_BOLT.get(), MagicBoltRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.SHOCK_BOLT.get(), MagicBoltRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FLAMES_SPRAY.get(), MagicSprayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FROSTBITE_SPRAY.get(), MagicSprayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.SPARKS_SPRAY.get(), SparksSprayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.WITCH_BOLT.get(), WitchBoltRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.ICE_KNIFE.get(), IceKnifeRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.ICE_KNIFE_SHARD.get(), IceKnifeShardRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_FIRE.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_FROST.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_LIGHTNING.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.ELDRITCH_BLAST.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.SCORCHING_RAY.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_SICKNESS.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_ENFEEBLEMENT.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.DISINTEGRATE.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FINGER_OF_DEATH.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.LIGHTNING_BOLT.get(), LightningBoltRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.CHAIN_LIGHTNING.get(), LightningBoltRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.INFESTATION.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.SHOCKING_GRASP.get(), ShockingGraspRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.GUARDIAN_LASER.get(), GuardianLaserRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.VICIOUS_MOCKERY.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.BESTOW_CURSE.get(), BestowCurseRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.CONCENTRATION_BESTOW_CURSE.get(), BestowCurseRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.REMOVE_CURSE.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.BLINDNESS_CURSE.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.DARKNESS_CURSE.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FATIGUE_CURSE.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.EYEBITE.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.HARM.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_WITHER_SKULL.get(), JerotesWitherSkullRenderer::new);
    }
}
