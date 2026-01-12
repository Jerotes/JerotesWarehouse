package com.jerotes.jerotes.init;

import com.jerotes.jerotes.client.particle.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public enum JerotesTiers implements Tier {
	COPPER(1, 190, 5.0f, 1.0f, 15, () -> {
		return Ingredient.of(Items.COPPER_INGOT);
	});

	private final int level;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;
	private final LazyLoadedValue<Ingredient> repairIngredient;

	private JerotesTiers(int p_43332_, int p_43333_, float p_43334_, float p_43335_, int p_43336_, Supplier p_43337_) {
		this.level = p_43332_;
		this.uses = p_43333_;
		this.speed = p_43334_;
		this.damage = p_43335_;
		this.enchantmentValue = p_43336_;
		this.repairIngredient = new LazyLoadedValue(p_43337_);
	}

	public int getUses() {
		return this.uses;
	}

	public float getSpeed() {
		return this.speed;
	}

	public float getAttackDamageBonus() {
		return this.damage;
	}

	public int getLevel() {
		return this.level;
	}

	public int getEnchantmentValue() {
		return this.enchantmentValue;
	}

	public Ingredient getRepairIngredient() {
		return (Ingredient)this.repairIngredient.get();
	}

	public @Nullable TagKey<Block> getTag() {
		return null;
	}

}
