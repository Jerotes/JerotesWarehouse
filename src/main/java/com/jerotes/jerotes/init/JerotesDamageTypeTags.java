package com.jerotes.jerotes.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public interface JerotesDamageTypeTags {
	TagKey<DamageType> MAGIC_RESISTANT = create("jerotes:magic_resistant");
	TagKey<DamageType> IS_MELEE = create("jerotes:is_melee");

	private static TagKey<DamageType> create(String string) {
		return TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(string));
	}
}
