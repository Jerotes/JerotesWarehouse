package com.jerotes.jerotes.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public interface JerotesMobEffectTags {
	TagKey<MobEffect> ABACK_AWAY_IMMUNE = create("jerotes:aback_away_immune");
	TagKey<MobEffect> SERPON_FACTION = create("jerotes:serpon_faction");
	TagKey<MobEffect> REMOVE_AFTER_SPELL = create("jerotes:remove_after_spell");
	TagKey<MobEffect> REMOVE_AFTER_ATTACK = create("jerotes:remove_after_attack");
	TagKey<MobEffect> BANDAGE_CAN_REMOVE = create("jerotes:bandage_can_remove");

	private static TagKey<MobEffect> create(String string) {
		return TagKey.create(Registries.MOB_EFFECT, new ResourceLocation(string));
	}
}
