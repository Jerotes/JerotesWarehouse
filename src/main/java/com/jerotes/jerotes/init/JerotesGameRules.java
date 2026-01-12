package com.jerotes.jerotes.init;

import com.jerotes.jerotes.config.MainConfig;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JerotesGameRules {
	public static GameRules.Key<GameRules.BooleanValue> JEROTES_SHIELD_CAN_BREAK;
	public static GameRules.Key<GameRules.BooleanValue> JEROTES_ARMOR_CAN_BREAK;
	public static GameRules.Key<GameRules.BooleanValue> JEROTES_MELEE_CAN_BREAK;
	public static GameRules.Key<GameRules.BooleanValue> JEROTES_RANGE_CAN_BREAK;
	public static GameRules.Key<GameRules.BooleanValue> JEROTES_MAGIC_CAN_BREAK;
	@SubscribeEvent
	public static void registerGameRules(FMLCommonSetupEvent event) {
		JEROTES_SHIELD_CAN_BREAK = GameRules.register("JerotesShieldCanBreak", GameRules.Category.MOBS, GameRules.BooleanValue.create(MainConfig.ShieldCanBreakBaseInGameRule));
		JEROTES_ARMOR_CAN_BREAK = GameRules.register("JerotesArmorCanBreak", GameRules.Category.MOBS, GameRules.BooleanValue.create(MainConfig.ArmorCanBreakBaseInGameRule));
		JEROTES_MELEE_CAN_BREAK = GameRules.register("JerotesMeleeCanBreak", GameRules.Category.MOBS, GameRules.BooleanValue.create(MainConfig.MeleeCanBreakBaseInGameRule));
		JEROTES_RANGE_CAN_BREAK = GameRules.register("JerotesRangeCanBreak", GameRules.Category.MOBS, GameRules.BooleanValue.create(MainConfig.RangeCanBreakBaseInGameRule));
		JEROTES_MAGIC_CAN_BREAK = GameRules.register("JerotesMagicCanBreak", GameRules.Category.MOBS, GameRules.BooleanValue.create(MainConfig.RangeCanBreakBaseInGameRule));
	}
}
