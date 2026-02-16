package com.jerotes.jerotes.event;

import com.jerotes.jerotes.JerotesWarehouse;
import com.jerotes.jerotes.config.MainConfig;
import com.jerotes.jerotes.entity.Interface.ControlVehicleEntity;
import com.jerotes.jerotes.init.JerotesMobEffects;
import com.jerotes.jerotes.item.AAExplorationEye;
import com.jerotes.jerotes.item.Tool.*;
import com.jerotes.jerotes.network.JerotesPlayerData;
import com.jerotes.jerotes.spell.*;
import com.jerotes.jerotes.util.Main;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class CameraEvent {
	private static final ResourceLocation NAUSEA_LOCATION = new ResourceLocation("textures/misc/nausea.png");
	private static final ResourceLocation DARK_LOCATION = new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/dark.png");

	@SubscribeEvent
	public static void onCameraSetup(ComputeFovModifierEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.player.getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity) {
			if (controlVehicleEntity.isManuallyControlCombatJerotes()) {
				event.setNewFovModifier(event.getFovModifier() * controlVehicleEntity.getManuallyControlCombatCameraChangeJerotes());
			}
		}
	}

	//屏幕震动
	@SubscribeEvent
	public static void Shake(ViewportEvent.ComputeCameraAngles event) {
		Player player = Minecraft.getInstance().player;
		float delta = Minecraft.getInstance().getFrameTime();
		if (player != null && player.hasEffect(JerotesMobEffects.QUAKE.get())) {
			if (Objects.requireNonNull(player.getEffect(JerotesMobEffects.QUAKE.get())).getDuration() > 0 && MainConfig.ScreenShake) {
				int time = Objects.requireNonNull(player.getEffect(JerotesMobEffects.QUAKE.get())).getDuration();
				double shakes = (double) time / 8;
				double ticksExistedDelta = shakes + delta;
				event.setPitch((float) (event.getPitch() + Math.cos(ticksExistedDelta * 0.5 + 2) * 1));
				event.setYaw((float) (event.getYaw() + Math.cos(ticksExistedDelta * 0.5 + 1) * 0.2));
				event.setRoll((float) (Math.sin((player.tickCount) * 1.0f) * (0.5f + shakes * 0.25f)));
			}
		}
	}

	//视角
	@SubscribeEvent
	public static void FOV(ComputeFovModifierEvent event) {
		Player player = event.getPlayer();
		if (player.isUsingItem()) {
			Item useItem = player.getUseItem().getItem();
			if (useItem instanceof ItemToolBaseBow) {
				float f = player.getTicksUsingItem() / 20.0F;
				f = f > 1.0F ? 1.0F : f * f;
				event.setNewFovModifier((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, (event.getFovModifier() * (1.0F - f * 0.15F))));
			}
			else if (useItem instanceof ItemToolBaseFlail itemToolBaseFlail) {
				float f = player.getTicksUsingItem() / (float) itemToolBaseFlail.useTick;
				f = f > 1.0F ? 1.0F : f * f;
				event.setNewFovModifier((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, (event.getFovModifier() * (1.0F - f * 0.05F))));
			}
			else if (useItem instanceof ItemToolBaseWhip itemToolBaseWhip) {
				float f = player.getTicksUsingItem() / (float) itemToolBaseWhip.useTick;
				f = f > 1.0F ? 1.0F : f * f;
				event.setNewFovModifier((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, (event.getFovModifier() * (1.0F - f * 0.05F))));
			}
			else if (useItem instanceof AAExplorationEye) {
				float f = player.getTicksUsingItem() / 20.0F;
				f = f > 1.0F ? 1.0F : f * f;
				event.setNewFovModifier((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, (event.getFovModifier() * (1.0F - f * 0.8F))));
			}
		}
	}


	//匕首
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void Dagger(RenderGuiEvent.Pre event) {
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
		Item item = player.getMainHandItem().getItem();
		if (player.isShiftKeyDown() && item instanceof ItemToolBaseDagger && Main.getTargetedEntity(player, 6, true) != null && Main.getTargetedEntity(player, 6) instanceof LivingEntity livingEntity && !Main.canSee(player, livingEntity)) {
			event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/dagger.png"), w / 2 - 6, h / 2 - 17, 0, 0, 12, 12, 12, 12);
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
	//匕首
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void ParryShield(RenderGuiEvent.Pre event) {
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
		Item item = player.getUseItem().getItem();
		if (player.isUsingItem() && item instanceof ItemToolBaseParryShield && (player.getPersistentData().getDouble("jerotes_shield_parry_cooldown") <= 0 || player.getPersistentData().get("jerotes_shield_parry_cooldown") == null)) {
			event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/parry_shield.png"), w / 2 - 6, h / 2 - 17, 0, 0, 12, 12, 12, 12);
		}
		else if (player.isUsingItem() && item instanceof ItemToolBaseParryShield && player.getPersistentData().getDouble("jerotes_shield_parry_tick") > 0) {
			event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/parry_shield_use.png"), w / 2 - 6, h / 2 - 17, 0, 0, 12, 12, 12, 12);
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

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
			int widthAdd = 16;
			int heightAdd = 0;
			//主要
			if (SpellRegistry.spellExists(player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget)) {
				event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/main_spell.png"), widthAdd, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);

				//图标
				SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget);
				MagicSpell magicSpell = SpellListByString.getSpellEasy(spellTypeInterface);
				ResourceLocation resourceLocation = magicSpell.getDisplayResourceLocation();
				event.getGuiGraphics().blit(resourceLocation, widthAdd + 2, h - 22 + heightAdd + 2, 0, 0, 18, 18, 18, 18);
				event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/main_spell_overlay.png"), widthAdd, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);
				//背景
				int now = SpellFindUseEvent.GetMainSpellUseCoolDownTick(player);
				int max = SpellFindUseEvent.GetMainSpellUseCoolDownTickMax(player);
				if (max > 0) {
					float progress = (float) now / max;
					int n = (int)(18 * progress);
					//间隔
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/main_spell_cooldown.png"), widthAdd + 3, h - 22 + 3 + heightAdd, 0, 0,
							16, n, 16, 16);
				}
				//法术等级
				event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/spell_level_back.png"), widthAdd + 22, h - 22  + heightAdd, 0, 0, 7, 22, 7, 22);
				event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/spell_level.png"), widthAdd + 22, h - 22 + heightAdd, 0, 0,
						7, 1 + ((10 - SpellFindUseEvent.GetMainMainSpellTargetLevel(player)) * 2), 7, 22);
			}
			//次要
			if (SpellRegistry.spellExists(player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget)) {
				event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/add_spell.png"), widthAdd + 22 + 7, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);

				//图标
				SpellTypeInterface spellTypeInterface = SpellRegistry.getSpellTypeById(player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget);
				MagicSpell magicSpell = SpellListByString.getSpellEasy(spellTypeInterface);
				ResourceLocation resourceLocation = magicSpell.getDisplayResourceLocation();
				event.getGuiGraphics().blit(resourceLocation, widthAdd + 22 + 7 + 2, h - 22 + heightAdd + 2, 0, 0, 18, 18, 18, 18);
				event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/add_spell_overlay.png"), widthAdd + 22 + 7, h - 22 + heightAdd, 0, 0, 22, 22, 22, 22);
				//背景
				int now = SpellFindUseEvent.GetAddSpellUseCoolDownTick(player);
				int max = SpellFindUseEvent.GetAddSpellUseCoolDownTickMax(player);
				if (max > 0) {
					float progress = (float) now / max;
					int n = (int)(18 * progress);
					//间隔
					event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/add_spell_cooldown.png"), widthAdd + 22 + 7 + 3, h - 22 + 3 + heightAdd, 0, 0,
							16, n, 16, 16);
				}
				//法术等级
				event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/spell_level_back.png"), widthAdd + 22 + 7 + 22, h - 22 + heightAdd, 0, 0, 7, 22, 7, 22);
				event.getGuiGraphics().blit(new ResourceLocation(JerotesWarehouse.MODID, "textures/gui/spell_level.png"), widthAdd + 22 + 7 + 22, h - 22 + heightAdd, 0, 0,
						7, 1 + ((10 - SpellFindUseEvent.GetAddSpellTargetLevel(player)) * 2), 7, 22);
			}
		}
		RenderSystem.depthMask(true);
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}