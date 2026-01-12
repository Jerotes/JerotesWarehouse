
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package com.jerotes.jerotes.init;

import com.jerotes.jerotes.network.PacketHandler;
import com.jerotes.jerotes.network.SpellUseMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class JerotesModKeyMappings {
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

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(MAIN_SPELL_USE);
		event.register(ADD_SPELL_USE);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				MAIN_SPELL_USE.consumeClick();
				ADD_SPELL_USE.consumeClick();
			}
		}
	}
}
