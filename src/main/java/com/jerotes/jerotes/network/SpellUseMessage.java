
package com.jerotes.jerotes.network;

import com.jerotes.jerotes.spell.*;
import com.jerotes.jerotes.util.Main;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public class SpellUseMessage {
		int type, pressedms;
		boolean isAdd;

		public SpellUseMessage(int type, int pressedms, boolean isAdd) {
			this.type = type;
			this.pressedms = pressedms;
			this.isAdd = isAdd;
		}

		public SpellUseMessage(FriendlyByteBuf buffer) {
			this.type = buffer.readInt();
			this.pressedms = buffer.readInt();
			this.isAdd = buffer.readBoolean();
		}

		public static void encode(com.jerotes.jerotes.network.SpellUseMessage message, FriendlyByteBuf buffer) {
			buffer.writeInt(message.type);
			buffer.writeInt(message.pressedms);
			buffer.writeBoolean(message.isAdd);
		}
		public static com.jerotes.jerotes.network.SpellUseMessage decode(FriendlyByteBuf buffer) {
			return new com.jerotes.jerotes.network.SpellUseMessage(buffer.readInt(),buffer.readInt(),buffer.readBoolean());
		}

		public static void consume(com.jerotes.jerotes.network.SpellUseMessage message, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				ServerPlayer playerEntity = context.get().getSender();
				if (playerEntity != null) {
					pressAction(context.get().getSender(), message.type, message.pressedms, message.isAdd);
				}
			});
			context.get().setPacketHandled(true);
		}

	public static void pressAction(Player player, int type, int pressedms, boolean isAdd) {
		Level level = player.level();
		if (!level.hasChunkAt(player.blockPosition()))
			return;
		if (type == 0) {
			//主
			if (!isAdd) {
				if (SpellFindUseEvent.GetMainSpellUseCoolDownTick(player) > 0) {
					return;
				}
				MainSpellUse(player);
			}
			//次
			else {
				if (SpellFindUseEvent.GetAddSpellUseCoolDownTick(player) > 0) {
					return;
				}
				AddSpellUse(player);
			}
		}
	}

	public static void MainSpellUse(Player player) {
		if (Main.getJerotesPersistentData(player).getDouble("jerotes_spell_cooldown") > 0) {
			return;
		}
		String magic = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTarget;
		int level = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).MainSpellTargetLevel;
		if (!magic.isEmpty() && SpellRegistry.spellExists(magic)) {
			Entity target = player;
			int trueLevel = SpellListByString.getSpell(level, player, target, SpellRegistry.getSpellTypeById(magic)).getSpellLevel();

			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
			if (Main.getTargetedEntity(player, 256) != null && Main.getTargetedEntity(player, 256) instanceof LivingEntity livingEntity2) {
				if (magicSpellStart.getMagicType() != MagicType.SELF)
					target = livingEntity2;
			}

			MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
			boolean CanUse = magicSpell.canUse();
			boolean NoCanNotSelf = magicSpell.canUseTargetNone() || (target != player || magicSpell.getMagicType() != MagicType.TARGET);
			boolean CanUseTo = !(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target));
			boolean DistanceIsTrue = !(magicSpell.getMagicType() == MagicType.TARGET && player.distanceTo(target) > magicSpell.getSpellDistance());
			if (CanUse && NoCanNotSelf && CanUseTo && DistanceIsTrue) {
				if (!(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target))) {
					//法术列表
					boolean bl = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic)).spellUse();
					if (bl) {
						player.swing(InteractionHand.MAIN_HAND);
						//增加cd
						player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
							capability.setMainSpellUseCoolDownTick(magicSpell.playerUseCooldownTick(player));
						});
					}
				}
			}

		}
	}
	public static void AddSpellUse(Player player) {
		if (Main.getJerotesPersistentData(player).getDouble("jerotes_spell_cooldown") > 0) {
			return;
		}
		String magic = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTarget;
		int level = player.getCapability(JerotesPlayerData.CAPABILITY, null).orElse(new JerotesPlayerData.PlayerVariables()).AddSpellTargetLevel;
		if (!magic.isEmpty() && SpellRegistry.spellExists(magic)) {
			Entity target = player;
			int trueLevel = SpellListByString.getSpell(level, player, target, SpellRegistry.getSpellTypeById(magic)).getSpellLevel();

			MagicSpell magicSpellStart = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
			if (Main.getTargetedEntity(player, 256) != null && Main.getTargetedEntity(player, 256) instanceof LivingEntity livingEntity2) {
				if (magicSpellStart.getMagicType() != MagicType.SELF)
					target = livingEntity2;
			}

			MagicSpell magicSpell = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic));
			boolean CanUse = magicSpell.canUse();
			boolean NoCanNotSelf = magicSpell.canUseTargetNone() || (target != player || magicSpell.getMagicType() != MagicType.TARGET);
			boolean CanUseTo = !(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target));
			boolean DistanceIsTrue = !(magicSpell.getMagicType() == MagicType.TARGET && player.distanceTo(target) > magicSpell.getSpellDistance());
			if (CanUse && NoCanNotSelf && CanUseTo && DistanceIsTrue) {
				if (!(magicSpell.getMagicType() == MagicType.TARGET && !magicSpell.canUseToEntity(target))) {
					//法术列表
					boolean bl = SpellListByString.getSpell(trueLevel, player, target, SpellRegistry.getSpellTypeById(magic)).spellUse();
					if (bl) {
						player.swing(InteractionHand.OFF_HAND);
						//增加cd
						player.getCapability(JerotesPlayerData.CAPABILITY, null).ifPresent(capability -> {
							capability.setAddSpellUseCoolDownTick(magicSpell.playerUseCooldownTick(player));
						});
					}
				}
			}

		}
	}
}
