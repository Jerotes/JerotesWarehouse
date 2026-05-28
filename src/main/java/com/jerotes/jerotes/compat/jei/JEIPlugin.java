package com.jerotes.jerotes.compat.jei;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.client.gui.MobInventoryGUIScreen;
import com.jerotes.jerotes.client.gui.SuchInventoryScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin
{
	private static final ResourceLocation ID = new ResourceLocation(JerotesWarehouse.MODID, "jei_plugin");


	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
	}

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}
}