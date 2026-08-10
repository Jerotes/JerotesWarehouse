package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.entity.Interface.MagicSpellLivingEntity;
import com.jerotes.jerotes.item.FlawlessMagicScroll;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class SpellGuiEvent {
	//法术
	@SubscribeEvent(priority = EventPriority.HIGH)
		public static void Spell(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		{
			if (player instanceof MagicSpellLivingEntity magicSpellLivingEntity) {
				int widthAdd = 16;
				int heightAdd = 0;
				//主要
				if (SpellRegistry.spellExists(player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget)) {
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/main_spell.png"), widthAdd, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);

					//图标
					SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget);
					MagicSpell magicSpell = SpellListByString.getSpellEasy(spellTypeInterface);
					ResourceLocation resourceLocation = magicSpell.getDisplayResourceLocation();
					event.getGuiGraphics().blit(resourceLocation, widthAdd + 2, h - 22 + heightAdd + 2, 0, 0, 18, 18, 18, 18);
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/main_spell_overlay.png"), widthAdd, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);
					//背景
					int now = SpellFindUseEvent.GetMainSpellUseCoolDownTick(player);
					int max = SpellFindUseEvent.GetMainSpellUseCoolDownTickMax(player);
					if (max > 0) {
						float progress = (float) now / max;
						int n = (int)(18 * progress);
						//间隔
						event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/main_spell_cooldown.png"), widthAdd + 3, h - 22 + 3 + heightAdd, 0, 0,
								16, n, 16, 16);
					}
					//法术等级
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/spell_level_back.png"), widthAdd + 22, h - 22  + heightAdd, 0, 0, 7, 22, 7, 22);
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/spell_level.png"), widthAdd + 22, h - 22 + heightAdd, 0, 0,
							7, 1 + ((10 - SpellFindUseEvent.GetMainMainSpellTargetLevel(player)) * 2), 7, 22);
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/spell_base_level.png"),
							widthAdd + 22, h - 22 + heightAdd + ((10 - magicSpell.getBaseSpellLevel()) * 2), 0, 0,
							7, 1 + (magicSpell.getBaseSpellLevel() * 2), 7, 22);
				}


				//次要
				if (SpellRegistry.spellExists(player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget)) {
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/add_spell.png"), widthAdd + 22 + 7, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);

					//图标
					SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget);
					MagicSpell magicSpell = SpellListByString.getSpellEasy(spellTypeInterface);
					ResourceLocation resourceLocation = magicSpell.getDisplayResourceLocation();
					event.getGuiGraphics().blit(resourceLocation, widthAdd + 22 + 7 + 2, h - 22 + heightAdd + 2, 0, 0, 18, 18, 18, 18);
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/add_spell_overlay.png"), widthAdd + 22 + 7, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);
					//背景
					int now = SpellFindUseEvent.GetAddSpellUseCoolDownTick(player);
					int max = SpellFindUseEvent.GetAddSpellUseCoolDownTickMax(player);
					if (max > 0) {
						float progress = (float) now / max;
						int n = (int)(18 * progress);
						//间隔
						event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/add_spell_cooldown.png"), widthAdd + 22 + 7 + 3, h - 22 + 3 + heightAdd, 0, 0,
								16, n, 16, 16);
					}
					//法术等级
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/spell_level_back.png"), widthAdd + 22 + 7 + 22, h - 22 + heightAdd, 0, 0, 7, 22, 7, 22);
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/spell_level.png"), widthAdd + 22 + 7 + 22, h - 22 + heightAdd, 0, 0,
							7, 1 + ((10 - SpellFindUseEvent.GetAddSpellTargetLevel(player)) * 2), 7, 22);
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/spell_base_level.png"),
							widthAdd + 22 + 7 + 22, h - 22 + heightAdd + ((10 - magicSpell.getBaseSpellLevel()) * 2), 0, 0,
							7, 1 + (magicSpell.getBaseSpellLevel() * 2), 7, 22);
				}


				//专注
				if (!SpellType.getConcentrationSpellTypeJerotes(player).isEmpty()) {
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/concentration_spell.png"), widthAdd + 44 + 14, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);
					//图标
					SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(SpellType.getConcentrationSpellTypeJerotes(player));
					MagicSpell magicSpell = SpellListByString.getSpellEasy(spellTypeInterface);
					ResourceLocation resourceLocation = magicSpell.getDisplayResourceLocation();
					event.getGuiGraphics().blit(resourceLocation, widthAdd + 44 + 14 + 2, h - 22 + heightAdd + 2, 0, 0, 18, 18, 18, 18);
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/concentration_spell_overlay.png"), widthAdd + 44 + 14, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);
				}

				//施法中
				if (magicSpellLivingEntity.getCastingTickJerotes() > -1 && magicSpellLivingEntity.getCastingMaxTickJerotes() > 0) {
					//基底渲染
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/casting_backround.png"),
							widthAdd, h - 31 + heightAdd, 0, 0, 58, 9, 58, 9);
					//进度渲染
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/magic/casting.png"),
							widthAdd + 3, h - 28 + heightAdd, 0, 0, (int) ((1f - (float) magicSpellLivingEntity.getCastingTickJerotes() / (float) magicSpellLivingEntity.getCastingMaxTickJerotes()) * 52), 3, 52, 3);


					if (!SpellType.getLastSpellTypeJerotes(player).isEmpty()) {
						SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(SpellType.getLastSpellTypeJerotes(player));
						MagicSpell magicSpell = SpellListByString.getSpellEasy(spellTypeInterface);
						if (magicSpell.isCastingCanNotStop()) {
							event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/sprites/container/mob_inventory/lock.png"),
									widthAdd + 20, h - 31 + heightAdd - 6, 0, 0, 16, 16, 16, 16);
						}
					}

					float f = ((float) magicSpellLivingEntity.getCastingTickJerotes() / (float) magicSpellLivingEntity.getCastingMaxTickJerotes()) * magicSpellLivingEntity.getCastingMaxTickJerotes() / 20f;
					String formatted = String.format("%.2f", f);

					PoseStack poseStack = event.getGuiGraphics().pose();
					poseStack.pushPose();
					float scale = 0.75f;
					poseStack.scale(scale, scale, scale);
					drawCenteredString(event.getGuiGraphics(), Minecraft.getInstance().font, Component.translatable("message.jerotes.second_s", formatted),
                            (int) (widthAdd/scale), (int) ((h - 31 + heightAdd)/scale), (int) ((widthAdd + 58)/scale), (int) ((h - 31 + heightAdd + 16)/scale), 0xd3faff);
					poseStack.popPose();
				}
			}
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	private static void drawCenteredString(GuiGraphics guiGraphics, Font font, Component text, int x1, int y1, int x2, int y2, int color) {
		int textWidth = font.width(text);
		int textHeight = font.lineHeight;
		int centerX = x1 + (x2 - x1) / 2 - textWidth / 2;
		int centerY = y1 + (y2 - y1) / 2 - textHeight / 2 - textHeight / 2;
		guiGraphics.drawString(font, text, centerX, centerY, color, false);
	}

}