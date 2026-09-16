package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class JerotesDamageTypes {
	private final Registry<DamageType> damageTypes;
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_SHOOT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_shoot"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_melee"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_AND_NO_KNOCKBACK_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_and_no_knockback_melee"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_magic"));
	public static final ResourceKey<DamageType> SYRINGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "syringe"));
	public static final ResourceKey<DamageType> SPEAR = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "spear"));
	public static final ResourceKey<DamageType> MAGIC_EFFECT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "magic_effect"));
	public static final ResourceKey<DamageType> BLEED = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bleed"));
	public static final ResourceKey<DamageType> CORROSION = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "corrosion"));
	public static final ResourceKey<DamageType> POISON = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "poison"));
	public static final ResourceKey<DamageType> RADIANT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "radiant"));
	public static final ResourceKey<DamageType> NECROTIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "necrotic"));
	public static final ResourceKey<DamageType> CURSED_ADDITIONAL_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "cursed_additional_damage"));
	public static final ResourceKey<DamageType> PSYCHIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "psychic"));
	public static final ResourceKey<DamageType> THUNDER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "thunder"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_THUNDER = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_thunder"));
	public static final ResourceKey<DamageType> FORCE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "force"));
	public static final ResourceKey<DamageType> ACID = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "acid"));

	public static final ResourceKey<DamageType> BYPASSES_ARMOR_SHOOT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_armor_shoot"));
	public static final ResourceKey<DamageType> BYPASSES_ARMOR_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_armor_melee"));
	public static final ResourceKey<DamageType> BYPASSES_SHIELD_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_shield_melee"));
	public static final ResourceKey<DamageType> BYPASSES_ARMOR_AND_SHIELD_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_armor_and_shield_melee"));

	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_ACID = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_acid"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_BLEED = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_bleed"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_CORROSION = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_corrosion"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_DROWN = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_drown"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_fire"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_FORCE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_force"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_FREEZE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_freeze"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_LIGHTNING = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_lightning"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_NECROTIC = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_necrotic"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_POISON = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_poison"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_PSYCHIC = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_psychic"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_RADIANT = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_radiant"));

	public static final ResourceKey<DamageType> FIRE_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "fire_melee"));
	public static final ResourceKey<DamageType> FREEZE_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "freeze_melee"));
	public static final ResourceKey<DamageType> LIGHTNING_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "lightning_melee"));
	public static final ResourceKey<DamageType> DROWN_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "drown_melee"));
	public static final ResourceKey<DamageType> FIRE_SHOOT = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "fire_shoot"));
	public static final ResourceKey<DamageType> FREEZE_SHOOT = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "freeze_shoot"));
	public static final ResourceKey<DamageType> LIGHTNING_SHOOT = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "lightning_shoot"));
	public static final ResourceKey<DamageType> DROWN_SHOOT = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "drown_shoot"));
	public static final ResourceKey<DamageType> FIRE_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "fire_magic"));
	public static final ResourceKey<DamageType> FREEZE_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "freeze_magic"));
	public static final ResourceKey<DamageType> LIGHTNING_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "lightning_magic"));
	public static final ResourceKey<DamageType> DROWN_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "drown_magic"));



	public JerotesDamageTypes(RegistryAccess registryAccess) {
		this.damageTypes = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
	}

	private DamageSource source(ResourceKey<DamageType> resourceKey) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(resourceKey));
	}
	private DamageSource source(ResourceKey<DamageType> resourceKey, @Nullable Entity entity, @Nullable Entity entity2) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(resourceKey), entity, entity2);
	}
	private DamageSource source(ResourceKey<DamageType> resourceKey, @Nullable Entity entity) {
		return new DamageSource(this.damageTypes.getHolderOrThrow(resourceKey), entity);
	}

	public DamageSource bypasses_cooldown_shoot() {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_SHOOT);
	}
	public DamageSource bypasses_cooldown_shoot(Entity entity) {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_SHOOT, entity);
	}
	public DamageSource bypasses_cooldown_shoot(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_SHOOT, entity, entity2);
	}
	public DamageSource bypasses_cooldown_melee() {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_MELEE);
	}
	public DamageSource bypasses_cooldown_melee(Entity entity) {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_MELEE, entity);
	}
	public DamageSource bypasses_cooldown_melee(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_MELEE, entity, entity2);
	}
	public DamageSource bypasses_cooldown_magic() {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_MAGIC);
	}
	public DamageSource bypasses_cooldown_magic(Entity entity) {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_MAGIC, entity);
	}
	public DamageSource bypasses_cooldown_magic(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.BYPASSES_COOLDOWN_MAGIC, entity, entity2);
	}

	public DamageSource spear(Entity entity) {
		return this.source(JerotesDamageTypes.SPEAR, entity);
	}

	public DamageSource syringe(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.SYRINGE, entity, entity2);
	}
	public DamageSource syringe(Entity entity) {
		return this.source(JerotesDamageTypes.SYRINGE, entity);
	}

	public DamageSource magic_effect(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.MAGIC_EFFECT, entity, entity2);
	}

	public DamageSource bleed() {
		return this.source(JerotesDamageTypes.BLEED);
	}
	public DamageSource bleed(Entity entity) {
		return this.source(JerotesDamageTypes.BLEED, entity);
	}
	public DamageSource bleed(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.BLEED, entity, entity2);
	}
	public DamageSource corrosion() {
		return this.source(JerotesDamageTypes.CORROSION);
	}
	public DamageSource corrosion(Entity entity) {
		return this.source(JerotesDamageTypes.CORROSION, entity);
	}
	public DamageSource corrosion(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.CORROSION, entity, entity2);
	}
	public DamageSource poison() {
		return this.source(JerotesDamageTypes.POISON);
	}
	public DamageSource poison(Entity entity) {
		return this.source(JerotesDamageTypes.POISON, entity);
	}
	public DamageSource poison(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.POISON, entity, entity2);
	}
}