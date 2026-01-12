package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class JerotesTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JerotesWarehouse.MODID);
	public static final RegistryObject<CreativeModeTab> JEROTES_STAB = REGISTRY.register("jerotes_stab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.jerotes.jerotes_stab")).icon(() ->
					new ItemStack(JerotesItems.NETHERITE_OLD_WAR_BEAST_ARMOR.get())).displayItems((parameters, tabData) -> {
				tabData.accept(JerotesItems.AA_CREATIVE_CLAW.get());
				tabData.accept(JerotesItems.AA_EXPLORATION_EYE.get());
				tabData.accept(JerotesItems.AA_SPELL_JAVELIN.get());
				tabData.accept(JerotesItems.TEST_SPAWN_EGG.get());
				tabData.accept(JerotesItems.HUMAN_SPAWN_EGG.get());
				tabData.accept(JerotesItems.JEROTES_PLAYER_SPAWN_EGG.get());

				tabData.accept(JerotesItems.HIGH_STRENGTH_STRING.get());
				tabData.accept(JerotesItems.RESILIENT_BANDAGE.get());
				tabData.accept(JerotesItems.ANESTHETIC_BANDAGE.get());
				tabData.accept(JerotesItems.ANESTHETIC_ARROW.get());
				tabData.accept(JerotesItems.SIMPLE_JAVELIN.get());
				tabData.accept(JerotesItems.HEAL_JAVELIN.get());
				tabData.accept(JerotesItems.ANESTHETIC_JAVELIN.get());
				tabData.accept(JerotesItems.TRANSPORT_JAVELIN.get());
				tabData.accept(JerotesItems.EXPLOSIVE_JAVELIN.get());
				tabData.accept(JerotesItems.WOODEN_JAVELIN.get());
				tabData.accept(JerotesItems.STONE_JAVELIN.get());
				tabData.accept(JerotesItems.COPPER_JAVELIN.get());
				tabData.accept(JerotesItems.IRON_JAVELIN.get());
				tabData.accept(JerotesItems.GOLDEN_JAVELIN.get());
				tabData.accept(JerotesItems.DIAMOND_JAVELIN.get());
				tabData.accept(JerotesItems.NETHERITE_JAVELIN.get());
				tabData.accept(JerotesItems.WOODEN_SPEAR.get());
				tabData.accept(JerotesItems.STONE_SPEAR.get());
				tabData.accept(JerotesItems.COPPER_SPEAR.get());
				tabData.accept(JerotesItems.IRON_SPEAR.get());
				tabData.accept(JerotesItems.GOLDEN_SPEAR.get());
				tabData.accept(JerotesItems.DIAMOND_SPEAR.get());
				tabData.accept(JerotesItems.NETHERITE_SPEAR.get());
				tabData.accept(JerotesItems.BONE_THROWING_SPEAR_OF_SPEAR.get());
				tabData.accept(JerotesItems.BONE_THROWING_SPEAR_OF_JAVELIN.get());
				tabData.accept(JerotesItems.IRON_PIKE.get());
				tabData.accept(JerotesItems.BEAST_ARMOR.get());
				tabData.accept(JerotesItems.WAR_BEAST_ARMOR_BASE.get());
				tabData.accept(JerotesItems.LEATHER_WAR_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.CHAINMAIL_WAR_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.COPPER_WAR_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.IRON_WAR_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.GOLDEN_WAR_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.DIAMOND_WAR_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.NETHERITE_WAR_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.GIANT_BEAST_ARMOR_BASE.get());
				tabData.accept(JerotesItems.LEATHER_GIANT_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.CHAINMAIL_GIANT_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.COPPER_GIANT_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.IRON_GIANT_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.GOLDEN_GIANT_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.DIAMOND_GIANT_BEAST_ARMOR.get());
				tabData.accept(JerotesItems.NETHERITE_GIANT_BEAST_ARMOR.get());
			}).withSearchBar().withBackgroundLocation(
					new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/container/creative_inventory/tab_item_search.png")
			).build());

}
