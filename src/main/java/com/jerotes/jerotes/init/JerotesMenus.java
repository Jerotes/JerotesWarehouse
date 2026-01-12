package com.jerotes.jerotes.init;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.world.inventory.MobInventoryGUIMenu;
import com.jerotes.jerotes.world.inventory.SuchInventoryMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class JerotesMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, JerotesWarehouse.MODID);
    public static final RegistryObject<MenuType<MobInventoryGUIMenu>> MOB_INVENTORY_GUI = REGISTRY.register("mob_inventory_gui", () -> IForgeMenuType.create(MobInventoryGUIMenu::new));
    public static final RegistryObject<MenuType<SuchInventoryMenu>> SUCH_INVENTORY = REGISTRY.register("such_inventory", () -> IForgeMenuType.create(SuchInventoryMenu::new));
}