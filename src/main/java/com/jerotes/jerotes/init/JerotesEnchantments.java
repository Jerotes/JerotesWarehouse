package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.enchantment.BaneOfHumanoidsEnchantment;
import com.jerotes.jerotes.enchantment.CorrosionResistanceEnchantment;
import com.jerotes.jerotes.enchantment.LungeEnchantment;
import com.jerotes.jerotes.item.Tool.ItemToolBasePike;
import com.jerotes.jerotes.item.Tool.ItemToolBaseSpearBase;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JerotesEnchantments {
	public static final EnchantmentCategory SPEAR_ABOUT = EnchantmentCategory.create("spear_about", item -> item instanceof ItemToolBaseSpearBase || item instanceof ItemToolBasePike);

	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, JerotesWarehouse.MODID);
	public static final RegistryObject<Enchantment> CORROSION_RESISTANCE = REGISTRY.register("corrosion_resistance", CorrosionResistanceEnchantment::new);
	public static final RegistryObject<Enchantment> BANE_OF_HUMANOIDS = REGISTRY.register("bane_of_humanoids", BaneOfHumanoidsEnchantment::new);
	public static final RegistryObject<Enchantment> LUNGE = REGISTRY.register("lunge", LungeEnchantment::new);
}