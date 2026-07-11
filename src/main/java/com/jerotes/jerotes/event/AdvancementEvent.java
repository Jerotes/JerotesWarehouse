package com.jerotes.jerotes.event;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AdvancementEvent {
	public static void AdvancementGive(ServerPlayer player, String string) {
		if (AdvancementGet(player, string) != null) {
			AdvancementProgress advancementProgress = player.getAdvancements().getOrStartProgress(AdvancementGet(player, string));
			if (!advancementProgress.isDone()) {
				for (String criteria : advancementProgress.getRemainingCriteria())
					player.getAdvancements().award(AdvancementGet(player, string), criteria);
			}
		}
	}
	public static boolean AdvancementDone(ServerPlayer player, String string) {
		if (AdvancementGet(player, string) != null) {
			AdvancementProgress advancementProgress = player.getAdvancements().getOrStartProgress(AdvancementGet(player, string));
			return advancementProgress.isDone();
		}
		return false;
	}

	public static Advancement AdvancementGet(ServerPlayer player, String string) {
		Advancement advancementHolder = player.server.getAdvancements().getAdvancement(new ResourceLocation(string));
		return advancementHolder;
	}
}

