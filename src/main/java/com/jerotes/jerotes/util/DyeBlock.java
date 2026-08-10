package com.jerotes.jerotes.util;

import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.*;
import com.jerotes.jerotes.entity.Shoot.Magic.MagicAbout;
import com.jerotes.jerotes.forge.JerotesMeleeDamageFromMainHandIsOffHandEvent;
import com.jerotes.jerotes.forge.JerotesMerorDamageEvent;
import com.jerotes.jerotes.init.JerotesDamageTypeTags;
import com.jerotes.jerotes.init.JerotesMobEffectTags;
import com.jerotes.jerotes.network.JerotesPlayerData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class DyeBlock {
	private static Block getSpecialDyedVariant(Block block, DyeColor color) {
		//混凝土
		if (block == Blocks.WHITE_CONCRETE || block == Blocks.ORANGE_CONCRETE || block == Blocks.MAGENTA_CONCRETE
				|| block == Blocks.LIGHT_BLUE_CONCRETE || block == Blocks.YELLOW_CONCRETE
				|| block == Blocks.LIME_CONCRETE || block == Blocks.PINK_CONCRETE
				|| block == Blocks.GRAY_CONCRETE || block == Blocks.LIGHT_GRAY_CONCRETE
				|| block == Blocks.CYAN_CONCRETE || block == Blocks.PURPLE_CONCRETE
				|| block == Blocks.BLUE_CONCRETE || block == Blocks.BROWN_CONCRETE
				|| block == Blocks.GREEN_CONCRETE || block == Blocks.RED_CONCRETE
				|| block == Blocks.BLACK_CONCRETE) {
			return switch (color) {
				case WHITE -> Blocks.WHITE_CONCRETE;
				case ORANGE -> Blocks.ORANGE_CONCRETE;
				case MAGENTA -> Blocks.MAGENTA_CONCRETE;
				case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE;
				case YELLOW -> Blocks.YELLOW_CONCRETE;
				case LIME -> Blocks.LIME_CONCRETE;
				case PINK -> Blocks.PINK_CONCRETE;
				case GRAY -> Blocks.GRAY_CONCRETE;
				case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE;
				case CYAN -> Blocks.CYAN_CONCRETE;
				case PURPLE -> Blocks.PURPLE_CONCRETE;
				case BLUE -> Blocks.BLUE_CONCRETE;
				case BROWN -> Blocks.BROWN_CONCRETE;
				case GREEN -> Blocks.GREEN_CONCRETE;
				case RED -> Blocks.RED_CONCRETE;
				case BLACK -> Blocks.BLACK_CONCRETE;
			};
		}

		//混凝土粉
		if (block == Blocks.WHITE_CONCRETE_POWDER || block == Blocks.ORANGE_CONCRETE_POWDER || block == Blocks.MAGENTA_CONCRETE_POWDER
				|| block == Blocks.LIGHT_BLUE_CONCRETE_POWDER || block == Blocks.YELLOW_CONCRETE_POWDER
				|| block == Blocks.LIME_CONCRETE_POWDER || block == Blocks.PINK_CONCRETE_POWDER
				|| block == Blocks.GRAY_CONCRETE_POWDER || block == Blocks.LIGHT_GRAY_CONCRETE_POWDER
				|| block == Blocks.CYAN_CONCRETE_POWDER || block == Blocks.PURPLE_CONCRETE_POWDER
				|| block == Blocks.BLUE_CONCRETE_POWDER || block == Blocks.BROWN_CONCRETE_POWDER
				|| block == Blocks.GREEN_CONCRETE_POWDER || block == Blocks.RED_CONCRETE_POWDER
				|| block == Blocks.BLACK_CONCRETE_POWDER) {
			return switch (color) {
				case WHITE -> Blocks.WHITE_CONCRETE_POWDER;
				case ORANGE -> Blocks.ORANGE_CONCRETE_POWDER;
				case MAGENTA -> Blocks.MAGENTA_CONCRETE_POWDER;
				case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE_POWDER;
				case YELLOW -> Blocks.YELLOW_CONCRETE_POWDER;
				case LIME -> Blocks.LIME_CONCRETE_POWDER;
				case PINK -> Blocks.PINK_CONCRETE_POWDER;
				case GRAY -> Blocks.GRAY_CONCRETE_POWDER;
				case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE_POWDER;
				case CYAN -> Blocks.CYAN_CONCRETE_POWDER;
				case PURPLE -> Blocks.PURPLE_CONCRETE_POWDER;
				case BLUE -> Blocks.BLUE_CONCRETE_POWDER;
				case BROWN -> Blocks.BROWN_CONCRETE_POWDER;
				case GREEN -> Blocks.GREEN_CONCRETE_POWDER;
				case RED -> Blocks.RED_CONCRETE_POWDER;
				case BLACK -> Blocks.BLACK_CONCRETE_POWDER;
			};
		}

		//带釉陶瓦
		if (block == Blocks.WHITE_GLAZED_TERRACOTTA || block == Blocks.ORANGE_GLAZED_TERRACOTTA || block == Blocks.MAGENTA_GLAZED_TERRACOTTA
				|| block == Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA || block == Blocks.YELLOW_GLAZED_TERRACOTTA
				|| block == Blocks.LIME_GLAZED_TERRACOTTA || block == Blocks.PINK_GLAZED_TERRACOTTA
				|| block == Blocks.GRAY_GLAZED_TERRACOTTA || block == Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA
				|| block == Blocks.CYAN_GLAZED_TERRACOTTA || block == Blocks.PURPLE_GLAZED_TERRACOTTA
				|| block == Blocks.BLUE_GLAZED_TERRACOTTA || block == Blocks.BROWN_GLAZED_TERRACOTTA
				|| block == Blocks.GREEN_GLAZED_TERRACOTTA || block == Blocks.RED_GLAZED_TERRACOTTA
				|| block == Blocks.BLACK_GLAZED_TERRACOTTA) {
			return switch (color) {
				case WHITE -> Blocks.WHITE_GLAZED_TERRACOTTA;
				case ORANGE -> Blocks.ORANGE_GLAZED_TERRACOTTA;
				case MAGENTA -> Blocks.MAGENTA_GLAZED_TERRACOTTA;
				case LIGHT_BLUE -> Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA;
				case YELLOW -> Blocks.YELLOW_GLAZED_TERRACOTTA;
				case LIME -> Blocks.LIME_GLAZED_TERRACOTTA;
				case PINK -> Blocks.PINK_GLAZED_TERRACOTTA;
				case GRAY -> Blocks.GRAY_GLAZED_TERRACOTTA;
				case LIGHT_GRAY -> Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA;
				case CYAN -> Blocks.CYAN_GLAZED_TERRACOTTA;
				case PURPLE -> Blocks.PURPLE_GLAZED_TERRACOTTA;
				case BLUE -> Blocks.BLUE_GLAZED_TERRACOTTA;
				case BROWN -> Blocks.BROWN_GLAZED_TERRACOTTA;
				case GREEN -> Blocks.GREEN_GLAZED_TERRACOTTA;
				case RED -> Blocks.RED_GLAZED_TERRACOTTA;
				case BLACK -> Blocks.BLACK_GLAZED_TERRACOTTA;
			};
		}

		//染色玻璃
		if (block == Blocks.WHITE_STAINED_GLASS || block == Blocks.ORANGE_STAINED_GLASS || block == Blocks.MAGENTA_STAINED_GLASS
				|| block == Blocks.LIGHT_BLUE_STAINED_GLASS || block == Blocks.YELLOW_STAINED_GLASS
				|| block == Blocks.LIME_STAINED_GLASS || block == Blocks.PINK_STAINED_GLASS
				|| block == Blocks.GRAY_STAINED_GLASS || block == Blocks.LIGHT_GRAY_STAINED_GLASS
				|| block == Blocks.CYAN_STAINED_GLASS || block == Blocks.PURPLE_STAINED_GLASS
				|| block == Blocks.BLUE_STAINED_GLASS || block == Blocks.BROWN_STAINED_GLASS
				|| block == Blocks.GREEN_STAINED_GLASS || block == Blocks.RED_STAINED_GLASS
				|| block == Blocks.BLACK_STAINED_GLASS) {
			return switch (color) {
				case WHITE -> Blocks.WHITE_STAINED_GLASS;
				case ORANGE -> Blocks.ORANGE_STAINED_GLASS;
				case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS;
				case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS;
				case YELLOW -> Blocks.YELLOW_STAINED_GLASS;
				case LIME -> Blocks.LIME_STAINED_GLASS;
				case PINK -> Blocks.PINK_STAINED_GLASS;
				case GRAY -> Blocks.GRAY_STAINED_GLASS;
				case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS;
				case CYAN -> Blocks.CYAN_STAINED_GLASS;
				case PURPLE -> Blocks.PURPLE_STAINED_GLASS;
				case BLUE -> Blocks.BLUE_STAINED_GLASS;
				case BROWN -> Blocks.BROWN_STAINED_GLASS;
				case GREEN -> Blocks.GREEN_STAINED_GLASS;
				case RED -> Blocks.RED_STAINED_GLASS;
				case BLACK -> Blocks.BLACK_STAINED_GLASS;
			};
		}

		//染色玻璃板
		if (block == Blocks.WHITE_STAINED_GLASS_PANE || block == Blocks.ORANGE_STAINED_GLASS_PANE || block == Blocks.MAGENTA_STAINED_GLASS_PANE
				|| block == Blocks.LIGHT_BLUE_STAINED_GLASS_PANE || block == Blocks.YELLOW_STAINED_GLASS_PANE
				|| block == Blocks.LIME_STAINED_GLASS_PANE || block == Blocks.PINK_STAINED_GLASS_PANE
				|| block == Blocks.GRAY_STAINED_GLASS_PANE || block == Blocks.LIGHT_GRAY_STAINED_GLASS_PANE
				|| block == Blocks.CYAN_STAINED_GLASS_PANE || block == Blocks.PURPLE_STAINED_GLASS_PANE
				|| block == Blocks.BLUE_STAINED_GLASS_PANE || block == Blocks.BROWN_STAINED_GLASS_PANE
				|| block == Blocks.GREEN_STAINED_GLASS_PANE || block == Blocks.RED_STAINED_GLASS_PANE
				|| block == Blocks.BLACK_STAINED_GLASS_PANE) {
			return switch (color) {
				case WHITE -> Blocks.WHITE_STAINED_GLASS_PANE;
				case ORANGE -> Blocks.ORANGE_STAINED_GLASS_PANE;
				case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS_PANE;
				case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS_PANE;
				case YELLOW -> Blocks.YELLOW_STAINED_GLASS_PANE;
				case LIME -> Blocks.LIME_STAINED_GLASS_PANE;
				case PINK -> Blocks.PINK_STAINED_GLASS_PANE;
				case GRAY -> Blocks.GRAY_STAINED_GLASS_PANE;
				case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS_PANE;
				case CYAN -> Blocks.CYAN_STAINED_GLASS_PANE;
				case PURPLE -> Blocks.PURPLE_STAINED_GLASS_PANE;
				case BLUE -> Blocks.BLUE_STAINED_GLASS_PANE;
				case BROWN -> Blocks.BROWN_STAINED_GLASS_PANE;
				case GREEN -> Blocks.GREEN_STAINED_GLASS_PANE;
				case RED -> Blocks.RED_STAINED_GLASS_PANE;
				case BLACK -> Blocks.BLACK_STAINED_GLASS_PANE;
			};
		}

		//染色陶瓦
		if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA
				|| block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA
				|| block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA
				|| block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA
				|| block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA
				|| block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA
				|| block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA
				|| block == Blocks.BLACK_TERRACOTTA) {
			return switch (color) {
				case WHITE -> Blocks.WHITE_TERRACOTTA;
				case ORANGE -> Blocks.ORANGE_TERRACOTTA;
				case MAGENTA -> Blocks.MAGENTA_TERRACOTTA;
				case LIGHT_BLUE -> Blocks.LIGHT_BLUE_TERRACOTTA;
				case YELLOW -> Blocks.YELLOW_TERRACOTTA;
				case LIME -> Blocks.LIME_TERRACOTTA;
				case PINK -> Blocks.PINK_TERRACOTTA;
				case GRAY -> Blocks.GRAY_TERRACOTTA;
				case LIGHT_GRAY -> Blocks.LIGHT_GRAY_TERRACOTTA;
				case CYAN -> Blocks.CYAN_TERRACOTTA;
				case PURPLE -> Blocks.PURPLE_TERRACOTTA;
				case BLUE -> Blocks.BLUE_TERRACOTTA;
				case BROWN -> Blocks.BROWN_TERRACOTTA;
				case GREEN -> Blocks.GREEN_TERRACOTTA;
				case RED -> Blocks.RED_TERRACOTTA;
				case BLACK -> Blocks.BLACK_TERRACOTTA;
			};
		}

		//蜡烛(支持原色蜡烛)
		if (block == Blocks.CANDLE || block == Blocks.WHITE_CANDLE || block == Blocks.ORANGE_CANDLE
				|| block == Blocks.MAGENTA_CANDLE || block == Blocks.LIGHT_BLUE_CANDLE
				|| block == Blocks.YELLOW_CANDLE || block == Blocks.LIME_CANDLE
				|| block == Blocks.PINK_CANDLE || block == Blocks.GRAY_CANDLE
				|| block == Blocks.LIGHT_GRAY_CANDLE || block == Blocks.CYAN_CANDLE
				|| block == Blocks.PURPLE_CANDLE || block == Blocks.BLUE_CANDLE
				|| block == Blocks.BROWN_CANDLE || block == Blocks.GREEN_CANDLE
				|| block == Blocks.RED_CANDLE || block == Blocks.BLACK_CANDLE) {
			return switch (color) {
				case WHITE -> Blocks.WHITE_CANDLE;
				case ORANGE -> Blocks.ORANGE_CANDLE;
				case MAGENTA -> Blocks.MAGENTA_CANDLE;
				case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CANDLE;
				case YELLOW -> Blocks.YELLOW_CANDLE;
				case LIME -> Blocks.LIME_CANDLE;
				case PINK -> Blocks.PINK_CANDLE;
				case GRAY -> Blocks.GRAY_CANDLE;
				case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CANDLE;
				case CYAN -> Blocks.CYAN_CANDLE;
				case PURPLE -> Blocks.PURPLE_CANDLE;
				case BLUE -> Blocks.BLUE_CANDLE;
				case BROWN -> Blocks.BROWN_CANDLE;
				case GREEN -> Blocks.GREEN_CANDLE;
				case RED -> Blocks.RED_CANDLE;
				case BLACK -> Blocks.BLACK_CANDLE;
			};
		}

		return null;
	}

	public static Block getDyedVariantCraftI(Block block, DyeColor color) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null)
			return null;

		CraftingContainer container = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
			@Override
			public boolean stillValid(Player player) {
				return false;
			}
			@Override
			public ItemStack quickMoveStack(Player player, int index) {
				return ItemStack.EMPTY;
			}
		}, 2, 1);
		container.setItem(0, new ItemStack(block));
		container.setItem(1, new ItemStack(DyeItem.byColor(color)));
		Optional<CraftingRecipe> recipe = server.getRecipeManager().getRecipeFor(
				RecipeType.CRAFTING,
				container,
				server.overworld()
		);
		if (recipe.isEmpty())
			return null;
		ItemStack result = recipe.get().assemble(container, server.registryAccess());
		if (!(result.getItem() instanceof BlockItem blockItem))
			return null;
		return blockItem.getBlock();
	}

	public static Block getDyedVariantCraftII(Block block, DyeColor color) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server == null)
			return null;

		CraftingContainer container = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
			@Override
			public boolean stillValid(Player player) {
				return false;
			}

			@Override
			public ItemStack quickMoveStack(Player player, int index) {
				return ItemStack.EMPTY;
			}
		}, 3, 3);

		ItemStack blockStack = new ItemStack(block);
		ItemStack dyeStack = new ItemStack(DyeItem.byColor(color));

		for (int i = 0; i < 9; i++) {
			if (i == 4)
				container.setItem(i, dyeStack.copy());
			else
				container.setItem(i, blockStack.copy());
		}

		Optional<CraftingRecipe> recipe = server.getRecipeManager().getRecipeFor(
				RecipeType.CRAFTING,
				container,
				server.overworld()
		);

		if (recipe.isEmpty())
			return null;

		ItemStack result = recipe.get().assemble(container, server.registryAccess());

		if (!(result.getItem() instanceof BlockItem blockItem))
			return null;

		return blockItem.getBlock();
	}

	public static Block getDyedVariant(Block block, DyeColor color) {
		Block special = getSpecialDyedVariant(block, color);
		if (special != null)
			return special;

		Block craft = getDyedVariantCraftI(block, color);
		if (craft != null)
			return craft;

		craft = getDyedVariantCraftII(block, color);
		if (craft != null)
			return craft;

		return null;
	}
}