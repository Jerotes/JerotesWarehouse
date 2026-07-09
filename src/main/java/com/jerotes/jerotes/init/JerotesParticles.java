package com.jerotes.jerotes.init;

import com.jerotes.jerotes.client.particle.*;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JerotesParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(JerotesParticleTypes.NULL.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ABACK.get(), SymbolParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.BLOOD.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ANESTHETIZED_I.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ANESTHETIZED_II.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ANESTHETIZED_III.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ANESTHETIZED_IV.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ANESTHETIZED_V.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ANESTHETIZED_VI.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ANESTHETIZED_VII.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ANESTHETIZED_VIII.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.TARGET.get(), TargetOtherParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.SUMMON_PARTICLE.get(), SummonParticle.Provider::create);

		event.registerSpriteSet(JerotesParticleTypes.MAGIC_MISSILE.get(), MagicMissileParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.POISON_BREATH.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.POISON_BREATH_FOG.get(), FogParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.RAY_OF_SICKNESS.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.RAY_OF_ENFEEBLEMENT.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.LIGHTNING_BOLT.get(), LargeShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.VICIOUS_MOCKERY.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.HOLD_PERSON.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.BESTOW_CURSE.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.PHANTASMAL_KILLER.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.EYEBITE.get(), ShootParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.BEAST_TOUGH_BREAK.get(), BigGlowDisplayParticle.Provider::new);

		event.registerSpriteSet(JerotesParticleTypes.MAGIC_MISSILE_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.POISON_BREATH_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.RAY_OF_SICKNESS_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.RAY_OF_ENFEEBLEMENT_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.LIGHTNING_BOLT_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.VICIOUS_MOCKERY_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.HOLD_PERSON_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.BESTOW_CURSE_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.PHANTASMAL_KILLER_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.EYEBITE_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.FIRE_ABSORPTION_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.FREEZE_ABSORPTION_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.LIGHTNING_ABSORPTION_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.CURE_WOUNDS_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.MISTY_STEP_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.MIRROR_IMAGE_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.INVISIBLE_PASSAGE_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.COUNTERSPELL_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.MAGIC_ABSORPTION_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.BEAST_TOUGH_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.ACTION_SURGE_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.CLOUD_OF_DAGGERS_DISPLAY.get(), DisplayParticle::provider);
		event.registerSpriteSet(JerotesParticleTypes.FIREBALL_DISPLAY.get(), DisplayParticle::provider);
	}
}
