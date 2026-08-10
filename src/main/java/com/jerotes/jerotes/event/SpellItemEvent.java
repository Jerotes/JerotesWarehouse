package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.MagicSpellLivingEntity;
import com.jerotes.jerotes.item.FlawlessMagicScroll;
import com.jerotes.jerotes.item.Interface.MagicItem;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = JerotesWarehouse.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SpellItemEvent {
	@SubscribeEvent
	public static void onScreenRender(ScreenEvent.Render.Post event) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.screen;
		if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) return;

		Slot hoveredSlot = containerScreen.getSlotUnderMouse();
		if (hoveredSlot == null || !hoveredSlot.hasItem()) return;

		ItemStack stack = hoveredSlot.getItem();
		if (!(stack.getItem() instanceof MagicItem flawlessMagicScroll && flawlessMagicScroll.showItemIcon())) return;

		String spellId = flawlessMagicScroll.getSpellIdMagicItem(stack);
		if (spellId.isEmpty() || !SpellRegistry.spellExists(spellId)) return;

		MagicSpell spell = SpellListByString.getSpell(1, null, null, SpellRegistry.getSpellTypeById(spellId));
		if (spell == null) return;
		ResourceLocation resourceLocation = spell.getDisplayResourceLocation();
		if (resourceLocation == null) return;

		GuiGraphics graphics = event.getGuiGraphics();
		int slotX = containerScreen.getGuiLeft() + hoveredSlot.x;
		int slotY = containerScreen.getGuiTop() + hoveredSlot.y;

		graphics.pose().pushPose();
		graphics.pose().translate(0, 0, 300);
		ResourceLocation resourceLocation2 = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/spell_background.png");
		graphics.blit(resourceLocation2, slotX-1, slotY-1, 0, 0, 18, 18, 18, 18);
		graphics.blit(resourceLocation, slotX-1, slotY-1, 0, 0, 18, 18, 18, 18);
		graphics.pose().popPose();
	}
}