package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.effect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JerotesMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, JerotesWarehouse.MODID);
	public static final RegistryObject<MobEffect> BLEEDING = REGISTRY.register("bleeding", () -> new BleedingMobEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "a82c37ee-5eae-467b-a951-64f57a6fabf4", -0.05, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_SPEED, "e38f1ba6-b618-4a73-9925-e81e13a42c1b", -0.05, AttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<MobEffect> ANESTHETIZED = REGISTRY.register("anesthetized", () -> new AnesthetizedMobEffect());
	public static final RegistryObject<MobEffect> ANESTHETIZED_HOLD = REGISTRY.register("anesthetized_hold", () -> new AnesthetizedHoldMobEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "3F453FB1-8839-5C76-9F53-F4D3E5F09A94", -5, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.FLYING_SPEED, "6E4DCFE3-E7E5-81E7-4511-3257AECFF1D7", -5, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.JUMP_STRENGTH, "ED35696B-C393-0685-EC01-D3D5C2D9107F", -5, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "AE438D6F-85CD-FCA0-6047-CBDDA8BEDD95", -60, AttributeModifier.Operation.ADDITION));
	public static final RegistryObject<MobEffect> ENSLAVEMENT = REGISTRY.register("enslavement", () -> new EnslavementMobEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "31F2026E-1894-8C79-23F5-A126E9A61B9F", -5, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.FLYING_SPEED, "A7F9C83C-3985-F513-1A19-0B9031C98BCC", -5, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.JUMP_STRENGTH, "32718860-6562-594E-3B9B-4898BD10EA27", -5, AttributeModifier.Operation.ADDITION));
	public static final RegistryObject<MobEffect> CORROSIVE = REGISTRY.register("corrosive", () -> new CorrosiveMobEffect()
			.addAttributeModifier(Attributes.ARMOR, "E7391E98-2919-5988-07A7-1782F8BB7E70", -3.0, AttributeModifier.Operation.ADDITION));
	public static final RegistryObject<MobEffect> DEADLY_POISON = REGISTRY.register("deadly_poison", () -> new DeadlyPoisonMobEffect());
	public static final RegistryObject<MobEffect> TRUESIGHT = REGISTRY.register("truesight", () -> new TruesightMobEffect());
	public static final RegistryObject<MobEffect> HOLD_MOB = REGISTRY.register("hold_mob", () -> new HoldMobEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "0235DCB5-D24D-1362-A042-BF3D59251E15", -6000, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "29B4FD71-D25D-EEB3-FD56-9CCC8E12AF60", -6000, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ATTACK_SPEED, "973d07cb-137f-41fc-bb2e-9eef1eb71372", -6000, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.FLYING_SPEED, "AC8E9123-E7C8-8A70-2901-8B5BACB69B28", -6000, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.JUMP_STRENGTH, "41996320-2460-36B5-83CD-70963400BC43", -6000, AttributeModifier.Operation.ADDITION));
	public static final RegistryObject<MobEffect> PHANTASMAL_KILLER = REGISTRY.register("phantasmal_killer", () -> new PhantasmalKillerEffect());
	public static final RegistryObject<MobEffect> MAGIC_ABSORPTION = REGISTRY.register("magic_absorption", () -> new MagicAbsorptionMobEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "ee603ca4-691c-4234-976e-f409bd2a6b27", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "9d42eebf-9ede-4416-8250-ab6d7491eff6", 3.0, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(Attributes.ATTACK_SPEED, "830DCB3B-D1E5-57D0-70F4-88F25ED516BA", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL));
	public static final RegistryObject<MobEffect> PRURITUS = REGISTRY.register("pruritus", () -> new PruritusMobEffect());
	public static final RegistryObject<MobEffect> ABACK = REGISTRY.register("aback", () -> new AbackMobEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, "B086F026-DE1D-E150-9139-A0D8B7E94E51", -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL)
			.addAttributeModifier(Attributes.ATTACK_DAMAGE, "0843A0D6-2F26-40A1-EF39-FEA3E03C1433", -4.0, AttributeModifier.Operation.ADDITION));
	public static final RegistryObject<MobEffect> QUAKE = REGISTRY.register("quake", () -> new QuakeMobEffect());
	public static final RegistryObject<MobEffect> CLOAKING = REGISTRY.register("cloaking", () -> new CloakingMobEffect());
	public static final RegistryObject<MobEffect> FIRE_ABSORPTION = REGISTRY.register("fire_absorption", () -> new FireAbsorptionMobEffect());
	public static final RegistryObject<MobEffect> FREEZE_ABSORPTION = REGISTRY.register("freeze_absorption", () -> new FreezeAbsorptionMobEffect());
	public static final RegistryObject<MobEffect> LIGHTNING_ABSORPTION = REGISTRY.register("lightning_absorption", () -> new LightningAbsorptionMobEffect());
	public static final RegistryObject<MobEffect> INVISIBLE_PASSAGE = REGISTRY.register("invisible_passage", () -> new InvisiblePassageMobEffect());
	public static final RegistryObject<MobEffect> COUNTERSPELL = REGISTRY.register("counterspell", () -> new CounterspellMobEffect());
	public static final RegistryObject<MobEffect> FOG = REGISTRY.register("fog", () -> new FogMobEffect());
	public static final RegistryObject<MobEffect> ANALYTICAL_EYE = REGISTRY.register("analytical_eye", () -> new AnalyticalEyeMobEffect());
	public static final RegistryObject<MobEffect> EXPLOSION = REGISTRY.register("explosion", () -> new ExplosionMobEffect());

	public static final RegistryObject<MobEffect> MORE_TOUCH = REGISTRY.register("more_touch", () -> new MoreTouchMobEffect()
			.addAttributeModifier(ForgeMod.ENTITY_REACH.get(), "72b41884-b2e4-47c4-a852-b3ddf9397c31", 1.0, AttributeModifier.Operation.ADDITION)
			.addAttributeModifier(ForgeMod.BLOCK_REACH.get(), "c19adc73-52ab-437a-ac0f-2fb982141e4c", 1.0, AttributeModifier.Operation.ADDITION));
}