package com.jerotes.jerotes.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public interface JerotesDamageTypeTags {
	TagKey<DamageType> IS_MAGIC = create("jerotes:is_magic");
	TagKey<DamageType> IS_MELEE = create("jerotes:is_melee");
	TagKey<DamageType> IS_NECROTIC = create("jerotes:is_necrotic");
	TagKey<DamageType> IS_THUNDER = create("jerotes:is_thunder");
	TagKey<DamageType> IS_CORROSION = create("jerotes:is_corrosion");
	TagKey<DamageType> IS_FORCE = create("jerotes:is_force");
	TagKey<DamageType> IS_TRUE_MAGIC = create("jerotes:is_true_magic");
	TagKey<DamageType> IS_POISON = create("jerotes:is_poison");
	TagKey<DamageType> IS_PSYCHIC = create("jerotes:is_psychic");
	TagKey<DamageType> IS_RADIANT = create("jerotes:is_radiant");
	TagKey<DamageType> IS_BLEED = create("jerotes:is_bleed");
	TagKey<DamageType> IS_WITHER = create("jerotes:is_wither");

	private static TagKey<DamageType> create(String string) {
		return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(string));
	}
}
