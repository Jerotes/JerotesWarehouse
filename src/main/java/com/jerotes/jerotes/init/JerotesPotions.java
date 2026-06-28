package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.recipe.brewing.JerotesCanBrewChangeLingeringRecipe;
import com.jerotes.jerotes.recipe.brewing.JerotesCanBrewChangeSplashRecipe;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class JerotesPotions {
    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, JerotesWarehouse.MODID);

    public static final RegistryObject<Potion> WASTE = REGISTRY.register("waste", () -> new Potion());
    public static final RegistryObject<Potion> CORROSIVE = REGISTRY.register("corrosive", () -> new Potion(new MobEffectInstance(JerotesMobEffects.CORROSIVE.get(), 240, 0)));
    public static final RegistryObject<Potion> LONG_CORROSIVE = REGISTRY.register("long_corrosive", () -> new Potion(new MobEffectInstance(JerotesMobEffects.CORROSIVE.get(), 600, 0)));
    public static final RegistryObject<Potion> STRONG_CORROSIVE = REGISTRY.register("strong_corrosive", () -> new Potion(new MobEffectInstance(JerotesMobEffects.CORROSIVE.get(), 160, 1)));
    public static final RegistryObject<Potion> DEADLY_POISON = REGISTRY.register("deadly_poison", () -> new Potion(new MobEffectInstance(JerotesMobEffects.DEADLY_POISON.get(), 240, 0)));
    public static final RegistryObject<Potion> LONG_DEADLY_POISON = REGISTRY.register("long_deadly_poison", () -> new Potion(new MobEffectInstance(JerotesMobEffects.DEADLY_POISON.get(), 600, 0)));
    public static final RegistryObject<Potion> STRONG_DEADLY_POISON = REGISTRY.register("strong_deadly_poison", () -> new Potion(new MobEffectInstance(JerotesMobEffects.DEADLY_POISON.get(), 160, 1)));

    public static void init() {
        BrewingRecipeRegistry.addRecipe(new JerotesCanBrewChangeSplashRecipe());
        BrewingRecipeRegistry.addRecipe(new JerotesCanBrewChangeLingeringRecipe());
    }

    public static ItemStack createPotion(RegistryObject<Potion> potion) {
        return createPotion(potion.get());
    }
    public static ItemStack createSplashPotion(RegistryObject<Potion> potion) {
        return createSplashPotion(potion.get());
    }
    public static ItemStack createLingeringPotion(RegistryObject<Potion> potion) {
        return createLingeringPotion(potion.get());
    }
    public static ItemStack createPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }
    public static ItemStack createSplashPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion);
    }
    public static ItemStack createLingeringPotion(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), potion);
    }

}
