package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.particle.SummonParticleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JerotesParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, JerotesWarehouse.MODID);
	public static final RegistryObject<SimpleParticleType> NULL = REGISTRY.register("null", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ABACK = REGISTRY.register("aback", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> BLOOD = REGISTRY.register("blood", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ANESTHETIZED_I = REGISTRY.register("anesthetized_1", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ANESTHETIZED_II = REGISTRY.register("anesthetized_2", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ANESTHETIZED_III = REGISTRY.register("anesthetized_3", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ANESTHETIZED_IV = REGISTRY.register("anesthetized_4", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ANESTHETIZED_V = REGISTRY.register("anesthetized_5", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ANESTHETIZED_VI = REGISTRY.register("anesthetized_6", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ANESTHETIZED_VII = REGISTRY.register("anesthetized_7", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ANESTHETIZED_VIII = REGISTRY.register("anesthetized_8", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> TARGET = REGISTRY.register("target", () -> new SimpleParticleType(true));
	public static final RegistryObject<ParticleType<SummonParticleOptions>> SUMMON_PARTICLE = REGISTRY.register("summon", () -> new ParticleType<SummonParticleOptions>(false, SummonParticleOptions.DESERIALIZER) {
		@Override
		public Codec<SummonParticleOptions> codec() {
			return null;
		}
	});

	public static final RegistryObject<SimpleParticleType> MAGIC_MISSILE = REGISTRY.register("magic_missile", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> POISON_BREATH = REGISTRY.register("poison_breath", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> POISON_BREATH_FOG = REGISTRY.register("poison_breath_fog", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> RAY_OF_SICKNESS = REGISTRY.register("ray_of_sickness", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> RAY_OF_ENFEEBLEMENT = REGISTRY.register("ray_of_enfeeblement", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> LIGHTNING_BOLT = REGISTRY.register("lightning_bolt", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> VICIOUS_MOCKERY = REGISTRY.register("vicious_mockery", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> HOLD_PERSON = REGISTRY.register("hold_person", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> BESTOW_CURSE = REGISTRY.register("bestow_curse", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> PHANTASMAL_KILLER = REGISTRY.register("phantasmal_killer", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> EYEBITE = REGISTRY.register("eyebite", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> BEAST_TOUGH_BREAK = REGISTRY.register("beast_tough_break", () -> new SimpleParticleType(true));

	public static final RegistryObject<SimpleParticleType> MAGIC_MISSILE_DISPLAY = REGISTRY.register("magic_missile_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> POISON_BREATH_DISPLAY = REGISTRY.register("poison_breath_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> RAY_OF_SICKNESS_DISPLAY = REGISTRY.register("ray_of_sickness_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> RAY_OF_ENFEEBLEMENT_DISPLAY = REGISTRY.register("ray_of_enfeeblement_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> LIGHTNING_BOLT_DISPLAY = REGISTRY.register("lightning_bolt_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> VICIOUS_MOCKERY_DISPLAY = REGISTRY.register("vicious_mockery_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> HOLD_PERSON_DISPLAY = REGISTRY.register("hold_person_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> BESTOW_CURSE_DISPLAY = REGISTRY.register("bestow_curse_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> PHANTASMAL_KILLER_DISPLAY = REGISTRY.register("phantasmal_killer_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> EYEBITE_DISPLAY = REGISTRY.register("eyebite_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> FIRE_ABSORPTION_DISPLAY = REGISTRY.register("fire_absorption_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> FREEZE_ABSORPTION_DISPLAY = REGISTRY.register("freeze_absorption_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> LIGHTNING_ABSORPTION_DISPLAY = REGISTRY.register("lightning_absorption_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> CURE_WOUNDS_DISPLAY = REGISTRY.register("cure_wounds_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> MISTY_STEP_DISPLAY = REGISTRY.register("misty_step_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> MIRROR_IMAGE_DISPLAY = REGISTRY.register("mirror_image_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> INVISIBLE_PASSAGE_DISPLAY = REGISTRY.register("invisible_passage_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> COUNTERSPELL_DISPLAY = REGISTRY.register("counterspell_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> MAGIC_ABSORPTION_DISPLAY = REGISTRY.register("magic_absorption_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> BEAST_TOUGH_DISPLAY = REGISTRY.register("beast_tough_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> ACTION_SURGE_DISPLAY = REGISTRY.register("action_surge_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> CLOUD_OF_DAGGERS_DISPLAY = REGISTRY.register("cloud_of_daggers_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> FIREBALL_DISPLAY = REGISTRY.register("fireball_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> LINEAR_EVOKER_FANG_DISPLAY = REGISTRY.register("linear_evoker_fang_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> CIRCULAR_EVOKER_FANG_DISPLAY = REGISTRY.register("circular_evoker_fang_display", () -> new SimpleParticleType(true));
	public static final RegistryObject<SimpleParticleType> CONJURE_VEX_DISPLAY = REGISTRY.register("conjure_vex_display", () -> new SimpleParticleType(true));
}
