package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.item.FlawlessMagicScroll;
import com.jerotes.jerotes.spell.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;
import java.util.List;

public class JerotesTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JerotesWarehouse.MODID);
	public static final RegistryObject<CreativeModeTab> JEROTES_TAB = REGISTRY.register("jerotes_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.jerotes.jerotes_tab")).icon(() ->
					new ItemStack(JerotesItems.NETHERITE_OLD_WAR_BEAST_ARMOR.get())).displayItems((parameters, tabData) -> {
				tabData.accept(JerotesItems.AA_CREATIVE_CLAW.get());
				tabData.accept(JerotesItems.AA_EXPLORATION_EYE.get());
				tabData.accept(JerotesItems.AA_SPELL_JAVELIN.get());
				tabData.accept(JerotesItems.TEST_SPAWN_EGG.get());
				//tabData.accept(JerotesItems.BIG_BEAST_SPAWN_EGG.get());
				tabData.accept(JerotesItems.HUMAN_SPAWN_EGG.get());
				tabData.accept(JerotesItems.JEROTES_PLAYER_SPAWN_EGG.get());
				tabData.accept(JerotesItems.JEROTES_HORSE_SPAWN_EGG.get());

				tabData.accept(JerotesItems.LEATHER_BOOK_SATCHEL.get());
				tabData.accept(JerotesItems.FLAWLESS_MAGIC_SCROLL.get());
				tabData.accept(JerotesItems.COPPER_CARVED_COMPANY_INVESTIGATION_RECORDS.get());

				tabData.accept(JerotesItems.COPPER_NUGGET.get());
				tabData.accept(JerotesItems.COPPER_SWORD.get());
				tabData.accept(JerotesItems.COPPER_SHOVEL.get());
				tabData.accept(JerotesItems.COPPER_PICKAXE.get());
				tabData.accept(JerotesItems.COPPER_AXE.get());
				tabData.accept(JerotesItems.COPPER_HOE.get());
				tabData.accept(JerotesItems.COPPER_HELMET.get());
				tabData.accept(JerotesItems.COPPER_CHESTPLATE.get());
				tabData.accept(JerotesItems.COPPER_LEGGINGS.get());
				tabData.accept(JerotesItems.COPPER_BOOTS.get());

				tabData.accept(JerotesItems.HIGH_STRENGTH_STRING.get());
				tabData.accept(JerotesItems.RESILIENT_BANDAGE.get());
				tabData.accept(JerotesItems.ANESTHETIC_BANDAGE.get());
				tabData.accept(JerotesItems.ANESTHETIC_ARROW.get());

				tabData.accept(JerotesItems.SIMPLE_DAGGER.get());
				tabData.accept(JerotesItems.WOODEN_DAGGER.get());
				tabData.accept(JerotesItems.STONE_DAGGER.get());
				tabData.accept(JerotesItems.COPPER_DAGGER.get());
				tabData.accept(JerotesItems.IRON_DAGGER.get());
				tabData.accept(JerotesItems.GOLDEN_DAGGER.get());
				tabData.accept(JerotesItems.DIAMOND_DAGGER.get());
				tabData.accept(JerotesItems.NETHERITE_DAGGER.get());

				tabData.accept(JerotesItems.SIMPLE_HAMMER.get());
				tabData.accept(JerotesItems.WOODEN_HAMMER.get());
				tabData.accept(JerotesItems.STONE_HAMMER.get());
				tabData.accept(JerotesItems.COPPER_HAMMER.get());
				tabData.accept(JerotesItems.IRON_HAMMER.get());
				tabData.accept(JerotesItems.GOLDEN_HAMMER.get());
				tabData.accept(JerotesItems.DIAMOND_HAMMER.get());
				tabData.accept(JerotesItems.NETHERITE_HAMMER.get());

				tabData.accept(JerotesItems.SIMPLE_GREATSWORD.get());
				tabData.accept(JerotesItems.WOODEN_GREATSWORD.get());
				tabData.accept(JerotesItems.STONE_GREATSWORD.get());
				tabData.accept(JerotesItems.COPPER_GREATSWORD.get());
				tabData.accept(JerotesItems.IRON_GREATSWORD.get());
				tabData.accept(JerotesItems.GOLDEN_GREATSWORD.get());
				tabData.accept(JerotesItems.DIAMOND_GREATSWORD.get());
				tabData.accept(JerotesItems.NETHERITE_GREATSWORD.get());

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
				tabData.accept(JerotesItems.SIMPLE_PIKE.get());
				tabData.accept(JerotesItems.WOODEN_PIKE.get());
				tabData.accept(JerotesItems.STONE_PIKE.get());
				tabData.accept(JerotesItems.COPPER_PIKE.get());
				tabData.accept(JerotesItems.IRON_PIKE.get());
				tabData.accept(JerotesItems.GOLDEN_PIKE.get());
				tabData.accept(JerotesItems.DIAMOND_PIKE.get());
				tabData.accept(JerotesItems.NETHERITE_PIKE.get());
				tabData.accept(JerotesItems.IRON_PARRY_SHIELD.get());
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

	private static final Item[] LEVEL_WOOL = {
			Items.WHITE_WOOL,
			Items.RED_WOOL,
			Items.ORANGE_WOOL,
			Items.YELLOW_WOOL,
			Items.LIME_WOOL,
			Items.LIGHT_BLUE_WOOL,
			Items.BLUE_WOOL,
			Items.CYAN_WOOL,
			Items.PURPLE_WOOL,
			Items.BLACK_WOOL,
	};
	public static final RegistryObject<CreativeModeTab> JEROTES_TAB_FLAWLESS_MAGIC_SCROLL_TAB = REGISTRY.register("jerotes_tab_flawless_magic_scroll_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.jerotes.jerotes_tab_flawless_magic_scroll_tab")).icon(() ->
					new ItemStack(JerotesItems.FLAWLESS_MAGIC_SCROLL.get())).displayItems((parameters, tabData) -> {
				boolean isSearch = parameters.hasPermissions();
				List<String> spells = SpellRegistry.getRegisteredSpellIds().stream()
						.filter(id -> !id.equals("jerotes_empty"))
						.sorted(
								Comparator
										.comparingInt((String id) -> {
											MagicSpell spell = SpellListByString.getSpell(1, null, null, SpellRegistry.getSpellTypeById(id));
											return spell.isCantrip() ? 0 : spell.getBaseSpellLevel();
										})
										.thenComparingInt(id -> {
											MagicSpell spell = SpellListByString.getSpell(1, null, null, SpellRegistry.getSpellTypeById(id));
											return spell.getSpellSchool().getSortIndex();
										})
						)
						.toList();

				int currentLevel = -1;
				int column = 0;
				int separatorId = 0;

				for (String spellId : spells) {
					MagicSpell spell = SpellListByString.getSpell(1, null, null, SpellRegistry.getSpellTypeById(spellId));
					int level = spell.isCantrip() ? 0 : spell.getBaseSpellLevel();

					//等级变化且上一行没满
					if (currentLevel != -1 && currentLevel != level && column != 0) {
						while (column < 8) {
							tabData.accept(levelWool(
									LEVEL_WOOL[Math.min(currentLevel, LEVEL_WOOL.length - 1)],
									separatorId++
							), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
							column++;
						}

						tabData.accept(levelWool(
								LEVEL_WOOL[Math.min(currentLevel, LEVEL_WOOL.length - 1)],
								separatorId++
						), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);

						column = 0;
					}

					currentLevel = level;

					FlawlessMagicScroll(tabData, spellId);
					column++;

					if (column == 8) {
						tabData.accept(levelWool(
								LEVEL_WOOL[Math.min(currentLevel, LEVEL_WOOL.length - 1)],
								separatorId++
						), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
						column = 0;
					}
				}

				if (column != 0) {
					while (column < 8) {
						tabData.accept(levelWool(
								LEVEL_WOOL[Math.min(currentLevel, LEVEL_WOOL.length - 1)],
								separatorId++
						), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
						column++;
					}

					tabData.accept(levelWool(
							LEVEL_WOOL[Math.min(currentLevel, LEVEL_WOOL.length - 1)],
							separatorId++
					), CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
				}

				parameters.holders().lookup(Registries.ITEM).ifPresent(lookup -> {
					TagKey<Item> customTag = TagKey.create(Registries.ITEM, new ResourceLocation(JerotesWarehouse.MODID, "magic_summoned_spawn_egg"));
					lookup.get(customTag).ifPresent(holders -> {holders.forEach(holder -> holder.unwrapKey().ifPresent(key -> tabData.accept(holder.value())));});
				});

				for (String spellId : spells) {
					MagicSpell spell = SpellListByString.getSpell(1, null, null, SpellRegistry.getSpellTypeById(spellId));
					if (spell == null) continue;
					int minLevel = spell.getBaseSpellLevel();
					int maxLevel = 9;
					for (int level = minLevel; level <= maxLevel; level++) {
						ItemStack stack = FlawlessMagicScrollLevel(tabData, spellId, level);
						tabData.accept(stack, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
					}
				}
			}).withSearchBar().build());

	private static ItemStack levelWool(Item item, int index) {
		ItemStack stack = new ItemStack(item);
		stack.getOrCreateTag().putInt("CreativeSeparator", index);
		return stack;
	}

	public static ItemStack FlawlessMagicScrollLevel(CreativeModeTab.Output tabData, String string, int level) {
		ItemStack stack = new ItemStack(JerotesItems.FLAWLESS_MAGIC_SCROLL.get());
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString(FlawlessMagicScroll.SPELL, string);
		MagicSpell spell = SpellListByString.getSpell(1, null, null, SpellRegistry.getSpellTypeById(string));
		compoundTag.putInt(FlawlessMagicScroll.SPELL_LEVEL, level);
		stack.setTag(compoundTag);
		return stack;
	}
	public static void FlawlessMagicScroll(CreativeModeTab.Output tabData, String string) {
		ItemStack stack = new ItemStack(JerotesItems.FLAWLESS_MAGIC_SCROLL.get());
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString(FlawlessMagicScroll.SPELL, string);
		MagicSpell spell = SpellListByString.getSpell(1, null, null, SpellRegistry.getSpellTypeById(string));
		compoundTag.putInt(FlawlessMagicScroll.SPELL_LEVEL, spell == null ? 1 : spell.getBaseSpellLevel());
		stack.setTag(compoundTag);
		tabData.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
	}
}
