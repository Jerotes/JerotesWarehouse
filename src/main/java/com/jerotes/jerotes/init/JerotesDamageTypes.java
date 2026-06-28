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
	public static final ResourceKey<DamageType> CLOUD_OF_DAGGERS = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "cloud_of_daggers"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_MAGIC = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_magic"));
	public static final ResourceKey<DamageType> SYRINGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "syringe"));
	public static final ResourceKey<DamageType> SPEAR = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "spear"));
	public static final ResourceKey<DamageType> MAGIC_EFFECT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "magic_effect"));
	public static final ResourceKey<DamageType> BLEEDING = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bleeding"));
	public static final ResourceKey<DamageType> CORROSIVE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "corrosive"));
	public static final ResourceKey<DamageType> POISON = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "poison"));

	public static final ResourceKey<DamageType> BYPASSES_ARMOR_SHOOT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_armor_shoot"));
	public static final ResourceKey<DamageType> BYPASSES_ARMOR_MELEE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(JerotesWarehouse.MODID, "bypasses_armor_melee"));

	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_FIRE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_fire"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_FREEZE = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_freeze"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_LIGHTNING = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_lightning"));
	public static final ResourceKey<DamageType> BYPASSES_COOLDOWN_DROWN = ResourceKey.create(Registries.DAMAGE_TYPE,
			new ResourceLocation(JerotesWarehouse.MODID, "bypasses_cooldown_drown"));
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

	public DamageSource bleeding() {
		return this.source(JerotesDamageTypes.BLEEDING);
	}
	public DamageSource bleeding(Entity entity) {
		return this.source(JerotesDamageTypes.BLEEDING, entity);
	}
	public DamageSource bleeding(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.BLEEDING, entity, entity2);
	}
	public DamageSource corrosive() {
		return this.source(JerotesDamageTypes.CORROSIVE);
	}
	public DamageSource corrosive(Entity entity) {
		return this.source(JerotesDamageTypes.CORROSIVE, entity);
	}
	public DamageSource corrosive(Entity entity, @Nullable Entity entity2) {
		return this.source(JerotesDamageTypes.CORROSIVE, entity, entity2);
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