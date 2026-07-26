package com.jerotes.jerotes.init;

import com.jerotes.jerotes.client.renderer.*;
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
        event.registerEntityRenderer(JerotesEntityType.JEROTES_EVOKER_FANGS.get(), JerotesEvokerFangRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.SPELL_CLOUD.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAIN_SPELL_CLOUD.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.CLOUD_OF_DAGGERS.get(), CloudOfDaggersRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.INSECT_PLAGUE.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.LIFE_MEND.get(), LifeMendRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.CURE_WOUNDS.get(), CureWoundsRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.HOLD_MOB.get(), HoldMobRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.HOLD_PERSON_MAIN.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.INVISIBLE_PASSAGE.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.PHANTASMAL_KILLER.get(), NoopRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FIREBALL.get(), FireballRenderer::new);

        event.registerEntityRenderer(JerotesEntityType.JEROTES_FALLING_BLOCK.get(), BaseFallingBlockRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_EARTHREND_BLOCK.get(), BaseFallingBlockRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.JEROTES_UNEVEN_BLOCK.get(), JerotesUnevenBlockRenderer::new);

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
        event.registerEntityRenderer(JerotesEntityType.JEROTES_ZOMBIE.get(), JerotesZombieRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FINGER_OF_DEATH_ZOMBIE.get(), FingerOfDeathZombieRenderer::new);
        //法术
        event.registerEntityRenderer(JerotesEntityType.MAGIC_MISSILE.get(), MagicMissileRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.POISON_BREATH.get(), ShootRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_FIRE.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_FROST.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_LIGHTNING.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_SICKNESS.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.RAY_OF_ENFEEBLEMENT.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.DISINTEGRATE.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.FINGER_OF_DEATH.get(), RayRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.LIGHTNING_BOLT.get(), LightningBoltRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.VICIOUS_MOCKERY.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.BESTOW_CURSE.get(), TargetRenderer::new);
        event.registerEntityRenderer(JerotesEntityType.EYEBITE.get(), TargetRenderer::new);
    }
}
