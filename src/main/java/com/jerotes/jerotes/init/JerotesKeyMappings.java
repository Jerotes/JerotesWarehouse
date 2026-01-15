
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.jerotes.jerotes.init;

import com.jerotes.jerotes.entity.Interface.ControlVehicleEntity;
import com.jerotes.jerotes.network.ControlVehicleMessage;
import com.jerotes.jerotes.network.PacketHandler;
import com.jerotes.jerotes.network.SpellUseMessage;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class JerotesKeyMappings {
	public static final KeyMapping MAIN_SPELL_USE = new KeyMapping("key.jerotes.main_spell_use", GLFW.GLFW_KEY_Y, "key.categories.gameplay") {
		private boolean isDownOld = false;
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				PacketHandler.sendToServer(new SpellUseMessage(0, 0, false));
				if (Minecraft.getInstance().player != null) {
					SpellUseMessage.pressAction(Minecraft.getInstance().player, 0, 0, false);
				}
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping ADD_SPELL_USE = new KeyMapping("key.jerotes.add_spell_use", GLFW.GLFW_KEY_U, "key.categories.gameplay") {
		private boolean isDownOld = false;
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				PacketHandler.sendToServer(new SpellUseMessage(0, 0, true));
				if (Minecraft.getInstance().player != null) {
					SpellUseMessage.pressAction(Minecraft.getInstance().player, 0, 0, true);
				}
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping CHANGE_CONTROL_COMBAT_TYPE = new KeyMapping("key.jerotes.change_control_combat_type", GLFW.GLFW_KEY_K, "key.categories.gameplay") {
		private boolean isDownOld = false;
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			Minecraft mc = Minecraft.getInstance();
			if (isDownOld != isDown && isDown && mc.player != null && mc.player.getControlledVehicle() != null) {
				PacketHandler.sendToServer(new ControlVehicleMessage(0, 0, mc.player.getControlledVehicle().getId(), 2));
				if (Minecraft.getInstance().player != null) {
					ControlVehicleMessage.pressAction(mc.player, 0, 0, mc.player.getControlledVehicle().getId(), 2);
				}
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping VEHICLE_CONTROL_MOUSE_MAIN = new KeyMapping("key.jerotes.vehicle_control_mouse_main", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_LEFT, "key.categories.gameplay") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			Minecraft mc = Minecraft.getInstance();
			if (isDownOld != isDown && isDown) {
				if (mc.player != null && mc.player.getControlledVehicle() != null && mc.player.getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity && controlVehicleEntity.canPressMain()) {
					if (controlVehicleEntity.isManuallyControlCombat()) {
						PacketHandler.sendToServer(new ControlVehicleMessage(0, 0, mc.player.getControlledVehicle().getId(), 0));
						if (mc.player != null) {
							ControlVehicleMessage.pressAction(mc.player, 0, 0, mc.player.getControlledVehicle().getId(), 0);
						}
					}
				}
			}
			isDownOld = isDown;
		}
	};

	public static final KeyMapping VEHICLE_CONTROL_MOUSE_ADD = new KeyMapping("key.jerotes.vehicle_control_mouse_add", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, "key.categories.gameplay") {
		private boolean isDownOld = false;
		@Override
		public void setDown(boolean isDown) {
			Minecraft mc = Minecraft.getInstance();
			if (isDownOld != isDown && isDown) {
				if (mc.player != null && mc.player.getControlledVehicle() != null && mc.player.getControlledVehicle() instanceof ControlVehicleEntity controlVehicleEntity && controlVehicleEntity.canPressMain()) {
					if (controlVehicleEntity.isManuallyControlCombat()) {
						PacketHandler.sendToServer(new ControlVehicleMessage(0, 0, mc.player.getControlledVehicle().getId(), 1));
						if (mc.player != null) {
							ControlVehicleMessage.pressAction(mc.player, 0, 0, mc.player.getControlledVehicle().getId(), 1);
						}
					}
				}
			}
			isDownOld = isDown;
		}
	};

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(MAIN_SPELL_USE);
		event.register(ADD_SPELL_USE);
		event.register(CHANGE_CONTROL_COMBAT_TYPE);
		event.register(VEHICLE_CONTROL_MOUSE_MAIN);
		event.register(VEHICLE_CONTROL_MOUSE_ADD);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				MAIN_SPELL_USE.consumeClick();
				ADD_SPELL_USE.consumeClick();
				CHANGE_CONTROL_COMBAT_TYPE.consumeClick();
				VEHICLE_CONTROL_MOUSE_MAIN.consumeClick();
				VEHICLE_CONTROL_MOUSE_ADD.consumeClick();
			}
		}
	}
}
